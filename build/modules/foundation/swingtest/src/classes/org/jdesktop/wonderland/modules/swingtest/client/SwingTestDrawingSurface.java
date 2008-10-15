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
package org.jdesktop.wonderland.modules.swingtest.client;

import java.awt.Graphics2D;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.appbase.client.DrawingSurface;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * @author deronj
 */

@ExperimentalAPI
public class SwingTestDrawingSurface extends DrawingSurface {

    private static final Logger logger = Logger.getLogger(SwingTestDrawingSurface.class.getName());
    
    public SwingTestDrawingSurface (int width, int height) {
	this();
	setSize(width, height);
    }

    public SwingTestDrawingSurface() {
        super();
    }
    
    protected void initSurface (Graphics2D g) {}
}
