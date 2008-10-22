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
package org.jdesktop.wonderland.servermanager.client.servlet;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.servermanager.client.PingDataCollector;

/**
 * Manage the installation and removal of the PingDataListener
 * @author jkaplan
 */
public class DataCollectionManager implements ServletContextListener 
{
    private static final Logger logger =
            Logger.getLogger(DataCollectionManager.class.getName());
    
    private PingDataCollector pdc;
    
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        pdc = new PingDataCollector();
        context.setAttribute(PingDataCollector.KEY, pdc);
    
        logger.warning("Created " + pdc);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (pdc != null) {
            logger.warning("Shutdown " + pdc);
            pdc.shutdown();
        } else {
            logger.warning("Data collector not found");
        }
        
        pdc = null;
    }
}
