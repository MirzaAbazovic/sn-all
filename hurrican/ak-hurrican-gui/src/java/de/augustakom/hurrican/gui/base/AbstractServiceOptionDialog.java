/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.06.2004 08:53:03
 */
package de.augustakom.hurrican.gui.base;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import de.augustakom.authentication.service.IAuthenticationService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelWatcher;
import de.augustakom.common.gui.swing.AKJAbstractOptionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJStatusBar;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.ObjectChangeDetector;
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
import de.augustakom.hurrican.service.reporting.IReportService;
import de.augustakom.hurrican.service.reporting.utils.IReportServiceFinder;
import de.augustakom.hurrican.service.reporting.utils.ReportServiceFinder;

/**
 * Abstrakte Implementierung eines OptionDialogs, ueber den Service-Objekte gefunden/geladen werden koennen. <br><br>
 * Der Dialog besitzt default-maessig die Buttons 'Speichern' und 'Abbrechen', die die Methoden <code>doSave</code> und
 * <code>cancel</code> aufrufen. <br> Sollen die Buttons nicht dargestellt werden, kann der Dialog ueber den Konstruktor
 * <code>AbstractServiceOptionDialog(String, boolean, boolean)</code> erzeugt werden. <br><br> Die Ableitungen sollten
 * ihre GUI-Komponenten nur auf dem Panel platzieren, das sie ueber die Methode <code>getChildPanel</code> erhalten.
 *
 *
 */
public abstract class AbstractServiceOptionDialog extends AKJAbstractOptionDialog implements ICCServiceFinder, IBillingServiceFinder, IReportServiceFinder, AKModelWatcher {

    private static final long serialVersionUID = 2711109071326181834L;

    protected static final String CMD_SAVE = "save";
    protected static final String CMD_CANCEL = "cancel";


    public static final String RESOURCE_WITH_SAVE_BTN = "de/augustakom/hurrican/gui/base/resources/AbstractServiceOptionDialog.xml";
    public static final String RESOURCE_WITH_OK_BTN = "de/augustakom/hurrican/gui/base/resources/AbstractServiceOptionDialog_Ok.xml";
    private String internalResource = RESOURCE_WITH_SAVE_BTN;

    private AKJButton btnSave = null;
    private AKJButton btnCancel = null;

    private boolean showButtons = true;

    private AKJPanel childPanel = null;
    private AKJPanel btnPanel = null;

    private ObjectChangeDetector detector = null;

    /**
     * Konstruktor mit Angabe der Resource-Datei.
     *
     * @param resource
     */
    public AbstractServiceOptionDialog(String resource) {
        super(resource);
        init();
    }

    public AbstractServiceOptionDialog(String resource, String internalResource) {
        super(resource);
        this.internalResource = internalResource;
        init();
    }

    /**
     * Konstruktor mit Angabe der Resource-Datei und ob eine ScrollPane angezeigt werden soll.
     *
     * @param resource
     * @param useScrollPane
     */
    public AbstractServiceOptionDialog(String resource, boolean useScrollPane) {
        super(resource, useScrollPane);
        init();
    }

    /**
     * Konstruktor mit Angabe der Resource-Datei und ob eine ScrollPane angezeigt werden soll.
     *
     * @param resource
     * @param useScrollPane
     * @param showButtons
     */
    public AbstractServiceOptionDialog(String resource, boolean useScrollPane, boolean showButtons) {
        super(resource, useScrollPane);
        this.showButtons = showButtons;
        init();
    }

    /**
     * Gibt das MainFrame der Hurrican-Applikation zurueck.
     *
     * @return
     */
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /* Initialisiert den Dialog */
    private void init() {
        detector = new ObjectChangeDetector();
        createDefaultGUI();
    }

    /* Erstellt die Standard-GUI fuer die Dialog */
    private void createDefaultGUI() {
        if (showButtons) {
            ButtonActionListener al = new ButtonActionListener();

            SwingFactory internalSF = SwingFactory.getInstance(internalResource);
            btnSave = internalSF.createButton(CMD_SAVE, al);
            btnCancel = internalSF.createButton(CMD_CANCEL, al);

            btnPanel = new AKJPanel(new GridBagLayout());
            btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
            btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

            validateSaveButton();
        }

        childPanel = new AKJPanel(new BorderLayout());
        childPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        AKJPanel master = new AKJPanel(new BorderLayout());
        master.add(childPanel, BorderLayout.CENTER);
        if (showButtons) {
            master.add(btnPanel, BorderLayout.SOUTH);
        }

        this.setLayout(new BorderLayout());
        if (useScrollPane) {
            this.add(new AKJScrollPane(master), BorderLayout.CENTER);
        }
        else {
            this.add(master, BorderLayout.CENTER);
        }
    }

