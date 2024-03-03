/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.2007 11:24:05
 */
package de.augustakom.hurrican.service.exmodules.sap.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.exmodules.sap.ISAPService;


/**
 * ServiceFinder-Implementierung, um nach SAP-Services zu suchen.
 *
 *
 */
public class SAPServiceFinder extends HurricanServiceFinder implements ISAPServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(SAPServiceFinder.class);

    private static SAPServiceFinder instance = null;

    /* Privater Konstruktor */
    protected SAPServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static SAPServiceFinder instance() {
        if (instance == null) {
            instance = new SAPServiceFinder();
        }
        return instance;
    }


    /**
     * @see de.augustakom.hurrican.service.exmodules.production.sap.ISAPServiceFinder#getISAPService(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public ISAPService getSAPService(String serviceName, Class serviceType)
            throws ServiceNotFoundException {
        try {
            return (ISAPService) getService(IServiceLocatorNames.HURRICAN_SAP_SERVICE,
                    serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ISAPService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.production.sap.ISAPServiceFinder#getISAPService(java.lang.Class)
     */
    @Override
    public ISAPService getSAPService(Class serviceType) throws ServiceNotFoundException {
        try {
            return (ISAPService) getService(IServiceLocatorNames.HURRICAN_SAP_SERVICE,
                    serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ISAPService.class.getName());
        }
    }
}


