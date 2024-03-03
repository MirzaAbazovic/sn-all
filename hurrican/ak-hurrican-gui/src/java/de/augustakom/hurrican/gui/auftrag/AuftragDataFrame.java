/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2004 08:15:17
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKSaveableAware;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJMenu;
import de.augustakom.common.gui.swing.AKJMenuItem;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.common.gui.swing.AdministrationMouseListener;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractDataFrame;
import de.augustakom.hurrican.gui.utils.GUIDefinitionHelper;
import de.augustakom.hurrican.gui.utils.MenuHelper;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.gui.GUIDefinition;
import de.augustakom.hurrican.model.cc.view.CCKundeAuftragView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.model.shared.view.DefaultSharedAuftragView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.LoadException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.GUIService;
import de.augustakom.hurrican.service.cc.ProduktService;

/**
 * Frame fuer die Darstellung der Auftragsdaten.
 *
 *
 */
public class AuftragDataFrame extends AbstractDataFrame implements ChangeListener {

    private static final long serialVersionUID = -1381901612541400081L;

    private static final Logger LOGGER = Logger.getLogger(AuftragDataFrame.class);

    private static final String TITLE_BASE = "Auftragsdaten";
    private static final String CANCEL = "cancel";
    private static final String REFRESH = "refresh";

    private CCAuftragModel ccAuftragModel = null;
    Produkt produkt = null;
    private AKJTabbedPane tabbedPane = null;
    private AuftragStammdatenPanel stammdatenPanel = null;

    private List<AKJMenuItem> defaultMenuItems = null;
    private List<AKJMenuItem> produktGruppeMenuItems = null;
    private List<AKJMenuItem> produktMenuItems = null;

    /*
     * HashMap mit MenuItem-Listen fuer die einzelnen Panels. Als Key wird der Klassenname des Panels verwendet.
     */
    private Map<String, List<AKJMenuItem>> menuItemsMap = null;

    private boolean setModelDone = false;
    private int lastSelectedTab = 0;
    private boolean guiInCreation = false;
    private boolean guiInRefresh = false;

    // Services
    ProduktService produktService;
    private GUIService guiService;
    CCAuftragService auftragService;
    KundenService kundenService;

    private AKJButton btnInvis;  // Dirty-Hack: Button dient zur Erkennung von Usern mit Admin-Rolle!

    private Map<String, List<AbstractAuftragPanel.PositionParameter>> positionParameters4Tabs;

    /**
     * Konstruktor
     */
    public AuftragDataFrame() {
        super("de/augustakom/hurrican/gui/auftrag/resources/AuftragDataFrame.xml");
        initServices();
        createGUI();
    }

