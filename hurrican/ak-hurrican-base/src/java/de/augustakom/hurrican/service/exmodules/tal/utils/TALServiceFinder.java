/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 09:02:05
 */
package de.augustakom.hurrican.service.exmodules.tal.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.exmodules.tal.ITALService;


/**
 * ServiceFinder-Implementierung, um nach TAL-Services zu suchen.
 *
 *
 */
public class TALServiceFinder extends HurricanServiceFinder implements ITALServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(TALServiceFinder.class);

    private static TALServiceFinder instance = null;

    /* Privater Konstruktor */
    protected TALServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static TALServiceFinder instance() {
        if (instance == null) {
            instance = new TALServiceFinder();
        }
        return instance;
    }


    /**
     * @see de.augustakom.hurrican.service.exmodules.tal.utils.ITALServiceFinder#getTALService(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public ITALService getTALService(String serviceName, Class serviceType)
            throws ServiceNotFoundException {
        try {
            return (ITALService) getService(IServiceLocatorNames.HURRICAN_TAL_SERVICE,
                    serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ITALService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.exmodules.tal.utils.ITALServiceFinder#getTALService(java.lang.Class)
     */
    @Override
    public ITALService getTALService(Class serviceType) throws ServiceNotFoundException {
        try {
            return (ITALService) getService(IServiceLocatorNames.HURRICAN_TAL_SERVICE,
                    serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + ITALService.class.getName());
        }
    }
}


