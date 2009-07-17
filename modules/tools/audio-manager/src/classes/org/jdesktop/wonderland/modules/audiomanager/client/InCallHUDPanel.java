/*
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

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatDialOutMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatHoldMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener.ChangeType;

import org.jdesktop.wonderland.client.softphone.SoftphoneControl;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.client.comms.WonderlandSession;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;

import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent.ComponentEventType;
import org.jdesktop.wonderland.client.hud.HUDComponentListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

import org.jdesktop.wonderland.modules.audiomanager.common.VolumeUtil;

/**
 *
 * @author  jp
 */
public class InCallHUDPanel extends javax.swing.JPanel implements PresenceManagerListener,
        MemberChangeListener, DisconnectListener {

    private static final Logger logger = Logger.getLogger(InCallHUDPanel.class.getName());
    private AudioManagerClient client;
    private WonderlandSession session;
    private PresenceManager pm;
    private PresenceInfo myPresenceInfo;
    private PresenceInfo caller;
    private DefaultListModel userListModel;
    private String group;
    private static int groupNumber;
    private static HashMap<String, InCallHUDPanel> inCallHUDPanelMap = new HashMap();
    private HUDComponent inCallHUDComponent;

    /** Creates new form InCallHUDPanel */
    public InCallHUDPanel() {
        initComponents();
    }

    public InCallHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, PresenceInfo caller) {

        this(client, session, myPresenceInfo, caller, null);
    }

    public InCallHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, PresenceInfo caller, String group) {

        this.client = client;
        this.session = session;
        this.myPresenceInfo = myPresenceInfo;
        this.caller = caller;

        initComponents();

        userListModel = new DefaultListModel();
        userList.setModel(userListModel);
        userList.setCellRenderer(new UserListCellRenderer());

        invitedMembers.add(myPresenceInfo);
        addToUserList(myPresenceInfo);

        if (caller.equals(myPresenceInfo) == false) {
            members.add(caller);
            addToUserList(caller);
        }

        hangupButton.setEnabled(false);

        pm = PresenceManagerFactory.getPresenceManager(session);

        pm.addPresenceManagerListener(this);

        client.addDisconnectListener(this);

        if (group == null) {
            group = caller.userID.getUsername() + "-" + groupNumber++;
        }

        this.group = group;

        inCallJLabel.setText("Call in progress: " + group);

        inCallHUDPanelMap.put(group, this);

        client.addMemberChangeListener(group, this);

        setVisible(true);
    }

    public void setCallHUDPanel(CallHUDPanel callHUDPanel) {
        this.callHUDPanel = callHUDPanel;
    }

    public void setHUDComponent(HUDComponent inCallHUDComponent) {
        this.inCallHUDComponent = inCallHUDComponent;

        inCallHUDComponent.addComponentListener(new HUDComponentListener() {

	    public void HUDComponentChanged(HUDComponentEvent e) {
                if (e.getEventType().equals(ComponentEventType.DISAPPEARED)) {
            	    session.send(client, new VoiceChatLeaveMessage(group, myPresenceInfo));
                }
            }
        });
    }

    public void callUser(String name, String number) {
       session.send(client, new VoiceChatJoinMessage(group, myPresenceInfo,
                new PresenceInfo[0], ChatType.PRIVATE));

        SoftphoneControl sc = SoftphoneControlImpl.getInstance();

        String callID = sc.getCallID();

        PresenceInfo info = new PresenceInfo(null, null, new WonderlandIdentity(name, name, null), callID);

        addToUserList(info);
        session.send(client, new VoiceChatDialOutMessage(group, callID, ChatType.PRIVATE, info, number));
    }

    public void inviteUsers(ArrayList<PresenceInfo> usersToInvite) {
        inviteUsers(usersToInvite, secretRadioButton.isSelected());
    }

    public void inviteUsers(ArrayList<PresenceInfo> usersToInvite, boolean isSecretChat) {
        for (PresenceInfo info : usersToInvite) {
            addToUserList(info);
            invitedMembers.add(info);
        }

        if (isSecretChat) {
            secretRadioButton.setSelected(true);
        } else {
            privateRadioButton.setSelected(true);
        }

        session.send(client, new VoiceChatJoinMessage(group, myPresenceInfo,
            usersToInvite.toArray(new PresenceInfo[0]),
            isSecretChat ? ChatType.SECRET : ChatType.PRIVATE));
    }

    public PresenceInfo getCaller() {
        return caller;
    }

    public String getGroup() {
        return group;
    }

    public HUDComponent getHUDComponent() {
        return inCallHUDComponent;
    }

    public static InCallHUDPanel getInCallHUDPanel(String group) {
        return inCallHUDPanelMap.get(group);
    }

    private void addToUserList(PresenceInfo info) {
        removeFromUserList(info);

        String name = NameTagNode.getDisplayName(info.usernameAlias,
                info.isSpeaking, info.isMuted);

        synchronized (userListModel) {
            userListModel.addElement(name);
        }
    }

    private void removeFromUserList(PresenceInfo info) {
        synchronized (userListModel) {
            String name = NameTagNode.getDisplayName(info.usernameAlias, false, false);
            userListModel.removeElement(name);

            name = NameTagNode.getDisplayName(info.usernameAlias, false, true);
            userListModel.removeElement(name);

            name = NameTagNode.getDisplayName(info.usernameAlias, true, false);
            userListModel.removeElement(name);

            name = NameTagNode.getDisplayName(info.usernameAlias, true, true);
            userListModel.removeElement(name);
        }
    }

    public void presenceInfoChanged(PresenceInfo presenceInfo, ChangeType type) {
        removeFromUserList(presenceInfo);

        if (members.contains(presenceInfo) == false &&
                invitedMembers.contains(presenceInfo) == false) {

            return;
        }

        if (type.equals(ChangeType.USER_REMOVED) == false) {
            addToUserList(presenceInfo);
        }
    }
    private ArrayList<PresenceInfo> members = new ArrayList();
    private ArrayList<PresenceInfo> invitedMembers = new ArrayList();

    public void setMemberList(PresenceInfo[] memberList) {
    }

    public void memberChange(PresenceInfo member, boolean added) {
        invitedMembers.remove(member);

        if (added == true) {
            members.add(member);
            addToUserList(member);
            return;
        }

        synchronized (members) {
            members.remove(member);
        }

        removeFromUserList(member);
    }

    public void disconnected() {
        inCallHUDPanelMap.remove(group);
        inCallHUDComponent.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        inCallJLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        secretRadioButton = new javax.swing.JRadioButton();
        privateRadioButton = new javax.swing.JRadioButton();
        addButton = new javax.swing.JButton();
        hangupButton = new javax.swing.JButton();
        speakerButton = new javax.swing.JButton();
        holdButton = new javax.swing.JButton();
        chatTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setRequestFocusEnabled(false);

        inCallJLabel.setFont(inCallJLabel.getFont().deriveFont(inCallJLabel.getFont().getStyle() | java.awt.Font.BOLD));
        inCallJLabel.setText("Call in progress: ");

        userList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(userList);

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText("Privacy:");

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setFont(secretRadioButton.getFont());
        secretRadioButton.setText("Secret");

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setFont(privateRadioButton.getFont());
        privateRadioButton.setText("Private");

        addButton.setText("Add User...");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        hangupButton.setText("Hang up");
        hangupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hangupButtonActionPerformed(evt);
            }
        });

        speakerButton.setText("Speaker");
        speakerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speakerButtonActionPerformed(evt);
            }
        });

        holdButton.setText("Hold");
        holdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                holdButtonActionPerformed(evt);
            }
        });

        chatTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatTextFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(jLabel3.getFont());
        jLabel3.setText("Chat:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(privateRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(secretRadioButton))
                            .add(inCallJLabel))
                        .add(7, 7, 7))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(chatTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(speakerButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(addButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(holdButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                            .add(hangupButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {hangupButton, holdButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(inCallJLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(privateRadioButton)
                    .add(secretRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(chatTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addButton)
                    .add(hangupButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(speakerButton)
                    .add(holdButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void userListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userListValueChanged
    setEnableHangupButton();
}//GEN-LAST:event_userListValueChanged
    private CallHUDPanel callHUDPanel;
    private HUDComponent callHUDComponent;

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    if (callHUDPanel != null) {
        callHUDComponent.setVisible(true);
        return;
    }

    callHUDPanel = new CallHUDPanel(client, session, myPresenceInfo, this);

    HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
    callHUDComponent = mainHUD.createComponent(callHUDPanel);

    callHUDPanel.setHUDComponent(callHUDComponent);

    //System.out.println("Call in progress x,y " + inCallHUDComponent.getX() + ", " + inCallHUDComponent.getY()
    //    + " width " + inCallHUDComponent.getWidth() + " height " + inCallHUDComponent.getHeight()
    //    + " Call x,y " + (inCallHUDComponent.getX() + inCallHUDComponent.getWidth())
    //    + ", " + (inCallHUDComponent.getY() + inCallHUDComponent.getHeight() - callHUDComponent.getHeight()));

    mainHUD.addComponent(callHUDComponent);
    callHUDComponent.addComponentListener(new HUDComponentListener() {

        public void HUDComponentChanged(HUDComponentEvent e) {
            if (e.getEventType().equals(ComponentEventType.DISAPPEARED)) {
            }
        }
    });

    callHUDComponent.setVisible(true);
    callHUDComponent.setLocation(inCallHUDComponent.getX() + inCallHUDComponent.getWidth(),
            inCallHUDComponent.getY() + inCallHUDComponent.getHeight() - callHUDComponent.getHeight());

    PropertyChangeListener plistener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent pe) {
            if (pe.getPropertyName().equals("ok") || pe.getPropertyName().equals("cancel")) {
                callHUDComponent.setVisible(false);
            }
        }
    };
    callHUDPanel.addPropertyChangeListener(plistener);
    callHUDComponent.setVisible(true);
}//GEN-LAST:event_addButtonActionPerformed

private void hangupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hangupButtonActionPerformed
    hangup();
}//GEN-LAST:event_hangupButtonActionPerformed

private void speakerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakerButtonActionPerformed
    changePrivacy(ChatType.PUBLIC);
}//GEN-LAST:event_speakerButtonActionPerformed

    private void changePrivacy(ChatType chatType) {
        ArrayList<PresenceInfo> membersInfo = getSelectedMembers();

        for (PresenceInfo info : membersInfo) {
            session.send(client, new VoiceChatJoinMessage(group, info, new PresenceInfo[0], chatType));
        }
    }
    private HoldHUDPanel holdHUDPanel;
    private HUDComponent holdHUDComponent;
    private boolean onHold = false;

private void holdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_holdButtonActionPerformed
    onHold = !onHold;

    hold(onHold);
}//GEN-LAST:event_holdButtonActionPerformed

private void chatTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_chatTextFieldActionPerformed

    private void hold(boolean onHold) {
        if (holdHUDPanel == null) {
            if (onHold == false) {
                return;
            }

            holdHUDPanel = new HoldHUDPanel(client, session, group, this);

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            holdHUDComponent = mainHUD.createComponent(holdHUDPanel);
            holdHUDComponent.setPreferredLocation(Layout.SOUTHWEST);

            mainHUD.addComponent(holdHUDComponent);
            holdHUDComponent.addComponentListener(new HUDComponentListener() {

                public void HUDComponentChanged(HUDComponentEvent e) {
                    if (e.getEventType().equals(ComponentEventType.DISAPPEARED)) {
                    }
                }
            });

            PropertyChangeListener plistener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent pe) {
                    if (pe.getPropertyName().equals("ok") || pe.getPropertyName().equals("cancel")) {
                        holdHUDComponent.setVisible(false);
                    }
                }
            };
            holdHUDPanel.addPropertyChangeListener(plistener);
        }

        holdHUDComponent.setVisible(onHold);

        inCallHUDComponent.setVisible(!onHold);
        setHold(onHold, 1);
    }

    public void setHold(boolean onHold, double volume) {
        this.onHold = onHold;

        try {
            session.send(client, new VoiceChatHoldMessage(group, myPresenceInfo, onHold,
                    VolumeUtil.getServerVolume(volume)));

            if (onHold == false) {
                holdOtherCalls();
            }

            inCallHUDComponent.setVisible(!onHold);
            holdHUDComponent.setVisible(onHold);
        } catch (IllegalStateException e) {
            hangup();
        }
    }

    public void holdOtherCalls() {
        InCallHUDPanel[] inCallHUDPanels = inCallHUDPanelMap.values().toArray(new InCallHUDPanel[0]);

        for (int i = 0; i < inCallHUDPanels.length; i++) {
            if (inCallHUDPanels[i] == this) {
                continue;
            }

            inCallHUDPanels[i].hold(true);
        }
    }

    private ArrayList<PresenceInfo> getSelectedMembers() {
        Object[] selectedValues = userList.getSelectedValues();

        ArrayList<PresenceInfo> membersInfo = new ArrayList();

        for (int i = 0; i < selectedValues.length; i++) {
            String usernameAlias = NameTagNode.getUsername((String) selectedValues[i]);

            PresenceInfo[] info = pm.getAliasPresenceInfo(usernameAlias);

            if (info == null || info.length == 0) {
                System.out.println("No presence info for " + (String) selectedValues[i]);
                continue;
            }

            membersInfo.add(info[0]);
        }

        return membersInfo;
    }

    private void setEnableHangupButton() {
        ArrayList<PresenceInfo> membersInfo = getSelectedMembers();

        for (PresenceInfo info : membersInfo) {
            /*
             * You can only select yourself or outworlders
             */
            if (info.clientID != null && myPresenceInfo.equals(info) == false) {
                hangupButton.setEnabled(false);
                return;
            }
        }

        hangupButton.setEnabled(true);
    }

    private void hangup() {
        ArrayList<PresenceInfo> membersInfo = getSelectedMembers();

        boolean hide = false;

        for (PresenceInfo info : membersInfo) {
            session.send(client, new VoiceChatLeaveMessage(group, info));
            if (info.equals(myPresenceInfo)) {
                hide = true;
            }
        }

        if (hide) {
            inCallHUDComponent.setVisible(false);
            inCallHUDPanelMap.remove(group);
        }
    }

    public void endHeldCall() {
        session.send(client, new VoiceChatLeaveMessage(group, myPresenceInfo));
        inCallHUDPanelMap.remove(group);
        inCallHUDComponent.setVisible(false);
    }

    private class UserListCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private Font font = new Font("SansSerif", Font.PLAIN, 13);

        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            String usernameAlias = NameTagNode.getUsername((String) value);

            PresenceInfo[] info = pm.getAliasPresenceInfo(usernameAlias);

            if (info == null || info.length == 0) {
                System.out.println("No presence info for " + usernameAlias);
                return renderer;
            }

            if (members.contains(info[0])) {
                renderer.setFont(font);
                renderer.setForeground(Color.BLACK);
            } else {
                renderer.setFont(font);
                renderer.setForeground(Color.BLUE);
            }
            return renderer;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField chatTextField;
    private javax.swing.JButton hangupButton;
    private javax.swing.JButton holdButton;
    private javax.swing.JLabel inCallJLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JButton speakerButton;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
