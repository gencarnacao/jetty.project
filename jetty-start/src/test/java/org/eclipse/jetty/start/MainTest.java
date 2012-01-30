// ========================================================================
// Copyright (c) 2009-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================

package org.eclipse.jetty.start;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.junit.Before;
import org.junit.Test;

/* ------------------------------------------------------------ */
/**
 */
public class MainTest
{
    /* ------------------------------------------------------------ */
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        File testJettyHome = MavenTestingUtils.getTestResourceDir("jetty.home");
        System.setProperty("jetty.home",testJettyHome.getAbsolutePath());
    }

    @Test
    public void testLoadStartIni() throws IOException
    {
        Main main = new Main();
        List<String> args = main.parseStartIniFiles();
        assertEquals("Expected 5 uncommented lines in start.ini",9,args.size());
        assertEquals("First uncommented line in start.ini doesn't match expected result","OPTIONS=Server,jsp,resources,websocket,ext",args.get(0));
        assertEquals("Last uncommented line in start.ini doesn't match expected result","etc/jetty-testrealm.xml",args.get(8));
    }

    @Test
    public void testExpandCommandLine() throws Exception
    {
        Main main = new Main();
        List<String> args = main.expandCommandLine(new String[] {});
        assertEquals("start.ini OPTIONS","OPTIONS=Server,jsp,resources,websocket,ext",args.get(0));
        assertEquals("start.d/jmx OPTIONS","OPTIONS=jmx",args.get(5));
        assertEquals("start.d/jmx XML","--pre=etc/jetty-jmx.xml",args.get(6));
        assertEquals("start.d/websocket OPTIONS","OPTIONS=websocket",args.get(7));
    }

    @Test
    public void testProcessCommandLine() throws Exception
    {
        Main main = new Main();
        List<String> args = main.expandCommandLine(new String[] {});
        List<String> xmls = main.processCommandLine(args);

        assertEquals("jmx --pre","etc/jetty-jmx.xml",xmls.get(0));
        assertEquals("start.ini","etc/jetty.xml",xmls.get(1));
        assertEquals("start.d","etc/jetty-testrealm.xml",xmls.get(5));
    }

    @Test
    public void testBuildCommandLine() throws IOException
    {
        List<String> jvmArgs = new ArrayList<String>();
        jvmArgs.add("--exec");
        jvmArgs.add("-Xms1024m");
        jvmArgs.add("-Xmx1024m");

        List<String> xmls = new ArrayList<String>();
        xmls.add("jetty.xml");
        xmls.add("jetty-jmx.xml");
        xmls.add("jetty-logging.xml");

        Main main = new Main();
        main.addJvmArgs(jvmArgs);

        String commandLine = main.buildCommandLine(new Classpath(""),xmls);
        assertTrue("CommandLine shouldn't be null",commandLine != null);
        assertTrue("CommandLine should contain jvmArgs",commandLine.contains("--exec -Xms1024m -Xmx1024m"));
        assertTrue("CommandLine should contain xmls",commandLine.contains("jetty.xml jetty-jmx.xml jetty-logging.xml"));

    }

}
