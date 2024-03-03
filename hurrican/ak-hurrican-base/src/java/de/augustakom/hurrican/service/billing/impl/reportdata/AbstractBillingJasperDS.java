/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.03.2005 14:40:55
 */
package de.augustakom.hurrican.service.billing.impl.reportdata;

import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.impl.AbstractHurricanJasperDS;
import de.augustakom.hurrican.service.base.utils.IServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;


/**
 * Abstrakte Klasse fuer Jasper-DataSources im Billing-Bereich.
 *
 *
 */
public abstract class AbstractBillingJasperDS extends AbstractHurricanJasperDS implements IServiceFinder,
        IBillingServiceFinder {

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(java.lang.String, java.lang.Class)
     */
    public IHurricanService findService(String serviceName, Class serviceType) throws ServiceNotFoundException {
        return null;
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

}


