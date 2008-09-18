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
package org.jdesktop.wonderland.client.jme.input;

/**
 * An event which indicates that a mouse button action occurred. 
 *
 * @author deronj
 */

@ExperimentalAPI
class MouseButtonEvent3D extends MouseEvent3D {

    static {
	/** Allocate this event type's ID. */
	EVENT_ID = Event.allocateEventID();
    }

    /**
     * Create a new MouseWheelEvent3D from the AWT mouse event with a null pickData.
     * @param evt the AWT event
     */
    MouseButtonEvent3D (MouseEvent awtEvent) {
        this(awtEvent, null);
    }

    /**
     * Create a new MouseWheelEvent3D from the AWT mouse event
     * @param evt the AWT event
     * @param pickData The pick data for the event.
     */
    MouseButtonEvent3D (MouseEvent awtEvent, PickData pickData) {
        super(awtEvent, pickData);

	if (isPressed()) {
	    MouseDraggedEvent3D.setPressPointScreen(awtEvent.getX(), awtEvent.getY());
	}
    }
    
    /**
     * Returns true if this event indicates mouse click.
     */
    public boolean isClicked() {
        return (getID() == MouseEvent.MOUSE_CLICKED);
    }
    
    /**
     * Returns true if this event indicates mouse press.
     */
    public boolean isPressed() {
        return (getID() == MouseEvent.MOUSE_PRESSED);
    }
    
    /**
     * Returns true if this event indicates mouse release.
     */
    public boolean isReleased() {
        return (getID() == MouseEvent.MOUSE_RELEASED);
    }
    
    /**
     * Returns the number of mouse clicks associated with this event.
     * @return integer value for the number of clicks
     */
    public int getClickCount() {
        return awtEvent.getClickCount();
    }
    
    /**
     * Returns a string representation of the object.
     */
    public String toString () {
	String str = "[" + super.toString(); 
	str += "]";
	return str;
    }
}
