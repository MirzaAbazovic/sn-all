/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.09.2014
 */
package de.mnet.hurrican.atlas.simulator;

import java.io.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 */
public class AtlasSimulatorServerJetty {
    public static void main(String[] args) throws Exception {
        Server server = new Server(Integer.valueOf(System.getProperty("atlas.simulator.port", "19222")));

        File warDir = new File(".", "/src/main/webapp");
        System.out.println("starting server with war dir " + warDir.getAbsolutePath());

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/atlas");
        webapp.setWar(warDir.getAbsolutePath());
        server.setHandler(webapp);

        server.start();
        server.join();
    }
}
