/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.08.2004 13:47:13
 */
package de.augustakom.hurrican.gui.base;

import java.util.*;

import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.wizard.AKJWizardComponents;
import de.augustakom.common.gui.swing.wizard.AKJWizardPanel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
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
 * Abstrakte Implementierung eines Wizard-Panels, ueber das Service-Objekte gefunden/geladen werden koennen.
 *
 *
 */
public abstract class AbstractServiceWizardPanel extends AKJWizardPanel implements IServiceFinder,
        ICCServiceFinder, IBillingServiceFinder {

    private static final long serialVersionUID = -4448293961540585141L;

    /**
     * Konstruktor
     *
     * @param resource
     * @param wizardComponents
     */
    public AbstractServiceWizardPanel(String resource, AKJWizardComponents wizardComponents) {
        super(resource, wizardComponents);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends IHurricanService> T findService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.String,
     * java.lang.Class)
     */
    @Override
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder#getBillingService(java.lang.Class)
     */
    @Override
    public <T extends IBillingService> T getBillingService(Class<T> serviceType) throws ServiceNotFoundException {
        return BillingServiceFinder.instance().getBillingService(serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.String, java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceName, serviceType);
    }

    /**
     * @see de.augustakom.hurrican.service.cc.utils.ICCServiceFinder#getCCService(java.lang.Class)
     */
    @Override
    public <T extends ICCService> T getCCService(Class<T> serviceType) throws ServiceNotFoundException {
        return CCServiceFinder.instance().getCCService(serviceType);
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    protected <T> T getService(Class<T> type) {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.HURRICAN_UNIFIED_SERVICE);
        return (T) locator.getBean(type.getName());
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param type
     * @param <T>
     * @return
     * @throws ServiceNotFoundException
     */
    public <T extends IAuthenticationService> T getAuthenticationService(Class<T> type)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(type.getName(), type, null);
    }

    /**
     * Gibt die Session-ID aus der Hurrican SystemRegistry zurueck.
     *
     * @return
     *
     */
    public Long getSessionId() {
        return HurricanSystemRegistry.instance().getSessionId();
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