    /**
     * Ueberprueft die Berechtigungen fuer den Save- bzw. OK-Button. <br> Ableitungen koennen diese Methode
     * ueberschreiben, um z.B. keine Ueberpruefung fuer den Save-Button zu realisieren.
     */
    protected void validateSaveButton() {
        manageGUI(new AKManageableComponent[] { btnSave });
    }

    /**
     * Gibt das Panel zurueck, auf dem die Ableitungen ihre GUI-Komponenten platzieren koennen.
     */
    @Override
    protected AKJPanel getChildPanel() {
        return childPanel;
    }

    /**
     * Gibt das Panel zurueck, auf dem die Buttons platziert sind.
     *
     * @return
     */
    protected AKJPanel getButtonPanel() {
        return btnPanel;
    }

    /**
     * Wird von dem Speichern-Button aufgerufen.
     */
    protected abstract void doSave();

    /**
     * Wird von dem Abbrechen-Button aufgerufen - schliesst den Dialog.
     */
    protected void cancel() {
        setValue(Integer.valueOf(JOptionPane.CANCEL_OPTION));
    }

    /**
     * Gibt den Button zurueck, der mit dem Namen <code>name</code> uebereinstimmt.
     *
     * @param name Name des gesuchten Buttons
     * @return AKJButton oder <code>null</code>
     */
    protected AKJButton getButton(String name) {
        if (CMD_SAVE.equals(name)) {
            return btnSave;
        }
        else if (CMD_CANCEL.equals(name)) {
            return btnCancel;
        }
        return null;
    }

    /**
     * Konfiguriert einen der Buttons 'Speichern' bzw. 'Abbrechen'.
     *
     * @param btnName Name des zu konfigurierenden Buttons
     * @param text    Text fuer den Button
     * @param tooltip Tooltip-Text fuer den Button
     * @param visible Sichtbarkeit des Buttons
     * @param enabled Button auf enabled/disabled setzen.
     */
    protected void configureButton(String btnName, String text, String tooltip, boolean visible, boolean enabled) {
        AKJButton btn = getButton(btnName);
        if (btn != null) {
            btn.setText(text);
            btn.setToolTipText(tooltip);
            btn.setVisible(visible);
            btn.setEnabled(enabled);
        }
    }

    /**
     * Stellt in der Status-Bar des MainFrames einen Fortschritssbalken dar.
     *
     * @param message optionale Message
     */
    protected void showProgressBar(String message) {
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

    public <T extends IElektraService> T getElektraService(Class<T> serviceType) throws ServiceNotFoundException {
        return HurricanServiceFinder.instance().findService(serviceType.getName(), serviceType);
    }

    /**
     * Sucht nach einem Authentication-Service und gibt diesen zurueck.
     *
     * @param name Name des gesuchten Services.
     * @param type Typ des gesuchten Services.
     * @return gesuchter Service vom Typ <code>type</code>
     * @throws ServiceNotFoundException wenn der Service nicht gefunden werden konnte.
     */
    protected <T extends IAuthenticationService> T getAuthenticationService(String name, Class<T> type) throws ServiceNotFoundException {
        IServiceLocator locator = ServiceLocatorRegistry.instance().getServiceLocator(
                IServiceLocatorNames.AUTHENTICATION_SERVICE);
        return locator.getService(name, type, null);
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

    @Override
    public void addObjectToWatch(String name, Object toWatch) {
        detector.addObjectToWatch(name, toWatch);
    }

    @Override
    public void removeObjectFromWatch(String name) {
        detector.removeObjectFromWatch(name);
    }

    @Override
    public void removeObjectsFromWatch() {
        detector.removeObjectsFromWatch();
    }

    @Override
    public boolean hasChanged(String name, Object actualModel) {
        return detector.hasChanged(name, actualModel);
    }

    /**
     * ActionListener fuer die internen Buttons.
     */
    class ButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (CMD_SAVE.equals(e.getActionCommand())) {
                doSave();
            }
            else if (CMD_CANCEL.equals(e.getActionCommand())) {
                cancel();
            }
        }
    }
}


