/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.04.2010 16:45:05
 */
package de.augustakom.common.service.locator;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ConfigurableApplicationContext;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;

public final class ServiceLocator {

    private static final Logger LOGGER = Logger.getLogger(ServiceLocator.class);

    private static ServiceLocator instance;
    private ConfigurableApplicationContext applicationContext = null;

    /**
     * Standardkonstruktor fuer die (Singleton-)Registry .
     */
    private ServiceLocator() {
    }

    /**
     * Gibt eine Singleton-Instanz der Registry zurueck.
     */
    public static ServiceLocator instance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public void setApplicationContext(ConfigurableApplicationContext appContext) {
        LOGGER.debug("ApplicationContext set!");
        this.applicationContext = appContext;
    }

    public <T> T getService(String serviceName, Class<T> requiredType) throws ServiceNotFoundException {
        try {
            return applicationContext.getBean(serviceName, requiredType);
        }
        catch (BeansException e) {
            LOGGER.error(e, e);
            throw new ServiceNotFoundException(e);
        }
    }

    /**
     * Ermittelt alle Beans eines bestimmten Typs.
     *
     * @return a map with the matching beans, containing the bean names as keys and the corresponding beans as values
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

    /**
     * Beendet den ServiceLocator und gibt dadurch alle Resourcen frei.
     */
    public void shutdownServiceLocator() {
        applicationContext.close();
        applicationContext = null;
    }
}
