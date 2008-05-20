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
package org.jdesktop.wonderland.client.cell;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.ClientConnection.Status;
import org.jdesktop.wonderland.client.comms.ResponseListener;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 *
 * @author paulby
 */
public class ChannelComponent extends CellComponent {
    
    private HashMap<Class, ComponentMessageReceiver> messageReceivers = new HashMap();
    
    private CellChannelConnection connection;
    
    public ChannelComponent(Cell cell) {
        super(cell);
    }


    /**
     * Notification of the CellChannelConnection to use when sending
     * data to the server for this cell.  This method will be called 
     * automatically at cell creation time.
     */
    public void setCellChannelConnection(CellChannelConnection connection) {
        this.connection = connection;
    }
    
    /**
     * Register a receiver for a specific message class. Only a single receiver
     * is allowed for each message class, calling this method to add a duplicate
     * receiver will cause an IllegalStateException to be thrown.
     * 
     * @param msgClass
     * @param receiver
     */
    public void addMessageReceiver(Class<? extends CellMessage> msgClass, ComponentMessageReceiver receiver) {
        System.out.println("**** registering receiver "+msgClass.getName());
        Object old = messageReceivers.put(msgClass, receiver);
        if (old!=null)
            throw new IllegalStateException("Duplicate Message class added "+msgClass);
    }
    
    /**
     * Dispatch messages to any receivers registered for the particular message class
     * @param sender
     * @param session
     * @param message
     */
    public void messageReceived(CellMessage message ) {
        
        ComponentMessageReceiver recvRef = messageReceivers.get(message.getClass());
        if (recvRef==null) {
            Logger.getAnonymousLogger().warning("No listener for message "+message.getClass() +"  "+cell.getClass().getName());
             
            return;
        }
        
        recvRef.messageReceived(message);
    }
    
    public Status getStatus() {
        return connection.getStatus();
    }
    
    public void send(CellMessage message, ResponseListener listener) {
        connection.send(message, listener);
    }
    
    public void send(CellMessage message) {
        connection.send(message);
    }
    
    // TODO various send methods required, cell to server, cell to cell, cell to channel
    // Not sure these need to be defined in this interface, implementors should have
    // the choice of which send messages to implement and expose (if any) in a cell.
//    public void send(CellMessage message);
    
    static public interface ComponentMessageReceiver {
        public void messageReceived(CellMessage message );        
    }
}
