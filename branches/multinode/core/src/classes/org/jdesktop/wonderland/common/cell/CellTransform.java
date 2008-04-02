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
package org.jdesktop.wonderland.common.cell;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.io.Serializable;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The transform for a cell.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class CellTransform implements Serializable {

    private Quaternion rotation;
    private Vector3f translation;
    private static final Vector3f scale = new Vector3f(1,1,1);
    
    /**
     * Create a cell transform. Either (or both) values may be null
     * 
     * @param quat
     * @param translation
     */
    public CellTransform(Quaternion rotate, Vector3f translation) {
        this.rotation = rotate;
        this.translation = translation;
        
        if (this.rotation==null)
            this.rotation = new Quaternion();
        if (this.translation==null)
            this.translation = new Vector3f();
    }

    private CellTransform(CellTransform orig) {
        this.rotation = new Quaternion(orig.rotation);
        this.translation = new Vector3f(orig.translation);
    }
    
    @Override
    public Object clone() {
        return new CellTransform(this);
    }

    /**
     * Transform the BoundingVolume 
     * @param ret
     */
    public void transform(BoundingVolume ret) {
        assert(ret!=null);
        ret.transform(rotation,translation, scale, ret);
    }
    
    /**
     * Transform the vector ret by this transform. ret is modified and returned.
     * @param ret
     */
    public Vector3f transform(Vector3f ret) {
        ret.multLocal(translation);
        
        return ret;
    }
    
    /**
     * Multiply this transform by t1. This transform will be update
     * and the result returned
     * 
     * @param transform
     * @return this
     */
    public CellTransform mul(CellTransform t1) {
        rotation.multLocal(t1.rotation);
//        System.out.print(translation +"  + "+t1.translation);
        translation.addLocal(t1.translation);
//        System.out.println(" = "+translation);
        return this;
    }
    
    /**
     * Populates translation with the translation of this CellTransform, if translation
     * is null, a new Vector3f will be created and returned
     * 
     * @param translation object to return (to avoid gc)
     * @return the translation for this transform
     */
    public Vector3f getTranslation(Vector3f translation) {
        if (translation==null)
            return new Vector3f(this.translation);
        
        translation.set(this.translation);
        return translation;
    }
    
    /**
     * Set the translation.
     * @param translation set the translation for this transform
     */
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
        if (this.translation==null)
            this.translation = new Vector3f();
    }

    /**
     * Get the rotation portion of this transform. Populates the rotation 
     * paramter with the current rotation and returns it, if rotation is null
     * a new Quaternion is returned.
     * 
     * @param rotation object to return (to avoid gc)
     * @return the rotation quaternion for this transform
     */
    public Quaternion getRotation(Quaternion rotation) {
        if (rotation==null)
            rotation = new Quaternion(this.rotation);
        else
            rotation.set(this.rotation);
        
        return rotation;
    }

    /**
     * Set the rotation portion of this transform
     * @param rotation set the rotation for this transform
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = new Quaternion(rotation);
    }
}
