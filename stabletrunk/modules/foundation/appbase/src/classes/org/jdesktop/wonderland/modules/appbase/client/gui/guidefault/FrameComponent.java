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
package org.jdesktop.wonderland.modules.appbase.client.gui.guidefault;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.state.RenderState;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.utils.graphics.GraphicsUtils;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.WindowView;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * The generic superclass of window frame components.
 *
 * @author deronj
 */ 

@ExperimentalAPI
public abstract class FrameComponent {

    /** The component name */
    protected String name;

    /** The color to display when the app has control. */
    protected static final ColorRGBA HAS_CONTROL_COLOR = new ColorRGBA(0f, 0.9f, 0f, 1f);

    /** The color to display when the app has control. */
    protected static final ColorRGBA NO_CONTROL_COLOR = new ColorRGBA(0.9f, 0f, 0f, 1f);

    /** The view of the window the frame encloses. */
    protected ViewWorldDefault view;

    /** The control arb of the app. */
    protected ControlArb controlArb;

    /** The event handler of this component. */
    protected Gui2D gui;

    /** 
     * The entity of this component's parent component. This components entity is attached to this as
     * a child when ever the parentEntity is non-null.
     */
    protected Entity parentEntity;

    /** This component's entity. The scene graph and event listeners component are attached to this. */
    protected Entity entity;

    /** 
     * The local-to-cell transform node. Moves the rect local coords into cell coords. This is parented
     * to attach point of the parent entity when the cell goes live.
     */
    protected Node localToCellNode;

    /** 
     * Create a new instance of <code>FrameComponent</code>.
     * @param name The component name.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     */
    public FrameComponent (String name, WindowView view, Gui2D gui) {
	this.name = name;
	this.view = (ViewWorldDefault) view;
	this.gui = gui;
	controlArb = view.getWindow().getApp().getControlArb();
	initEntity();
    }

    /**
     * Clean up resources.
     */
    public void cleanup () {
	view = null;
	if (gui != null) {
	    gui.cleanup();
	    gui = null;
	}
	cleanupEntity();
    }

    /**
     * Return this component's entity.
     */
    public Entity getEntity () {
	return entity;
    }

    /**
     * Initialize this component's entity.
     */
    protected void initEntity () {

	// Create this component's entity and parent it
	entity = new Entity("Entity for frame component " + name);

	// Create this component scene graph (l2c -> geometry)
	initSceneGraph();

	// Attach event listeners for this component
	attachEventListeners(entity);

	attachToParentEntity();
    }

    /**
     * Clean up resources for this component's entity.
     */
    protected void cleanupEntity () {
	detachFromParentEntity();
	detachEventListeners(entity);
	cleanupSceneGraph();
	entity = null;
    }

    /**
     * Construct this component's scene graph. This consists of the following nodes.
     *
     * parentEntity attachPoint -> localToCellNode -> Geometry (subclass provided)
     */
    protected void initSceneGraph () {
	
	// Attach the localToCell node to the entity
	localToCellNode = new Node("Local-to-cell node for frame component " + name);
	RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().
	    createRenderComponent(localToCellNode);
	entity.addComponent(RenderComponent.class, rc);
	rc.setEntity(entity);
	System.err.println("initSceneGraph: rc = " + rc);
	System.err.println("entity = " + getEntity());

	// Attach the subclass geometries to the localToCell node
	Geometry[] geoms = getGeometries();
	for (Geometry geom : geoms) {
	    localToCellNode.attachChild(geom);
	}
    }

    /**
     * Detach and deallocate this component's scene graph nodes.
     */
    protected void cleanupSceneGraph () {
	entity.removeComponent(RenderComponent.class);
	localToCellNode = null;
	// Note: the subclasses cleanup routine is responsible for cleaning up the geometries.
    }

    /**
     * Returns a list of this component's geometry spatials. Non-container subclasses should
     * override this to return actual geometries.
     */
    protected Geometry[] getGeometries () {
	return null;
    }

    /**
     * Attach this component's entity to its parent entity.
     */
    protected void attachToParentEntity () {
	if (parentEntity != null) {
	    parentEntity.addEntity(entity);
	    RenderComponent rcParentEntity = 
		(RenderComponent) parentEntity.getComponent(RenderComponent.class);
	    RenderComponent rcEntity = (RenderComponent)entity.getComponent(RenderComponent.class);
	    System.err.println("rcEntity = " + rcEntity);
	    System.err.println("rcEntity.getEntity() = " + rcEntity.getEntity());

	    // TODO: hack
	    ClientContextJME.getWorldManager().addEntity(rcEntity.getEntity());

	    if (rcParentEntity != null && rcParentEntity.getSceneRoot() != null && rcEntity != null) {
		rcEntity.setAttachPoint(rcParentEntity.getSceneRoot());
	    }
	}
    }

    /**
     * Detach this component's entity from its parent entity.
     */
    protected void detachFromParentEntity () {
	if (parentEntity != null) {
	    parentEntity.removeEntity(entity);
	    RenderComponent rcEntity = (RenderComponent)entity.getComponent(RenderComponent.class);
	    if (rcEntity != null) {
		rcEntity.setAttachPoint(null);
	    }
	}
	parentEntity = null;
    }

    /**
     * Specify the parent entity of this component.
     */
    public void setParentEntity (Entity parentEntity) {
	if (parentEntity != null) {
	    detachFromParentEntity();
	}
	this.parentEntity = parentEntity;
	if (this.parentEntity != null) {
	    System.err.println("Attach to parentEntity");
	    System.err.println("this = " + this);
	    System.err.println("parentEntity = " + parentEntity);
	    attachToParentEntity();
	}
    }

    /**
     * The size of the view has changed. Make the corresponding
     * position and/or size updates for this frame component.
     *
     * @throw InstantiationException if couldn't allocate resources for the visual representation.
     */
    public void update () throws InstantiationException {
	updateColor();
    }

    /**
     * The control state of the app has changed. Make the corresponding change in the frame.
     *
     * @param controlArb The app's control arb.
     */
    public void updateControl (ControlArb controlArb) {
	updateColor();
    }

    /**
     * Update the component color based on whether the user has control of the app.
     */
    protected void updateColor () {
        if (controlArb == null || controlArb.hasControl()) {
	    setColor(HAS_CONTROL_COLOR);
        } else {
	    setColor(NO_CONTROL_COLOR);
	}               
    }

    /**
     * Set the background color of the component.
     *
     * @param color The new background color.
     */
    public abstract void setColor (ColorRGBA color);

    /**
     * Get the background color of the component.
     */
    public abstract ColorRGBA getColor ();

    /**
     * Sets the localToCell translation of this component.
     *
     * @param trans The translation vector.
     */
    public void setLocalTranslation (Vector3f trans) {
	localToCellNode.setLocalTranslation(trans);
    }

    /**
     * Attach this component's event listeners to the givenentity.
     */
    protected void attachEventListeners (Entity entity) {
	if (gui != null) {
	    gui.attachEventListeners(entity);
	}
    }

    /**
     * Detach this component's event listeners from the given entity.
     */
    protected void detachEventListeners (Entity entity) {
	if (gui != null) {
	    gui.detachEventListeners(entity);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString () {
	return "Frame component " + name;
    }
}