/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 08:46:14
 */
package de.augustakom.hurrican.gui.base;

import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelWatcher;
import de.augustakom.common.gui.swing.AKJAbstractInternalFrame;
import de.augustakom.common.gui.swing.AKJStatusBar;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.ObjectChangeDetector;
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
 * Abstrakte Implementierung eines InternalFrames, ueber das Service-Objekte gefunden/geladen werden koennen.
 *
 *
 */
public abstract class AbstractInternalServiceFrame extends AKJAbstractInternalFrame implements IServiceFinder, ICCServiceFinder, IBillingServiceFinder, AKModelWatcher {

    private ObjectChangeDetector detector = null;

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     *
     * @param resource
     */
    public AbstractInternalServiceFrame(String resource) {
        super(resource);
        detector = new ObjectChangeDetector();
    }

    /**
     * @see de.augustakom.hurrican.service.base.utils.IServiceFinder#findService(java.lang.String, java.lang.Class)
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

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#addObjectToWatch(java.lang.String, java.lang.Object)
     */
    public void addObjectToWatch(String name, Object toWatch) {
        detector.addObjectToWatch(name, toWatch);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#removeObjectFromWatch(java.lang.String)
     */
    public void removeObjectFromWatch(String name) {
        detector.removeObjectFromWatch(name);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#removeObjectsFromWatch()
     */
    public void removeObjectsFromWatch() {
        detector.removeObjectsFromWatch();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelWatcher#hasChanged(java.lang.String, java.lang.Object)
     */
    public boolean hasChanged(String name, Object actualModel) {
        return detector.hasChanged(name, actualModel);
    }

    /**
     * Stellt in der Status-Bar des MainFrames einen Fortschritssbalken dar.
     *
     * @param message optionale Message
     */
    protected void showProgressBar(final String message) {
        HurricanSystemRegistry.instance().getMainFrame().showProgress(
                getNumberOfAreas() - 1, AKJStatusBar.START_PROGRESS, message, true);
    }

    /**
     * Stoppt den Fortschrittsbalken in der StatusBar des MainFrames.
     */
    protected void stopProgressBar() {
        HurricanSystemRegistry.instance().getMainFrame().showProgress(
                getNumberOfAreas() - 1, AKJStatusBar.STOP_PROGRESS, " ", false);
    }

    private int getNumberOfAreas() {
        return HurricanSystemRegistry.instance().getMainFrame().getStatusBar().getAreaCount();
    }
}


