/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.server.app.sas;

/**
 * Selects a SAS provider client to launch an app. Asks the provider to launch the app.
 * If it refuses (usually because of capacity exhaustion) a new candidate provider is 
 * chosed and asked to launch the app, and so on, until either a provider is found that 
 * agrees to launch the app or no more suitable providers can be found.
 *
 * @author deronj
 */

@ExperimentalAPI
class Distributor {

    /** The time we will wait for provider to respond to a launch message: 30 secs */
    private static final long PROVIDER_LAUNCH_TIMEOUT_MS = 30000;

    private class LaunchAttemptState {
	int sequenceNumber;
	int userSequenceNumber;
	SasLaunchMessage launchMessage;
	BigInteger providerClientID;
	WonderlandClientSender providerSender;
	WonderlandClientSender userSender;
	String executionCapability;
	String appName;
	Serializable launchInfo;
	Timer timer;
	boolean timedOut;
	LinkedList<BigInteger> providersAlreadyTried;
	LaunchAttemptState previousAttempt;

	void cleanup () {
	    removeLaunchAttemptState(state.providerClientID, this);

	    if (providersAlreadyTried != null) {
		providersAlreadyTried.clear();
		providersAlreadyTried = null;
	    }

	    LaunchAttemptState state = previousAttempt;
	    while (state != null) {
		state.cleanup();
		state = state.previousAttempt;
	    }
	    previousAttempt = null;
	}
    }

    private int nextSequenceNumber = 0;

    private HashMap<BigInteger,LinkedList<LaunchAttemptState>> pendingLaunches;

    void tryLaunch (BigInteger userClientID, WonderlandClientSender userSender, int userSequenceNumber,
		    String executionCapability, String appName, String launchInfo, 
		    LaunchAttemptState previousAttempt) {
	
	LinkedList<BigInteger> providersAlreadyTried = null;
	if (previousAttempt != null) {
	    providersAlreadyTried = previousAttempt.providersAlreadyTried.clone();
	}

	// Get all the possible providers
	LinkedList<ProviderInfo> providerInfos = registry.getAppProviders(executionCapability, appName,
									  providersAlreadyTried);
	if (providerInfos == null || providerInfos.size() <= 0) {
	    userSender.send(SasMessage.createUserLaunchStatusMessage(UserLaunchStatus.NO_PROVIDER, userSequenceNumber));
	    previousAttempt.cleanup();
	}

	ProviderInfo providerInfo = selectProvider(providerInfos);
	if (providerInfo == null) {
	    userSender.send(SasMessage.createUserLaunchStatusMessage(UserLaunchStatus.NO_PROVIDER, userSequenceNumber));
	    previousAttempt.cleanup();
	}

	LaunchAttemptState state = new LaunchAttemptState();
	state.sequenceNumber = nextSequenceNumber;
	if (nextSequenceNumber == Integer.MAX_VALUE) {
	    nextSequenceNumber = 0;
	} else {
	    nextSequenceNumber++;
	}
	state.launchMessage = msg;
	state.providerClientID = providerInfo.providerClientID;
	state.providerSender = providerInfo.providerSender;
	state.userSender = userSender;
	state.userSequenceNumber = userSequenceNumber;
	state.executionCapability = executionCapability;
	state.appName = appName;
	state.launchInfo = launchInfo;
	state.previousAttempt = previousAttempt;

	if (providersAlreadyTried == null) {
	    state.providersAlreadyTried = new LinkedList<BigInteger>();
	} else {
	    state.providersAlreadyTried = providersAlreadyTried;
	}
	state.providersAlreadyTried.add(providerInfo.providerClientID);

	synchronized (pendingLaunches) {
	    addLaunchAttemptState(providerInfo.providerClientID, state);
	}

	startTimer(state);

	SasLaunchMessage msg = SasMessage.createSasServerLaunchMessage(sequenceNumber, userClientID, 
							 executionCapability, appName, launchInfo);
        providerInfo.providerSender.send(msg);
    }

    // Note: After tryLaunch is called, at least one of the following methods will be called:
    //    1. launchSucceeded - If provider responds to the launch message with success.
    //    2. launchFailed - If provider responds to the launch message with failure.
    //    3. launchTimedOut - If provider doesn't respond to the launch message with a status within the timeout.

    void launchSucceeded (BigInteger providerClientID, int sequenceNumber) {
	LaunchAttemptState state;
	synchronized (pendingLaunches) {
	    state = findLaunchAttemptState(providerClientID, sequenceNumber);
	    if (state == null || state.timedOut) {
		// We've already given up on the launch request to this provider
		return;
	    }
	}

	Registry.incrementProviderNumApps(providerClientID);
	state.cleanup();

	// Notify user client of a successful launch
	state.userSender.send(SasMessage.createUserLaunchStatusMessage(UserLaunchStatus.SUCCESS, 
								       state.userSequenceNumber));
    }

