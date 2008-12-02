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
package org.jdesktop.wonderland.runner;

/**
 * An exception that is thrown when there is a problem configuring a runner.
 * @author jkaplan
 */
public class RunnerConfigurationException extends RunnerException {

    /**
     * Creates a new instance of <code>RunnerConfigurationException</code> 
     * without detail message or cause.
     */
    public RunnerConfigurationException() {
    }

    /**
     * Constructs an instance of <code>RunnerConfigurationException</code> with
     * the specified detail message.
     * @param msg the detail message.
     */
    public RunnerConfigurationException(String msg) {
        super (msg);
    }
    
    /**
     * Constructs an instance of <code>RunnerConfigurationException</code> with
     * the specified cause.
     * @param cause the root cause.
     */
    public RunnerConfigurationException(Throwable cause) {
        super (cause);
    }
    
    /**
     * Constructs an instance of <code>RunnerConfigurationException</code> with
     * the specified detail message and cause.
     * @param msg the detail message.
     * @param cause the root cause.
     */
    public RunnerConfigurationException(String msg, Throwable cause) {
        super (msg, cause);
    }
}
