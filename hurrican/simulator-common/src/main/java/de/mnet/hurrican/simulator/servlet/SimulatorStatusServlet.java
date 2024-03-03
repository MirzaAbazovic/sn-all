/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.07.13
 */
package de.mnet.hurrican.simulator.servlet;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import de.mnet.hurrican.simulator.builder.SimulatorTestBuilder;

/**
 * Status servlet shows latest simulator use cases executed. Servlet shows list of use cases executed and success/failed
 * state with optional error messages.
 *
 *
 */
public class SimulatorStatusServlet extends AbstractSimulatorServlet {

    private static final long serialVersionUID = 6905907827878688440L;

    /**
     * Handlebars
     */
    private Template statusTemplate;

    /**
     * Date format for start and end date display
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Simulator startup time.
     */
    private Long startupTime = 0L;

    @Override
    public void init() throws ServletException {
        super.init();
        startupTime = System.currentTimeMillis();
        statusTemplate = compileHandlebarsTemplate("status");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Boolean> statusChecks = new LinkedHashMap<>();
        Map<String, String> statusInfo = new LinkedHashMap<>();

        statusInfo.put("CitrusTestBuilders", String.valueOf(getApplicationContext().getBeansOfType(SimulatorTestBuilder.class).size()));
        statusInfo.put("Version", System.getProperty("simulator.version", "N/A"));

        statusChecks.put("Application context", getApplicationContext() != null);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("startupTime", dateFormat.format(new Date(startupTime)));
        model.put("statusChecks", statusChecks);
        model.put("statusInfo", statusInfo);
        model.put("overallStatus", getOverAllStatus(statusChecks));
        model.put("contextPath", req.getContextPath());

        Context context = Context.newContext(model);
        statusTemplate.apply(context, resp.getWriter());
    }

    /**
     * Calculates overall results by checking all status check results to be true. In case one
     * status check is false overall result is false, too.
     * @param statusChecks
     * @return
     */
    private Boolean getOverAllStatus(Map<String, Boolean> statusChecks) {
        for (Boolean result : statusChecks.values()) {
            if (!result) {
                return false;
            }
        }

        return true;
    }

}
