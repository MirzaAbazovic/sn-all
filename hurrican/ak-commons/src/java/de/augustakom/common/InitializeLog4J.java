/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2004
 */
package de.augustakom.common;

import java.io.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;

import de.augustakom.common.tools.lang.PropertyUtil;


/**
 * initialisiere Log4J Logging
 */
public class InitializeLog4J {

    private static Logger LOGGER; // NOSONAR squid:S1312

    private static final String LOGGER_FILE_GUI_PROPERTY = "log4j.appender.file-gui.File";
    private static String logDirectory;

    public static void initializeLog4J(String configFileName) {
        initializeLog4J(Arrays.asList(configFileName));
    }

    public static void initializeLog4J(List<String> configFileNames) {
        // prevent Log4J warning
        BasicConfigurator.configure();
        if (configFileNames.isEmpty()) {
            LOGGER = Logger.getLogger(InitializeLog4J.class);
            LOGGER.warn("No logging configuration given. Using BasicConfigurator.");
            return;
        }
        Properties logProperties = PropertyUtil.loadPropertyHierarchy(configFileNames, "properties", true);

        String logFileName = (String) logProperties.get(LOGGER_FILE_GUI_PROPERTY);
        if (StringUtils.isNotBlank(logFileName)) {
            logFileName = logFileName.replace("${user.home}", System.getProperty("user.home"));
            File logFile = new File(logFileName);
            logDirectory = logFile.getParent();
        }

        PropertyConfigurator.configure(logProperties);
        redirectJulToSlf4j();
    }

    private static void redirectJulToSlf4j() {
        java.util.logging.Logger rootLogger =
                java.util.logging.LogManager.getLogManager().getLogger("");
        java.util.logging.Handler[] handlers = rootLogger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            rootLogger.removeHandler(handlers[i]);
        }
        SLF4JBridgeHandler.install();
    }

    /**
     * @return das Log4J Logging Verzeichnis wenn dieses ermittelt werden konnte, ansonsten <code>null</code>
     */
    public static String getLogDirectory() {
        return logDirectory;
    }
}
