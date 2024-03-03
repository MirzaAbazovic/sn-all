/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2004
 */
package de.augustakom.common.service.locator;

import java.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceContext;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceObject;


/**
 * Abstrakte Implementierung eines Service-Locators, der auf dem Spring-Framework basiert. <br><br> Es gibt
 * unterschiedliche SpringServiceLocator-Implementierungen fuer Standalone- und Web-Applikationen.
 *
 *
 */
public abstract class AbstractSpringServiceLocator implements IServiceLocator {

    private static final Logger LOGGER = Logger.getLogger(AbstractSpringServiceLocator.class);

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    public abstract String getServiceLocatorName();

    /**
     * Gibt den Spring-ApplicationContext zurueck.
     *
     * @return Instanz eines Spring-ApplicationContexts.
     */
    protected abstract ApplicationContext getApplicationContext();

    /**
     * Veranlasst die Implementierung dazu, den ServiceLocator zu initialisieren.
     *
     * @throws BeansException
     * @throws IllegalArgumentException
     */
    protected abstract void initServiceLocator();

    /**
     * Gibt an, ob der ServiceLocator initialisiert ist.
     *
     * @return
     */
    protected abstract boolean isInitialized();

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String,
     * de.augustakom.common.service.iface.IServiceContext)
     */
    public IServiceObject getService(String serviceName, IServiceContext serviceContext) throws ServiceNotFoundException {
        return getService(serviceName, null, serviceContext);
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getService(java.lang.String, java.lang.Class,
     * de.augustakom.common.service.iface.IServiceContext)
     */
    public <T extends IServiceObject> T getService(String serviceName, Class<T> requiredType, IServiceContext serviceContext) throws ServiceNotFoundException {
        Object service = null;
        try {
            if (!isInitialized()) {
                initServiceLocator();
            }

            if (getApplicationContext() != null) {
                if (requiredType != null) {
                    service = getApplicationContext().getBean(serviceName, requiredType);
                }
                else {
                    service = getApplicationContext().getBean(serviceName);
                }
            }
            else {
                throw new ServiceNotFoundException("Spring Service-Locator is not configured correctly!");
            }
        }
        catch (BeansException e) {
            String msg = null;
            if (requiredType != null) {
                msg = "The requested service could not be created or found or is not of type " + requiredType.getName() + "! Service: " + serviceName;
            }
            else {
                msg = "The requested service could not be created or found! Service: " + serviceName;
            }

            throw new ServiceNotFoundException(msg, e);
        }

        if (service instanceof IServiceObject) {
            return (T) service;
        }
        else {
            throw new ServiceNotFoundException("Requested service is not of type IServiceObject! Requested service: " + serviceName);
        }
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getBean(java.lang.String)
     */
    public Object getBean(String name) {
        if (!isInitialized()) {
            initServiceLocator();
        }
        if (getApplicationContext() != null) {
            try {
                Object bean = getApplicationContext().getBean(name);
                return bean;
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getBeansOfType(java.lang.Class)
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        if (!isInitialized()) {
            initServiceLocator();
        }
        if (getApplicationContext() != null) {
            try {
                return getApplicationContext().getBeansOfType(type);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#closeServiceLocator()
     */
    public void closeServiceLocator() {
        Object appCtx = getApplicationContext();
        if (appCtx instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) appCtx).close();
        }
    }
}
