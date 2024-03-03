/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 07:42:46
 */
package de.augustakom.hurrican.service.base.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.base.iface.IHurricanService;


/**
 * Interface fuer Klassen, die nach best. Services suchen koennen.
 *
 *
 */
public class HurricanServiceFinder implements IServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(HurricanServiceFinder.class);

    private static HurricanServiceFinder instance = null;

    /* Privater Konstruktor */
    protected HurricanServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static HurricanServiceFinder instance() {
        if (instance == null) {
            instance = new HurricanServiceFinder();
        }
        return instance;
    }

    /**
     * Durchsucht alle registrierten ServiceLocator nach einem Service mit dem angegebenen Namen und Typ. <br> Die erste
     * Uebereinstimmung wird zurueck gegeben.
     *
     * @param serviceName Name des gesuchten Services.
     * @param serviceType Typ des gesuchten Services.
     * @return Service-Objekt.
     * @throws ServiceNotFoundException wenn der gesuchte Service auf keinem registrierten ServiceLocator gefunden
     *                                  wurde.
     */
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        String[] locators = ServiceLocatorRegistry.instance().getServiceLocatorNames();
        if (locators != null) {
            for (int i = 0; i < locators.length; i++) {
                try {
                    T service = getService(locators[i], serviceName, serviceType);
                    return service;
                }
                catch (ServiceNotFoundException e) {
                    e.printStackTrace();
                    LOGGER.warn(e.getLocalizedMessage());
                    if (i == locators.length - 1) {
                        throw e;
                    }
                }
            }
        }

        throw new ServiceNotFoundException("There are no ServiceLocators registered!");
    }

    /**
     * Sucht auf dem angegebenen ServiceLocator nach einem Service mit Namen <code>serviceName</code>. Der Typ muss mit
     * <code>serviceType</code> uebereinstimmen.
     *
     * @param locatorName Name des ServiceLocators, auf dem der Service gesucht werden soll
     * @param serviceName Name des gesuchten Services
     * @param serviceType Typ des gesuchten Services
     * @return Service-Objekt
     * @throws ServiceNotFoundException wenn der gesuchte Service nicht gefunden wurde.
     */
    protected <T extends IHurricanService> T getService(String locatorName, String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(locatorName);
        if (locator != null) {
            return locator.getService(serviceName, serviceType, null);
        }

        throw new ServiceNotFoundException("Required ServiceLocator not found in ServiceLocatorRegistry!");
    }
}


