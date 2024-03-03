/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.03.2005 11:02:00
 */
package de.augustakom.hurrican.gui.utils;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder;
import de.augustakom.hurrican.service.internet.utils.InternetServiceFinder;


/**
 * Abstrakte Klasse fuer Util-Klassen, die Services benoetigen.
 *
 *
 */
public abstract class AbstractHurricanServiceHelper implements IServiceFinder, ICCServiceFinder,
        IBillingServiceFinder, IInternetServiceFinder {

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(String, String)
     */
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.String,
     * java.lang.Class)
     */
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.Class)
     */
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.Class)
     */
    public IInternetService getInternetService(Class serviceType) throws ServiceNotFoundException {
        return InternetServiceFinder.instance().getInternetService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.String,
     * java.lang.Class)
     */
    public IInternetService getInternetService(String serviceName, Class serviceType) throws ServiceNotFoundException {
        return InternetServiceFinder.instance().getInternetService(serviceName, serviceType);
    }

}


