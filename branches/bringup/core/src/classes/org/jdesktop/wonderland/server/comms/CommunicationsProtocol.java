/**
 * Project Wonderland
 *
 * $RCSfile:$
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision:$
 * $Date:$
 * $State:$
 */

package org.jdesktop.wonderland.server.comms;

import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import java.io.Serializable;
import org.jdesktop.wonderland.ExperimentalAPI;

/**
 * A communications protocol describes how a client communicates with the
 * Wonderland server.  When the client connects, it request a communications
 * protocol in the first packet it sends.  All subsequent packets are handled
 * by the SessionListener that is returned.
 * 
 * @author jkaplan
 */
@ExperimentalAPI
public interface CommunicationsProtocol extends Serializable {
    /**
     * Get the name of this protocol.  The protocol name must be unique,
     * and will be requested by clients.
     * @return the unique name of the protocol
     */
    public String getName();

    /**
     * Get the version of this protocol that is supported.  Right now, only
     * a single version of a given protocol is supported, but protocols may
     * be backwards compatible to support clients with older versions.
     * @return the version of the protocol
     */
    public ProtocolVersion getVersion();
    
    /**
     * Get the session listener associated with this protocol.
     * @param session the client we are creating a session for
     * @param version the protocol version the client is connecting with
     * @return the session manager that will be used to handle client sessions
     * from this manager.
     */
    public ClientSessionListener createSessionListener(ClientSession session,
                                                       ProtocolVersion version);
}
