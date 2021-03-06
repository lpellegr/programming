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
package diff;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DiffJavaDir {

    /**
     * Perform an unified diff between to directory.
     * Only Java file are compared
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usageAndExit();
        }

        File dir1 = new File(args[0]);
        File dir2 = new File(args[1]);

        if (!directoryExist(dir1) || !directoryExist(dir2)) {
            System.exit(1);
        }

        List<File> files = exploreDirectory(dir1);
        boolean diffFound = false;

        for (File file : files) {
            String file2 = file.toString().replaceFirst(dir1.toString(), dir2.toString());
            if (!new File(file2).exists()) {
                System.err.println(file2 + " does not exist in " + dir2);
                diffFound = true;
                continue;
            }

            if (DiffPrint.printUnifiedDiff(file.toString(), file2)) {
                diffFound = true;
            }
        }

        if (diffFound) {
            System.exit(1);
        }
    }

    static private List<File> exploreDirectory(File dir) {
        List<File> files = new ArrayList<File>();

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(exploreDirectory(file));
            }

            if (!file.toString().endsWith(".java")) {
                continue;
            }

            files.add(file);
        }

        return files;
    }

    private static boolean directoryExist(File dir) {
        if (!dir.exists()) {
            return false;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        if (!dir.canRead()) {
            return false;
        }

        return true;
    }

    private static void usageAndExit() {
        System.err.println("Usage:");
        System.err.println("\tcommand dir1 dir2");
        System.exit(2);
    }
}
