/*
 *  Project Wonderland
 * 
 *  $Id$
 * 
 *  Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 * 
 *  Redistributions in source code form must reproduce the above
 *  copyright and this condition.
 * 
 *  The contents of this file are subject to the GNU General Public
 *  License, Version 2 (the "License"); you may not use this file
 *  except in compliance with the License. A copy of the License is
 *  available at http://www.opensource.org/licenses/gpl-license.php.
 * 
 *  $Revision$
 *  $Date$
 */

package org.jdesktop.wonderland.client.avatar;

import org.jdesktop.wonderland.client.cell.MovableCell;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * ViewCell defines the view into the virtual world for a specific window
 * on a client. A client may have many ViewCells instanstantiated, however
 * there is a 1-1 correlation between the ViewCell and a rendering of the
 * virtual world.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ViewCell extends MovableCell {

    public ViewCell(CellID cellID) {
        super(cellID);
    }
}
