/**
 * Project Wonderland
 *
 * $RCSfile:$
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision:$
 * $Date:$
 * $State:$
 */
package org.jdesktop.wonderland.modules.phone.server;

import java.util.logging.Logger;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.CommsManager;

/**
 * Pluging to support the phone
 * @author jprovino
 */
public class PhoneServerPlugin implements ServerPlugin {
    private static final Logger logger =
            Logger.getLogger(PhoneServerPlugin.class.getName());
    
    public void initialize() {
	logger.fine("phone initialized");
    }
    
}
