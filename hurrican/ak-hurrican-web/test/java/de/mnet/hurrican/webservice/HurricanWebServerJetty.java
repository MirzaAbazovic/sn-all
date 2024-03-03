/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.04.2012 08:49:16
 */
package de.mnet.hurrican.webservice;

import java.io.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Bootstrap fuer den HurricanWeb Jetty Server
 */
public class HurricanWebServerJetty {
    public static void main(String[] args) throws Exception {
        Server server = new Server((args != null && args.length==1) ? Integer.valueOf(args[0]) : 8080);

        File warDir = new File(".", "/WebContent");
        System.out.println("starting server with war dir " + warDir.getAbsolutePath());

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/hurricanweb");
        webapp.setWar(warDir.getAbsolutePath());
        server.setHandler(webapp);

        server.start();
        server.join();
    }

}


