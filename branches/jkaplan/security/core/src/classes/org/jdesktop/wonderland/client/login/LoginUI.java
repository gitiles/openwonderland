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
package org.jdesktop.wonderland.client.login;

import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.LoginManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.LoginManager.WebURLLoginControl;

/**
 * An interface that the login system will call back to to request
 * login details from the user.
 * @author jkaplan
 */
public interface LoginUI {
    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to no authorization, so prompts for
     * username and full name, but no password.  Login is granted if the
     * username is unique.
     * @param control the login control
     */
    public void requestLogin(NoAuthLoginControl control);

    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to web service authorization, so prompts for
     * username and password.  Login is granted if the username and password
     * are validated by the web service.
     * @param control the login control
     */
    public void requestLogin(UserPasswordLoginControl control);

    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to web authorization.  The control object
     * gives the external URL that should be contacted with authentication
     * information.  The user should be notified of the login in progress
     * and given the option to cancel it, but any data collection happens
     * in a separate web browser.
     * @param control the login control
     */
    public void requestLogin(WebURLLoginControl control);

    /**
     * Create a new WonderlandSession for the given server and classloader.
     * This gives the user-interface a hook to listen for session-related
     * events.
     * @param serverInfo the information about the server to connect to
     * @param loader the classloader with all modules loaded
     * @return the newly created Wonderland session
     */
    public WonderlandSession createSession(WonderlandServerInfo serverInfo,
                                           ClassLoader loader);
}
