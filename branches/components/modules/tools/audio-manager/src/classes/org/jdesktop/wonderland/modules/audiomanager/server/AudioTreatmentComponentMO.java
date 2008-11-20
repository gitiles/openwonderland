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
package org.jdesktop.wonderland.modules.audiomanager.server;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.TaskManager;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.setup.CellComponentSetup;

import org.jdesktop.wonderland.server.TimeManager;
import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;

import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentMessage;

import com.sun.voip.client.connector.CallStatus;
import com.sun.voip.client.connector.CallStatusListener;

import com.sun.voip.CallParticipant;

import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.VoiceManager;

/**
 *
 * @author jprovino
 */
public class AudioTreatmentComponentMO extends CellComponentMO implements CallStatusListener {

    private static final Logger logger =
            Logger.getLogger(AudioTreatmentComponentMO.class.getName());

    private ManagedReference<ChannelComponentMO> channelComponentRef = null;
    
    private String treatment;
    private String groupId;
    private double lowerLeftY;
    private double lowerLeftZ;
    private double upperRightX;
    private double upperRightY;
    private double upperRightZ;

    /**
     * Create a AudioTreatmentComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public AudioTreatmentComponentMO(CellMO cell) {
        super(cell);
        
        ChannelComponentMO channelComponent = (ChannelComponentMO) 
	    cell.getComponent(ChannelComponentMO.class);

        if (channelComponent==null) {
            throw new IllegalStateException("Cell does not have a ChannelComponent");
	}

        channelComponentRef = AppContext.getDataManager().createReference(channelComponent); 
                
        channelComponent.addMessageReceiver(AudioTreatmentMessage.class, new ComponentMessageReceiverImpl(this));
    }
    
    @Override
    public void setupCellComponent(CellComponentSetup setup) {
	AudioComponentCellSetup accs = (AudioComponentCellSetup) setup;

	treatment = sccs.getTreatment();
	groupId = sccs.getGroupId();

	logger.warning("setup:  treatment=" + treatment + " groupId " + groupId);
    }

    @Override
    public void setLive(boolean live) {
	/*
	 * Get the treatment group and list of treatments.
	 */
/*
	String groupId = 

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	TreatmentGroup group = vm.createTreatmentGroup(groupId);

	String[] treatments =

	for (int i = 0; i < treatments.size; i++) {
	    TreatmentSetup setup = new TreatmentSetup();

	    setup.treatment = treatments[i];
	    
	    set the rest of the parameters

	    Treatment treatment = vm.createTreatment(treatments[i], setup);

	    group.addTreatment(treatment);
	}
*/
    }
    
    private static class ComponentMessageReceiverImpl implements ComponentMessageReceiver {

        private ManagedReference<AudioTreatmentComponentMO> compRef;
        
        public ComponentMessageReceiverImpl(AudioTreatmentComponentMO comp) {
            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender, ClientSession session, 
		CellMessage message) {

            AudioTreatmentMessage msg = (AudioTreatmentMessage) message;
        }
    }

    public void callStatusChanged(CallStatus callStatus) {
	String callId = callStatus.getCallId();

	switch (callStatus.getCode()) {
	case CallStatus.ESTABLISHED:
	    break;

	case CallStatus.TREATMENTDONE:
	    break;
	}

    }

}
