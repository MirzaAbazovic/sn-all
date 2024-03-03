/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2004 15:56:53
 */
package de.augustakom.hurrican.gui.base;

import javax.swing.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;
import de.augustakom.hurrican.service.reporting.utils.IReportServiceFinder;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;


/**
 * Abstrakte Action-Klasse, ueber die auch Services bezogen werden koennen.
 *
 *
 */
public abstract class AbstractServiceAction extends AKAbstractAction implements ICCServiceFinder, IReportServiceFinder, IBillingServiceFinder {

    public AbstractServiceAction() {
        super();
    }

    public AbstractServiceAction(String name) {
        super(name);
    }

    public AbstractServiceAction(String name, Icon icon) {
        super(name, icon);
    }

    /**
     * Gibt das MainFrame der Hurrican-Applikation zurueck.
     */
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    @Override
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceName, serviceType);
    }

    @Override
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceName, serviceType);
    }

    @Override
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceType);
    }

    @Override
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceName, serviceType);
    }

    @Override
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceType);
    }

    @Override
    public <T extends IReportService> T getReportService(Class<T> serviceType) throws ServiceNotFoundException {
        return ReportServiceFinder.instance().getReportService(serviceType);
    }

    @Override
    public <T extends IReportService> T getReportService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return ReportServiceFinder.instance().getReportService(serviceName, serviceType);
    }

}


