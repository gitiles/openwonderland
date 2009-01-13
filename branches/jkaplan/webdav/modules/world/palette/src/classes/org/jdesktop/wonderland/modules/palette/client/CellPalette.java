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

import com.jme.math.Vector3f;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.registry.CellFactory;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState.Origin;

/**
 * A palette of cell types available to create in the world.
 * 
 * @author  Jordan Slott <jslott@dev.java.net>
 */
public class CellPalette extends javax.swing.JFrame implements ListSelectionListener {
    /* A map of cell display names and their cell factories */
    private Map<String, CellFactory> cellFactoryMap = new HashMap();
    
    /** Creates new form CellPalette */
    public CellPalette() {
        // Initialize the GUI components
        initComponents();
        
        // Listen for list selection events and update the preview panel with
        // the selected item's image
        cellList.addListSelectionListener(this);
    }

    @Override
    public void setVisible(boolean b) {
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
        cellList = new javax.swing.JList();
        createButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cell Palette");
        setName("cellFrame"); // NOI18N

        cellList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cellScrollPane.setViewportView(cellList);

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createActionPerformed(evt);
            }
        });

        jLabel1.setText("Select a Cell from the List and Click 'Create'");

        previewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        previewPanel.setMinimumSize(new java.awt.Dimension(128, 128));
        previewPanel.setPreferredSize(new java.awt.Dimension(128, 128));
        previewPanel.setLayout(new java.awt.GridLayout(1, 0));

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        previewLabel.setIconTextGap(0);
        previewPanel.add(previewLabel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(createButton)
                            .add(cellScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(previewPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cellScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(previewPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createActionPerformed

    // From the selected value, find the proper means to create the object
    String cellDisplayName = (String) cellList.getSelectedValue();
    CellFactory factory = getCellFactory(cellDisplayName);
    CellServerState setup = factory.getDefaultCellSetup();
    
    // Choose a random origin for now
    Vector3f origin = new Vector3f(new Random().nextInt(10) - 5,
            new Random().nextInt(5), new Random().nextInt(10) - 5);
    setup.setOrigin(new Origin(origin));
    
    // Send the message to the server
    WonderlandSession session = LoginManager.getPrimary().getPrimarySession();
    CellEditChannelConnection connection = (CellEditChannelConnection)session.getConnection(CellEditConnectionType.CLIENT_TYPE);
    CellCreateMessage msg = new CellCreateMessage(null, setup);
    connection.send(msg);
}//GEN-LAST:event_createActionPerformed



    /**
     * Updates the list of values displayed from the CellRegistry
     */
    private void updateListValues() {
        // Fetch the registry of cells and for each, get the palette info and
        // populate the list.
        CellRegistry registry = CellRegistry.getCellRegistry();
        Set<CellFactory> cellFactories = registry.getAllCellFactories();
        List<String> listNames = new LinkedList();
        Iterator<CellFactory> it = cellFactories.iterator();
        int i = 0;
        while (it.hasNext() == true) {
            CellFactory cellFactory = it.next();
            try {
                String name = cellFactory.getCellPaletteInfo().getDisplayName();
                listNames.add(name);
                cellFactoryMap.put(name, cellFactory);
            } catch (java.lang.Exception excp) {
                // Just ignore, but log a message
                Logger logger = Logger.getLogger(CellPalette.class.getName());
                logger.log(Level.WARNING, "[PALETTE] No Display Name for Cell " +
                        "Factory " + cellFactory, excp);
            }
        }
        cellList.setListData(listNames.toArray(new String[] {}));
        //cellList.setDragEnabled(true);        
    }

    /**
     * Returns the cell factory given its display name
     */
    private CellFactory getCellFactory(String name) {
        CellRegistry registry = CellRegistry.getCellRegistry();
        Set<CellFactory> cellFactories = registry.getAllCellFactories();
        Iterator<CellFactory> it = cellFactories.iterator();
        int i = 0;
        while (it.hasNext() == true) {
            CellFactory cellFactory = it.next();
            try {
                String cellName = cellFactory.getCellPaletteInfo().getDisplayName();
                if (cellName.equals(name) == true) {
                    return cellFactory;
                }
            } catch (java.lang.Exception excp) {
            }
        }
        return null;
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    
    /**
     * Handles when a selection has been made of the list of cell type names.
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {
        // Create a JLabel with the image, resized to be 128x128.
        String selectedName = (String)cellList.getSelectedValue();
        Logger logger = Logger.getLogger(CellPalette.class.getName());
        if (selectedName != null) {
            CellFactory cellFactory = cellFactoryMap.get(selectedName);
            if (cellFactory != null) {
                Image image = cellFactory.getCellPaletteInfo().getPreviewImage();
                if (image != null) {
                    ImageIcon icon = new ImageIcon(image);
                    previewLabel.setIcon(icon);
                    previewLabel.setText(null);
                }
                else {
                    previewLabel.setIcon(null);
                    previewLabel.setText("<html><center>No Preview<br>Available</center></html>");
                }
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList cellList;
    private javax.swing.JScrollPane cellScrollPane;
    private javax.swing.JButton createButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables

}
