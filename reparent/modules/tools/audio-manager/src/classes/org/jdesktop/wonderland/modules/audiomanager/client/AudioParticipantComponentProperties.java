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
package org.jdesktop.wonderland.modules.audiomanager.client;

import javax.swing.JPanel;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioParticipantComponentServerState;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;

/**
 *
 * @author  jp
 */
@PropertiesFactory(AudioParticipantComponentServerState.class)
public class AudioParticipantComponentProperties extends javax.swing.JPanel
        implements PropertiesFactorySPI {

    /** Creates new form AudioParticipantComponentProperties */
    public AudioParticipantComponentProperties() {
        initComponents();
    }

    /**
     * @{inheritDoc}
     */
    public String getDisplayName() {
        return "Audio Participant";
    }

    /**
     * @{inheritDoc}
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @{inheritDoc}
     */
    public void apply() {
        // Do nothing
    }

    /**
     * @{inheritDoc}
     */
    public void close() {
        // Do nothing
    }

    /**
     * @{inheritDoc}
     */
    public void open() {
        // Do nothing
    }

    /**
     * @{inheritDoc}
     */
    public void restore() {
        // Do nothing
    }

    /**
     * @{inheritDoc}
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        // Do nothing
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
