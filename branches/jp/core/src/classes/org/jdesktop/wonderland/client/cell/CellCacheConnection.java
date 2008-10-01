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

import com.jme.bounding.BoundingVolume;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.view.ClientView;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.cell.CellCacheConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.ViewCreateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellHierarchyMessage;
import org.jdesktop.wonderland.common.cell.config.CellConfig;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageList;

/**
 * Handler for Cell cache information
 * @author jkaplan
 */
@ExperimentalAPI
public class CellCacheConnection extends BaseConnection {
    private static final Logger logger = Logger.getLogger(CellCacheConnection.class.getName());
    
    private ArrayList<CellCacheMessageListener> listeners = new ArrayList();
    
    private ClientView clientView;
    private CellID viewCellID = null;
    
    public CellCacheConnection(ClientView clientView) {
        this.clientView = clientView;
    }
    
    /**
     * Get the type of client
     * @return CellClientType.CELL_CLIENT_TYPE
     */
    public ConnectionType getConnectionType() {
        return CellCacheConnectionType.CLIENT_TYPE;
    }

    /**
     * Add a listener for cell cache actions. This should be called during setup
     * not once the system is running
     * @param listener
     */
    public void addListener(CellCacheMessageListener listener) {
        listeners.add(listener);
    }
    
    private ViewCreateResponseMessage registerView(String viewID) {
        try {
            ViewCreateResponseMessage response = 
                    (ViewCreateResponseMessage)sendAndWait(
                            CellHierarchyMessage.newSetAvatarMessage(viewID));

            return response;
        } catch (InterruptedException ex) {
            Logger.getLogger(CellCacheConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Handle a message from the server
     * @param message the message to handle
     */
    public void handleMessage(Message message) {
        if (message instanceof MessageList) {
            List<Message> list = ((MessageList)message).getMessages();
            for(Message m : list)
                handleMessage(m);
            return;
        }
        
        if (!(message instanceof CellHierarchyMessage))
            throw new RuntimeException("Unexpected message type "+message.getClass().getName());
        
        CellHierarchyMessage msg = (CellHierarchyMessage)message;
        switch(msg.getActionType()) {
            case LOAD_CELL :
                for(CellCacheMessageListener l : listeners) {
                    l.loadCell(msg.getCellID(),
                                msg.getCellClassName(),
                                msg.getLocalBounds(),
                                msg.getParentID(),
                                msg.getCellTransform(),
                                msg.getSetupData(),
                                msg.getCellName()
                                );
                }
                if (viewCellID!=null && viewCellID.equals(msg.getCellID())) {
                    clientView.viewCellConfigured(viewCellID);
                    // We only need notification once
                    viewCellID = null;
                }
                break;
//            case MOVE_CELL :  // TODO remove - no longer used
//                for(CellCacheMessageListener l : listeners) {
//                    l.moveCell(msg.getCellID(),
//                            msg.getCellTransform());
//                }
//                 break;
            case UNLOAD_CELL :
                for(CellCacheMessageListener l : listeners) {
                    l.unloadCell(msg.getCellID());
                }
                break;
            case DELETE_CELL :
                for(CellCacheMessageListener l : listeners) {
                    l.deleteCell(msg.getCellID());
                }
                break;
            default :
                logger.warning("Message type not implemented "+msg.getActionType());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(WonderlandSession session) throws ConnectionFailureException {
        super.connect(session);
        ViewCreateResponseMessage msg = registerView(clientView.getViewID());
        clientView.serverInitialized(msg);
        viewCellID = msg.getViewCellID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
        // remove any action listeners
        listeners.clear();
    }
    
    /**
     * Listener interface for cell cache action messages
     */
    @ExperimentalAPI
    public static interface CellCacheMessageListener {
        /**
         * Load the cell and prepare it for use
         * @param cellID
         * @param className
         * @param computedWorldBounds
         * @param parentCellID
         * @param cellTransform
         * @param setup
         */
        public Cell loadCell(CellID cellID, 
                               String className, 
                               BoundingVolume localBounds,
                               CellID parentCellID,
                               CellTransform cellTransform,
                               CellConfig setup,
                               String cellName);
        /**
         * Unload the cell. This removes the cell from memory but will leave
         * cell data cached on the client
         * @param cellID
         */
        public void unloadCell(CellID cellID);
        
        /**
         * Delete the cell and all its content from the client
         * @param cellID
         */
        public void deleteCell(CellID cellID);
        
        public void moveCell(CellID cellID,
                             CellTransform cellTransform);
        
    }
    
}
