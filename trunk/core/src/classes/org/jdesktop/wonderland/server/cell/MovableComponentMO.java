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
package org.jdesktop.wonderland.server.cell;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.TaskManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.messages.MovableMessage;
import org.jdesktop.wonderland.server.TimeManager;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.CellMO.SpaceInfo;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 *
 * @author paulby
 */
public class MovableComponentMO extends CellComponentMO {

    private ArrayList<ManagedReference<CellTransformChangeListener>> listeners = null;
    private ManagedReference<ChannelComponentMO> channelComponentRef = null;
    private ManagedReference<SpaceMO> currentSpaceRef = null;
    private long transformTimestamp;
    private CellTransform cellTransformTmp = new CellTransform(new Quaternion(), new Vector3f());
    private Vector3f v3fTmp = new Vector3f();
    
    /**
     * Create a MovableComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public MovableComponentMO(CellMO cell) {
        super(cell);
        
        cell.setMovable(true);
        ChannelComponentMO channelComponent = (ChannelComponentMO) cell.getComponent(ChannelComponentMO.class);
        if (channelComponent==null)
            throw new IllegalStateException("Cell does not have a ChannelComponent");
        channelComponentRef = AppContext.getDataManager().createReference(channelComponent); 
                
        channelComponent.addMessageReceiver(MovableMessage.class, new ComponentMessageReceiverImpl(this));
    }
    
    @Override
    public void setLive(boolean live) {
        if (live) {
            CellMO cell = cellRef.get();
            SpaceMO[] currentSpace = WonderlandContext.getCellManager().getEnclosingSpace(
                                        cell.getWorldTransform(null).getTranslation(null));
            currentSpaceRef = AppContext.getDataManager().createReference(currentSpace[0]);  
        } else {
            currentSpaceRef = null;
        }
    }
    
    /**
     * Set the transform for the cell and notify all client cells of the move.
     * @param transform
     */
    public void moveRequest(CellTransform transform) {
        CellMO cell = cellRef.getForUpdate();
        ChannelComponentMO channelComponent;
        cell.setLocalTransform(transform);
        
        channelComponent = channelComponentRef.getForUpdate();
        
        // TODO only handles a single space at the moment
        CellTransform cellWorld = cell.getWorldTransform(cellTransformTmp);
        SpaceMO[] spaceSet = WonderlandContext.getCellManager().getEnclosingSpace(cellWorld.getTranslation(v3fTmp));
        SpaceMO newSpace = spaceSet[0];
        ManagedReference<SpaceMO> newSpaceRef = AppContext.getDataManager().createReference(newSpace);
        if (newSpaceRef!=currentSpaceRef) {
            cell.removeFromSpace(currentSpaceRef.getForUpdate());
            cell.addToSpace(newSpace);
            currentSpaceRef = newSpaceRef;
        }
        transformTimestamp = TimeManager.getWonderlandTime();
        
        if (cell.isLive()) {
            channelComponent.sendAll(MovableMessage.newMovedMessage(cell.getCellID(), transform));
        }
    }
    
    /**
     * Check if the object needs to be added as 'in' other spaces
     */
    private void checkForNewSpace(CellMO cell, Collection<SpaceInfo> currentSpaces) {
        Vector3f origin = cell.getWorldTransform(cellTransformTmp).getTranslation(v3fTmp);
        
        for(SpaceInfo spaceInfo : currentSpaces) {
            Collection<ManagedReference<SpaceMO>> proximity = spaceInfo.getSpaceRef().get().getSpaces(cell.getWorldBounds());
            for(ManagedReference<SpaceMO> spaceCellRef : proximity) {
                if (spaceCellRef.get().getWorldBounds(null).contains(origin)) {
                    cell.addToSpace(spaceCellRef.getForUpdate());
                }
            }
        }
    }
       
    /**
     * Listener inteface for cell movement
     */
    public interface CellTransformChangeListener extends ManagedObject {
        public void transformChanged(CellMO cell, CellTransform transform);
    }

    private static class ComponentMessageReceiverImpl implements ComponentMessageReceiver {

        private ManagedReference<MovableComponentMO> compRef;
        
        public ComponentMessageReceiverImpl(MovableComponentMO comp) {
            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender, ClientSession session, CellMessage message) {
            MovableMessage ent = (MovableMessage) message;
//            System.out.println("MovableComponentMO.messageReceived "+ent.getActionType());
            switch (ent.getActionType()) {
                case MOVE_REQUEST:
                    // TODO check permisions
                    
                    compRef.getForUpdate().moveRequest(new CellTransform(ent.getRotation(), ent.getTranslation()));

                    // Only need to send a response if the move can not be completed as requested
                    //sender.send(session, MovableMessageResponse.newMoveModifiedMessage(ent.getMessageID(), ent.getTranslation(), ent.getRotation()));
                    break;
                case MOVED:
                    Logger.getAnonymousLogger().severe("Server should never receive MOVED messages");
                    break;
            }
        }
    }
}
