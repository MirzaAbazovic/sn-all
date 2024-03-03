/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.2015
 */
package de.mnet.hurrican.simulator.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * Servlet providing simulator log file access in browser.
 *
 *
 */
public class SimulatorLoggingServlet extends AbstractSimulatorServlet {

    private static final long serialVersionUID = 4580866666563547695L;
    public static final String LOG_DIRECTORY = "logs";

    /** Handlebars templates */
    private Template loggingTemplate;

    /** Logging directory cached once it is defined */
    private String logDirectory;

    @Override
    public void init() throws ServletException {
        super.init();

        loggingTemplate = compileHandlebarsTemplate("logging");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("file");
        if (StringUtils.hasText(fileName)) {
            Resource logFileResource = getLogFileResource(fileName);
            IOUtils.copy(logFileResource.getInputStream(), resp.getWriter());
        } else {
            Context context = Context.newContext(buildViewModel(req));
            loggingTemplate.apply(context, resp.getWriter());
        }
    }

    /**
     * Builds default view model for this servlet's handlebars view template.
     *
     * @param req
     * @return
     */
    private Map<String, Object> buildViewModel(HttpServletRequest req) {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("logDirectory", getLogDirectory());
        model.put("logFiles", getLogFiles());
        model.put("contextPath", req.getContextPath());

        return model;
    }

    /**
     * Lists all available log files in log directory.
     * @return
     */
    private List<String> getLogFiles() {
        List<String> logFiles = new ArrayList<>();
        File logDirectory = new FileSystemResource(getLogDirectory()).getFile();

        if (logDirectory.exists() && logDirectory.isDirectory()) {
            for (File file : logDirectory.listFiles()) {
                logFiles.add(file.getName());
            }
        }

        return logFiles;
    }

    /**
     * Gets a file resource in log directory.
     * @param fileName
     * @return
     */
    private Resource getLogFileResource(String fileName) {
        return new FileSystemResource(getLogDirectory() + File.separator + fileName);
    }

    /**
     * Gets the simulator log directory from system properties. Either uses working directory or
     * project base directory in order to find proper logs directory. Once directory is found
     * it is cached for better performance in future calls.
     * @return
     */
    private String getLogDirectory() {
        if (!StringUtils.hasText(logDirectory)) {
            String workingDir = System.getProperty("base.working.dir");

            if (StringUtils.hasText(workingDir)) {
                File dir = new FileSystemResource(workingDir + File.separator + LOG_DIRECTORY).getFile();

                if (dir.exists() && dir.isDirectory()) {
                    logDirectory = workingDir + File.separator + LOG_DIRECTORY;
                } else {
                    logDirectory = workingDir.substring(0, workingDir.lastIndexOf(File.separatorChar) + 1) + LOG_DIRECTORY;
                }
            } else {
                logDirectory = LOG_DIRECTORY;
            }
        }

        return logDirectory;
    }

}