    void launchFailed (BigInteger providerClientID, int sequenceNumber) {
	LaunchAttemptState state;
	synchronized (pendingLaunches) {
	    state = findLaunchAttemptState(providerClientID, sequenceNumber);
	    if (state == null || state.timedOut) {
		// We've already given up on the launch request to this provider
		return;
	    }
	}

	retry(state);
    }

    private void launchTimedOut (BigInteger providerClientID, int sequenceNumber) {
	LaunchAttemptState state;
	synchronized (pendingLaunches) {
	    state = findLaunchAttemptState(providerClientID, sequenceNumber);
	    if (state == null) {
		// Cannot find launch state. We must have gotten some message back from the provider before the timeout.
		return;
	    }
	    state.timedOut = true;
	}

	// Try to kill the app because the launch timed out
	abort(providerClientID, sequenceNumber);

	// Treat as failure and retry to launch again with a different provider
	retry(state);
    }

    void retry (LaunchAttemptState state) {
	tryLaunch(state.launchMessage.getUserClientID(), state.userSender, state.userSequenceNumber, 
		  state.executionCapability, state.appName, state.launchInfo, state);
    }

    private void abort (BigInteger providerClientID, LaunchAttemptState state) {
	// TODO: create abort message
	state.providerSender.send(SasMessage.createAbortMessage)(TODO);
    }

    private void addLaunchAttemptState (BigInteger providerClientID, LaunchAttemptState state) {
	LinkedList<LaunchAttemptState> pendingList = pendingLaunches.get(providerInfo.providerClientID);
	if (pendingList == null) {
	    pendingList = new LinkedList<LaunchAttemptState>();
	    pendingLaunches.put(providerInfo.providerClientID, pendingList);
	}
	pendingList.add(state);
    }

    private void removeLaunchAttemptState (BigInteger providerClientID, LaunchAttemptState state) {
	LinkedList<LaunchAttemptState> pendingList = pendingLaunches.get(providerClientID);
	if (pendingList == null) {
	    return null;
	}

	LaunchAttemptState toDelete = null;
	for (LaunchAttemptState las : pendingList) {
	    if (las.sequenceNumber == state.sequenceNumber) {
		toDelete = las;
		break;
	    }
	}
	if (toDelete != null) {
	    toDelete.timer.cancel();
	    pendingList.remove(toDelete);
	}
    }

    private LaunchAttemptState findLaunchAttemptState (BigInteger providerClientID, int sequenceNumber) {
	LinkedList<LaunchAttemptState> pendingList = pendingLaunches.get(providerClientID);
	if (pendingList == null) {
	    return null;
	}
	    
	for (LaunchAttemptState state : pendingList) {
	    if (state.sequenceNumber == sequenceNumber) {
		return state;
	    }
	}
	    
	return null;
    }

    private void startTimer (final LaunchAttemptState state) {
	state.timedOut = false;
	state.timer = new Timer();
	state.timer.schedule(new TimerTask() {
	    public void run () {
		this.launchTimedOut(state.providerClientID, state.sequenceNumber);
	    }
	}, PROVIDER_LAUNCH_TIMEOUT_MS);
    }


    /**
     * Select a suitable provider from among a list of providers.
     *
     * HEURISTIC: this method tries to find provider which is running the fewest number
     * of apps and, failing that, chooses one at random. If there are multiple providers
     * running the least number of apps once is chosen at random.
     */
    private ProviderInfo selectProvider (LinkedList<ProviderInfo> providerInfos) {

	// Determine the minimum number of apps that are being run by all providers
	int minNumApps = Integer.MAX_VALUE;
	for (ProviderInfo providerInfo : providerInfos) {
	    if (providerInfo.providerNumApps < minNumApps) {
		minNumApps = providerInfo.providerNumApps;
	    }
	}
	if (minNumApps == Integer.MAX_VALUE) {
	    return null;
	}

	// Accumulate the list of minimum number providers
	LinkedList<ProviderInfo> minProviders = new LinkedList<ProviderInfo>();
	for (ProviderInfo providerInfo : providerInfos) {
	    if (providerInfo.providerNumApps == minNumApps) {
		minProviders.add(providerInfo);
	    }
	}
	if (minProviders.size() <= 0) {
	    return null;
	}

	ProviderInfo providerInfo = selectRandomProvider(minProviders);
	minProviders.clear();
	
	return providerInfo;
    }

    /**
     * Selects and returns a random provider from the given list. Assumes that there
     * is at least one provider in the list.
     */
    private ProviderInfo selectRandomProvider (LinkedList<ProviderInfo> providerInfos) {
	int choice = Random.nextInt(providerInfos.size());
	return providerInfos.get(choice);
    }
}

