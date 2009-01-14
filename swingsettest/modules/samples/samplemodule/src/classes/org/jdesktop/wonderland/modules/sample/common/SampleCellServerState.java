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
package org.jdesktop.wonderland.modules.sample.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.spi.CellServerStateSPI;

/**
 * Represents the server-side configuration information for the sample cell.
 * Has JAXB annotations so that it can be serialized to XML. Note that the
 * "info" field is not really used anywhere, it is just an example of how
 * to use the server state and JAXB annotations.
 *
 * @author jkaplan
 */
@XmlRootElement(name="sample-cell")
public class SampleCellServerState extends CellServerState implements CellServerStateSPI {
    @XmlElement(name="info")
    private String info = null;

    /** Default constructor */
    public SampleCellServerState() {
    }

    @Override
    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.sample.server.SampleCellMO";
    }

    @XmlTransient public String getInfo() { return this.info; }
    public void setInfo(String info) { this.info = info; }
}
