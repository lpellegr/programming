/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.webservices;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Implementation of the {@link ProActiveWSCaller} interface using the {@link http://cxf.apache.org/ CXF} API.
 *
 * @author The ProActive Team
 * @see ProActiveWSCaller
 */
@PublicAPI
public class CXFWSCaller implements ProActiveWSCaller {
    private Client client;

    public CXFWSCaller() {
    }

    public void setup(Class<?> serviceClass, String wsUrl) {
        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(serviceClass);
        factory.setAddress(wsUrl);
        client = factory.create();
    }

    public Object callWS(String methodName, Object[] args, Class<?> returnType) {
        if (client != null) {
            try {
                Object[] results = client.invoke(methodName, args);
                if (returnType == null) {
                    return null;
                } else {
                    return results[0];
                }
            } catch (Exception e) {
                logger.error("[CXF] Failed to invoke web service: " +
                    client.getEndpoint().getEndpointInfo().getAddress(), e);
                return null;
            }
        } else {
            logger.error("[CXF] Cannot invoke web service since the set up has not been done");
            return null;
        }
    }
}