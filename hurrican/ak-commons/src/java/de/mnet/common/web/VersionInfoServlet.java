/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.14
 */
package de.mnet.common.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

public class VersionInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final Properties properties = new Properties();
        try (final InputStream in = VersionInfoServlet.class.getResourceAsStream("version.properties")) {
            properties.load(in);
        }

        final String version = properties.getProperty("project.version", "unknown");
        final String buildDate = properties.getProperty("build.date", "unknown");

        resp.setContentType("text/html");
        resp.getWriter().print(""
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
                + "\"http://www.w3.org/TR/html4/strict.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n"
                + "  <title>Version Information</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <h1>Version: " + version + "</h1>\n"
                + "  <h1>Build Date: " + buildDate + "</h1>\n"
                + "</body>\n"
                + "</html>\n");
    }
}
