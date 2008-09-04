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

package org.jdesktop.wonderland.modules;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * A module's repository information, as represented by this class, represents
 * the collection of art assets and where they can be found over the Internet.
 * <p>
 * A "repository" is a collection of art assets located somewhere on the
 * Internet and available to clients for download. Two fundamental kinds of
 * repositories exist: master and mirror. Master repositories represent the
 * primary copy of art assets; mirror repositories represent copies of the
 * art assets, available perhaps by a server that provides quicker downloads.
 * <p>
 * This class stores a list of "resources", each some piece of art generally.
 * This list is optional -- entries need not exist for resources that exist
 * themselves in the module. This list, therefore, provides a means to include
 * a resource in the module without including the actual artwork itself.
 * <p>
 * This class also stores the name of the master repository where the artwork
 * can be downloaded and also a list of mirror repositories. Both the master
 * and mirror repositories are optional. If no master or mirror is specified,
 * then it is assumed the artwork is made available by the Wonderland server
 * in which the module is installed (if use_server is not false).
 * <p>
 * If an entry contains the special string %WL_SERVER% then the hostname of
 * the machine on which the module is installed is inserted before send to
 * the client. This special tag can be use as either the master or any one of
 * the mirror repositories.
 * <p>
 * By default, the hostname of the machine on which the module is installed is
 * inserted as the final mirror, in case other repositories cannot be found. This
 * happens only if no other entry contains the %WL_SERVER% tag and if the
 * use_server attribute is not set to false.
 * <p>
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="module-repository")
public class ModuleRepository implements Serializable {
    /*
     * The special string that denotes the Wonderland server from which the
     * module was installed should be used.
     */
    public static final String WL_SERVER = "%WLSERVER%";
    
    /*
     * An attribute saying whether or not to insert the server at the end of
     * the list of mirrors if it does not already exist in the list of asset
     * servers somewhere.
     */
    @XmlAttribute(name="server_fallback")
    private boolean useServer = true;
    
    /* An array of module resources, as relative paths within the module */
    @XmlElements({
        @XmlElement(name="resource")
    })
    private String[] resources = null;
    
    /* The hostname of the master asset server for this repository */
    @XmlElement(name="master") private String master = null;
  
    /* An array of hostnames that serve as mirrors for serving the assets */
    @XmlElements({
        @XmlElement(name = "mirror")
    })
    private String[] mirrors   = null;
    
    /* The XML marshaller and unmarshaller for later use */
    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;
    
    /* Create the XML marshaller and unmarshaller once for all ModuleRepositorys */
    static {
        try {
            JAXBContext jc = JAXBContext.newInstance(ModuleRepository.class);
            ModuleRepository.unmarshaller = jc.createUnmarshaller();
            ModuleRepository.marshaller = jc.createMarshaller();
            ModuleRepository.marshaller.setProperty("jaxb.formatted.output", true);
        } catch (javax.xml.bind.JAXBException excp) {
            System.out.println(excp.toString());
        }
    }
    
    /** Default constructor */
    public ModuleRepository() {}
    
    /** Constructor that takes an existing ModuleRepository and makes a copy */
    public ModuleRepository(ModuleRepository repository) {
        this.useServer = repository.isUseServer();
        this.master = (repository.getMaster() != null) ? new String(repository.getMaster()) : null;
        this.mirrors = (repository.getMirrors() != null) ? new String[repository.getMirrors().length] : null;
        if (this.mirrors != null) {
            for (int i = 0; i < this.mirrors.length; i++) {
                this.mirrors[i] = mirrors[i];
            }
        }
        this.resources = (repository.getResources() != null) ? new String[repository.getResources().length] : null;
        if (this.resources != null) {
            for (int i = 0; i < this.resources.length; i++) {
                this.resources[i] = resources[i];
            }
        }
    }
    
    /* Setters and getters */
    @XmlTransient public String[] getResources() { return this.resources; }
    public void setResources(String[] resources) { this.resources = resources; }
    @XmlTransient public String getMaster() { return this.master; }
    public void setMaster(String master) { this.master = master; }
    @XmlTransient public String[] getMirrors() { return this.mirrors; }
    public void setMirrors(String[] mirrors) { this.mirrors = mirrors; }
    @XmlTransient public boolean isUseServer() { return this.useServer; }
    public void setUseServer(boolean useServer) { this.useServer = useServer; }
    
        /**
     * Returns the version as a string: <major>.<minor>
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Module Repository:\n");
        str.append("Use Server: " + this.isUseServer());
        str.append("Master:\n  " + this.getMaster() + "\n");
        str.append("Mirrors:\n");
        if (this.mirrors != null) {
            for (String mirror : mirrors) {
                str.append("  " + mirror + "\n");
            }
        }
        if (this.resources != null) {
            str.append("Resources:\n");
            for (String resource : resources) {
                str.append("  " + resource + "\n");
            }
        }
        return str.toString();
    }
     
    /**
     * Takes the input reader of the XML file and instantiates an instance of
     * the ModuleRepository class
     * <p>
     * @param r The input stream of the version XML file
     * @throw ClassCastException If the input file does not map to ModuleRepository
     * @throw JAXBException Upon error reading the XML file
     */
    public static ModuleRepository decode(Reader r) throws JAXBException {
        return (ModuleRepository)ModuleRepository.unmarshaller.unmarshal(r);        
    }
    
    /**
     * Writes the ModuleRepository class to an output writer.
     * <p>
     * @param w The output writer to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(Writer w) throws JAXBException {
        ModuleRepository.marshaller.marshal(this, w);
    }

    /**
     * Writes the ModuleRepository class to an output stream.
     * <p>
     * @param os The output stream to write to
     * @throw JAXBException Upon error writing the XML file
     */
    public void encode(OutputStream os) throws JAXBException {
        ModuleRepository.marshaller.marshal(this, os);
    }
    
    /**
     * Main method which writes a sample WFSVersion class to disk
     */
    public static void main(String args[]) {
        try {
            ModuleRepository rep = new ModuleRepository();
            rep.setMaster("http://www.arts.com/");
            rep.setMirrors(new String[] { "http://www.foo.com" });
            rep.setResources(new String[] { "mpk20/models/building.j3s.gz"});
            rep.encode(new FileWriter(new File("/Users/jordanslott/module-wlm/repository.xml")));
        } catch (java.lang.Exception excp) {
            System.out.println(excp.toString());
        }
    }
}
