/*
 *  *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
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
 *  * $$PROACTIVE_INITIAL_DEV$$
 */
package gcmdeployment.parseArg;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderHelper;


/**
 * TestParseArg
 *
 * @author The ProActive Team
 */
public class TestParseArg {

    String[] args = {
            "-option1 My option1 is there",
            "-option2 \"My option2 - is there\"",
            "-option3=\"My option3 - is there\"",
            "\"My option4 - is there\"",
            "My option5 is there",
            "-option1 My option1 is there -option2 \"My option2 - is there\" -option3=\"My option3 - is there\" \"My option4 - is there\" My option5 is there" };
    String[][] parsed = {
            { "-option1", "My option1 is there" },
            { "-option2", "My option2 - is there" },
            { "-option3=My option3 - is there" },
            { "My option4 - is there" },
            { "My option5 is there" },
            { "-option1", "My option1 is there", "-option2", "My option2 - is there",
                    "-option3=My option3 - is there", "My option4 - is there", "My option5 is there" } };

    @Test
    public void TestParseArg() {
        for (int i = 0; i < args.length; i++) {
            List<String> options = CommandBuilderHelper.parseArg(args[i]);
            List<String> expected = Arrays.asList(parsed[i]);
            System.out.println("" + i);
            System.out.println("Received : " + options);
            System.out.println("Expected : " + expected);
            Assert.assertEquals(expected, options);
        }
    }
}
