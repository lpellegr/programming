/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.ic2d.p2p.Monitoring.jung;

import org.objectweb.proactive.extra.p2p.monitoring.P2PNode;

import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;


public class P2PUndirectedSparseVertex extends UndirectedSparseVertex {
    P2PNode node;

    //    protected int noa;
    //    protected int maxNoa;
    //    protected String name;

    public P2PUndirectedSparseVertex() {
        super();
    }

    public P2PUndirectedSparseVertex(P2PNode p) {
        super();
        this.node = p;
    }

    public void setNode(P2PNode p) {
        this.node = p;
    }

    public P2PNode getNode() {
        return this.node;
    }

    //    public P2PNodeColor getColor() {
    //        return this.node.getColor();
    //    }

    //    public int getMaxNoa() {
    //        return maxNoa;
    //    }

    //    public void setMaxNoa(int maxNOA) {
    //        this.maxNoa = maxNOA;
    //    }

    //    public int getNoa() {
    //        return noa;
    //    }

    //    public void setNoa(int noa) {
    //        this.noa = noa;
    //    }
    //
    //    public void setName(String name) {
    //        this.name = name;
    //    }

    public String getName() {
        return this.node.getName();
    }
}