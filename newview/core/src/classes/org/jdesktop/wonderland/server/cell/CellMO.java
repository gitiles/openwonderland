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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.messages.CellClientComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateMessage;
import org.jdesktop.wonderland.common.cell.messages.CellClientStateMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.server.cell.annotation.DependsOnCellComponentMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.spatial.UniverseManager;
import org.jdesktop.wonderland.server.spatial.UniverseManagerFactory;
import org.jdesktop.wonderland.server.state.PositionServerStateHelper;

/**
 * Superclass for all server side representation of a cell
 * 
 * @author paulby
 */
@ExperimentalAPI
public abstract class CellMO implements ManagedObject, Serializable {

    private ManagedReference<CellMO> parentRef=null;
    private ArrayList<ManagedReference<CellMO>> childCellRefs = null;
    private CellTransform localTransform = null;
    protected CellID cellID;
    private BoundingVolume localBounds;
    private CellID parentCellID;
    
    // a check if there is a bounds change that has not been committed.  If
    // there are uncommitted bounds changes, certain operations (like 
    // getting the computed VW bounds) are not valid
//    private transient boolean boundsChanged = false;
    
    private String name=null;
    
    private boolean live = false;
    
    protected ManagedReference<Channel> cellChannelRef = null;
    
    protected static Logger logger = Logger.getLogger(CellMO.class.getName());
    
    private short priority;
    
    // ManagedReferences of ClientSessions
    protected HashSet<ManagedReference<ClientSession>> clientSessionRefs = null;
    
    private HashMap<Class, ManagedReference<CellComponentMO>> components = new HashMap();
    
    private HashSet<TransformChangeListenerSrv> transformChangeListeners=null;
    
