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

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * For cells that are expected to move frequently
 * 
 * @author paulby
 */
public class MoveableCellMO extends CellMO {

    private ArrayList<ManagedReference<CellMoveListener>> listeners = null;
    private CopyOnWriteArrayList<ManagedReference> caches = new CopyOnWriteArrayList<ManagedReference>();
    
    @Override
    public void setTransform(CellTransform transform) {
        super.setTransform(transform);
        
        // Notify listeners
        if (listeners!=null) {
            for(ManagedReference<CellMoveListener> listenerRef : listeners)
                listenerRef.getForUpdate().cellMoved(this, transform);
        }
    }
    
    
    /**
     * Add a CellMoveListener. This listener is notified when the setTransform 
     * method is called. super.setTransform is called first, so the cell transform
     * will have been updated before the listener is called.
     * 
     * @param listener
     */
//    public void addCellMoveListener(CellMoveListener listener) {
//        if (listeners==null)
//            listeners = new ArrayList<ManagedReference>();
//        
//        listeners.add(AppContext.getDataManager().createReference(listener));
//    }
    
    /**
     * Remove the CellMoveListener
     * @param listener
     */
    public void removeCellMoveListener(CellMoveListener listener) {
        if (listeners!=null)
            listeners.remove(AppContext.getDataManager().createReference(listener));
    }
       
    public interface CellMoveListener extends ManagedObject {
        public void cellMoved(MoveableCellMO cell, CellTransform transform);
    }

}
