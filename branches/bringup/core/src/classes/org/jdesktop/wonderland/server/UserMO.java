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
package org.jdesktop.wonderland.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.ExperimentalAPI;
import org.jdesktop.wonderland.server.cell.AvatarMO;

/**
 * This class represensents a real world user. A user can be logged into
 * the system from multiple concurrent clients with different protocols
 * 
 * For example a user may be logged in from a 3D client and a cell phone
 * 
 * @author paulby
 */
@ExperimentalAPI
public class UserMO implements ManagedObject, Serializable {

    private String username;
    private String fullname;
    private ArrayList<String> groups = null;
    
    private Set<ClientSession> activeSessions = null;
    private Map<String, Serializable> extendedData = null;
    private Map<String, ManagedReference> avatars = new HashMap();
    
    protected static Logger logger = Logger.getLogger(UserMO.class.getName());

    /**
     * Create a new User managed object with a unique username
     * 
     * @param username
     */
    UserMO(String username) {
        this.username = username;
    }
    
    /**
     * Get unique username
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get full name of user (may not be unique)
     * @return
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Set full name of user (may not be unique)
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    
    /**
     * Put a named object in the extended data Map for this User
     * 
     * This name/object pairing allows developers to add data to the user class
     * without needing to modify the userMO class.
     * 
     * @param name
     * @param object
     */
    public void putExtendedData(String name, Serializable object) {
        if (extendedData==null) {
            extendedData = new HashMap();
        }
        extendedData.put(name, object);
    }
    
    /**
     * Return the object associated with the name.
     * @param name
     * @return Object, or null if their is no object associated with the given name
     */
    public Object getExtendedData(String name) {
        if (extendedData==null)
            return null;
        return extendedData.get(name);
    }
    
    /**
     * Return the specified avatar for this User, or null if that avatar
     * does not exist
     * 
     * @param avatarName
     * @return
     */
    public AvatarMO getAvatar(String avatarName) {
        ManagedReference avatarRef = avatars.get(avatarName);
        if (avatarRef == null) {
            return null;
        }
        
        return avatarRef.get(AvatarMO.class);
    }
    
    /**
     * Put the avatarRef and the name in the set of avatars for this user
     * @param avatarName
     * @param avatar
     */
    public void putAvatar(String avatarName, AvatarMO avatar) {
        DataManager dm = AppContext.getDataManager();
        avatars.put(avatarName, dm.createReference(avatar));
    }
    
    /**
     * User has logged in from specified session with specificed protocol listener
     * @param session
     * @param protocol
     */
    void login(ClientSession session) {
        if (activeSessions==null) {
            activeSessions = new HashSet();
        }
        logger.info("User Login " + username);
        activeSessions.add(session);
    }
    
    /**
     * User has logged out from specified session
     * @param session
     * @param protocol
     */
    void logout(ClientSession session) {
        activeSessions.remove(session);
    }
    
    /**
     * Return true if this user is logged in, false otherwise
     * @return
     */
    boolean isLoggedIn() {
        return activeSessions.size()>0;
    }
    
    /**
     * Convenience method that returns the ManagedReference to this ManagedObject
     * @return
     */
    public ManagedReference getReference() {
        return AppContext.getDataManager().createReference(this);
    }
}
