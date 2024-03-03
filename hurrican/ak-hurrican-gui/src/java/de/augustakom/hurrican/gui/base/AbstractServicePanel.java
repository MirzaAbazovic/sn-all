/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 08:16:11
 */
package de.augustakom.hurrican.gui.base;

import java.awt.*;
import java.util.*;
import java.util.List;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.swing.AKJAbstractPanel;
import de.augustakom.common.gui.swing.AKJStatusBar;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.mail.IHurricanMailSender;
import de.augustakom.hurrican.HurricanConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.service.base.iface.IHurricanService;
import de.augustakom.hurrican.service.base.utils.HurricanServiceFinder;
import de.augustakom.hurrican.service.billing.IBillingService;
import de.augustakom.hurrican.service.billing.utils.BillingServiceFinder;
import de.augustakom.hurrican.service.billing.utils.IBillingServiceFinder;
import de.augustakom.hurrican.service.cc.ICCService;
import de.augustakom.hurrican.service.cc.utils.CCServiceFinder;
import de.augustakom.hurrican.service.cc.utils.ICCServiceFinder;
import de.augustakom.hurrican.service.elektra.IElektraService;
import de.augustakom.hurrican.service.internet.IInternetService;
import de.augustakom.hurrican.service.internet.utils.IInternetServiceFinder;
import de.augustakom.hurrican.service.internet.utils.InternetServiceFinder;
import de.augustakom.hurrican.service.reporting.IReportService;
import de.augustakom.hurrican.service.reporting.utils.IReportServiceFinder;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;

/**
 * Abstrakte Implementierung eines Panels, ueber das Service-Objekte gefunden/geladen werden koennen.
 *
 *
 */
public abstract class AbstractServicePanel extends AKJAbstractPanel implements ICCServiceFinder, IBillingServiceFinder,
        IInternetServiceFinder, IReportServiceFinder {

    private static final long serialVersionUID = 3156645778181719897L;

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     */
    public AbstractServicePanel(String resource) {
        super(resource);
    }

    /**
     * Konstruktor mit Angabe der Resource-Datei und des zu verwendenden Layout-Managers.
     */
    public AbstractServicePanel(String resource, LayoutManager layout) {
        super(resource, layout);
    }

    /**
     * Gibt das MainFrame der Hurrican-Applikation zurueck. <br> Das MainFrame wird ueber die HurricanSystemRegistry
     * ausgelesen!
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
    public <T extends IBillingService> T getBillingService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
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
    public <T extends IInternetService> T getInternetService(Class<T> serviceType) throws ServiceNotFoundException {
        return InternetServiceFinder.instance().getInternetService(serviceType);
    }

    @Override
    public <T extends IInternetService> T getInternetService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return InternetServiceFinder.instance().getInternetService(serviceName, serviceType);
    }

    @Override
    public <T extends IReportService> T getReportService(Class<T> serviceType) throws ServiceNotFoundException {
        return ReportServiceFinder.instance().getReportService(serviceType);
    }

    @Override
    public <T extends IReportService> T getReportService(String serviceName, Class<T> serviceType)
            throws ServiceNotFoundException {
        return ReportServiceFinder.instance().getReportService(serviceName, serviceType);
    }

    public <T extends IElektraService> T getElektraService(Class<T> serviceType) throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceType.getName(), serviceType);
    }

    /**
     * @return einen Service um Mails zu versenden (remote ueber server).
     * @throws ServiceNotFoundException
     */
    public IHurricanMailSender getMailService() throws ServiceNotFoundException {
        return ServiceLocatorRegistry.instance().getServiceLocator().getService("mailSender", IHurricanMailSender.class, null);
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected <T extends IAuthenticationService> T getAuthenticationService(Class<T> type)
            throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(type.getName(), type, null);
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(AKManageableComponent... components) {
        ManageGuiComponentHelper.manageGUI(this, components);
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(Collection<AKManageableComponent> components) {
        ManageGuiComponentHelper.manageGUI(this, Iterables.toArray(components, AKManageableComponent.class));
    }

    /**
     * Wertet das Verhalten fuer die angegebenen Komponenten aus.
     */
    protected void manageGUI(String parent, AKManageableComponent... components) {
        ManageGuiComponentHelper.manageGUI(parent, components);
    }

    /**
     * Sucht nach allen GUI-Komponenten auf dem Panel, die Rollenabhaengig angepasst werden koennen und gibt diese als
     * Array zurueck.
     */
    protected AKManageableComponent[] getAllManageableComponents() {
        List<AKManageableComponent> comps = new ArrayList<AKManageableComponent>();

        Component[] children = getComponents();
        for (Component c : children) {
            if (c instanceof AKManageableComponent) {
                comps.add((AKManageableComponent) c);
            }

            if (c instanceof Container) {
                addManageableComponentsOfContainer(comps, (Container) c);
            }
        }

        return comps.toArray(new AKManageableComponent[comps.size()]);
    }

    /**
     * Speichert alle Components vom Typ AKManageableComponent des Containers und aller Children in der ArrayList ab.
     * REKURSION!!!
     */
    private void addManageableComponentsOfContainer(List<AKManageableComponent> manageable, Container container) {
        Component[] comps = container.getComponents();
        if (comps != null) {
            for (Component comp : comps) {
                if (comp instanceof AKManageableComponent) {
                    manageable.add((AKManageableComponent) comp);
                }

                if (comp instanceof Container) {
                    addManageableComponentsOfContainer(manageable, (Container) comp);
                }
            }
        }
    }

    /**
     * Stellt in der Status-Bar des MainFrames einen Fortschritssbalken dar.
     *
     * @param message optionale Message
     */
    protected void showProgressBar(String message) {
        int areas = HurricanSystemRegistry.instance().getMainFrame().getStatusBar().getAreaCount();
        HurricanSystemRegistry.instance().getMainFrame()
                .showProgress(areas - 1, AKJStatusBar.START_PROGRESS, message, true);
    }

    /**
     * Stoppt den Fortschrittsbalken in der StatusBar des MainFrames.
     */
    protected void stopProgressBar() {
        int areas = HurricanSystemRegistry.instance().getMainFrame().getStatusBar().getAreaCount();
        HurricanSystemRegistry.instance().getMainFrame()
                .showProgress(areas - 1, AKJStatusBar.STOP_PROGRESS, " ", false);
    }

    public static String appendUserAndDateIfChanged(String oldBemerkung, String neuBemerkung) {
        return appendUserAndDateIfChanged(oldBemerkung, neuBemerkung, "dd.MM.yyyy");
    }

    public static String appendUserAndDateIfChanged(String oldBemerkung, String neuBemerkung, String dateFormat) {
        if (StringUtils.isBlank(neuBemerkung) || neuBemerkung.equals(oldBemerkung)) {
            return neuBemerkung;
        }
        String bearbeiter = HurricanSystemRegistry.instance().getCurrentUserName();
        if (bearbeiter == null) {
            bearbeiter = HurricanConstants.UNKNOWN;
        }
        return neuBemerkung + " (" + bearbeiter + ", " + DateTools.formatDate(new Date(), dateFormat) + ")";
    }

    protected void doAndHandleException(final Runnable r, final Logger logger) {
        try {
            r.run();  // NOSONAR squid:S1217 ; helper method for Lamdas - should be executed in current thread!
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }
}
