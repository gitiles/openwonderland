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
package org.jdesktop.wonderland.modules.audiomanager.client;

import javax.swing.JMenuItem;
import org.jdesktop.wonderland.modules.audiomanager.client.AudioMenuListener;

/**
 *
 * @author paulby
 */
public class AudioMenu extends javax.swing.JPanel {

    private AudioMenuListener audioMenuListener;
    private static AudioMenu audioM=null;

    /** Creates new form AudioMenu */
    AudioMenu(AudioMenuListener audioMenuListener) {
        initComponents();
        this.audioMenuListener = audioMenuListener;
    }

    public static JMenuItem getAudioMenu(AudioMenuListener listener) {
        if (audioM==null)
            audioM = new AudioMenu(listener);

        return audioM.audioMenu;
    }

    public static void updateSoftphoneCheckBoxMenuItem(boolean selected) {
        audioM.softphoneMenuItem.setSelected(selected);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        audioMenu = new javax.swing.JMenu();
        softphoneMenuItem = new javax.swing.JCheckBoxMenuItem();
        testAudioMenuItem = new javax.swing.JMenuItem();
        reconnectSoftphoneMenuItem = new javax.swing.JMenuItem();
        transferCallMenuItem = new javax.swing.JMenuItem();
        logAudioProblemMenuItem = new javax.swing.JMenuItem();

        audioMenu.setText("Audio");
        audioMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioMenuActionPerformed(evt);
            }
        });

        softphoneMenuItem.setText("Softphone");
        softphoneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                softphoneMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(softphoneMenuItem);

        testAudioMenuItem.setText("Test Audio");
        testAudioMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testAudioMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(testAudioMenuItem);

        reconnectSoftphoneMenuItem.setText("Reconnect Softphone");
        reconnectSoftphoneMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconnectSoftphoneMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(reconnectSoftphoneMenuItem);

        transferCallMenuItem.setText("Transfer Call");
        transferCallMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transferCallMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(transferCallMenuItem);

        logAudioProblemMenuItem.setText("Log Audio Problem");
        logAudioProblemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logAudioProblemMenuItemActionPerformed(evt);
            }
        });
        audioMenu.add(logAudioProblemMenuItem);

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

    private void softphoneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_softphoneMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.showSoftphone(softphoneMenuItem.isSelected());
        }
}//GEN-LAST:event_softphoneMenuItemActionPerformed

    private void testAudioMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testAudioMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.testAudio();
        }
}//GEN-LAST:event_testAudioMenuItemActionPerformed

    private void reconnectSoftphoneMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconnectSoftphoneMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.reconnectSoftphone();
        }
}//GEN-LAST:event_reconnectSoftphoneMenuItemActionPerformed

    private void transferCallMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transferCallMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.transferCall();
        }
}//GEN-LAST:event_transferCallMenuItemActionPerformed

    private void logAudioProblemMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logAudioProblemMenuItemActionPerformed
        if (audioMenuListener != null) {
            audioMenuListener.logAudioProblem();
        }
}//GEN-LAST:event_logAudioProblemMenuItemActionPerformed

    private void audioMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioMenuActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_audioMenuActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu audioMenu;
    private javax.swing.JMenuItem logAudioProblemMenuItem;
    private javax.swing.JMenuItem reconnectSoftphoneMenuItem;
    private javax.swing.JCheckBoxMenuItem softphoneMenuItem;
    private javax.swing.JMenuItem testAudioMenuItem;
    private javax.swing.JMenuItem transferCallMenuItem;
    // End of variables declaration//GEN-END:variables

}
