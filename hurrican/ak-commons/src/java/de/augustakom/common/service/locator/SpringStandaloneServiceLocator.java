/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2004
 */
package de.augustakom.common.service.locator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Standard-Implementierung eines Spring-ServiceLocators, die fuer Standalone-Applikationen verwendet werden kann.
 *
 *
 * @deprecated Use {@link de.mnet.common.service.locator.ServiceLocator} instead
 */
@Deprecated
public class SpringStandaloneServiceLocator extends AbstractSpringServiceLocator {

    private boolean initialized = false;
    private ClassPathXmlApplicationContext applicationContext = null;
    private String serviceLocatorName = null;
    private String xmlConfiguration = null;

    public SpringStandaloneServiceLocator() {
    }

    /**
     * @param serviceLocatorName Name des Service Locators
     * @param xmlConfiguration   Pfad der XML Konfigurationsdatei (z.B. de/mnet/...)
     */
    public SpringStandaloneServiceLocator(String serviceLocatorName, String xmlConfiguration) {
        this.serviceLocatorName = serviceLocatorName;
        this.xmlConfiguration = xmlConfiguration;
    }


    /**
     * Gibt den Namen der XML-Konfigurationsdatei zurueck, die fuer den ServiceLocator verwendet werden soll. <br> Bsp.:
     * de/augustakom/resources/MySpringConfiguration.xml
     *
     * @return
     */
    public String getXMLConfiguration() {
        return xmlConfiguration;
    }

    /**
     * @see de.augustakom.common.service.iface.IServiceLocator#getServiceLocatorName()
     */
    @Override
    public String getServiceLocatorName() {
        return serviceLocatorName;
    }

    /**
     * @see de.augustakom.common.service.locator.AbstractSpringServiceLocator
     */
    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @see de.augustakom.common.service.locator.AbstractSpringServiceLocator
     */
    @Override
    protected boolean isInitialized() {
        return initialized;
    }

    /**
     * @see de.augustakom.common.service.locator.AbstractSpringServiceLocator
     */
    @Override
    protected void initServiceLocator() {
        try {
            applicationContext = new ClassPathXmlApplicationContext(getXMLConfiguration());
            initialized = true;
        }
        catch (BeansException e) {
            throw e;
        }
    }

    /**
     * Veranlasst den ApplicationContext dazu, sich neu zu laden
     */
    public void refreshApplicationContext() {
        applicationContext.refresh();
    }
}
