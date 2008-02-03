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
package org.jdesktop.wonderland.serverlistenertest.server;

import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.ClientType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.ClientHandler;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientChannel;
import org.jdesktop.wonderland.server.comms.WonderlandClientSession;
import org.jdesktop.wonderland.serverlistenertest.common.TestClientType;
import org.jdesktop.wonderland.serverlistenertest.common.TestMessageOne;
import org.jdesktop.wonderland.serverlistenertest.common.TestMessageTwo;

/**
 * Sample plugin that doesn't do anything
 * @author jkaplan
 */
public class TestListenerPlugin implements ServerPlugin {
    private static final Logger logger =
            Logger.getLogger(TestListenerPlugin.class.getName());
    
    public void initialize() {
        CommsManager cm = WonderlandContext.getCommsManager();
       
        cm.registerClientHandler(new TestClientOneHandler());
        cm.registerClientHandler(new TestClientTwoHandler());
        cm.registerClientHandler(new TestClientThreeHandler());
    }
    
    abstract static class TestClientHandler implements ClientHandler {
        public abstract String getName();
        
        public void registered(WonderlandClientChannel channel) {
            logger.info(getName() + " registered: " + channel.getName());
        }
        
        public void clientAttached(WonderlandClientSession session) {
            logger.info(getName() + " client attached: " + session);
        }
        
        public void messageReceived(WonderlandClientSession session, 
                                    Message message)
        {
            logger.info(getName() + " received message " + message +
                        " from session " + session.getName());
        }
        
        public void clientDetached(WonderlandClientSession session) {
            logger.info(getName() + " client detached: " + session);
        }
    }
    
    static class TestClientOneHandler extends TestClientHandler
            implements Serializable 
    {
        public String getName() { return "TestClientOneHandler"; }
        
        public ClientType getClientType() { 
            return TestClientType.CLIENT_ONE_TYPE;
        }
        
        @Override
        public void messageReceived(WonderlandClientSession session, 
                                    Message message)
        {
            assert message instanceof TestMessageOne :
                   logger.getName() + " received bad message: " + message;
            
            super.messageReceived(session, message);
        }
    }
    
    static class TestClientTwoHandler extends TestClientHandler
            implements ManagedObject, Serializable 
    {
        private int count;
        
        public String getName() { return "TestClientTwoHandler"; }
        
        public ClientType getClientType() { 
            return TestClientType.CLIENT_TWO_TYPE;
        }
        
        @Override
        public void messageReceived(WonderlandClientSession session, 
                                    Message message)
        {
            assert message instanceof TestMessageTwo :
                   getName() + " received bad message: " + message;
            
            logger.info(getName() + " received message " + message +
                        " from session " + session.getName() + 
                        " count " + count++);
        }
    }
    
    static class TestClientThreeHandler extends TestClientHandler
            implements Serializable 
    {
        public String getName() { return "TestClientThreeHandler"; }
        
        public ClientType getClientType() { 
            return TestClientType.CLIENT_THREE_TYPE;
        }
        
        @Override
        public void messageReceived(WonderlandClientSession session, 
                                    Message message)
        {
            assert !(message instanceof TestMessageOne) : 
                   getName() + " received bad message: " + message;
            
            super.messageReceived(session, message);
        }
    }
}
