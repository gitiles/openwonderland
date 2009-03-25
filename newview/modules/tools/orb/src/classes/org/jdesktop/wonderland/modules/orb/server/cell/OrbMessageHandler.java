/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.orb.server.cell;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;

import org.jdesktop.wonderland.server.UserManager;

import com.jme.math.Vector3f;

import org.jdesktop.wonderland.modules.orb.common.messages.OrbEndCallMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbMuteCallMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbSetVolumeMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbSpeakingMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbStartCallMessage;
import org.jdesktop.wonderland.server.cell.ChannelComponentImplMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * A server cell that provides Orb functionality
 * @author jprovino
 */
public class OrbMessageHandler extends AbstractComponentMessageReceiver
	implements Serializable {

    private static final Logger logger =
        Logger.getLogger(OrbCellMO.class.getName());
     
    private CellID cellID;

    private String username;

    private String callID;

    private boolean simulateCalls;

    private ManagedReference<OrbStatusListener> orbStatusListenerRef;

    public OrbMessageHandler(OrbCellMO orbCellMO, String username, String callID, 
	    boolean simulateCalls) {

	super(orbCellMO);

	cellID = orbCellMO.getCellID();

	this.username = username;
	this.callID = callID;
	this.simulateCalls = simulateCalls;

	logger.info("Username is " + username + " Call id is " + callID 
	    + " simulateCalls " + simulateCalls);

        OrbStatusListener orbStatusListener = new OrbStatusListener(orbCellMO, callID);

        orbStatusListenerRef =  AppContext.getDataManager().createReference(orbStatusListener);

	WonderlandClientSender sender =  
	    WonderlandContext.getCommsManager().getSender(CellChannelConnectionType.CLIENT_TYPE);

        ChannelComponentMO channelComponentMO = getChannelComponent();

        channelComponentMO.addMessageReceiver(OrbStartCallMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbEndCallMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbMuteCallMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbSetVolumeMessage.class, this);
	channelComponentMO.removeMessageReceiver(OrbSpeakingMessage.class);
    }

    public void done() {
	ChannelComponentMO channelComponentMO = getChannelComponent();

	channelComponentMO.removeMessageReceiver(OrbStartCallMessage.class);
	channelComponentMO.removeMessageReceiver(OrbEndCallMessage.class);
	channelComponentMO.removeMessageReceiver(OrbMuteCallMessage.class);
	channelComponentMO.removeMessageReceiver(OrbSetVolumeMessage.class);
	channelComponentMO.removeMessageReceiver(OrbSpeakingMessage.class);

	orbStatusListenerRef.get().endCall(callID);
    }

    public void messageReceived(WonderlandClientSender sender, 
	    WonderlandClientID clientID, CellMessage message) {

	logger.finest("got message " + message);

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Call call = null;

	Player player = null;

	if (simulateCalls == false) {
	    call = vm.getCall(callID);

	    if (call == null) {
	        logger.warning("Can't find call for " + callID);
	        return;
	    }

	    player = vm.getPlayer(callID);
	}

	if (message instanceof OrbStartCallMessage) {
	    sender.send(clientID, new OrbStartCallMessage(cellID, username, callID));
	    return;
	}

	if (message instanceof OrbEndCallMessage) {
	    if (call != null) {
	        try {
	            vm.endCall(call, true);
	        } catch (IOException e) {
		    logger.warning("Unable to end call " + call + ": " 
		        + e.getMessage());
	        }
 	    } else {
		orbStatusListenerRef.get().endCall(callID);
	    }
	    return;
	}

	if (message instanceof OrbMuteCallMessage) {
	    try {
	        call.mute(((OrbMuteCallMessage)message).isMuted());
	    } catch (IOException e) {
		logger.warning("Unable to mute call " + call + ": " 
		    + e.getMessage());
	    }
	    return;
	}

	if (message instanceof OrbSetVolumeMessage) {
	    if (player == null) {
		logger.warning("no player for " + callID);
		return;
	    }

	    OrbSetVolumeMessage msg = (OrbSetVolumeMessage) message;

	    String softphoneCallID = msg.getSoftphoneCallID();

	    Player softphonePlayer = vm.getPlayer(softphoneCallID);

	    if (softphonePlayer == null) {
		logger.warning("Can't find Player for softphone " + softphoneCallID);
		return;
	    }

	    DefaultSpatializer spatializer = (DefaultSpatializer)
		vm.getVoiceManagerParameters().livePlayerSpatializer.clone();


	    double volume;

	    if (msg.getVolume() <= 5.0) {
		volume = msg.getVolume() / 5.0;
	    } else {
		volume = (msg.getVolume() - 5) * .8;
	    } 

	    spatializer.setAttenuator(volume);

	    softphonePlayer.setPrivateSpatializer(player, spatializer);

	    return;
 	}
	
	if (message instanceof OrbSpeakingMessage) {
	    boolean isSpeaking = ((OrbSpeakingMessage) message).isSpeaking();

	    logger.info("Call " + callID 
		+ (isSpeaking ? "Started Speaking" : "Stopped Speaking"));
	}
    }

}
