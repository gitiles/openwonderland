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
package org.jdesktop.wonderland.testharness.master;

import java.util.ArrayList;
import org.jdesktop.wonderland.testharness.common.LoginRequest;

/**
 *
 * @author paulby
 */
public class SimpleTestDirector implements TestDirector {

    private ArrayList<SlaveConnection> slaves = new ArrayList();
    
    
    public boolean slaveJoined(SlaveConnection slaveController) {
        slaves.add(slaveController);
        
        String serverName = MasterMain.getMaster().getSgsServerName();
        int serverPort = MasterMain.getMaster().getSgsPort();
        
        slaveController.send(new LoginRequest(serverName, 
                                              serverPort, 
                                              UsernameManager.getUniqueUsername(), 
                                              new char[] {}, 
                                              0f, 0f, 0f));
        
        return true; // We used the slave so return true
    }

}
