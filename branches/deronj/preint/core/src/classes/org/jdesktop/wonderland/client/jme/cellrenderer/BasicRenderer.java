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
package org.jdesktop.wonderland.client.jme.cellrenderer;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.resource.ResourceLocator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JBulletDynamicCollisionSystem;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.PhysicsSystem;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 *
 * Abstract Renderer class that implements CellRendererJME
 * 
 * @author paulby
 */
@ExperimentalAPI
public abstract class BasicRenderer implements CellRendererJME {
    /** <server name>:<port> of the web server */
    private String serverHostAndPort;
    
    protected static Logger logger = Logger.getLogger(BasicRenderer.class.getName());
    protected Cell cell;
    protected Entity entity;
    protected Node rootNode;
    protected MoveProcessor moveProcessor = null;
    
    private Vector3f tmpV3f = new Vector3f();
    private Quaternion tmpQuat = new Quaternion();
    
    private static ZBufferState zbuf = null;

    private boolean isRendering = false;
    
    static {
        zbuf = (ZBufferState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_ZBUFFER);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    }
    
    public BasicRenderer(Cell cell) {
        this.cell = cell;
    }

    public void setStatus(CellStatus status) {
        switch(status) {
            case ACTIVE :
                CellRenderer rend = cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
                if (cell!=null && rend!=null && !isRendering) {
                    if (rend instanceof CellRendererJME) {
                        Entity parentEntity= findParentEntity(cell.getParent());
                        Entity thisEntity = ((CellRendererJME)rend).getEntity();

                        thisEntity.addComponent(CellRefComponent.class, new CellRefComponent(cell));

                        if (parentEntity!=null) {
//                            System.err.println("ADDING to parent "+parentEntity+"  "+thisEntity);
                            parentEntity.addEntity(thisEntity);
                        } else {
//                            System.err.println("ADDING ROOT "+thisEntity);
                            ClientContextJME.getWorldManager().addEntity(thisEntity);
                        }

                        // Figure out the correct parent entity for this cells entity.
                        if (parentEntity!=null && thisEntity!=null) {
                            RenderComponent parentRendComp = (RenderComponent) parentEntity.getComponent(RenderComponent.class);
                            RenderComponent thisRendComp = (RenderComponent)thisEntity.getComponent(RenderComponent.class);
                            if (parentRendComp!=null && parentRendComp.getSceneRoot()!=null && thisRendComp!=null) {
                                thisRendComp.setAttachPoint(parentRendComp.getSceneRoot());
                            }
                        }
                        isRendering = true;

                    } else
                        logger.warning("Unexpected renderer class "+rend.getClass().getName());
                } else {
                    logger.info("No Entity for Cell "+cell.getClass().getName());
                }
            break;
        }

    }

    /**
     * Traverse up the cell hierarchy and return the first Entity
     * @param cell
     * @return
     */
    private Entity findParentEntity(Cell cell) {
        if (cell==null)
            return null;

        CellRenderer rend = cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        if (cell!=null && rend!=null) {
            if (rend instanceof CellRendererJME) {
//                    System.out.println("FOUND PARENT ENTITY on CELL "+cell.getName());
                return ((CellRendererJME)rend).getEntity();
            }
        }

        return findParentEntity(cell.getParent());
    }

    protected Entity createEntity() {
        Entity ret = new Entity(this.getClass().getName()+"_"+cell.getCellID());
        
        rootNode = createSceneGraph(ret);
        addRenderState(rootNode);

        addDefaultComponents(ret, rootNode);

        return ret;        
    }
    
    /**
     * Add the default renderstate to the root node. Override this method
     * if you want to apply a different RenderState
     * @param node
     */
    protected void addRenderState(Node node) {
        node.setRenderState(zbuf);
    }
    
    protected void addDefaultComponents(Entity entity, Node rootNode) {
        if (cell.getComponent(MovableComponent.class)!=null) {
            if (rootNode==null) {
                logger.warning("Cell is movable, but has no root node !");
            } else {           
                // The cell is movable so create a move processor
                moveProcessor = new MoveProcessor(ClientContextJME.getWorldManager(), rootNode);
                entity.addComponent(MoveProcessor.class, moveProcessor);
            }
        }

        if (rootNode!=null) {
            RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(rootNode);
            entity.addComponent(RenderComponent.class, rc);

//            JMECollisionSystem collisionSystem = (JMECollisionSystem)
//                    ClientContextJME.getWorldManager().getCollisionManager().loadCollisionSystem(JMECollisionSystem.class);
//            JBulletDynamicCollisionSystem collisionSystem = (JBulletDynamicCollisionSystem)
//                    ClientContextJME.getWorldManager().getCollisionManager().loadCollisionSystem(JBulletDynamicCollisionSystem.class);
//            JBulletPhysicsSystem physicsSystem = (JBulletPhysicsSystem)
//                    ClientContextJME.getWorldManager().getPhysicsManager().loadPhysicsSystem(JBulletPhysicsSystem.class, collisionSystem);

            WonderlandSession session = cell.getCellCache().getSession();
            CollisionSystem collisionSystem = ClientContextJME.getCollisionSystem(LoginManager.find(session), "Default");

            if (collisionSystem instanceof JBulletDynamicCollisionSystem) {
                CollisionComponent cc = ((JBulletDynamicCollisionSystem)collisionSystem).createCollisionComponent(rootNode);
                entity.addComponent(CollisionComponent.class, cc);
            } else {
                logger.warning("Unsuppoerted CollisionSystem "+collisionSystem);
            }

            // TODO Physics setup
//            PhysicsSystem physicsSystem = ClientContextJME.getPhysicSystem(session, "Default");
        }

    }

