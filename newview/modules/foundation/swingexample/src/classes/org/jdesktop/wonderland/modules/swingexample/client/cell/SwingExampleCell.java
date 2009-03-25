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
package org.jdesktop.wonderland.modules.swingexample.client.cell;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCell;
import org.jdesktop.wonderland.modules.swingexample.common.cell.SwingExampleCellClientState;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.swingexample.client.SwingExampleApp;
import org.jdesktop.wonderland.modules.swingexample.client.SwingExampleWindow;

/**
 * Client cell for the swing example.
 *
 * @author deronj
 */
@ExperimentalAPI
public class SwingExampleCell extends App2DCell {

    /** The logger used by this class */
    private static final Logger logger = Logger.getLogger(SwingExampleCell.class.getName());
    /** The (singleton) window created by the Swing example app */
    private SwingExampleWindow window;
    /** The cell client state message received from the server cell */
    private SwingExampleCellClientState clientState;

    /**
     * Create an instance of SwingExampleCell.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public SwingExampleCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
    }

    /**
     * Initialize the cell with parameters from the server.
     *
     * @param state the client state with which initialize the cell.
     */
    @Override
    public void setClientState(CellClientState state) {
        super.setClientState(state);
        clientState = (SwingExampleCellClientState) state;
    }

    /**
     * This is called when the status of the cell changes.
     */
    @Override
    public boolean setStatus(CellStatus status) {
        boolean ret = super.setStatus(status);

        switch (status) {

            // The cell is now visible
            case ACTIVE:

                SwingExampleApp stApp = new SwingExampleApp("Swing Example", clientState.getPixelScale());
                setApp(stApp);

                // Tell the app to be displayed in this cell.
                stApp.addDisplayer(this);

                // This app has only one window, so it is always top-level
                try {
                    window = new SwingExampleWindow(this, stApp, clientState.getPreferredWidth(), 
                                                 clientState.getPreferredHeight(), 
                                                 /*TODO: until debugged: true*/ false, pixelScale);
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                }

                // Both the app and the user want this window to be visible
                window.setVisibleApp(true);
                window.setVisibleUser(this, true);
                break;

            // The cell is no longer visible
            case DISK:
                window.setVisibleApp(false);
                window = null;
                break;
        }

        return ret;
    }
}
