/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 08:53:48
 */
package de.augustakom.hurrican.service.cc.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;


/**
 * ServiceFinder-Implementierung, um nach CCServices zu suchen.
 *
 *
 */
public class CCServiceFinder extends HurricanServiceFinder implements ICCServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(CCServiceFinder.class);

    private static CCServiceFinder instance = null;

    /* Privater Konstruktor */
    protected CCServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static CCServiceFinder instance() {
        if (instance == null) {
            instance = new CCServiceFinder();
        }
        return instance;
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_CC_SERVICE, serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage());
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ICCService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_CC_SERVICE, serviceType.getName(), serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage());
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ICCService.class.getName());
        }
    }
}


