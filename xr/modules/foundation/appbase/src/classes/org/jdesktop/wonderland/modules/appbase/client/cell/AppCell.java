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
package org.jdesktop.wonderland.modules.appbase.client.cell;

import java.lang.reflect.Constructor;
import org.jdesktop.wonderland.modules.appbase.client.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.gui.Displayer;
import org.jdesktop.wonderland.modules.appbase.client.gui.GuiFactory;
import org.jdesktop.wonderland.modules.appbase.client.gui.WindowView;

/**
 * The generic application cell superclass. Created with a subclass-specific constructor.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class AppCell extends Cell implements Displayer, ClientPlugin {

    private static final Logger logger = Logger.getLogger(AppCell.class.getName());

    /** The default GUI factory to use. */
    private static final String GUI_FACTORY_CLASS_DEFAULT = 
        "org.jdesktop.wonderland.modules.appbase.client.cell.gui.guidefault.Gui2DFactory";

    /** The global default appbase 2D GUI factory.*/
    private static GuiFactoryCell gui2DFactory;

    /** A list of all app cells in existence */
    private static final ArrayList<AppCell> appCells = new ArrayList<AppCell>();

    /**
     * If non-null, this is the master app which was awaiting its cell.
     * If null, this is a slave cell.
     */
    protected App app;

    /**
     * Statically initialize the appbase cell package (in a user client only). 
     */
    public void initialize(ServerSessionManager loginInfo) {

        // TODO: later on we might allow the default gui factory to be overridden by the user. 

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            Class clazz = Class.forName(GUI_FACTORY_CLASS_DEFAULT, true, classLoader);
            Constructor constructor = clazz.getConstructor();
            gui2DFactory = (GuiFactoryCell) constructor.newInstance();
        } catch(Exception e) {
            logger.severe("Error instantiating app GUI factory "+ GUI_FACTORY_CLASS_DEFAULT+
                        ", Exception = " + e);
        }
    }

    /**
     * Create an instance of AppCell.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public AppCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        appCells.add(this);
    }

    /**
     * Clean up resources held. 
     */
    public void cleanup() {
        synchronized (appCells) {
            appCells.remove(this);
        }
        if (app != null) {
            app.cleanup();
        }
        app = null;
    }

    /** 
     * Return the app type of this cell.
     */
    public abstract AppType getAppType();

    /**
     * Associate the app with a cell. May only be called one time.
     *
     * @param app The world cell containing the app.
     * @throws IllegalArgumentException If the app already is associated with a cell .
     * @throws IllegalStateException If the cell is already associated with an app.
     */
    public void setApp(App app)
            throws IllegalArgumentException, IllegalStateException {
        if (app == null) {
            throw new NullPointerException();
        }
        if (app.getDisplayer() != null) {
            throw new IllegalArgumentException("App already has a displayer");
        }
        if (this.app != null) {
            throw new IllegalStateException("Cell already has an app");
        }

        this.app = app;
        updateBoundsFromApp();
    }

    /**
     * Get the app associated with this cell.
     */
    public App getApp() {
        return app;
    }

    /**
     * Update cell bounds from the app bounds.
     */
    private void updateBoundsFromApp() {
        if (app == null) {
            return;
        }
        //TODO: How do we do this?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        CellRenderer ret = null;
        switch (rendererType) {
            case RENDERER_2D:
                // No 2D Renderer yet
                break;
            case RENDERER_JME:
                ret = gui2DFactory.createCellRenderer(this);
                break;
        }

        return ret;
    }

    /**
     * Attach the given view to the specified renderer of this cell.
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public void attachView(WindowView view, RendererType rendererType) {
        switch (rendererType) {
            case RENDERER_JME:
                ((AppCellRenderer) getCellRenderer(rendererType)).attachView(view);
                break;
            default:
                throw new RuntimeException("Unsupported cell renderer type: " + rendererType);
        }
    }

    /**
     * Detach the given view from the specified renderer of this cell.
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public void detachView(WindowView view, RendererType rendererType) {
        switch (rendererType) {
            case RENDERER_JME:
                ((AppCellRenderer) getCellRenderer(rendererType)).detachView(view);
                break;
            default:
                throw new RuntimeException("Unsupported cell renderer type: " + rendererType);
        }
    }

    /**
     * {@inheritDoc}
     */
    public GuiFactory getGuiFactory () {
        return gui2DFactory;
    }

    /**
     * Log this cell's scene graph.
     * <br>
     * FOR DEBUG. INTERNAL ONLY.
     */
    @InternalAPI
    public void logSceneGraph(RendererType rendererType) {
        switch (rendererType) {
            case RENDERER_JME:
                ((AppCellRenderer) getCellRenderer(rendererType)).logSceneGraph();
                break;
            default:
                throw new RuntimeException("Unsupported cell renderer type: " + rendererType);
        }
    }

    /**
     * Returns the string representation of this object.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(super.toString());
        buf.append(",app=[" + app + "]");
        return buf.toString();
    }
}
