/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2007 13:13:05
 */
package de.augustakom.hurrican.service.exmodules;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.service.base.impl.DefaultHurricanService;


/**
 * Abstrakte Service-Klasse fuer alle ExModule-Services.
 *
 *
 */
public abstract class DefaultExModuleService extends DefaultHurricanService {

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
     * @see DefaultExModuleService#getBillingService(String, Class)
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
     * @see DefaultExModuleService#getCCService(String, Class)
     */
    protected Object getCCService(Class type) throws ServiceNotFoundException {
        return getCCService(type.getName(), type);
    }

}
