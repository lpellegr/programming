/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.group.spmd;

import org.objectweb.proactive.core.group.MethodCallControlForGroup;


/**
 * @author The ProActive Team
 */
public class MethodCallBarrierWithMethodName extends MethodCallControlForGroup {

    private static final long serialVersionUID = 60L;
    private String[] methodNames;

    /**
     * Constructor
     * @param methodNames - the id name of the barrier
     */
    public MethodCallBarrierWithMethodName(String[] methodNames) {
        this.methodNames = methodNames;
    }

    /**
     * Returns the name of the method call
     * @return "MethodCallBarrierWithMethodName"
     */
    @Override
    public String getName() {
        return "MethodCallBarrierWithMethodName";
    }

    /**
     * Returns the names of the awaited methods
     * @return the names of the awaited methods;
     */
    public String[] getMethodNames() {
        return this.methodNames;
    }
}
