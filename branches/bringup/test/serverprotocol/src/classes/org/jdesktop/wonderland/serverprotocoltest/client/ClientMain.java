/**
 * Project Wonderland
 *
 * $RCSfile:$
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision:$
 * $Date:$
 * $State:$
 */

package org.jdesktop.wonderland.serverprotocoltest.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandClient;
import org.jdesktop.wonderland.client.comms.WonderlandClientListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.ProtocolSelectionMessage;
import org.jdesktop.wonderland.serverprotocoltest.common.TestProtocolVersion;

/**
 * Simple client
 * @author jkaplan
 */
public class ClientMain {
    /** logger */
    private static final Logger logger =
            Logger.getLogger(ClientMain.class.getName());
    
    // the server info
    private WonderlandServerInfo serverInfo;
    
    // whether we are done
    boolean finished = false;
    
    /**
     * Create a new client
     * @param serverInfo the information about the server
     */
    public ClientMain(WonderlandServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
    
    /**
     * Run the test
     */
    public void runTest() throws Exception {
        // read the username and properties from files
        String username = System.getProperty("sgs.user", "sample");
        String password = System.getProperty("sgs.password", "sample");
        
        // create the client & login
        WonderlandClient wc = new WonderlandClient(serverInfo) {
            @Override
            protected WonderlandClientListener createListener(LoginParameters params) {
                return new ErrorReportingClientListener(this, params);
            }
        };
        
        wc.login(new LoginParameters(username, password.toCharArray()));
        
        logger.info("Login suceeded");
        
        // request the test protocol
        wc.sendMessage(new ProtocolSelectionMessage(TestProtocolVersion.PROTOCOL_NAME,
                                                    TestProtocolVersion.VERSION));
        
        // wait for a while
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            // ignore
        }
    }
    
    class ErrorReportingClientListener extends WonderlandClientListener {
        public ErrorReportingClientListener(WonderlandClient client,
                                            LoginParameters params)
        {
            super (client, params);
        }

        @Override
        public void receivedMessage(byte[] data) {
            // message from the server
            Message message = Message.extract(data);
            if (message instanceof ErrorMessage) {
                ErrorMessage em = (ErrorMessage) message;
                logger.log(Level.WARNING, em.getErrorMessage(), em.getErrorCause());
            }
        }
    }
    
    public static void main(String[] args) {
        // read server and port from properties
        String server = System.getProperty("sgs.server", "locahost");
        int port = Integer.parseInt(System.getProperty("sgs.port", "1139"));
        
        // create a login information object
        WonderlandServerInfo serverInfo = new WonderlandServerInfo(server, port);
        
        // the main client
        ClientMain cm = new ClientMain(serverInfo);
        
        try {
            cm.runTest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