    void initServices() {
        try {
            produktService = getCCService(ProduktService.class);
            guiService = getCCService(GUIService.class);
            auftragService = getCCService(CCAuftragService.class);
            kundenService = getBillingService(KundenService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected void createGUI() {
        setTitle(TITLE_BASE);
        setIcon("de/augustakom/hurrican/gui/images/auftrag.gif");

        stammdatenPanel = new AuftragStammdatenPanel();
        stammdatenPanel.getAccessibleContext().setAccessibleName("auftrag.stammdaten.panel");

        tabbedPane = new AKJTabbedPane();
        tabbedPane.addChangeListener(this);
        tabbedPane.addTab("Stammdaten", stammdatenPanel);

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().setBorder(null);
        getChildPanel().add(tabbedPane, BorderLayout.CENTER);

        AKJButton btnRefresh = getSwingFactory().createButton(REFRESH, getActionListener());
        AKJButton btnCancel = getSwingFactory().createButton(CANCEL, getActionListener());
        btnInvis = getSwingFactory().createButton("Invis", getActionListener());
        btnInvis.setVisible(false);
        int x = getButtonPanel().getComponentCount();
        // @formatter:off
        getButtonPanel().add(btnCancel  , GBCFactory.createGBC(0, 0, ++x, 0, 1, 1, GridBagConstraints.NONE));
        getButtonPanel().add(btnRefresh , GBCFactory.createGBC(0, 0, ++x, 0, 1, 1, GridBagConstraints.NONE));
        // @formatter:on

        manageGUI(new AKManageableComponent[] { getButton(CMD_SAVE), btnInvis });
        pack();
    }

    @Override
    public void setModel(Observable model) {
        setModelDone = true;
        produktGruppeMenuItems = null;

        if (model instanceof CCAuftragModel) {
            if (this.ccAuftragModel == model) {
                // nur ein refresh, kein neuer Auftrag! save positional parameters of tabs
                savePositionParameters4Tabs();
            }
            this.ccAuftragModel = (CCAuftragModel) model;
            try {
                produkt = getProdukt4Auftrag(ccAuftragModel.getAuftragId());
            }
            catch (FindException e) {
                LOGGER.error(e);
                MessageHelper.showErrorDialog(this, e);
            }
            validateCustomerLock();
            createGUI4Auftrag();
        }
        else {
            this.ccAuftragModel = null;
            this.positionParameters4Tabs = Collections.emptyMap();
            removeTabs(1);
        }

        showTitle();
    }


    /**
     * Ermittelt von jedem Tab (sofern die Komponente davon aus AbstractAuftragPanel ist) die aktuellen
     * {@link de.augustakom.hurrican.gui.auftrag.AbstractAuftragPanel.PositionParameter}s und speichert diese in
     * einer Map ab.
     * @return
     */
    private void savePositionParameters4Tabs() {
        positionParameters4Tabs = new HashMap<>();
        for (int i=0; i<tabbedPane.getTabCount(); i++) {
            Component tab = tabbedPane.getComponentAt(i);
            if (tab instanceof AbstractAuftragPanel) {
                List<AbstractAuftragPanel.PositionParameter> positionParameters4Tab =
                        ((AbstractAuftragPanel) tab).getPositionParameters();
                positionParameters4Tabs.put(tabbedPane.getTitleAt(i), positionParameters4Tab);
            }
        }
    }


    /**
     * Ermittelt die gespeicherten Positions-Parameter zu den einzelnen Tabs und uebergibt diese wieder.
     */
    private void resetPositionParameters4Tabs() {
        if (positionParameters4Tabs != null) {
            for (int i=0; i<tabbedPane.getTabCount(); i++) {
                String title = tabbedPane.getTitleAt(i);
                resetPositionParameters4TabAtIndex(i, title);
            }
        }
    }

    private void resetPositionParameters4TabAtIndex(int tabIndex, String title) {
        if (positionParameters4Tabs != null) {
            Component tab = tabbedPane.getComponentAt(tabIndex);
            if (tab instanceof AbstractAuftragPanel && positionParameters4Tabs.containsKey(title)) {
                ((AbstractAuftragPanel) tab).setPositionParameters(positionParameters4Tabs.get(title));
            }
        }
    }

    /**
     * Ueberprueft, ob der Kunde zum angegebenen Auftrag nach §95 TKG gesperrt ist. In diesem Fall wird das Frame
     * geschlossen und ein entsprechender Hinweis erstellt.
     */
    private void validateCustomerLock() {
        try {
            if (ccAuftragModel != null) {
                Auftrag auftrag = auftragService.findAuftragById(ccAuftragModel.getAuftragId());
                if (auftrag != null) {
                    Kunde kunde = kundenService.findKunde(auftrag.getKundeNo());
                    boolean isLocked = (kunde != null) ? kunde.isLocked() : false;
                    if (isLocked) {
                        closeFrame();
                        MessageHelper.showInfoDialog(getMainFrame(),
                                "Auftrag kann nicht geöffnet werden, da Kunde nach §95 TKG gesperrt ist!");
                    }
                }
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /* Erzeugt die GUI fuer einen CC-Auftrag (bzw. fuer eine best. ProduktGruppe). */
    void createGUI4Auftrag() {
        try {
            setWaitCursor();
            guiInCreation = true;
            showProgressBar("Auftrag laden...");

            // TabbedPanes mit Index >= 1 entfernen
            tabbedPane.removeChangeListener(this);
            removeTabs(1);
            lastSelectedTab = 0;
            tabbedPane.setSelectedIndex(-1);
            tabbedPane.addChangeListener(this);
            tabbedPane.setSelectedIndex(0);

            if (produkt == null) {
                produkt = getProdukt4Auftrag(ccAuftragModel.getAuftragId());
            }

            if (produkt != null) {
                loadPanels();
                loadActions();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            guiInCreation = false;
            notifyMenuOwnerListeners();
            stopProgressBar();
            setDefaultCursor();
            pack();
        }
    }

    /* Laedt die notwendigen Panels */
    private void loadPanels() throws LoadException {
        List<GUIDefinition> allDefs = new ArrayList<>();
        // Panels speziell fuer das Produkt auslesen und der TabbedPane hinzufuegen
        List<GUIDefinition> prodDefs = guiService.getGUIDefinitions4Reference(produkt.getId(), Produkt.class.getName(),
                GUIDefinition.TYPE_PANEL);
        if(prodDefs != null) {
            allDefs.addAll(prodDefs);
        }
        // Panels fuer ProduktGruppe auslesen und der TabbedPane hinzufuegen
        List<GUIDefinition> defs = guiService.getGUIDefinitions4Reference(produkt.getProduktGruppeId(),
                ProduktGruppe.class.getName(), GUIDefinition.TYPE_PANEL);
        if(defs != null) {
            allDefs.addAll(defs);
        }
        allDefs.sort((a, b)-> ObjectUtils.compare(a.getOrderNo(), b.getOrderNo(), true));

        List<AKJPanel> panels = GUIDefinitionHelper.createPanels(allDefs);
        for (AKJPanel panel : panels) {
            tabbedPane.addTab(panel.getAccessibleContext().getAccessibleDescription(), panel);
        }
    }

    /* Laedt die notwendigen Actions */
    private void loadActions() throws LoadException {
        AdministrationMouseListener adminML = new AdministrationMouseListener();
        // Actions fuer die ProduktGruppe auslesen
        produktGruppeMenuItems = new ArrayList<AKJMenuItem>();
        List<GUIDefinition> pgActionDefs = guiService.getGUIDefinitions4Reference(produkt.getProduktGruppeId(),
                ProduktGruppe.class.getName(), GUIDefinition.TYPE_ACTION);
        List<AKAbstractAction> pgActions = GUIDefinitionHelper.createActions(pgActionDefs);
        for (Iterator<AKAbstractAction> iter = pgActions.iterator(); iter.hasNext(); ) {
            Action action = iter.next();
            action.putValue(AKAbstractAction.MODEL_OWNER, stammdatenPanel);

            AKJMenuItem item = new AKJMenuItem(action);
            item.addMouseListener(adminML);
            item.addMenuKeyListener(adminML);
            produktGruppeMenuItems.add(item);
        }

        // Aktions fuer das Produkt auslesen
        produktMenuItems = new ArrayList<AKJMenuItem>();
        List<GUIDefinition> prodActionDefs = guiService.getGUIDefinitions4Reference(produkt.getId(),
                Produkt.class.getName(), GUIDefinition.TYPE_ACTION);
        List<AKAbstractAction> prodActions = GUIDefinitionHelper.createActions(prodActionDefs);
        for (Iterator<AKAbstractAction> iter = prodActions.iterator(); iter.hasNext(); ) {
            Action action = iter.next();
            action.putValue(AKAbstractAction.MODEL_OWNER, stammdatenPanel);

            AKJMenuItem item = new AKJMenuItem(action);
            item.addMouseListener(adminML);
            item.addMenuKeyListener(adminML);
            produktMenuItems.add(item);
        }
    }

    @Override
    public AKJMenu getMenuOfOwner() {
        try {
            // Standard-Actions auslesen (Actions, die dem Stammdaten-Panel zugeordnet sind)
            if (defaultMenuItems == null) {
                defaultMenuItems = getMenuItems4Component(stammdatenPanel);
            }

            // Actions auslesen, die dem aktuellen Panel zugeordnet sind
            List<AKJMenuItem> actMenuItems = null;
            if (tabbedPane.getSelectedIndex() > 0) {
                actMenuItems = getMenuItems4Component(tabbedPane.getSelectedComponent());
            }

            // Actions dem Menu uebergeben
            AKJMenu menu = MenuHelper.createMenu("Auftrag", null, KeyEvent.VK_A);
            MenuHelper.addMenuItems2Menu(menu, defaultMenuItems);
            MenuHelper.addMenuItems2Menu(menu, produktGruppeMenuItems, true);
            MenuHelper.addMenuItems2Menu(menu, actMenuItems, true);
            MenuHelper.addMenuItems2Menu(menu, produktMenuItems, true);

            return menu;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    /* Gibt die MenuItems zurueck, die der Komponente <code>comp</code> zugeordnet sind. */
    private List<AKJMenuItem> getMenuItems4Component(Component comp) throws FindException, LoadException {
        if (menuItemsMap == null) {
            menuItemsMap = new HashMap<String, List<AKJMenuItem>>();
        }

        if ((comp != null) && !menuItemsMap.containsKey(comp.getClass().getName())) {
            GUIDefinition guiDefSP = guiService.findGUIDefinitionByClass(comp.getClass().getName());
            if (guiDefSP != null) {
                AdministrationMouseListener adminML = new AdministrationMouseListener();
                List<GUIDefinition> defs = guiService.getGUIDefinitions4Reference(guiDefSP.getId(),
                        GUIDefinition.class.getName(), GUIDefinition.TYPE_ACTION);
                List<AKAbstractAction> actions = GUIDefinitionHelper.createActions(defs);
                List<AKJMenuItem> retVal = new ArrayList<>();
                for (Iterator<AKAbstractAction> iter = actions.iterator(); iter.hasNext(); ) {
                    Action action = iter.next();
                    action.putValue(AKAbstractAction.MODEL_OWNER, comp);

                    AKJMenuItem item = new AKJMenuItem(action);
                    item.addMouseListener(adminML);
                    item.addMenuKeyListener(adminML);
                    retVal.add(item);
                }

                menuItemsMap.put(comp.getClass().getName(), retVal);
                return retVal;
            }
        }
        else if (comp != null) {
            return menuItemsMap.get(comp.getClass().getName());
        }

        return new ArrayList<AKJMenuItem>();
    }

    @Override
    public Object getModel() {
        return this.ccAuftragModel;
    }

    @Override
    public void readModel() {
        // not used
    }

    @Override
    public void saveModel() {
        try {
            callSaveOnTab(tabbedPane.getSelectedIndex());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /*
     * Uebergibt dem Panel am Index <code>tabIndex</code>, ob speichern erlaubt ist oder nicht.
     */
    private void setSaveableOnTab(int tabIndex) {
        Component comp = tabbedPane.getComponentAt(tabIndex);
        if (comp instanceof AKSaveableAware) {
            ((AKSaveableAware) comp).setSaveable(isSaveEnabled());
        }
    }

    /*
     * Ruft die Save-Methode des Panels auf, das auf der TabbedPane an Index <code>tabIndex</code> dargestellt wird.
     *
     * @param tabIndex
     *
     * @throws PropertyVetoException wenn beim Speichern ein Fehler auftritt.
     */
    private void callSaveOnTab(int tabIndex) throws PropertyVetoException {
        setSaveableOnTab(tabIndex);
        Component comp = tabbedPane.getComponentAt(tabIndex);
        if (isSaveEnabled() && (comp instanceof AKModelOwner)) {
            try {
                setWaitCursor();
                showProgressBar("speichern...");
                ((AKModelOwner) tabbedPane.getComponentAt(tabIndex)).saveModel();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
                throw new PropertyVetoException("error.on.save", new PropertyChangeEvent(this, "", null, null));
            }
            finally {
                stopProgressBar();
                setDefaultCursor();
            }
        }
    }

    /**
     * Ueberprueft, ob der Save-Button enabled ist.
     *
     * @return
     *
     */
    public boolean isSaveEnabled() {
        return getButton(CMD_SAVE).isEnabled();
    }

    /**
     * Ruft die Save-Methode auf der aktuell dargestellten Tab auf und schliesst das Frame.
     */
    @Override
    protected int saveQuestion() {
        saveModel();
        return JOptionPane.NO_OPTION;
    }

    @Override
    public boolean hasModelChanged() {
        return super.hasModelChanged(this);
    }

    @Override
    protected void execute(String command) {
        if (REFRESH.equals(command)) {
            refresh();
        }
        else if (CANCEL.equals(command)) {
            closeWithoutSave();
        }
    }

    /**
     * Aktualisiert die Daten des Frames.
     */
    public void refresh() {
        guiInRefresh = true;
        int lastSelected = tabbedPane.getSelectedIndex();
        try {
            setModel((Observable) ccAuftragModel);
            tabbedPane.setSelectedIndex(lastSelected);

            resetPositionParameters4Tabs();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            guiInRefresh = false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // not used
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (setModelDone && (e.getSource() == tabbedPane)) {
            if (!guiInCreation && !guiInRefresh && tabbedPane.getTabCount() > 1) {
                savePositionParameters4Tabs();
            }

            AKModelOwner actualTab = (AKModelOwner) tabbedPane.getComponentAt(lastSelectedTab);
            setSaveableOnTab(tabbedPane.getSelectedIndex());
            if (actualTab.hasModelChanged()) {
                try {
                    tabbedPane.removeChangeListener(this);
                    callSaveOnTab(lastSelectedTab);
                }
                catch (PropertyVetoException ex) {
                    LOGGER.error(ex.getMessage());
                    return;
                }
                finally {
                    tabbedPane.addChangeListener(this);
                }
            }

            // Save-Button validieren
            AKJButton save = getButton(CMD_SAVE);
            save.getAccessibleContext().setAccessibleName(
                    "save." + tabbedPane.getSelectedComponent().getAccessibleContext().getAccessibleName());
            manageGUI(new AKManageableComponent[] { save });

            notifyMenuOwnerListeners();
            try {
                if (tabbedPane.getSelectedComponent() instanceof AKModelOwner) {
                    AKModelOwner newTab = (AKModelOwner) tabbedPane.getSelectedComponent();
                    lastSelectedTab = tabbedPane.getSelectedIndex();

                    try {
                        newTab.setModel((Observable) ccAuftragModel);
                        if (!guiInCreation && !guiInRefresh) {
                            resetPositionParameters4TabAtIndex(lastSelectedTab, tabbedPane.getTitleAt(lastSelectedTab));
                        }
                    }
                    catch (AKGUIException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                        MessageHelper.showErrorDialog(HurricanSystemRegistry.instance().getMainFrame(), ex);
                    }
                }
            }
            finally {
                setSaveableOnTab(tabbedPane.getSelectedIndex());
            }
        }
    }

    @Override
    public String getUniqueName() {
        return "auftrag.daten.frame";
    }

    /**
     * Entfernt alle TabbedPanes ab dem Index <code>index</code>
     */
    private void removeTabs(int index) {
        for (int i = tabbedPane.getTabCount() - 1; i >= index; i--) {
            tabbedPane.remove(i);
        }
        notifyMenuOwnerListeners();
    }


    /* Sucht nach dem Produkt, das dem Auftrag zugeordnet ist. */
    private Produkt getProdukt4Auftrag(Long auftragId) throws FindException {
        return produktService.findProdukt4Auftrag(auftragId);
    }

    /* Erstellt den Titel fuer das Frame */
    private void showTitle() {
        StringBuilder title = new StringBuilder();
        title.append(TITLE_BASE);
        if (this.ccAuftragModel != null) {
            title.append(" für Auftrag ");
            title.append(this.ccAuftragModel.getAuftragId());
        }

        if (this.ccAuftragModel instanceof DefaultSharedAuftragView) {
            DefaultSharedAuftragView view = (DefaultSharedAuftragView) ccAuftragModel;
            title.append(" - Kunde: ");
            title.append(view.getKundeNo());
        }
        else if (this.ccAuftragModel instanceof CCKundeAuftragView) {
            CCKundeAuftragView view = (CCKundeAuftragView) ccAuftragModel;
            title.append(" - Kunde: ");
            title.append(view.getKundeNo());
        }

        try {
            String vbz = (String) PropertyUtils.getProperty(this.ccAuftragModel, "vbz");
            if (StringUtils.isNotBlank(vbz)) { // Bug-ID: 11473
                title.append(" - " + VerbindungsBezeichnung.VBZ_BEZEICHNUNG + ": ");
                title.append(vbz);
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }

        setTitle(title.toString());
    }

    /**
     * Oeffnet oder aktiviert das Frame und zeigt die Daten von <code>auftragModel</code> an.
     *
     * @param auftragModel Modell, dessen Daten angezeigt werden sollen.
     */
    public static void openFrame(CCAuftragModel auftragModel) {
        AbstractMDIMainFrame mainFrame = HurricanSystemRegistry.instance().getMainFrame();
        AKJInternalFrame[] frames = mainFrame.findInternalFrames(AuftragDataFrame.class);
        AuftragDataFrame dataFrame = null;
        if ((frames != null) && (frames.length == 1)) {
            dataFrame = (AuftragDataFrame) frames[0];
            mainFrame.activateInternalFrame(dataFrame.getUniqueName());
        }
        else {
            dataFrame = new AuftragDataFrame();
            mainFrame.registerFrame(dataFrame, false);
        }

        dataFrame.setModel((Observable) auftragModel);
    }
}
