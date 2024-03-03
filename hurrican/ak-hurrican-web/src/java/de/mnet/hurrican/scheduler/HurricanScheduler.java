/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.12.2005 16:30:06
 */
package de.mnet.hurrican.scheduler;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.augustakom.common.service.iface.IServiceMode;
import de.mnet.hurrican.scheduler.exceptions.AKSchedulerException;

/**
 * Scheduler-Implementierung basierend auf der Quartz-Implementierung von Spring.
 */
public class HurricanScheduler {

    private static final Logger LOGGER = Logger.getLogger(HurricanScheduler.class);

    private static HurricanScheduler instance;
    private static Long sessionId;

    public static HurricanScheduler getInstance() {
        return instance;
    }

    public static Long getSessionId() {
        return sessionId;
    }

    public static void setSessionId(Long sessionId) {
        HurricanScheduler.sessionId = sessionId;
    }

    public static String getApplicationMode() {
        return System.getProperty(IServiceMode.SYSTEM_PROPERTY_MODE);
    }

    private ClassPathXmlApplicationContext applicationContext;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Instanz muss hier gesetzt werden")
    public void start(ApplicationContext rootContext) throws AKSchedulerException {
        String schedulerConfigFile = System.getProperty("scheduler.config");
        LOGGER.info("Configuration file for hurrican-scheduler: " + schedulerConfigFile);

        if (StringUtils.isNotBlank(schedulerConfigFile)) {
            try {
                applicationContext = new ClassPathXmlApplicationContext(new String[] { schedulerConfigFile },
                        rootContext);
                HurricanScheduler.instance = this;
                LOGGER.info("Scheduler is initialized!");
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        else {
            LOGGER.warn("Keine Scheduler-Konfiguration vorhanden - Scheduler wird nicht gestartet");
        }
    }

    public void shutdown() throws SchedulerException {
        applicationContext.destroy();
    }

    /**
     * Ermittelt aus dem ApplicationContext des Schedulers eine Bean mit dem Namen <code>name</code> vom Typ
     * <code>reqType</code>.
     */
    public <T> T getBean(String name, Class<T> reqType) {
        return applicationContext.getBean(name, reqType);
    }
}
