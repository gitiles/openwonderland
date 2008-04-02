/**
 * Project Wonderland
 *
 * $RCSfile: WonderlandConfigBase.java,v $
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
 * $Revision: 1.9 $
 * $Date: 2007/08/07 01:10:36 $
 * $State: Exp $
 */
package org.jdesktop.wonderland.common.config;

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Represents Wonderland configuration options common to the entire system (server/client)
 */
@ExperimentalAPI
public abstract class WonderlandConfigBase implements WonderlandConfigInterface {

    protected static Logger logger = Logger.getLogger("wonderland.config");

    private static boolean doInit =
	Boolean.parseBoolean(System.getProperty("wonderland.config.init", "true"));

    public WonderlandConfigBase() {
	this(doInit);
    }

    public WonderlandConfigBase(boolean init) {
	if (init) init();
    }

    public String getName() {
	return WonderlandConfigUtil.getConfigBaseName(getClass());
    }

    public String getDescription() {
	return getClass().getName();
    }

    public abstract void init();

    public boolean writeSystemConfig() {
	return WonderlandConfigUtil.writeSystemConfig(this);
    }

    public boolean writeUserConfig() {
	return WonderlandConfigUtil.writeUserConfig(this);
    }

    public String toString() {
	return getName();
    }
}
