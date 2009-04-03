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
package functionalTests.annotations.remoteobject.inputs;

import java.util.List;

import org.objectweb.proactive.extensions.annotation.RemoteObject;


@RemoteObject
public class Reject {
    public Reject(int n) {
    } // E no-arg constructor

    private volatile List<Object> _someLocks;

    private synchronized void doNothingSynchronized() {
    }

    private final int dontOverrideMe() {
        return 0;
    } //E final method

    public int _counter; // W no getters/setters

    //public int getCounter() { return _counter; }

    public void setCounter(int counter) {
        _counter = counter;
    }
}