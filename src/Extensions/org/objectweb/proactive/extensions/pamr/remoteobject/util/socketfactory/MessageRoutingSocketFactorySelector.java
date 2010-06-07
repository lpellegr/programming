/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.pamr.remoteobject.util.socketfactory;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.pamr.PAMRConfig;


/**
 * This class will instantiate the appropriate Socket Factory
 * according to user preferences
 *
 * @since ProActive 4.2.0
 */
public class MessageRoutingSocketFactorySelector {

    static final Logger logger = ProActiveLogger.getLogger(PAMRConfig.Loggers.FORWARDING_CLIENT_TUNNEL);

    /**
     * aliases for the Socket Factories provided with ProActive
     */
    public static MessageRoutingSocketFactorySPI get() {

        if (!PAMRConfig.PA_PAMR_SOCKET_FACTORY.isSet()) {
            // the user wants the default
            return getDefaultSocketFactory();
        }

        String socketFactory = PAMRConfig.PA_PAMR_SOCKET_FACTORY.getValue();

        Iterator<MessageRoutingSocketFactorySPI> socketFactories = ServiceRegistry
                .lookupProviders(MessageRoutingSocketFactorySPI.class);
        try {
            while (socketFactories.hasNext()) {
                MessageRoutingSocketFactorySPI factory = socketFactories.next();
                if (socketFactory.equals(factory.getAlias()) ||
                    socketFactories.equals(factory.getClass().getName())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Installing the " + factory.getAlias() + " (" +
                            factory.getClass().getName() + ") socket factory for message routing");
                    }
                    return factory;
                }
            }
        } catch (Error e) {
            logger.warn("Failed to load a service provider for " +
                MessageRoutingSocketFactorySPI.class.getName(), e);
        }

        logger
                .warn(socketFactory +
                    " is neither an alias for a socket factory provided with ProActive,\n" +
                    "   nor a class name for a socket factory which could be found using the service provider mechanisms.\n" +
                    "   Will instantiate the default, plain socket factory.");

        return getDefaultSocketFactory();
    }

    private static MessageRoutingSocketFactorySPI getDefaultSocketFactory() {
        return new MessageRoutingPlainSocketFactory();
    }
}
