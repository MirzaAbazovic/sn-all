/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.05.2004
 */
package de.mnet.migration.common.util;

import java.util.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.augustakom.common.tools.lang.PropertyUtil;

/**
 * initialisiere Log4J Logging
 *
 *
 */
public class InitializeLog4J {
    private static Logger LOGGER;

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
        PropertyConfigurator.configure(logProperties);

    }
}
