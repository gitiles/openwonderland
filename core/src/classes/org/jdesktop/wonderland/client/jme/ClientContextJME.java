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
package org.jdesktop.wonderland.client.jme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.mtgame.PhysicsSystem;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.client.login.LoginManager;

/**
 * A subclass of ClientContext which adds JME client specific context accessors.
 * 
 * @author paulby
 */
public class ClientContextJME extends ClientContext {

    private static WorldManager worldManager;
    private static HashMap<LoginManager, HashMap<String, PhysicsSystem>> physicsSystems = new HashMap();
    private static HashMap<LoginManager, HashMap<String, CollisionSystem>> collisionSystems = new HashMap();
    
    static {
        worldManager = new WorldManager("Wonderland");
        InputManager3D.getInputManager(); // worldManager must be instantiated first
    }

    /**
     * Get the view manager
     * @return
     */
    public static ViewManager getViewManager() {
        return ViewManager.getViewManager();
    }
    
    /**
     * Get the mtgame world manager
     * @return
     */
    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static EnvironmentManager getEnvironmentManager() {
        return EnvironmentManager.getEnvironmentManager();
    }

    public static AvatarRenderManager getAvatarRenderManager() {
        return AvatarRenderManager.getAvatarRenderManager();
    }

    public static void addPhysicsSystem(LoginManager session, String name, PhysicsSystem physicsSystem) {
//        System.err.println("SESSION addPhysics "+session);
        synchronized(physicsSystems) {
            HashMap<String, PhysicsSystem> sessionPhy = physicsSystems.get(session);
            if (sessionPhy==null) {
                sessionPhy = new HashMap();
                physicsSystems.put(session, sessionPhy);
            }
            sessionPhy.put(name, physicsSystem);
        }
    }

    /**
     * Return the PhysicsSystem for the specified session with the given name.
     * Returns null if there is no system with the given name
     * @param session
     * @param name
     * @return
     */
    public static PhysicsSystem getPhysicsSystem(LoginManager session, String name) {
        synchronized(physicsSystems) {
//        System.err.println("SESSION getPhysics "+((Object)session).toString()+"  "+physicsSystems.get(session));
            HashMap<String, PhysicsSystem> sessionPhy = physicsSystems.get(session);
            if (sessionPhy==null)
                return null;

            return sessionPhy.get(name);
        }
    }

    public static void addCollisionSystem(LoginManager session, String name, CollisionSystem collisionSystem) {
//        System.err.println("SESSION addColl"+session);
        synchronized(collisionSystems) {
            HashMap<String, CollisionSystem> sessionCollision = collisionSystems.get(session);
            if (sessionCollision==null) {
                sessionCollision = new HashMap();
                collisionSystems.put(session, sessionCollision);
            }
            sessionCollision.put(name, collisionSystem);
        }
    }

    /**
     * Return the PhysicsSystem for the specified session with the given name.
     * Returns null if there is no system with the given name
     * @param session
     * @param name
     * @return
     */
    public static CollisionSystem getCollisionSystem(LoginManager session, String name) {
//        System.err.println("SESSION getColl "+session);
        synchronized(collisionSystems) {
            HashMap<String, CollisionSystem> sessionCollisions = collisionSystems.get(session);
            if (sessionCollisions==null)
                return null;

            return sessionCollisions.get(name);
        }
    }

    /**
     * Remove all the physics systems for this session
     * @param session
     */
    static void removeAllPhysicsSystems(LoginManager session) {
        synchronized(physicsSystems) {
            physicsSystems.remove(session);
        }
    }
    
    /**
     * Remove all the collision systems for this session
     * @param session
     */
    static void removeAllCollisionSystems(LoginManager session) {
        synchronized(collisionSystems) {
            collisionSystems.remove(session);
        }
    }
}
