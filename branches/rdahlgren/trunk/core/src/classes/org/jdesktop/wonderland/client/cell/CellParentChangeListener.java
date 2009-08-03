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
package org.jdesktop.wonderland.client.cell;

import java.io.Serializable;

/**
 * Listener for tracking changes to the parent of a Cell
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface CellParentChangeListener extends Serializable {
    /**
     * Called when this cell's parent has changed
     * @param cell the cell whose component list has changed
     * @param parent the new parent cell (may be null)
     */
    public void parentChanged(Cell cell, Cell parent);
}
