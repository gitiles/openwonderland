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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;


/**
 * A cell for deployed models
 * @author paulby
 */
@ExperimentalAPI
public class ModelCellMO extends CellMO {
    	
    /** Default constructor, used when cell is created via WFS */
    public ModelCellMO() {
    }
    
    public ModelCellMO(BoundingVolume bounds, CellTransform transform) {
        super(bounds, transform);
    }
    
    public void addComponent(CellComponentMO component, Class componentClass) {
        super.addComponent(component, componentClass);
        System.err.println("ADDING COMP "+componentClass);
        Thread.dumpStack();
    }
    @Override protected String getClientCellClassName(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.client.cell.ModelCell";
    }

    @Override
    public CellServerState getServerState(CellServerState state) {
        return super.getServerState(new ModelCellServerState());
    }
}
