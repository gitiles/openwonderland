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
package org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket;

import java.math.BigInteger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A listener which is called when there is activity from a master or client socket.
 */
@ExperimentalAPI
public interface ClientSocketListener {

    /**
     * A message has arrived from client on the other end.
     * @param otherClientID The Wonderland ID of the client from whom the message has arrived.
     * @param buf The contents of the message.
     */
    public void receivedMessage(BigInteger otherClientID, byte[] buf);

    /**
     * The client on the other end has disconnected.
     * @param otherClientID The client who has disconnected.
     */
    public void otherClientHasLeft (BigInteger otherClientID);
}
