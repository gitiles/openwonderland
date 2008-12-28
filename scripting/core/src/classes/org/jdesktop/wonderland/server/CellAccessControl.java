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
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.server;

import org.jdesktop.wonderland.server.cell.CellDescription;

/**
 *  Provides access control support for cells and users
 * 
 *  Current implementation is very simple, but the interface is sufficient 
 *  for a more complex access check.
 * 
 * @author paulby
 */
public class CellAccessControl {
    
    private CellAccessControl() {
    }
    
    /**
     * Returns true if this user can view this cell.
     */
    public static boolean canView(UserSecurityContextMO user, CellDescription cellMirror) {
        return true;
    }
    
    
}
