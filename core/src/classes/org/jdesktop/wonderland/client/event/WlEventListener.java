/**
 * Project Looking Glass
 *
 * $RCSfile: LgEventListener.java,v $
 *
 * Copyright (c) 2004, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision: 1.4 $
 * $Date: 2005/08/09 00:55:58 $
 * $State: Exp $
 */
package org.jdesktop.wonderland.client.event;

/**
 * The definition of the Wonderland Event Listener. The listener must implement
 * the processing of the WlEvent.
 */
public interface WlEventListener {
    /**
     * Process an incoming event. Invoked when a registered event happends.
     * 
     * @param event  an event of a registered type and from a registerd source.
     */
    public void processEvent(WlEvent evt);
    
    /**
     * Called when adding this listener to the EventProcessor
     * in order to obtain the list of WlEvent classes which this listens to.
     * 
     * @return  the list of WlEvent classes which this listener listens to.
     */
    public Class<? extends WlEvent>[] getTargetEventClasses();
//    public Class[] getTargetEventClasses();
}



