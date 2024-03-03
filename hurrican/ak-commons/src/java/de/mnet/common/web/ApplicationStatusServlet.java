/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2011 14:13:15
 */
package de.mnet.common.web;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.mnet.common.service.status.ApplicationStatusResult;
import de.mnet.common.service.status.ApplicationStatusService;

/**
 * Servlet zur Abfrage des Anwendungsstatus
 */
public class ApplicationStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 5146999909140887152L;

    protected WebApplicationContext getWebApplicationContext() {
        return WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebApplicationContext webApplicationContext = getWebApplicationContext();

        Collection<ApplicationStatusService> statusServices = webApplicationContext.getBeansOfType(
                ApplicationStatusService.class).values();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");
        boolean allOk = true;

        writeHeader(writer);

        writer.append("<h1>Application Status</h1>\n");
        writer.append("<ul>\n");

        for (ApplicationStatusService statusService : statusServices) {
            ApplicationStatusResult status = statusService.getStatus();
            allOk = allOk && status.isOk();
            writeStatusLine(writer, statusService.getStatusName(), status);
        }
        writer.append("</ul>\n");
        writer.append("<h2>Overall result: ");
        if (allOk) {
            writer.append(colorSpan("OK", "green"));
        }
        else {
            writer.append(colorSpan("FAILURE", "red"));
        }
        writer.append("</h2>\n");
        writer.append("</body>\n");
        writer.append("</html>\n");
    }

    private void writeStatusLine(PrintWriter writer, String statusName,
            ApplicationStatusResult status) {
        writer.append("<li>" + statusName + ": ");
        if (status.isOk()) {
            writer.append(colorSpan("OK", "green"));
        }
        else {
            writer.append(colorSpan("FAILURE", "red"));
            writer.append("<br />");
            writer.append("Errors: ");
            for (String error : status.getErrors()) {
                writer.append(error + "<br />");
            }
        }
        writer.append("</li>\n");
    }

    private void writeHeader(PrintWriter writer) {
        writer.append("<html>");
        writer.append("<head>");
        writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>");
        writer.append("<title>Application Status</title>");
        writer.append("</head>");
        writer.append("<body>");
    }

    private String colorSpan(String text, String color) {
        return "<span style=\"font-weight: bold; color: " + color + ";\">" + text + "</span>";
    }
}


