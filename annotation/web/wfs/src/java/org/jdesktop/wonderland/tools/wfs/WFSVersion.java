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
package org.jdesktop.wonderland.tools.wfs;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The WFSVersion class represents a version of the Wonderland File System. Note
 * that this version reflect the WFS architecture specification version defining
 * the structure of the file system, etc. Each individual cell has a separate
 * version number too. The WFS version consists of a 'major' and 'minor' version
 * number, both integers.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wfs-version")
public class WFSVersion {
    /* The major and minor version numbers */
    @XmlElement(name="major") private int major;
    @XmlElement(name="minor") private int minor;

    /* The XML marshaller and unmarshaller for later use */
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleInfos */
    static {
        try {
            JAXBContext jc = JAXBContext.newInstance(WFSVersion.class);
            WFSVersion.unmarshaller = jc.createUnmarshaller();
            WFSVersion.marshaller = jc.createMarshaller();
            WFSVersion.marshaller.setProperty("jaxb.formatted.output", true);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    /** Default constructor */
    public WFSVersion() {}
    
    /** Constructor which takes major/minor version number */
    public WFSVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }
    
    /* Java Bean Setter/Getter methods */
    @XmlTransient public int getMajor() { return this.major; }
    public void setMajor(int major) { this.major = major; }
    @XmlTransient public int getMinor() { return this.minor; }
    public void setMinor(int minor) { this.minor = minor; }
    
    /**
     * Returns the version as a string: <major>.<minor>
     */
    @Override
    public String toString() {
        return Integer.toString(this.getMajor()) + "." + Integer.toString(this.getMinor());
    }
    
    /**
     * Writes the WFSVersion class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        WFSVersion.marshaller.marshal(this, w);
    }
    
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the WFSVersion class
     * <p>
     * @param r The input reader of the version XML file
     * @throw ClassCastException If the input file does not map to WFSVersion
     * @throw JAXBException Upon error reading the XML file
     */
    public static WFSVersion decode(Reader r) throws JAXBException {
        return (WFSVersion) WFSVersion.unmarshaller.unmarshal(r);
    }
    
    /**
     * Main method which writes a sample WFSVersion class to disk
     */
    public static void main(String args[]) {
        try {
            WFSVersion version = new WFSVersion();
            version.setMajor(5);
            version.setMinor(3);
            version.encode(new FileWriter(new File("version.xml")));
        } catch (java.lang.Exception excp) {
            System.out.println(excp.toString());
        }
    }
}
