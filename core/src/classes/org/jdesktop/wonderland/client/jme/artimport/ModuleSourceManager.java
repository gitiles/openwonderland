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
package org.jdesktop.wonderland.client.jme.artimport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paulby
 */
public class ModuleSourceManager {

    private ModuleSourceManager() {

    }

    /**
     * Create a module with the given name in the specified directory
     * @param moduleName
     * @param parentDirectory
     */
    public static void createModule(String moduleName, File parentDirectory, boolean includeArt) {
        File moduleDir = new File(parentDirectory.getAbsolutePath()+File.separatorChar+moduleName);
        if (moduleDir.exists())
            throw new RuntimeException("Module Directory already exists");

        moduleDir.mkdir();

        String srcPkg = "org.jdesktop.wonderland.modules."+moduleName;

        String srcPkgDir = ("src.classes."+srcPkg).replaceAll("\\.", File.separator);

        File clientSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"client");
        clientSrc.mkdirs();

        File commonSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"common");
        commonSrc.mkdirs();

        File serverSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"server");
        serverSrc.mkdirs();

        // Copy build.xml and my.build.properties
        File myBuildProp = new File(moduleDir.getAbsolutePath()+File.separatorChar+"my.module.properties");
        File buildXML = new File(moduleDir.getAbsolutePath()+File.separatorChar+"build.xml");

        try {
            copyFile(ModuleSourceManager.class.getClassLoader().getResourceAsStream("org/jdesktop/wonderland/client/jme/artimport/resources/module_build_template.xml"),
                    new FileOutputStream(buildXML),
                    "@#@",
                    includeArt ? "" : "#");
            copyFile(ModuleSourceManager.class.getClassLoader().getResourceAsStream("org/jdesktop/wonderland/client/jme/artimport/resources/module_properties_template.xml"),
                    new FileOutputStream(myBuildProp),
                    "@MODULE_NAME@",
                    moduleName);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Copy and update default nb project
        File nbProjDir = new File(moduleDir.getAbsolutePath()+File.separatorChar+"nbproject");
        nbProjDir.mkdirs();
        File nbProj = new File(nbProjDir.getAbsolutePath()+File.separatorChar+"project.xml");

        try {
            copyFile(ModuleSourceManager.class.getClassLoader().getResourceAsStream("org/jdesktop/wonderland/client/jme/artimport/resources/module_nbproject_template.xml"),
                    new FileOutputStream(nbProj),
                    "@MODULE_NAME@",
                    moduleName);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void copyFile(InputStream inFile, OutputStream outFile, String replace, String replaceWith) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inFile));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile));

        String line = null;

        try {
            do {
                line = reader.readLine();
                if (line != null) {
                    if (replace!=null) {
                        line = line.replaceAll(replace, replaceWith);
                    }

                    writer.write(line);
                    writer.newLine();
                }

            } while(line!=null);

            reader.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
