/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.08.2004 08:58:01
 */
package de.augustakom.hurrican.gui.base;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.wizard.AKJWizardOptionDialog;
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
 * Ableitung von AKJWizardOptionDialog, die Operationen zur Verfuegung stellt, um nach Services zu suchen.
 */
public abstract class AbstractServiceWizardOptionDialog extends AKJWizardOptionDialog implements IServiceFinder,
        ICCServiceFinder, IBillingServiceFinder {

    private static final long serialVersionUID = 1498695916984427132L;

    /**
     * @param resource URL der Resource-Datei
     */
    public AbstractServiceWizardOptionDialog(String resource) {
        super(resource);
    }

    /**
     * @param resource URL der Resource-Datei
     * @param title    Titel fuer den Dialog
     * @param iconURL  Icon-URL fuer den Dialog
     */
    public AbstractServiceWizardOptionDialog(String resource, String title, String iconURL) {
        super(resource, title, iconURL);
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

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(AKManageableComponent[] components) {
        ManageGuiComponentHelper.manageGUI(this, components);
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(String parent, AKManageableComponent[] components) {
        ManageGuiComponentHelper.manageGUI(parent, components);
    }

}


