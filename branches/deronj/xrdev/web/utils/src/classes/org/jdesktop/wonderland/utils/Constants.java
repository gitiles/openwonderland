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
package org.jdesktop.wonderland.utils;

/**
 *
 * @author jkaplan
 */
public class Constants {
    /** the directory to run from */
    public static final String RUN_DIR_PROP = "wonderland.run.dir";

    /** whether or not to cleanup the run directory */
    public static final String RUN_DIR_CLEANUP_PROP = "wonderland.run.dir.cleanup";

    /** the port to run the webserver on */
    public static final String WEBSERVER_PORT_PROP = "wonderland.webserver.port";

    /** the webserver public hostname */
    public static final String WEBSERVER_HOST_PROP = "wonderland.webserver.host";

    /** the webserver private hostname */
    public static final String WEBSERVER_HOST_PRIVATE_PROP = "wonderland.webserver.host.private";

    /** the full URL of the web server */
    public static final String WEBSERVER_URL_PROP  = "wonderland.web.server.url";

    /** whether or not we should overwrite data */
    public static final String WEBSERVER_NEWVERSION_PROP = "wonderland.webserver.newversion";
}