    /** Default constructor, used when the cell is created via WFS */
    public CellMO() {
        this.localBounds = null;
        this.localTransform = null;
        this.cellID = WonderlandContext.getCellManager().createCellID(this);

        if (this instanceof AvatarCellMO) {
            Iterable<Class<? extends CellComponentMO>> avatarComponentClasses = CellManagerMO.getCellManager().getAvatarCellComponentClasses();

            if (avatarComponentClasses!=null) {
                for(Class<? extends CellComponentMO> c : avatarComponentClasses) {
                    try {
                        Constructor con = c.getConstructor(CellMO.class);
                        CellComponentMO comp = (CellComponentMO) con.newInstance(this);
                        addComponent(comp);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    /**
     * Create a CellMO with the specified localBounds and transform.
     * If either parameter is null an IllegalArgumentException will be thrown.
     * @param localBounds the bounds of the new cell, must not be null
     * @param transform the transform for this cell, must not be null
     */
    public CellMO(BoundingVolume localBounds, CellTransform transform) {
        this();
        if (localBounds==null)
            throw new IllegalArgumentException("localBounds must not be null");
        if (transform==null)
            throw new IllegalArgumentException("transform must not be null");
        
        this.localTransform = transform;
        setLocalBounds(localBounds);

    }
    
    /**
     * Set the bounds of the cell in cell local coordinates
     * @param bounds
     */
    public void setLocalBounds(BoundingVolume bounds) {
        localBounds = bounds.clone(null);
        if (live) {
            throw new RuntimeException("SetBounds on live cells is not implemented yet");
//            UniverseManager.getUniverseManager().setLocalBounds(bounds);
        }
    }
    
    /**
     *  Return (a clone) of the cells bounds in cell local coordinates
     * @return the bounds in local coordinates
     */
    public BoundingVolume getLocalBounds() {
        return (BoundingVolume) localBounds.clone(null);     
    }
    
    /**
     * Returns the local bounds transformed into VW coordinates. These bounds
     * do not include the subgraph bounds. This call is only valid for live
     * cells
     * 
     * @return
     */
    public BoundingVolume getWorldBounds() {
        if (!live)
            throw new IllegalStateException("Cell is not live");
        
        return UniverseManagerFactory.getUniverseManager().getWorldBounds(this, null);
    }
   
    /**
     * Get the world transform of this cells origin. This call
     * can only be made on live cells, an IllegalStateException will be thrown
     * if the cell is not live.
     * 
     * TODO - should we create our own exception type ?
     * 
     * @param result the CellTransform to populate with the result and return, 
     * can be null in which case a new CellTransform will be returned.
     * @return
     */
    public CellTransform getWorldTransform(CellTransform result) {
        if (!live)
            throw new IllegalStateException("Unsupported Operation, only valid for a live Cell "+this.getClass().getName());
        
        return UniverseManagerFactory.getUniverseManager().getWorldTransform(this, result);
    }
    
    /**
     *  Add a child cell to list of children contained within this cell.
     *  A cell can only be attached to a single parent cell at any given time,
     *  attempting to add a cell to multiple parents will result in a
     *  MultipleParentException being thrown.
     * 
     * @param child
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    public void addChild(CellMO child) throws MultipleParentException {
        if (childCellRefs==null)
            childCellRefs = new ArrayList<ManagedReference<CellMO>>();
        
        child.setParent(this);
        
        childCellRefs.add(AppContext.getDataManager().createReference(child));
        
        if (live) {
           child.setLive(true);
        }

    }
    
    /**
     * Remove the child from the list of children of this cell.
     * 
     * @param child to remove
     * @return true if the child was removed, false if the cell was not a child of
     * this cell.
     */
    public boolean removeChild(CellMO child) {
        ManagedReference childRef = AppContext.getDataManager().createReference(child);
        
        if (childCellRefs.remove(childRef)) {
            try {
                child.setParent(null);
                if (live) {
                    child.setLive(false);
                }
                UniverseManagerFactory.getUniverseManager().removeChild(this, child);
                return true;
            } catch (MultipleParentException ex) {
                // This should never happen
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        
        // Not a child of this cell
        return false;
    }
    
    /**
     * Return the number of children of this cell
     * @return the number of children
     */
    public int getNumChildren() {
        if (childCellRefs==null)
            return 0;
        
        return childCellRefs.size();
    }
    
    /**
     * Return the collection of children references for this cell. 
     * If this cell has no children an empty collection is returned.
     * Users of this call should not make changes to the collection directly
     * 
     * @return a collection of references to the children of this cell.
     */
    public Collection<ManagedReference<CellMO>> getAllChildrenRefs() {
        if (childCellRefs==null)
            return new ArrayList<ManagedReference<CellMO>>();
        
        return childCellRefs;
    }
        
    /**
     *  Return the cell which is the parentRef of this cell, null if this not
     * attached to a parentRef
     */
    public CellMO getParent() {
        if (parentRef==null)
            return null;
        return parentRef.get();                
    }
    
    /**
     * Return the cellID of the parent.  This method was added for debugging
     * and is used by SpaceMO to check that the lists are ordered correctly.
     * 
     * TODO remove
     * 
     * @return
     */
    CellID getParentCellID() {
        return parentCellID;
    }
    
    /**
     * Detach this cell from its parent
     */
    public void detach() {
        CellMO parent = getParent();
        if (parent==null)
            return;
        
        parent.removeChild(this);
    }
    
    /**
     * Set the parent of this cell. Package private because the parent is
     * controlled through add and remove child.
     * 
     * @param parent the parent cell
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    void setParent(CellMO parent) throws MultipleParentException {
        if (parent!=null && parentRef!=null)
            throw new MultipleParentException();
        
        if (parent==null) {
            this.parentRef = null;
            this.parentCellID = CellID.getInvalidCellID();
        } else {
            this.parentRef = AppContext.getDataManager().createReference(parent);
            this.parentCellID = parent.getCellID();
        }
    }
    
    /**
     * Set the transform for this cell. This will define the localOrigin of
     * the cell on the client. This transform is combined with all parentRef 
     * transforms to define the location of the cell in 3 space. 
     * 
     * Changing the transform repositions the cell which is a fairly expensive
     * operation as it changes the computed bounds of this cell and potentially
     * all it's parent cells.
     * 
     * This method is usually called during cell construction or from
     * reconfigureCell. If you want a cell that moves regularly around the
     * world use MovableComponent.
     * 
     * @param transform
     */
    protected void setLocalTransform(CellTransform transform) {
        
        this.localTransform = (CellTransform) transform.clone(null);

        if (live)
            UniverseManagerFactory.getUniverseManager().setLocalTransform(this, localTransform);
    }
    

    /**
     * Return the cells transform
     * 
     * @return return a clone of the transform
     */
    public CellTransform getLocalTransform(CellTransform result) {
        return (CellTransform) localTransform.clone(result);
    }
    
    /**
     * Notify the client that the contents of the cell have changed
     *
     * REPLACED BY setServerState
     *
     */
//    public void contentChanged() {
//        logger.severe("CellMO.contentChanged NOT IMPLEMENTED");
//    }
       
    /**
     * Return the cellID for this cell
     * 
     * @return cellID
     */
    public CellID getCellID() {
        return cellID;
    }
    
    /**
     * Get the live state of this cell. live cells are connected to the
     * world root, inlive cells are not
     */
    public boolean isLive() {
        return live;
    }
    
    /**
     * Set the live state of this cell. Live cells are connected to the
     * world root and are present in the world, non-live cells are not
     * @param live
     */
    protected void setLive(boolean live) {
        if (this.live==live)
            return;

        if (live) {
            if (localBounds==null) {
                logger.severe("CELL HAS NULL BOUNDS, defaulting to unit sphere");
                localBounds = new BoundingSphere(1f, new Vector3f());
            }

            createChannelComponent();
            resolveAutoComponentAnnotationsForCell();

            this.live = live;  // Needs to happen after resolveAutoComponentAnnotationsForCell

            addToUniverse(UniverseManagerFactory.getUniverseManager());

            // Add a message receiver to handle messages to dynamically add and
            // remove components, get and set the server state.
            ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
            if (channel != null) {
                channel.addMessageReceiver(CellServerComponentMessage.class,
                        new ComponentMessageReceiver(this));
                channel.addMessageReceiver(CellServerStateMessage.class,
                        new ComponentStateMessageReceiver(this));
            }

            Collection<ManagedReference<CellComponentMO>> compList = components.values();
            for(ManagedReference<CellComponentMO> c : compList) {
                resolveAutoComponentAnnotationsForComponent(c);
            }
        } else {
            this.live = live;
            removeFromUniverse(UniverseManagerFactory.getUniverseManager());

            // Remove the message receiver that handles messages to dynamically
            // add and remove components, get and set the server state.
            ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
            if (channel != null) {
                channel.removeMessageReceiver(CellServerComponentMessage.class);
                channel.removeMessageReceiver(CellServerStateMessage.class);
            }
        }


        // Notify all components of new live state
        Collection<ManagedReference<CellComponentMO>> compList = components.values();
        for(ManagedReference<CellComponentMO> c : compList) {
            c.get().setLive(live);
        }
        
        for(ManagedReference<CellMO> ref : getAllChildrenRefs()) {
            CellMO child = ref.get();
            child.setLive(live);
        }
    }

    /**
     * Check for @AutoCellComponent annotations in the cellcomponent and
     * populate fields appropriately. Also checks the superclassses of the
     * cell component upto CellComponent.class
     *
     * @param c
     */
    private void resolveAutoComponentAnnotationsForComponent(ManagedReference<CellComponentMO> c) {
            Class clazz = c.get().getClass();
            while(clazz!=CellComponentMO.class) {
                resolveAnnotations(clazz, c);
                clazz = clazz.getSuperclass();
            }
    }

    /**
     * Check for @AutoCellComponent annotations in the cell and
     * populate fields appropriately. Also checks the superclassses of the
     * cell upto Cell.class
     *
     * @param c
     */
    private void resolveAutoComponentAnnotationsForCell() {
        Class clazz = this.getClass();
        ManagedReference<CellMO> c = AppContext.getDataManager().createReference(this);
        while(clazz!=CellMO.class) {
            resolveAnnotations(clazz, c);
            clazz = clazz.getSuperclass();
        }

    }
    private void resolveAnnotations(Class clazz, ManagedReference<? extends ManagedObject> o) {

        // Resolve @DependsOnCellComponentMO on class
        DependsOnCellComponentMO dependsOn = (DependsOnCellComponentMO) clazz.getAnnotation(DependsOnCellComponentMO.class);
        if (dependsOn!=null) {
            Class[] dependClasses = dependsOn.value();
            if (dependClasses!=null) {
                for(Class c : dependClasses) {
                    checkComponentFromAnnotation(c);
                }
            }
        }

        // Resolve @UsesCellComponentMO annotation on fields
        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields) {
            UsesCellComponentMO a = f.getAnnotation(UsesCellComponentMO.class);
            if (a!=null) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("****** GOT ANNOTATION for field "+f.getName());

                CellComponentMO comp = checkComponentFromAnnotation(a.value());

                try {
                    f.setAccessible(true);
                    f.set(o.getForUpdate(), AppContext.getDataManager().createReference(comp));
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Check if the component is already added to the cell, if not create and add it
     * @param componentClazz class of component
     * @return the component object
     */
    private CellComponentMO checkComponentFromAnnotation(Class componentClazz) {
        CellComponentMO comp = getComponent(componentClazz);
        if (comp==null) {

            try {
                // Create the component and add it to the map. We must
                // also recursively create the component's dependencies
                // too!
                comp = (CellComponentMO) (componentClazz.getConstructor(CellMO.class).newInstance(this));
                addComponent(comp);
            } catch (InstantiationException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, "Error instantiating component "+componentClazz, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(CellMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return comp;
    }

    /**
     * Create the channel component for this cell. All cells have a channel component, but by
     * default only root cells actually open a channel. Child cells of a root will use
     * the roots channel. This is hidden from the user as they always access the common
     * ChannelComponentMO api
     */
    private void createChannelComponent() {
        if (getComponent(ChannelComponentMO.class)!=null)
            return;

        if (parentRef==null) {
            // Root node
            addComponent(new ChannelComponentImplMO(this));
        } else {
            // Not a root node
            addComponent(new ChannelComponentRefMO(this));
        }
    }

    /**
     * Add this cell to the universe
     */
    void addToUniverse(UniverseManager universe) {
        universe.createCell(this);
//        System.err.println("CREATING SPATIAL CELL " + getCellID().toString() + " " + this.getClass().getName());

        if (transformChangeListeners != null) {
            for (TransformChangeListenerSrv listener : transformChangeListeners) {
                universe.addTransformChangeListener(this, listener);
            }
        }

        if (parentRef != null) {
            universe.addChild(parentRef.getForUpdate(), this);
        }
    }

    /**
     * Remove this cell from the universe
     */
    void removeFromUniverse(UniverseManager universe) {
        universe.removeCell(this);
    }

    /**
     * Get the name of the cell, by default the name is the cell id.
     * @return the cell's name
     */
    public String getName() {
        if (name==null)
            return cellID.toString();
        
        return name;
    }

    /**
     * Set the name of the cell. The name is simply for developer reference.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Add a client session with the specified capabilities to this cell. 
     * Called by the ViewCellCacheMO as part of makeing a cell active, only 
     * applicable to cells with a ChannelComponent.
     * 
     * @param clientID the ID of the client that is being added
     * @param capabilities
     * @return
     */
    protected CellSessionProperties addClient(WonderlandClientID clientID,
                                            ClientCapabilities capabilities) {
        ChannelComponentMO chan = getComponent(ChannelComponentMO.class);
        if (chan!=null) {
            chan.addUserToCellChannel(clientID);
        }
        
        return new CellSessionProperties(getViewCellCacheRevalidationListener(), 
                getClientCellClassName(clientID, capabilities),
                getClientState(null, clientID, capabilities));
    }
    
    /**
     * Called to notify the cell that some aspect of the client sessions capabilities
     * have changed. This call is made from the ViewCellCacheOperations exectue
     * method returned by addSession.
     * 
     * @param clientID
     * @param capabilities
     * @return
     */
    protected CellSessionProperties changeClient(WonderlandClientID clientID,
                                               ClientCapabilities capabilities) {
        return new CellSessionProperties(getViewCellCacheRevalidationListener(), 
                getClientCellClassName(clientID, capabilities),
                getClientState(null, clientID, capabilities));
        
    }
    
    /**
     * Remove this cell from the specified session, only applicable to cells
     * with a ChannelComponent. This modifies the ChannelComponent for this cell
     * (if it exists) but does not modify the CellMO itself.
     * 
     * @param clientID
     */
    protected void removeSession(WonderlandClientID clientID) {
        ChannelComponentMO chan = getComponent(ChannelComponentMO.class);
        if (chan!=null) {
            chan.removeUserFromCellChannel(clientID);
        }
    }

    /**
     * Returns the fully qualified name of the class that represents
     * this cell on the client
     */
    protected abstract String getClientCellClassName(WonderlandClientID clientID,
                                                     ClientCapabilities capabilities);
    
    /**
     * Returns the client-side state of the cell. If the cellClientState argument
     * is null, then the method should create an appropriate class, otherwise,
     * the method should just fill in details in the class. Returns the client-
     * side state class
     *
     * @param cellClientState If null, create a new object
     * @param clientID The unique ID of the client
     * @param capabilities The client capabilities
     */
    protected CellClientState getClientState(CellClientState cellClientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // If the given cellClientState is null, create a new one
        if (cellClientState == null) {
            cellClientState = new CellClientState();
        }
        populateCellClientState(cellClientState, clientID, capabilities);

        // Set the name of the cell
        cellClientState.setName(this.getName());
        return cellClientState;
    }

    private void populateCellClientState(CellClientState config,
            WonderlandClientID clientID, ClientCapabilities capabilities) {

        Iterable<ManagedReference<CellComponentMO>> compReferences = components.values();
        for(ManagedReference<CellComponentMO> ref : compReferences) {
            CellComponentMO componentMO = ref.get();
            String clientClass = componentMO.getClientClass();
            if (clientClass != null) {
                CellComponentClientState clientState = componentMO.getClientState(null, clientID, capabilities);
                config.addClientComponentClasses(clientClass, clientState);
            }
        }
    }
    
    /**
     * Returns the ViewCacheOperation, or null
     * @return
     */
    protected ViewCellCacheRevalidationListener getViewCellCacheRevalidationListener() {
        return null;
    }
    
    
    /**
     * Sets the server-side state of the cell, given the server state properties
     * passed in.
     *
     * @param state the properties to set the state with
     */
    public void setServerState(CellServerState state) {
        // Set the name of the cell if it is not null
        if (state.getName() != null) {
            this.setName(state.getName());
        }
        
        // For all components in the setup class, create the component classes
        // and setup them up and add to the cell.
        for (Map.Entry<Class, CellComponentServerState> e : state.getComponentServerStates().entrySet()) {
            CellComponentServerState compState = e.getValue();

            // Check to see if the component server state is the special case
            // of the Position state. If so, set the values in the cell manually.
            if (compState instanceof PositionComponentServerState) {
                
                // Set up the transform (origin, rotation, scaling) and cell bounds
                PositionComponentServerState posState = (PositionComponentServerState)compState;
                setLocalTransform(PositionServerStateHelper.getCellTransform(posState));
                setLocalBounds(PositionServerStateHelper.getCellBounds(posState));
                continue;
            }

            // Otherwise, set the state of the server-side component, creating
            // it if necessary.
            String className = compState.getServerComponentClassName();
            if (className == null) {
                continue;
            }
            try {
                Class clazz = Class.forName(className);
                Class lookupClazz = CellComponentMO.getLookupClass(clazz);
                CellComponentMO comp = this.getComponent(lookupClazz);
                if (comp == null) {
                    Constructor<CellComponentMO> constructor = clazz.getConstructor(CellMO.class);
                    comp = constructor.newInstance(this);
                    comp.setServerState(compState);
                    this.addComponent(comp);
                }
                else {
                    comp.setServerState(compState);
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Returns the setup information currently configured on the cell. If the
     * setup argument is non-null, fill in that object and return it. If the
     * setup argument is null, create a new setup object.
     * 
     * @param setup The setup object, if null, creates one.
     * @return The current setup information
     */
    public CellServerState getServerState(CellServerState setup) {
        // In the case of CellMO, if the 'setup' parameter is null, it means
        // it was not created by the super class. In which case, this class
        // should just return null
        if (setup == null) {
            return null;
        }

        // Set the name of the cell
        setup.setName(this.getName());

        // Fill in the details about the origin, rotation, and scaling. Create
        // and add a PositionComponentServerState with all of this information
        PositionComponentServerState position = new PositionComponentServerState();
        position.setBounds(PositionServerStateHelper.getSetupBounds(localBounds));
        position.setOrigin(PositionServerStateHelper.getSetupOrigin(localTransform));
        position.setRotation(PositionServerStateHelper.getSetupRotation(localTransform));
        position.setScaling(PositionServerStateHelper.getSetupScaling(localTransform));
        setup.addComponentServerState(position);

        // add setups for each component
        for (ManagedReference<CellComponentMO> componentRef : components.values()) {
            CellComponentMO component = componentRef.get();
            CellComponentServerState compSetup = component.getServerState(null);
            if (compSetup != null) {
                setup.addComponentServerState(compSetup);
            }
        }

        return setup;
    }
    
    /**
     * Return the priorty of the cell. A cells priority dictates the order
     * in which it is loaded by a client. Priortity 0 cells are loaded first, 
     * followed by subsequent priority levels. Priority is only a hint to the 
     * client, it has no effect on the server
     * 
     * The default priority is 5
     * 
     * @return
     */
    public short getPriority() {
        return priority;
    }

    /**
     * Set the cell priority. The priority must be >=0 otherwise an 
     * IllegalArgumentException will be thrown.
     * 
     * The default priority is 5
     * 
     * @param priority
     */
    public void setPriority(short priority) {
        if (priority<0)
            throw new IllegalArgumentException("priorty must be >= 0");
        
        this.priority = priority;
    }
    
    /**
     * If this cell supports the capabilities of cellComponent then
     * return an instance of cellComponent associated with this cell. Otherwise
     * return null.
     * 
     * @see MovableCellComponent
     * @param cellComponent
     * @return
     */
    public <T extends CellComponentMO> T getComponent(Class<T> cellComponentClass) {
        assert(CellComponentMO.class.isAssignableFrom(cellComponentClass));
        ManagedReference<CellComponentMO> comp = components.get(cellComponentClass);
        if (comp==null)
            return null;
        return (T) comp.get();
    }
    
    /**
     * Add a component to this cell. Only a single instance of each component
     * class can be added to a cell. Adding duplicate components will result in
     * an IllegalArgumentException 
     * 
     * @param component
     */
    public void addComponent(CellComponentMO component) {
        addComponent(component,
                CellComponentMO.getLookupClass(component.getClass()));
    }

    public void addComponent(CellComponentMO component, Class componentClass) {
        // Add the component to the map of components. If it already exists,
        // then throw an exception
        ManagedReference<CellComponentMO> previous = components.put(componentClass,
                AppContext.getDataManager().createReference(component));
        if (previous != null)
            throw new IllegalArgumentException("Adding duplicate component of class " + component.getClass().getName());

        // If the cell is live, then tell the clients to create all of the
        // components.
        if (live) {
  
            // Loop through and recursively create the components that are listed
            // as dependencies. We get back a set of components that have been
            // created.
            resolveAutoComponentAnnotationsForComponent(AppContext.getDataManager().createReference(component));

            // Send a message to all clients that a new component has been added
            // to the cell. We only need to do this when the cell is live because
            // a setLive(true) will send client states then.
            CellComponentClientState clientState = component.getClientState(null, null, null);
            String className = component.getClientClass();
            sendCellMessage(null, CellClientComponentMessage.newAddMessage(cellID, className, clientState));

            // Finally set the component to the live state
            component.setLive(live);
        }
    }

    /**
     * Removes a component from this cell. If the cell component does not exist
     * on this cell, this method does nothing.
     *
     * @param component The component to remove from this cell
     */
    public void removeComponent(CellComponentMO component) {
        // First tell the component that it is no longer "alive"
        component.setLive(false);

        // Remove the component from the map of components
        Class clazz = CellComponentMO.getLookupClass(component.getClass());
        components.remove(clazz);

        // Finally, tell all of the clients to remove the component, if there
        // is a client-side class
        String clientClass = component.getClientClass();
        if (clientClass != null) {
            sendCellMessage(null, CellClientComponentMessage.newRemoveMessage(cellID, clientClass));
        }
    }

    /**
     * Add a TransformChangeListener to this cell. The listener will be
     * called for any changes to the cells transform. The listener can either
     * be a Serialized object, or an instance of ManagedReference. Both types
     * are handled correctly.
     * 
     * Listeners should generally execute quickly, if they take a long time
     * it is recommended that the listener schedules a new task to service
     * the callback.
     * 
     * @param listener to add
     */
    public void addTransformChangeListener(TransformChangeListenerSrv listener) {
        if (transformChangeListeners==null)
            transformChangeListeners = new HashSet();
        transformChangeListeners.add(listener);

        if (isLive())
            UniverseManagerFactory.getUniverseManager().addTransformChangeListener(this, listener);

    }
    
    /**
     * Remove the specified listener.
     * @param listener to be removed
     */
    public void removeTransformChangeListener(TransformChangeListenerSrv listener) {
	if (transformChangeListeners == null)
	    return;
        transformChangeListeners.remove(listener);
        if (isLive())
            UniverseManagerFactory.getUniverseManager().removeTransformChangeListener(this, listener);
    }

    /**
     * A utility routine that fetches the channel component of the cell and
     * sends a message on it. If there is no channel component (should never
     * happen), this method logs an error message.
     *
     * @param clientID An optional client-side if the message is in response
     * @param message The CellMessage
     */
    public void sendCellMessage(WonderlandClientID clientID, CellMessage message) {
        ChannelComponentMO channel = getComponent(ChannelComponentMO.class);
        if (channel == null) {
            logger.severe("Unable to find channel on cell id " + getCellID() +
                    " with name " + getName());
            return;
        }
        channel.sendAll(clientID, message);
    }

    /**
     * Inner class to receive messages to get or set the server state of the
     * cell
     */
    private static class ComponentStateMessageReceiver extends AbstractComponentMessageReceiver {

        public ComponentStateMessageReceiver(CellMO cellMO) {
            super(cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            CellServerStateMessage cssm = (CellServerStateMessage)message;
            switch (cssm.getStateAction()) {
                case GET:
                    handleGetStateMessage(sender, clientID, message);
                    break;
                    
                case SET:
                    handleSetStateMessage(sender, clientID, message);
                    break;
            }
        }

        /**
         * Handles when a GET state message is received.
         */
        private void handleGetStateMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            // If we want to query the cell setup for the given cell ID, first
            // fetch the cell and ask it for its cell setup class. We also
            // want to catch any exception to make sure we send back a
            // response
            try {
                CellMO cellMO = getCell();
                CellServerState cellSetup = cellMO.getServerState(null);

                // Formulate a response message, fill in the cell setup, and return
                // to the client.
                MessageID messageID = message.getMessageID();
                CellServerStateResponseMessage response = new CellServerStateResponseMessage(messageID, cellSetup);
                sender.send(clientID, response);
            }
            catch (java.lang.Exception excp) {
                // Log a warning and send back a null response
                logger.log(Level.WARNING, "Unable to fetch cell server state",
                        excp);
                MessageID messageID = message.getMessageID();
                CellServerStateResponseMessage response = new CellServerStateResponseMessage(messageID, null);
                sender.send(clientID, response);
            }
        }

        /**
         * Handles when a SET state message is received.
         */
        private void handleSetStateMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {

            // Fetch the cell, and set its server state. Catch all exceptions
            // and report.
            CellServerStateMessage msg = (CellServerStateMessage)message;
            try {
                CellServerState state = msg.getCellServerState();
                CellMO cellMO = getCell();
                cellMO.setServerState(state);

                // Fetch a new client-state and set it. Send a message on the
                // cell channel with the new state.
                CellClientState clientState = cellMO.getClientState(null, clientID, null);
                cellMO.sendCellMessage(clientID, new CellClientStateMessage(cellMO.getCellID(), clientState));
            }
            catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Unable to set cell server state " +
                        msg.getCellID(), excp);
            }
        }
    }

    /**
     * Inner class to receive messages to dynamically add and remove components
     */
    private static class ComponentMessageReceiver extends AbstractComponentMessageReceiver {

        public ComponentMessageReceiver(CellMO cellMO) {
            super(cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            // Dispatch to either the "add" or "remove" message handler
            CellServerComponentMessage cm = (CellServerComponentMessage)message;
            switch (cm.getComponentAction()) {
                case ADD:
                    handleAddComponentMessage(sender, clientID, cm);
                    break;

                case REMOVE:
                    handleRemoveComponentMessage(sender, clientID, cm);
                    break;
            }
        }

        /**
         * Handles an "add" message by creating and adding the component.
         */
        private void handleAddComponentMessage(WonderlandClientSender sender,
                WonderlandClientID clientID, CellServerComponentMessage message) {

            // Fetch the initial component server state and the class name of
            // the component to create.
            CellComponentServerState state = message.getCellComponentServerState();
            String className = message.getCellComponentServerClassName();
            CellMO cellMO = getCell();

            try {
                // Try to create the cmponent class and add it if the component
                // does not exist. Upon success, return a message
                Class clazz = Class.forName(className);
                Class lookupClazz = CellComponentMO.getLookupClass(clazz);
                CellComponentMO comp = cellMO.getComponent(lookupClazz);
                if (comp == null) {
                    // Create the component class, set its state, and add it
                    Constructor<CellComponentMO> constructor = clazz.getConstructor(CellMO.class);
                    comp = constructor.newInstance(cellMO);
                    comp.setServerState(state);
                    cellMO.addComponent(comp);

                    // Send a response message back to the client indicating
                    // success
                    sender.send(clientID, new OKMessage(message.getMessageID()));

                    // Send the same server state object to all clients as an
                    // asynchronous event
                    cellMO.sendCellMessage(clientID, message);
                    return;
                }

                // Otherwise, the component already exists, so send an error
                // message back to the client.
                sender.send(clientID, new ErrorMessage(message.getMessageID(),
                        "The Component " + className + " already exists."));
            } catch (java.lang.Exception excp) {
                // Log an error in the log and send back an error message.
                logger.log(Level.WARNING, "Unable to add component " +
                        className + " for cell " + cellMO.getName(), excp);
                sender.send(clientID, new ErrorMessage(message.getMessageID(), excp));
            }
        }

        /**
         * Handles a "remove" message by removing the component
         */
        private void handleRemoveComponentMessage(WonderlandClientSender sender,
                WonderlandClientID clientID, CellServerComponentMessage message) {

            // Fetch the server-side component class name and remove the
            // component. Upon success, send a general "ok" message.
            try {
                // Find the component on the cell. If it is not present, then
                // send back an error message.
                CellMO cellMO = getCell();
                String className = message.getCellComponentServerClassName();
                Class clazz = CellComponentMO.getLookupClass(Class.forName(className));
                CellComponentMO component = cellMO.getComponent(clazz);
                if (component == null) {
                    logger.warning("Cannot find component for class " + className);
                    sender.send(clientID, new ErrorMessage(message.getMessageID()));
                    return;
                }

                // Remove the component and send a success message back to the
                // client
                cellMO.removeComponent(component);
                sender.send(clientID, new OKMessage(message.getMessageID()));

                // Send the same event message to all clients as an asynchronous
                // event
                cellMO.sendCellMessage(clientID, message);
                
            } catch (java.lang.ClassNotFoundException excp) {
                // Just got an exception and ignore here
                logger.log(Level.WARNING, "Cannot find component class", excp);
                sender.send(clientID, new ErrorMessage(message.getMessageID(), excp));
            }
        }
    }
}

