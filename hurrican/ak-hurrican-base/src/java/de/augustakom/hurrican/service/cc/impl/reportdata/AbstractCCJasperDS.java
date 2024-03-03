/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2005 12:52:20
 */
package de.augustakom.hurrican.service.cc.impl.reportdata;

import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.reports.AKReportException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder;


/**
 * Abstrakte Klasse fuer Jasper-DataSources.
 *
 *
 */
public abstract class AbstractCCJasperDS extends AbstractHurricanJasperDS implements IBillingServiceFinder,
        ICCServiceFinder, IInternetServiceFinder {

    public static final String VBZ_KEY = "VBZ";

    /**
     * Kann von den Ableitungen implementiert werden, um die DataSource zu initialisieren (z.B. um die notwendigen Daten
     * zu laden).
     */
    protected abstract void init() throws AKReportException;

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     */
    protected <T extends IAuthenticationService> T getAuthenticationService(Class<T> type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(type.getName(), type, null);
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
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        return getCCService(serviceType.getName(), serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_CC_SERVICE);
        return locator.getService(serviceName, serviceType, null);
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.Class)
     */
    public <T extends IInternetService> T getInternetService(Class<T> serviceType) throws ServiceNotFoundException {
        return getInternetService(serviceType.getName(), serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder#getInternetService(java.lang.String,
     * java.lang.Class)
     */
    public <T extends IInternetService> T getInternetService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_INTERNET_SERVICE);
        return locator.getService(serviceName, serviceType, null);
    }

    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return null;
    }

}


