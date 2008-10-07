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

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.appbase.client.Window2DView;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A 2D label which displays a text string.
 *
 * @author deronj
 */ 

@ExperimentalAPI
public class FrameLabel extends /*TODO: FrameTranspTexRect */ FrameTexRect {
       
    private static final Logger logger = Logger.getLogger(FrameLabel.class.getName());

    /** The height of this label */
    //protected static final float LABEL_HEIGHT = 0.17f;
    // TODO: the following hangs the machine?
    protected static final float LABEL_HEIGHT = FrameWorldDefault.HEADER_HEIGHT * 0.9f;

    /** The distance the label is "above" (i.e. toward the eye from) the header */
    //protected static float Z_OFFSET = 0.0005f;
    protected static float Z_OFFSET = 0.01f;

    /** TODO: dial these down for now 

    /** Convert from width world units to texture width */
    protected static final float wScale = /*180.0f*/ /*10*/ 2;

    /** Convert from height world units to texture width */
    protected static final float hScale = /*180.0f*/ /*10*/ 2;

    /** The text to display in the label */
    protected String text;

    /** The texture created from the text string */
    Texture texture;

    /** Width (in pixels) of the texture */
    protected int texWidth;

    /** Height (in pixels) of the texture */
    protected int texHeight;

    /** The x position of the label (in cell local coordinates) */
    protected float x;

    /** The y position of the label (in cell local coordinates) */
    protected float y;

    /** 
     * Create a new instance of FrameComponent with no positioning constraints and a default name.
     *
     * @param view The view the frame encloses.
     * @param gui The event handler.
     */
    public FrameLabel (Window2DView view, /*TODO: Gui2D */ Object gui) {
	this("FrameLabel", view, gui);
    }

    /** 
     * Create a new instance of FrameComponent with no positioning constraints.
     *
     * @param name The node name.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     */
    public FrameLabel (String name, Window2DView view, /*TODO: Gui2D */ Object gui) {
	// The texture and size is calculated in update() based on the text string.
	super(name, view, gui, /*TODO */null, /**/ 0f, 0f);
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup () {
	super.cleanup();
	text = null;
	texture = null;
    }

    /**
     * Set the text string displayed in this label.
     *
     * @param text The new text of the label.
     */
    public void setText (String text) {
	this.text = text;

	// Force all geometry and render state to be recalculated
	if (quad != null) {
	    detachChild(quad);
	    quad = null;
	}
	try {
            update();
        } catch (InstantiationException ex) {
            logger.warning("Cannot update FrameLabel component");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void update () throws InstantiationException {
	super.update();

	updateTexture();

	// Labels have transparent backgrounds
	//TODO: setAlpha(1f);
        
        // Update position relative to view
	setTranslation(new Vector3f(x, y, Z_OFFSET));
    }

    /**
     * Recalulate texture from the text string.
     */
    private void updateTexture () {

	float w = getWidth();
	float h = getHeight();
	logger.warning("wh = " + w + " " + h);
	logger.warning("whScale = " + wScale + " " + hScale);

	texWidth = (int)(wScale * getWidth());
	texHeight = (int)(hScale * getHeight());
	logger.warning("updateTexture: texWH = " + texWidth + " " + texHeight);

	// For debug
	texWidth = 256;
	texHeight = 64;

	/**/
        TextTextureGenerator ttg = new TextTextureGenerator(text, texWidth, texHeight);
        texture = ttg.getTexture();
	texture.setApply(Texture.ApplyMode.Modulate);
	/**/

	/*-------------- 
	java.awt.Image bi = Toolkit.getDefaultToolkit().getImage(
		              "/home/dj/jme/cvs/jme/src/jmetest/data/images/Monkey.jpg");

	Image image = TextureManager.loadImage(bi, false);

	texture = new Texture();
	texture.setImage(image);
        texture.setFilter(Texture.FM_LINEAR);
        texture.setMipmapState(Texture.MM_LINEAR);
	texture.setApply(Texture.AM_REPLACE);
	--------------*/

	setTexture(texture);

	/* For debug 
	printRenderState();
	if (quad != null) {
	    printGeometry();
	}
	*/
    }
}
