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
package org.jdesktop.wonderland.modules.affordances.server.cell;

import org.jdesktop.wonderland.server.cell.*;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.cell.setup.BasicCellSetup;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.config.CellConfig;
import org.jdesktop.wonderland.common.cell.config.jme.MaterialJME;
import org.jdesktop.wonderland.modules.affordances.common.cell.config.AffordanceTestCellConfig;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;


/**
 * Cell that renders a basic shape
 * 
 * @author paulby
 */
@ExperimentalAPI
public class AffordanceTestCellMO extends CellMO {
    
    private AffordanceTestCellConfig.Shape shape;
    private MaterialJME materialJME = null;
    
    /** Default constructor, used when cell is created via WFS */
    public AffordanceTestCellMO() {
        this(new Vector3f(), 1);
    }

    public AffordanceTestCellMO(Vector3f center, float size) {
        this(center, size, AffordanceTestCellConfig.Shape.BOX);
    }

    public AffordanceTestCellMO(Vector3f center, float size, AffordanceTestCellConfig.Shape shape) {
        this(center, size, shape, null);
    }

    public AffordanceTestCellMO(Vector3f center, float size, AffordanceTestCellConfig.Shape shape, MaterialJME materialJME) {
        super(new BoundingBox(new Vector3f(), size, size, size), new CellTransform(null, center));
        this.shape = shape;
        this.materialJME = materialJME;

        addComponent(new ChannelComponentMO(this));
        addComponent(new MovableComponentMO(this));
    }
    
    @Override
    protected String getClientCellClassName(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.affordances.client.cell.AffordanceTestCell";
    }

    @Override
    public CellConfig getCellConfig(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return new AffordanceTestCellConfig(shape, materialJME);
    }

    @Override
    public void setupCell(BasicCellSetup setup) {
        super.setupCell(setup);
    }

    @Override
    public void reconfigureCell(BasicCellSetup setup) {
        super.reconfigureCell(setup);
        setupCell(setup);
    }
}
