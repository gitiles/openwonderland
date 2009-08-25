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
package org.jdesktop.wonderland.modules.sas.provider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.ProgrammaticLogin;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The SAS provider client.
 *
 * @author deronj
 */
@ExperimentalAPI
public class SasProvider {
    private static final Logger logger =
            Logger.getLogger(SasProvider.class.getName());

    /** The execution site dependent listener for messages from the SAS server to provider. */
    private SasProviderConnectionListener listener;

    /** The username to log in with */
    private String userName;

    /** The optional password file */
    private File passwordFile;

    /** The login object to connect with */
    private SasLogin login;

    /** the property to set to "false" in order to not reconnect */
    private static final String RECONNECT_PROP = "sas.reconnect";
    /** the default value of the reconnect property */
    private static final String RECONNECT_DEFAULT = "true";
    /** whether or not to reconnect */
    private boolean reconnect;

    /**
     * Create a new instance of SasProvider, given login information.
     */
    public SasProvider (String userName, File passwordFile, String serverUrl,
                        SasProviderConnectionListener listener) {

        this.userName = userName;
        this.passwordFile = passwordFile;
        this.listener = listener;

        // determine whether or not to reconnect automatically
        reconnect = Boolean.parseBoolean(System.getProperty(RECONNECT_PROP,
                                                            RECONNECT_DEFAULT));

        // create a new programmatic login object
        this.login = new SasLogin(serverUrl);

        // perform the login
        doLogin();
    }
    
    protected void doLogin() {
        // Log in.  This will wait until the server is available, and then
        // connect when the server is available.
        SasProviderSession curSession = login.login(userName, passwordFile);
        
        // make sure we logged in successfully
        if (curSession == null) {
            throw new RuntimeException("Unable to create session.");
        }

        // add a listener that will attempt to log in again when the server
        // disconnects
        curSession.addSessionStatusListener(new SessionStatusListener() {
            public void sessionStatusChanged(WonderlandSession session,
                                             WonderlandSession.Status status)
            {
                if (status == WonderlandSession.Status.DISCONNECTED) {
                    logger.warning("Server disconnected.");

                    // reconnect in a new thread
                    if (reconnect) {
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                doLogin();
                            }
                        },  "SAS Reconnect");
                        t.start();
                    }
                }
            }
        });
    }

    /**
     *  Provides the login information from the constructor to the login manager.
     */
    private class SasLogin extends ProgrammaticLogin<SasProviderSession> {
        public SasLogin(String serverURL) {
            super (serverURL);
        }

        @Override
        protected SasProviderSession createSession(ServerSessionManager sessionManager,
                                                   WonderlandServerInfo server,
                                                   ClassLoader loader)
        {
            return new SasProviderSession(sessionManager, server, loader, listener);
        }
    }
}
