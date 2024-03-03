/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2014
 */
package de.mnet.common.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

public class SettingsListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().print(""
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
                + "\"http://www.w3.org/TR/html4/strict.dtd\">\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n"
                + "  <title>Settings</title>\n"
                + "</head>\n"
                + "<body>\n");

        printSystemProperties(resp);

        resp.getWriter().print(""
                + "</body>\n"
                + "</html>\n");
    }

    private void printSystemProperties(HttpServletResponse resp) throws IOException {
        resp.getWriter().print(""
                + "  <h1>Properties:</h1>\n"
                + "  <pre>\n");

        final List<Map.Entry<Object, Object>> arrayList = new ArrayList<>(System.getProperties().entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<Object, Object>>() {
            @Override
            public int compare(Map.Entry<Object, Object> o1, Map.Entry<Object, Object> o2) {
                return o1.getKey().toString().compareTo(o2.getKey().toString());
            }
        });

        for (Map.Entry<Object, Object> entry : arrayList) {
            final String key = entry.getKey().toString();
            final boolean containsPasswd =
                    key.contains("authkey") || key.contains("passwd") || key.contains("password");
            final String value = containsPasswd ? "???" : entry.getValue().toString();

            resp.getWriter().print(key + " = " + value + "\n");
        }

        resp.getWriter().print(""
                + "  </pre>\n");
    }
}