    /**
     * Create the scene graph
     * @return
     */
    protected abstract Node createSceneGraph(Entity entity);

    /**
     * Apply the transform to the jme node
     * @param node
     * @param transform
     */
    public static void applyTransform(Spatial node, CellTransform transform) {
        node.setLocalRotation(transform.getRotation(null));
        node.setLocalScale(transform.getScaling(null));
        node.setLocalTranslation(transform.getTranslation(null));
    }
    
    public Entity getEntity() {
        synchronized(this) {
            logger.fine("Get Entity "+this.getClass().getName());
            if (entity==null)
                entity = createEntity();
        }
        return entity;
    }
    
    public void cellTransformUpdate(CellTransform worldTransform) {
        if (moveProcessor!=null) {
            moveProcessor.cellMoved(worldTransform);
        }
    }

    /**
     * Given a url, determine and return the full asset URL.
     * 
     * @param origURL
     * @return
     * @throws java.net.MalformedURLException
     */
    protected URL getAssetURL(String origURL) throws MalformedURLException {
        // TODO: fix me?
        if (!origURL.startsWith("wla")) {
            return new URL(origURL);
        }

        // annotate wla URIs with the server name and port
        if (serverHostAndPort == null) {
            WonderlandSession session = cell.getCellCache().getSession();
            LoginManager manager = LoginManager.find(session);
            serverHostAndPort = manager.getServerNameAndPort();
        }

        try {
            AssetURI uri = new AssetURI(origURL).getAnnotatedURI(serverHostAndPort);
            return uri.toURL();
        } catch (URISyntaxException use) {
            MalformedURLException mue =
                    new MalformedURLException("Error creating asset URI");
            mue.initCause(use);
            throw mue;
        }
    }

    /**
     * JME Asset locator using WL Asset manager
     */
    public class AssetResourceLocator implements ResourceLocator {

        private String modulename;
        private String path;

        /**
         * Locate resources for the given file
         * @param url
         */
        public AssetResourceLocator(URL url) {
            // The modulename can either be in the "user info" field or the
            // "host" field. If "user info" is null, then use the host name.

            if (url.getUserInfo() == null) {
                modulename = url.getHost();
            }
            else {
                modulename = url.getUserInfo();
            }
            path = url.getPath();
            path = path.substring(0, path.lastIndexOf('/')+1);
        }

        public URL locateResource(String resource) {
//            System.err.println("Looking for resource "+resource);
//            System.err.println("Module "+modulename+"  path "+path);
            try {
                if (resource.startsWith("/")) {
                    URL url = getAssetURL("wla://"+modulename+resource);
//                    System.err.println("Using alternate "+url.toExternalForm());
                    return url;
                } else {
                    String urlStr = trimUrlStr("wla://"+modulename+path + resource);

                    URL url = getAssetURL(urlStr);
//                    System.err.println(url.toExternalForm());
                    return url;
                }
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Unable to locateResource "+resource, ex);
                return null;
            }
        }

        /**
         * Trim ../ from url
         * @param urlStr
         */
        private String trimUrlStr(String urlStr) {
            int pos = urlStr.indexOf("/../");
            if (pos==-1)
                return urlStr;

            StringBuilder buf = new StringBuilder(urlStr);
            int start = pos;
            while(buf.charAt(--start)!='/') {}
            buf.replace(start, pos+4, "/");
//            System.out.println("Trimmed "+buf.toString());

           return buf.toString();
        }

    }

    /**
     * An mtgame ProcessorCompoenent to process cell moves.
     */
    public class MoveProcessor extends ProcessorComponent {

        private CellTransform cellTransform;
        private boolean dirty = false;
        private Node node;
        private WorldManager worldManager;

        private boolean isChained = false;
        private NewFrameCondition postCondition = new NewFrameCondition(this);
        
        public MoveProcessor(WorldManager worldManager, Node node) {
            this.node = node;
            this.worldManager = worldManager;
        }
        
        @Override
        public void compute(ProcessorArmingCollection arg0) {
        }

        @Override
        public void commit(ProcessorArmingCollection arg0) {
            synchronized(this) {
                if (dirty) {
                    node.setLocalTranslation(cellTransform.getTranslation(tmpV3f));
                    node.setLocalRotation(cellTransform.getRotation(tmpQuat));
//                    System.err.println("BasicRenderer.cellMoved "+tmpV3f);
                    dirty = false;
                    worldManager.addToUpdateList(node);
//            System.err.println("--------------------------------");
                }
            }
        }

        @Override
        
        public void initialize() {
            setArmingCondition(postCondition);
        }

        /**
         * Notify the MoveProcessor that the cell has moved
         * 
         * @param transform cell transform in world coordinates
         */
        public void cellMoved(CellTransform transform) {
            synchronized(this) {
                this.cellTransform = transform;
                dirty = true;


            }
        }

        public void setChained(boolean isChained) {
            this.isChained = isChained;
            if (isChained)
                setArmingCondition(null);
            else 
                setArmingCondition(postCondition);
        }
    }
}
