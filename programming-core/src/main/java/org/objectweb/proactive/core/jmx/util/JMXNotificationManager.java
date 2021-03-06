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
package org.objectweb.proactive.core.jmx.util;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.client.ClientConnector;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class is an utility class.
 * It gives you the possibility to register/unregister a listener to the notifications of a remote JMX MBean.
 * When an active object migrates, the notificationManager subscribes to the new JMX MBean server, and send you the notifications.
 *
 * @author The ProActive Team
 * @version 07/28/2007
 * @see org.objectweb.proactive.core.jmx.ProActiveConnection
 * @see org.objectweb.proactive.core.jmx.client.ClientConnector
 * @see org.objectweb.proactive.core.jmx.server.ServerConnector
 */
public class JMXNotificationManager implements NotificationListener {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.JMX);

    // --- Variables ---
    /**
     * To find the listeners for a given ObjectName
     */
    private Map<ObjectName, ConcurrentLinkedQueue<NotificationListener>> allListeners;

    /**
     * To find the JMX Connection for a given ObjectName
     * (Several ObjectNames can have the same connection)
     */
    private Map<ObjectName, Connection> connectionsWithObjectName;

    /**
     * To find the JMX Connection for a given runtime URL
     * (A runtime has an unique connection)
     */
    private Map<String, Connection> connectionsWithRuntimeUrl;

    // Singleton
    private static JMXNotificationManager instance;

    /**
     * The active object listener of all notifications.
     */
    private JMXNotificationListener notificationlistener;

    private JMXNotificationManager() {
        allListeners = new ConcurrentHashMap<ObjectName, ConcurrentLinkedQueue<NotificationListener>>();
        connectionsWithObjectName = new ConcurrentHashMap<ObjectName, Connection>();
        connectionsWithRuntimeUrl = new ConcurrentHashMap<String, Connection>();

        try {
            // Initalise the JMXNotificationListener which is an active object listening all needed MBeans
            this.notificationlistener = PAActiveObject.newActive(JMXNotificationListener.class,
                    new Object[] {});
        } catch (ActiveObjectCreationException e) {
            logger.error("Can't create the JMX notifications listener active object", e);
        } catch (NodeException e) {
            logger.error("Can't create the JMX notifications listener active object", e);
        }
    }

    /**
     * Returns the unique instance of the JMXNotificationManager
     * @return Returns the unique instance of the JMXNotificationManager
     */
    public synchronized static JMXNotificationManager getInstance() {
        if (instance == null) {
            instance = new JMXNotificationManager();
        }
        return instance;
    }

    /**
     * Subscribes a notification listener to a <b>LOCAL</b> JMX MBean Server.
     * @param objectName The name of the MBean on which the listener should
     * be added.
     * @param listener The listener object which will handle the
     * notifications emitted by the registered MBean.
     */
    public void subscribe(ObjectName objectName, NotificationListener listener) {
        subscribe(objectName, listener, (NotificationFilter) null, null);
    }

    /**
     * Subscribes a notification listener to a <b>LOCAL</b> JMX MBean Server.
     * @param name The name of the MBean on which the listener should
     * be added.
     * @param listener The listener object which will handle the
     * notifications emitted by the registered MBean.
     * @param filter The filter object. If filter is null, no
     * filtering will be performed before handling notifications.
     * @param handback The context to be sent to the listener when a
     * notification is emitted.
     */
    public void subscribe(ObjectName objectName, NotificationListener listener, NotificationFilter filter,
            Object handback) {
        try {
            ManagementFactory.getPlatformMBeanServer().addNotificationListener(objectName, listener, filter,
                    handback);
        } catch (InstanceNotFoundException e) {
            logger.error("The objectName: " + objectName + " cooresponds tot none registered MBeans", e);
        }
    }

    /**
     * Subscribes a notification listener to a <b>REMOTE</b> JMX MBean Server.
     * @param objectName The object name of the MBean.
     * @param listener The notification listener.
     * @param runtimeUrl The url of the remote ProActiveRuntime where the MBean is located
     */
    public void subscribe(ObjectName objectName, NotificationListener listener, String runtimeUrl)
            throws IOException {
        // Search if this objectName is already listened
        ConcurrentLinkedQueue<NotificationListener> listeners = allListeners.get(objectName);

        // The MBean with this objectName is not listened
        if (listeners == null) {
            listeners = new ConcurrentLinkedQueue<NotificationListener>();
            allListeners.put(objectName, listeners);

            // Search if we are already connected to this JMX Server connector
            Connection connection = connectionsWithRuntimeUrl.get(runtimeUrl);
            if (connection == null) {
                // Create a new connection
                connection = new Connection(runtimeUrl);

                // Store the connection in order to be able to close the connection later
                connectionsWithRuntimeUrl.put(runtimeUrl, connection);
            }

            // Add the objectName to the established connection
            //if the object name is not already monitored through the connection
            if (!connection.objectNames.contains(objectName)) {
                connection.addObjectName(objectName);
                // Subscribes the JMXNotificationManager to the notifications of this MBean.
                // this.notificationlistener.subscribe(connection.getConnection(),
                //    objectName, null, null);
                this.subscribeObjectToRemoteMBean(connection.getConnection(), objectName, null, null);
                // Updates our map
                connectionsWithObjectName.put(objectName, connection);
            } //if not contains
        }

        // Add the listener to the set of listeners to notify
        listeners.add(listener);
    }

    /**
     * Unsubscribes a notification listener to a local OR remote JMX MBean Server.
     * @param objectName The object name if the MBean.
     * @param listener The notification listener.
     */
    public void unsubscribe(ObjectName objectName, NotificationListener listener) {
        //---------- Try to unsubscribe to an MBean Server - always use a connection to do this, wheater is a REMOTE or a LOCAL BeanServer -----------
        ConcurrentLinkedQueue<NotificationListener> listeners = allListeners.get(objectName);

        // No listener listen this objectName, so we display an error message.
        if (listeners == null) {
            logger.warn("The unsubscribe action has failed : The objectName=" + objectName +
                " has been already unsubscribe");
            return;
        }
        // We have to remove the listener.
        else {
            // Try to remove the listener
            boolean isRemoved = listeners.remove(listener);

            // The listener didn't be listening this objectName, so we display an error message.
            if (!isRemoved) {
                logger.warn("The unsubscribe action has failed : The given listener doesn't listen the objectName=" +
                    objectName);
                return;
            }

            // None listeners listen this objectName, so we stop to listen this one.
            if (listeners.isEmpty()) {
                allListeners.remove(objectName);
                Connection connection = connectionsWithObjectName.get(objectName);
                // Remove the objectName to the established connection
                connection.removeObjectName(objectName);
                // notificationlistener.unsubscribe(connection.getConnection(),
                //     objectName, null, null);
                unsubscribeObjectFromRemoteMBean(connection.getConnection(), objectName, null, null);

                // The connection is not used, so we close this connection
                if (!connection.isUsed()) {
                    // Updates our map
                    connectionsWithRuntimeUrl.remove(connection.getRuntimeUrl());
                    connection.getConnection().unsubscribeFromRegistry();
                    //TODO: terminates the connection active object 
                    //Next line throws:  org.objectweb.proactive.core.ProActiveRuntimeException: FutureProxy: Illegal arguments in call _terminateAO
                    System.out.println("Terminating connection with runtime :" + connection.getRuntimeUrl());
                    PAActiveObject.terminateActiveObject(connection.getConnection(), false);
                }
                // Updates our map
                connectionsWithObjectName.remove(objectName);
            }
        }
    }

    public void subscribeObjectToRemoteMBean(ProActiveConnection connection, ObjectName oname,
            NotificationFilter filter, Object handback) {
        try {
            if (!connection.isRegistered(oname)) {
                System.err.println("JMXNotificationListener.subscribe() Oooops oname not known:" + oname);
                return;
            }
            connection.addNotificationListener(oname, notificationlistener, filter, handback);
        } catch (InstanceNotFoundException e) {
            logger.error("Doesn't find the object name " + oname + " during the registration", e);
        } catch (IOException e) {
            logger.error("Doesn't subscribe the JMX Notification listener to the Notifications", e);
        }
    }

    /**
     * Unsubscribes the JMXNotificationLsitener from the JMX notifications of a remote MBean.
     * @param connection The ProActiveConnection in order to connect to the remote server MBean.
     * @param oname The ObjectName of the MBean
     * @param filter A notification filter
     * @param handback A hanback
     *
     */
    public void unsubscribeObjectFromRemoteMBean(ProActiveConnection connection, ObjectName oname,
            NotificationFilter filter, Object handback) {
        if (!PAActiveObject.pingActiveObject(connection)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying to unregister listener on a connection with terminated body. Ping faild on the connection object: " +
                    connection.toString());
            }
            return;
        }
        try {
            if (connection.isRegistered(oname)) {
                connection.removeNotificationListener(oname, notificationlistener, filter, handback);
            }
        } catch (InstanceNotFoundException e) {
            logger.error("Doesn't find the object name " + oname + " during the registration", e);
        } catch (ListenerNotFoundException e) {
            logger.error("Doesn't find the Notification Listener", e);
        } catch (IOException e) {
            logger.error("Can't unsubscribe the JMX Notification listener to the Notifications", e);
        }
    }

    public void handleNotification(Notification notification, Object handback) {
        String type = notification.getType();
        ObjectName oname = (ObjectName) notification.getSource();

        if (logger.isDebugEnabled()) {
            logger.debug("[" + type + "]\n[JMXNotificationManager] source=" + oname);
        }

        if (type.equals(NotificationType.setOfNotifications)) {
            @SuppressWarnings("unchecked")
            ConcurrentLinkedQueue<Notification> notifications = (ConcurrentLinkedQueue<Notification>) notification
                    .getUserData();

            String msg = notification.getMessage();

            try {

                // The active object containing the MBean has migrated, so we have to connect to a new remote host.
                if ((msg != null) && msg.equals(NotificationType.migrationMessage)) {
                    Notification notif = notifications.element();
                    ObjectName ob = (ObjectName) notif.getSource();

                    // The url of the runtime
                    String runtimeUrl = (String) notif.getUserData();

                    Connection establishedConnection = connectionsWithRuntimeUrl.get(runtimeUrl);
                    if (establishedConnection == null) {
                        // We need to open a new connection
                        establishedConnection = new Connection(runtimeUrl);

                        // Updates our map
                        connectionsWithRuntimeUrl.put(runtimeUrl, establishedConnection);
                    }
                    // Add the objectName to the established connection
                    establishedConnection.addObjectName(ob);

                    // Subscribes to the JMX notifications
                    subscribeObjectToRemoteMBean(establishedConnection.getConnection(), ob, null, null);

                    //Unsubscribes to the JMXNotifications within the old connection
                    // (the one used before the migration)
                    Connection oldConnection = connectionsWithObjectName.get(ob);
                    if (oldConnection == null) {
                        logger.warn("Could not unsubscribe listener for object " + ob +
                            " from the old host after migration");
                    } else {
                        oldConnection.removeObjectName(ob);
                        unsubscribeObjectFromRemoteMBean(oldConnection.getConnection(), ob, null, null);
                    }
                    // Updates our map
                    connectionsWithObjectName.put(ob, establishedConnection);
                }

            } catch (IOException e) {
                logger.error(
                        "Got a migration notification but was not able to contact the new runtime for the following reason : ",
                        e);
            }

        }

        ConcurrentLinkedQueue<NotificationListener> notificationListeners = allListeners.get(oname);
        if (notificationListeners == null) {
            // No listener listen this objectName
            allListeners.remove(oname);
            return;
        }

        // Sends to the listeners the notification
        for (NotificationListener listener : notificationListeners) {
            listener.handleNotification(notification, handback);
        }
    }

    /**
     * Returns the JMX ProActiveConnection to the remote JMX MBean Server
     * @param runtimeUrl The url of the remote runtime
     * @return The ProActiveConnection used to connect to the JMX MBean Server where the MBean is located
     */
    public ProActiveConnection getConnection(String runtimeUrl) {
        Connection connection = connectionsWithRuntimeUrl.get(runtimeUrl);
        if (connection != null) {
            return connection.getConnection();
        }
        return null;
    }

    private ProActiveConnection createProActiveConnection(String runtimeUrl) throws IOException {
        return createProActiveConnection(URI.create(runtimeUrl));
    }

    /**
     * Creates an actives a new Remote JMX Connection
     * @param runtimeUrl
     * @return
     */
    private ProActiveConnection createProActiveConnection(URI runtimeURI) throws IOException {
        RemoteObject<?> remoteObject = null;
        Object stub = null;
        try {
            remoteObject = RemoteObjectHelper.lookup(runtimeURI);
            stub = RemoteObjectHelper.generatedObjectStub(remoteObject);
        } catch (ProActiveException e) {
            logger.error("Can't lookup the ProActiveRuntime: " + runtimeURI, e);
        }
        if (stub instanceof ProActiveRuntime) {
            // Active the Remote JMX Server Connector
            ProActiveRuntime proActiveRuntime = (ProActiveRuntime) stub;
            proActiveRuntime.startJMXServerConnector();

            // Create a new connection
            ClientConnector cc = new ClientConnector(runtimeURI.toString(),
                FactoryName.getJMXServerName(runtimeURI));
            // Connect to the remote JMX Server Connector
            cc.connect();

            ProActiveConnection connection = cc.getConnection();
            return connection;
        } else {
            logger.error("Can't create a JMX/ProActive connection: the object is not an instance of ProActiveRuntime");
            return null;
        }
    }

    //
    // ------- INNER CLASS ---------
    //

    /**
     * This class represents a connection to a remote JMX Server Connector.
     */
    private class Connection {

        /**
         * The url of the runtime
         */
        private String runtimeUrl;

        /**
         * The collection of objectName representing the MBeans listened with the connection
         */
        private Collection<ObjectName> objectNames;

        /**
         * The connection
         */
        private ProActiveConnection connection;

        /**
         * Creates a new Connection
         * @param runtimeUrl The url of the remote ProActive Runtime
         */
        public Connection(String runtimeUrl) throws IOException {
            this.runtimeUrl = runtimeUrl;
            this.objectNames = new ConcurrentLinkedQueue<ObjectName>();
            this.connection = createProActiveConnection(runtimeUrl);
        }

        /**
         * Add a MBean to listen.
         * @param objectName The objectName of the MBean
         */
        public void addObjectName(ObjectName objectName) {
            objectNames.add(objectName);
        }

        /**
         * Removes an MBean to listen
         * @param objectName The objectName of the MBean
         */
        public void removeObjectName(ObjectName objectName) {
            objectNames.remove(objectName);
        }

        /**
         * @return true if this connection is used to listen a MBean, false otherwise.
         */
        public boolean isUsed() {
            return !objectNames.isEmpty();
        }

        /**
         * @return The url of the remote runtime, where the MBean Server is located.
         */
        public String getRuntimeUrl() {
            return runtimeUrl;
        }

        /**
         * @return The ProActiveConnection used to connect to the JMX Bean Server.
         */
        public ProActiveConnection getConnection() {
            return connection;
        }
    }

    /**
     * Unsubscribes the notificationlistener from all remote mbeans
     *
     */
    public void kill() {
        unsubscribeAll();
        //TODO (Emil): see if this code is better than the code in unsubscribeAll()
        // use this code or remove it
        //        Iterator<ObjectName> allObjectNames = connectionsWithObjectName.keySet()
        //                                                                       .iterator();
        //        while (allObjectNames.hasNext()) {
        //            ObjectName objectName = allObjectNames.next();
        //            Connection connection = connectionsWithObjectName.get(objectName);
        //            connection.removeObjectName(objectName);
        //            //if (!connection.isUsed()) 
        //            { // Unsubscribes to the JMX notifications of the remote/local MBean.
        //                try {
        //                    notificationlistener.unsubscribe(connection.getConnection(),
        //                        objectName, null, null);
        //                } catch (Exception e) {
        //                    System.out.println("Exception when removing jmx listener:" +
        //                        e.getMessage() + " caused by" +
        //                        e.getCause().getMessage());
        //                }
        //            } //if !connection.isUsed()
        //        } //forall objectNames
        //        System.out.println(
        //            "JMXNotification manager have unsubscribed all listeners to the local and remote beans.");
    } //kill()

    private void unsubscribeAll() {
        //Unregisters the notificationlistener for all objectNames
        Iterator<ObjectName> allObjectNames = connectionsWithObjectName.keySet().iterator();
        while (allObjectNames.hasNext()) {
            ObjectName objectName = allObjectNames.next();
            ConcurrentLinkedQueue<NotificationListener> listeners = allListeners.get(objectName);
            if (listeners != null) {
                Iterator<NotificationListener> listenersIterator = listeners.iterator();
                while (listenersIterator.hasNext()) {
                    NotificationListener listener = listenersIterator.next();
                    this.unsubscribe(objectName, listener);
                } //while listenersIterator.hasNext()
            } //if listeners!=null
        } //while allObjectNames.hasNext()

        //Removes the reference for the notificationlistener from the local registry(ies);
        try {
            if (!notificationlistener.unsubscribeFromRegistry()) {
                System.out.println("could not unregister JMXNotificationListener");
            }
            ;

            //System.out.println("Unregistered the JMXNotificationListener for IC2D from the regisrty.");
        } catch (Exception e) {
            System.out
                    .println("Could not unregistered the JMXNotificationListener for IC2D from the regisrty.");
            e.printStackTrace();
        }

        //The localBody for this thread will be removed by the unregisterAllHalfBodiesFromRegistry()
        //So we don't need to run this code (even if we could)
        //        Body myBody=PAActiveObject.getBodyOnThis();
        //        if (myBody instanceof AbstractBody)
        //        {
        //           
        //        	myBody.terminate();    
        //        	RemoteObjectExposer roe=((AbstractBody)myBody).getRemoteObjectExposer();
        //            roe.unregisterAll();
        //        }
    }
}
