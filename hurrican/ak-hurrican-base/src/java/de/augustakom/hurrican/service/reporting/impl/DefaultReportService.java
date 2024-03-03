/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.02.2007 10:40:48
 */
package de.augustakom.hurrican.service.reporting.impl;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.base.impl.DefaultHurricanService;
import de.augustakom.hurrican.service.reporting.IReportService;


/**
 * Basisklasse fuer alle Hurrican Service-Implementierungen im Reporting-Bereich.
 *
 *
 */
public abstract class DefaultReportService extends DefaultHurricanService implements IReportService {

    /**
     * Gibt einen Billing-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return Billing-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected Object getBillingService(String name, Class type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_BILLING_SERVICE);
        return locator.getService(name, type, null);
    }

    /**
     * @see getBillingService(String, Class)
     */
    protected Object getBillingService(Class type) throws ServiceNotFoundException {
        return getBillingService(type.getName(), type);
    }

    /**
     * Gibt einen anderen CC-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return CC-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected Object getCCService(String name, Class type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_CC_SERVICE);
        return locator.getService(name, type, null);
    }

    /**
     * @see getCCService(String, Class)
     */
    protected Object getCCService(Class type) throws ServiceNotFoundException {
        return getCCService(type.getName(), type);
    }

    /**
     * Gibt einen Report-Service zurueck.
     *
     * @param name Name des Services.
     * @param type Typ des Services
     * @return Report-Service
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected Object getReportService(String name, Class type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_REPORT_SERVICE);
        return locator.getService(name, type, null);
    }

    /**
     * @see getReportService(String, Class)
     */
    protected Object getReportService(Class type) throws ServiceNotFoundException {
        return getReportService(type.getName(), type);
    }

}


