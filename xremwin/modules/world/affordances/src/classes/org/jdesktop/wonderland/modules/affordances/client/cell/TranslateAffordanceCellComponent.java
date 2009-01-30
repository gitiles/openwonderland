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
package org.jdesktop.wonderland.modules.affordances.client.cell;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.affordances.client.jme.TranslateAffordance;

/**
 * A client-side cell component for translate affordances
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TranslateAffordanceCellComponent extends AffordanceCellComponent {

    private TranslateAffordance translateAffordance = null;

    public TranslateAffordanceCellComponent(Cell cell) {
        super(cell);
    }

    @Override
    public void setStatus(CellStatus status) {
        // If we are making this component active, then create the affordance,
        // if the first time through.
        super.setStatus(status);
        if (status == CellStatus.ACTIVE) {
            if (translateAffordance == null) {
                translateAffordance = TranslateAffordance.addToCell(cell);
            }
        }
    }

    @Override
    public void setSize(float size) {
        super.setSize(size);
        translateAffordance.setSize(size);
    }

    @Override
    public void remove() {
        super.remove();
        translateAffordance.remove();
        cell.removeComponent(TranslateAffordanceCellComponent.class);
        translateAffordance = null;
    }
}
