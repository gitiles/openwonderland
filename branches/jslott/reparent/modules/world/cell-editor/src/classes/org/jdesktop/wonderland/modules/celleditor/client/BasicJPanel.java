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
package org.jdesktop.wonderland.modules.celleditor.client;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * A property sheet to edit the basic attributes of a cell
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class BasicJPanel extends JPanel implements PropertiesFactorySPI {

    private CellPropertiesEditor editor = null;
    private String originalCellName = null;

    /** Creates new form BasicJPanel */
    public BasicJPanel() {
        initComponents();

        // Listen for changes in the entry for the text field
        cellNameTextField.getDocument().addDocumentListener(new NameTextFieldListener());
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Update the server-side state for the Cell.
        String name = cellNameTextField.getText();
        CellServerState cellServerState = editor.getCellServerState();
        ((CellServerState)cellServerState).setName(name);
        editor.addToUpdateList(cellServerState);
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // We do nothing here, since any changes in the GUI property sheet do
        // not take effect until apply(), so there is no state to revert here.
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return "Basic";
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @inheritDoc()
     */
    public void refresh() {
        // Fetch the name and CellID from the Cell and Cell server state and
        // update the GUI
        Cell cell = editor.getCell();
        CellServerState cellServerState = editor.getCellServerState();

        if (cellServerState != null) {
            originalCellName = cellServerState.getName();
            cellNameTextField.setText(originalCellName);
            cellIDLabel.setText(cell.getCellID().toString());
        }
    }

    /**
     * @inheritDoc()
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class NameTextFieldListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void removeUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void changedUpdate(DocumentEvent e) {
            checkDirty();
        }

        private void checkDirty() {
            String name = cellNameTextField.getText();
            if (editor != null && name.equals(originalCellName) == false) {
                editor.setPanelDirty(BasicJPanel.class, true);
            }
            else if (editor != null) {
                editor.setPanelDirty(BasicJPanel.class, false);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cellIDLabel = new javax.swing.JLabel();
        cellNameTextField = new javax.swing.JTextField();

        jLabel1.setText("Cell ID:");

        jLabel2.setText("Cell Name:");

        cellIDLabel.setText("<none>");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(35, 35, 35)
                        .add(cellIDLabel))
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cellNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cellIDLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cellNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(398, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cellIDLabel;
    private javax.swing.JTextField cellNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
