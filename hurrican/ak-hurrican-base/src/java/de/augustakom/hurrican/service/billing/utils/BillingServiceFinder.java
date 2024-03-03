/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2004 09:02:05
 */
package de.augustakom.hurrican.service.billing.utils;

import org.apache.log4j.Logger;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;


/**
 * ServiceFinder-Implementierung, um nach Billing-Services zu suchen.
 *
 *
 */
public class BillingServiceFinder extends HurricanServiceFinder implements IBillingServiceFinder {

    private static final Logger LOGGER = Logger.getLogger(BillingServiceFinder.class);

    private static BillingServiceFinder instance = null;

    /* Privater Konstruktor */
    protected BillingServiceFinder() {
    }

    /**
     * Gibt eine Singleton-Instanz der Klasse zurueck.
     *
     * @return Singleton-Instanz der Klasse.
     */
    public static BillingServiceFinder instance() {
        if (instance == null) {
            instance = new BillingServiceFinder();
        }
        return instance;
    }


    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.String,
     * java.lang.Class)
     */
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_BILLING_SERVICE, serviceName, serviceType);
        }
        catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IBillingService.class.getName());
        }
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.Class)
     */
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException {
        try {
            return getService(IServiceLocatorNames.HURRICAN_BILLING_SERVICE, serviceType.getName(), serviceType);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceNotFoundException("Der gesuchte Service ist nicht vom Typ " + IBillingService.class.getName());
        }
    }
}


