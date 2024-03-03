/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.07.2004 16:15:26
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJAbstractDialog;
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


/**
 * Abstrakte Dialog-Klasse, ueber die auch Services gefunden werden koennen.
 *
 *
 */
public abstract class AbstractServiceDialog extends AKJAbstractDialog implements IServiceFinder, ICCServiceFinder, IBillingServiceFinder {

    /**
     * @param resource
     */
    public AbstractServiceDialog(String resource) {
        super(resource);
    }

    /**
     * @param resource
     * @param owner
     */
    public AbstractServiceDialog(String resource, Frame owner) {
        super(resource, owner);
    }

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
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     *
     * @param components
     */
    protected void manageGUI(AKManageableComponent[] components) {
        ManageGuiComponentHelper.manageGUI(this, components);
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     *
     * @param parent
     * @param components
     */
    protected void manageGUI(String parent, AKManageableComponent[] components) {
        ManageGuiComponentHelper.manageGUI(parent, components);
    }

}


