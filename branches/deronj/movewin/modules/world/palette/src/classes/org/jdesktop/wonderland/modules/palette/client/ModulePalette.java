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
package org.jdesktop.wonderland.modules.palette.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.modules.ModuleUtils;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleList;

/**
 * A palette of cell types available to create in the world.
 * 
 * @author  Jordan Slott <jslott@dev.java.net>
 */
public class ModulePalette extends javax.swing.JFrame implements ListSelectionListener {

    private static Logger logger = Logger.getLogger(ModulePalette.class.getName());

    /* The list of modules present */
    private ModuleList modules = null;

    /* A map of module names and their artwork */
    private Map<String, Set<String>> moduleArtMap = new HashMap();

    /* The scalar distance from the camera to place new cells */
    private static final float NEW_CELL_DISTANCE = 5.0f;

    /** Creates new form CellPalette */
    public ModulePalette() {
        // Initialize the GUI components
        initComponents();

        // Listen for list selection events and update the preview panel with
        // the selected item's image
        moduleList.addListSelectionListener(this);
    }

    @Override
    public void setVisible(boolean b) {
        // Fetch the list of modules and update the visible list
        String serverURL = System.getProperty(JmeClientMain.SERVER_URL_PROP);
        modules = ModuleUtils.fetchModuleList(serverURL);
        if (modules == null) {
            JOptionPane.showMessageDialog(this,
                    "The system could not fetch the module information from " +
                    serverURL);
            return;
        }

        // Update the list values and set it visible or not
        updateListValues();
        super.setVisible(b);
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        cellScrollPane = new javax.swing.JScrollPane();
        moduleList = new javax.swing.JList();
        createButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cellScrollPane1 = new javax.swing.JScrollPane();
        artList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Module Art Palette");
        setName("cellFrame"); // NOI18N

        moduleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cellScrollPane.setViewportView(moduleList);

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Module:");

        artList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cellScrollPane1.setViewportView(artList);

        jLabel2.setText("Select Art Asset:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, createButton)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cellScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(cellScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cellScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 267, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cellScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 267, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createActionPerformed

    // From the selected value, find the proper means to create the cell, fetch
    // the cell factory.
    String moduleName = (String)moduleList.getSelectedValue();
    String artPath = (String) artList.getSelectedValue();
    CellFactorySPI factory = getCellFactory(artPath);
    if (factory == null) {
        logger.warning("Cannot find factory for asset " + artPath);
        return;
    }

    // Fetch the default server state, inject the content uri
    Properties props = new Properties();
    props.put("content-uri", "wla://" + moduleName + "/" + artPath);
    CellServerState setup = factory.getDefaultCellServerState(props);

    // Create the new cell at a distance away from the avatar
    try {
        CellUtils.createCell(setup, NEW_CELL_DISTANCE);
    } catch (CellCreationException excp) {
        logger.log(Level.WARNING, "Unable to create cell for artwork " + artPath, excp);
    }
}//GEN-LAST:event_createActionPerformed

    /**
     * Updates the list of values displayed from the CellRegistry
     */
    private void updateListValues() {
        // Fetch the registry of cells and for each, get the palette info and
        // populate the list.
        List<String> listNames = new LinkedList();
        for (ModuleInfo moduleInfo : modules.getModuleInfos()) {
            String moduleName = moduleInfo.getName();
            if (updateModuleArtList(moduleName) == true) {
                listNames.add(moduleInfo.getName());
            }
        }
        moduleList.setListData(listNames.toArray(new String[] {}));
    }

    /**
     * Updates the map of module art assets, if the module contains any art
     * assets. If so, returns true, if not, returns false.
     */
    private boolean updateModuleArtList(String moduleName) {
        // Fetch the list of modules art assets. If the map contains entries
        // then add to the map of module names and return true.
        String serverURL = System.getProperty(JmeClientMain.SERVER_URL_PROP);
        ChecksumList checksums = ModuleUtils.fetchAssetChecksums(serverURL, moduleName, "art");
        Map<String, Checksum> checksumMap = checksums.getChecksumMap();
        if (checksumMap != null && checksumMap.isEmpty() == false) {
            // For each asset name, string off the "art/" prefix
            Set<String> strippedNameSet = new HashSet();
            for (String assetName : checksumMap.keySet()) {
                strippedNameSet.add(assetName.substring(new String("art/").length()));
            }
            moduleArtMap.put(moduleName, strippedNameSet);
            return true;
        }
        return false;
    }

    /**
     * Returns the cell factory given the path of the asset
     */
    private CellFactorySPI getCellFactory(String name) {
        // First parse out the extension, return null if there is none
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        String extension = name.substring(index + 1);

        // Fetch and return the cell factory given the extension. There is a
        // set, so just take the first for now. Eventually, we'll have to show
        // a dialog asking which one they want to choose!
        CellRegistry registry = CellRegistry.getCellRegistry();
        Set<CellFactorySPI> factories = registry.getCellFactoriesByExtension(extension);
        if (factories != null) {
            return factories.toArray(new CellFactorySPI[] {})[0];
        }
        return null;
    }
    
    /**
     * Handles when a selection has been made of the list of cell type names.
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
        // For the selected module name, fetch the list of art assets in the
        // stored map and display them.
        String selectedName = (String)moduleList.getSelectedValue();
        if (selectedName != null) {
            Set<String> artAssets = moduleArtMap.get(selectedName);
            artList.setListData(artAssets.toArray(new String[]{}));
            return;
        }
        artList.setListData(new String[] {});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList artList;
    private javax.swing.JScrollPane cellScrollPane;
    private javax.swing.JScrollPane cellScrollPane1;
    private javax.swing.JButton createButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JList moduleList;
    // End of variables declaration//GEN-END:variables

}
