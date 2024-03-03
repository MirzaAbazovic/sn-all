/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 16:47:05
 */
package de.augustakom.hurrican.service.internet.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.internet.IInternetService;


/**
 * ServiceFinder-Implementierung, um nach IInternetServices zu suchen.
 *
 *
 */
public class InternetServiceFinder extends HurricanServiceFinder implements IInternetServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(InternetServiceFinder.class);

    private static InternetServiceFinder instance = null;

    /* Privater Konstruktor */
    protected InternetServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static InternetServiceFinder instance() {
        if (instance == null) {
            instance = new InternetServiceFinder();
        }
        return instance;
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.String,
     * java.lang.Class)
     */
    public <T extends IInternetService> T getInternetService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_INTERNET_SERVICE, serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IInternetService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.Class)
     */
    public <T extends IInternetService> T getInternetService(Class<T> serviceType) throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_INTERNET_SERVICE, serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IInternetService.class.getName());
        }
    }
}


