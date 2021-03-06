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
package org.objectweb.proactive.core.descriptor.data;

import java.io.Serializable;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.xml.VariableContract;


/**
 * <p>
 * A <code>ProactiveDescriptor</code> is a representation of XML
 * Descriptor. It offers a set of services to access/activate/desactivate
 * <code>VirtualNode</code>.
 * </p>
 *
 *
 * @author The ProActive Team
 * @see VirtualNode
 * @see ProActiveDescriptorInternal
 * @see VirtualNodeInternal
 */
@PublicAPI
//@snippet-start proactivedescriptor
public interface ProActiveDescriptor extends Serializable {

    /**
     * This method can be used to access to the internal view of this
     * ProActiveDescriptor.
     *
     * If you are not a ProActive developper you shouldn't use this
     * method. ProActiveDescriptorInternal is not a public interface
     * and is not supported.
     *
     * @return the internal view of this ProActiveDescriptor
     */
    public ProActiveDescriptorInternal getProActiveDescriptorInternal();

    /**
     * Returns the Url of the pad
     * @return String in fact it is an identifire for the pad that is returned.
     * This identifier is build from the pad url appended with the pad's jobId.
     */
    public String getUrl();

    /**
     * Returns the descriptor's location
     * @return the location of the xml proactive descriptor file used.
     */
    public String getProActiveDescriptorURL();

    /**
     * Returns all VirtualNodes described in the XML Descriptor
     * @return VirtualNode[] all the VirtualNodes described in the XML Descriptor
     */
    public VirtualNode[] getVirtualNodes();

    /**
     * Returns the specified VirtualNode
     * @param name name of the VirtualNode
     * @return VirtualNode VirtualNode of the given name
     */
    public VirtualNode getVirtualNode(String name);

    /**
     * Activates all VirtualNodes defined in the XML Descriptor.
     */
    public void activateMappings();

    /**
     * Activates the specified VirtualNode defined in the XML Descriptor
     * @param virtualNodeName name of the VirtulNode to be activated
     */
    public void activateMapping(String virtualNodeName);

    /**
     * Kills all Nodes and JVMs(local or remote) created when activating the descriptor
     * @param softly if false, all jvms created when activating the descriptor are killed abruptely
     * if true a jvm that originates the creation of  a rmi registry waits until registry is empty before
     * dying. To be more precise a thread is created to ask periodically the registry if objects are still
     * registered.
     * @throws ProActiveException if a problem occurs when terminating all jvms
     */
    public void killall(boolean softly) throws ProActiveException;

    /**
     *
     * @return The current variable contract, or null.
     */
    public VariableContract getVariableContract();
}
//@snippet-end proactivedescriptor