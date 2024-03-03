/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 10:02:05
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKNavigationBarListener;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJNavigationBar;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.SwingFactory;
import de.augustakom.common.gui.swing.table.AKFilterTableModelListener;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.ArrayTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanGUIConstants;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.TableOpenOrderAction;
import de.augustakom.hurrican.gui.utils.PrintVerlaufHelper;
import de.augustakom.hurrican.model.billing.Kunde;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.ProjektierungsView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.KundenService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;


/**
 * Panel fuer die Darstellung der Projektierungen fuer eine Abteilung.
 */
public abstract class AbstractProjektierungPanel extends AbstractVerlaufPanel implements
        AKDataLoaderComponent, ActionListener, AKTableOwner, AKObjectSelectionListener, AKFilterTableModelListener,
        ItemListener, AKNavigationBarListener {

    private static final Logger LOGGER = Logger.getLogger(AbstractProjektierungPanel.class);

    private static final SwingFactory internalSF =
            SwingFactory.getInstance("de/augustakom/hurrican/gui/verlauf/resources/AbstractProjektierungPanel.xml");

    private static final Integer COL_NIEDERLASSUNG = 13;
    private static final Integer COL_AM_BEREICH = 14;
    private static final long serialVersionUID = 1689863792044985360L;

    protected Niederlassung niederlassung = null;
    protected AKUser user = null;

    private boolean showRL = false;
    private boolean showESPanel = true;

    private AKJPanel childPanel = null;

    // GUI-Komponenten
    private VerlaufTable tbViews = null;
    private AKReferenceAwareTableModel<ProjektierungsView> tableModel = null;
    private AKJTextField tfVbz = null;
    private AKJTextField tfProdukt = null;
    private AKJTextField tfOeName = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfOrderNoOrig = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJDateComponent dcVorgabeAm = null;
    private AKJDateComponent dcAnDispo = null;
    private AKJTextField tfBearbeiter = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfKundeVorname = null;
    private AKJTextArea taBemerkungen = null;
    private AKJComboBox cbAbteilung = null;
    private AKJComboBox cbNiederlassung = null;
    private EndstellenDetailTableModel tbMdlES = null;
    private AKJPanel detailPanel = null;
    private VerlaufOrdersPanel voPanel = null;
    private AKJNavigationBar navBar4Endstellen = null;
    private AKJFormattedTextField tfAuftragId4Endstellen = null;

    // Modelle
    private ProjektierungsView actView = null;
    private Map<Long, List<Endstelle>> endstellen4Auftraege = null;

    // Panes
    private AKJTabbedPane tabPane;

    // Sonstiges
    private boolean guiCreated = false;
    private boolean showAbteilungsFilter = false;
    private boolean initDone = false;


    public AbstractProjektierungPanel(String resource, boolean showESPanel) {
        super(resource, null);
        this.showAbteilungsFilter = true;
        this.showESPanel = showESPanel;
        createDefaultGUI();
        init();
    }

    /**
     * Konstruktor mit Angabe aller benoetigten Daten fuer die Projektierungs-Anzeige.
     *
     * @param resource    Resource-File
     * @param abtId       ID der Abteilung
     * @param showRL      Flag, ob nach Ruecklaeufern gesucht werden soll (nur fuer DISPO wichtig)
     * @param showESPanel Flag, ob das Panel mit den Endstellen-Daten angezeigt werden soll
     */
    public AbstractProjektierungPanel(String resource, Long abtId, boolean showRL, boolean showESPanel) {
        super(resource, abtId);
        this.showRL = showRL;
        this.showESPanel = showESPanel;
        createDefaultGUI();
        init();
    }

    /**
     * In dieser Methode koennen die Ableitungen Detail-Daten laden/darstellen, die speziell fuer eine Abteilung
     * benoetigt werden. <br> Die Methode wird aufgerufen, wenn ueber die Navigations-Bar zu einem anderen Datensatz
     * navigiert wird.
     *
     * @param verlaufId ID des Verlaufs (der Projektierung) zu dem navigiert wurde.
     */
    protected abstract void showDetails4Verlauf(Long verlaufId);

    /**
     * Erzeugt die Default-GUI fuer das Panel.
     */
    protected final void createDefaultGUI() {
        TextFieldMouseListener tfML = new TextFieldMouseListener();
        detailPanel = createBasePanel(tfML);
        childPanel = new AKJPanel(new GridBagLayout());

        AKJButton btnRefresh = internalSF.createButton("refresh", this, null);

        String[] colNames = new String[] {
                VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Vorgabe AM", "Proj. an Dispo", "Realisierung", "Bearb. Abt.", "Produkt", "Produkt (Billing)", // 00-05
                "BA-Hinweise", "Kunde", "Haupt-RN", "Bearbeiter AM", "Auftrag-ID", "VPN",                                           // 06-11
                "Niederlassung", "AM-Bereich", "Projektverantwortlicher", "Bündelnr", "Gerätebez." };
        String[] propNames = new String[] {
                "vbz", "vorgabeAm", "projAnDispo", "realisierungstermin", "bearbeiter", "produktName", "oeName",
                "baHinweise", "kundenName", "hauptRN", "bearbeiterAm", "auftragId", "vpnNr",
                "niederlassung", "amResponsibility", "projectResponsibleName", "buendelNr", "geraeteBez" };
        Class<?>[] classTypes = new Class[] {
                String.class, Date.class, Date.class, Date.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, Long.class, Long.class,
                String.class, Long.class, String.class, Long.class, String.class };

        int[] colSize = new int[] { 125, 80, 80, 80, 70, 100, 100, 100, 120, 90, 70, 70, 45, 70, 70, 120, 60, 100 };

        if (showRL) {
            // Ruecklaeufer-Views erhalten in Tabelle das Feld 'erledigt'
            colNames = (String[]) ArrayTools.add(colNames, "erledigt");
            propNames = (String[]) ArrayTools.add(propNames, "erledigt");
            classTypes = (Class[]) ArrayTools.add(classTypes, Boolean.class);
            colSize = ArrayTools.add(colSize, 30);
        }

        AKTableListener tableListener = new AKTableListener(this, false);
        tableModel = new AKReferenceAwareTableModel<>(colNames, propNames, classTypes);
        tableModel.addFilterTableModelListener(this);
        tbViews = new VerlaufTable(true, true);
        tbViews.setModel(tableModel);
        tbViews.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbViews.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbViews.attachSorter();
        tbViews.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbViews.addPopupAction(new TableOpenOrderAction());
        SplitVerlaufAction splitVerlaufAction = new SplitVerlaufAction();
        splitVerlaufAction.setParentClassName(getClassName());
        tbViews.addPopupAction(splitVerlaufAction);
        if (Abteilung.DISPO.equals(abteilungId)) {
            AssignPRToNetzplanungAction assign2NetzplanungAction = new AssignPRToNetzplanungAction();
            assign2NetzplanungAction.setParentClass(this.getClass());
            tbViews.addPopupAction(assign2NetzplanungAction);
        }
        tbViews.fitTable(colSize);
        tbViews.addMouseListener(tableListener);
        tbViews.addKeyListener(tableListener);
        AKJScrollPane spViews = new AKJScrollPane(tbViews, new Dimension(500, 125));

        // Filter-Panel
        AKJLabel lblAbteilung = internalSF.createLabel("abteilung");
        AKJLabel lblNiederlassung = internalSF.createLabel("niederlassung");
        cbAbteilung = internalSF.createComboBox("abteilung",
                new AKCustomListCellRenderer<>(Abteilung.class, Abteilung::getName));
        cbAbteilung.addItemListener(this);
        cbNiederlassung = internalSF.createComboBox("niederlassung",
                new AKCustomListCellRenderer<>(Niederlassung.class, Niederlassung::getName));
        cbNiederlassung.addItemListener(this);

        // @formatter:off
        int nextGridBagXIdx = 0;
        AKJPanel top = new AKJPanel(new GridBagLayout());
        if (showAbteilungsFilter) {
            top.add(lblAbteilung    , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
            top.add(new AKJPanel()  , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
            top.add(cbAbteilung     , GBCFactory.createGBC( 50,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        }
        top.add(lblNiederlassung    , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL, 10));
        top.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        top.add(cbNiederlassung     , GBCFactory.createGBC( 50,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()      , GBCFactory.createGBC(  0,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        top.add(btnRefresh          , GBCFactory.createGBC( 50,  0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(new AKJPanel()      , GBCFactory.createGBC(100,  0, nextGridBagXIdx, 0, 5, 1, GridBagConstraints.HORIZONTAL));
        top.add(spViews             , GBCFactory.createGBC(100,100,                 0, 1,14, 1, GridBagConstraints.BOTH));
        // @formatter:on

        AKJPanel detPnl = new AKJPanel(new GridBagLayout());
        if (showESPanel) {
            AKJLabel lblAuftragIdSubOrder = internalSF.createLabel("auftrag.id");

            tfAuftragId4Endstellen = internalSF.createFormattedTextField("auftrag.id", false);
            tfAuftragId4Endstellen.addMouseListener(tfML);
            navBar4Endstellen = internalSF.createNavigationBar("nav.bar", this, true, true);

            tbMdlES = new EndstellenDetailTableModel();
            AKJTable tbEndstellen = new AKJTable(tbMdlES, JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            tbEndstellen.fitTable(new int[] { 110, 120, 120 });
            AKJScrollPane spTable = new AKJScrollPane(tbEndstellen);
            spTable.setPreferredSize(new Dimension(385, 250));

            // @formatter:off
            AKJPanel navPanel = new AKJPanel(new GridBagLayout());
            navPanel.add(navBar4Endstellen      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            navPanel.add(new AKJPanel()         , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
            navPanel.add(lblAuftragIdSubOrder   , GBCFactory.createGBC(  0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            navPanel.add(new AKJPanel()         , GBCFactory.createGBC(  0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
            navPanel.add(tfAuftragId4Endstellen , GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
            navPanel.add(new AKJPanel()         , GBCFactory.createGBC(  0, 0, 0, 1, 5, 1, GridBagConstraints.NONE));

            AKJPanel tbPnl = new AKJPanel(new BorderLayout());
            tbPnl.setBorder(BorderFactory.createTitledBorder("Endstellen-Daten:"));
            tbPnl.add(navPanel, BorderLayout.NORTH);
            tbPnl.add(spTable, BorderLayout.CENTER);

            detPnl.add(detailPanel      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 2, GridBagConstraints.VERTICAL));
            detPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
            detPnl.add(tbPnl            , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.VERTICAL));
            detPnl.add(childPanel       , GBCFactory.createGBC(100,100, 0, 2, 3, 1, GridBagConstraints.BOTH));
            // @formatter:on
        }
        else {
            // @formatter:off
            detPnl.add(detailPanel      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
            detPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
            detPnl.add(childPanel       , GBCFactory.createGBC(100,100, 0, 1, 2, 1, GridBagConstraints.BOTH));
            // @formatter:on
        }

        AKJScrollPane spDetail = new AKJScrollPane(detPnl);
        spDetail.setBorder(null);

        voPanel = new VerlaufOrdersPanel();

        tabPane = new AKJTabbedPane();
        tabPane.add("Details", spDetail);

        AKJSplitPane splitPane = new AKJSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(1d);  // Top-Panel erhaelt komplette Ausdehnung!
        splitPane.setTopComponent(top);
        splitPane.setBottomComponent(tabPane);

        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        manageGUI(getClassName(), splitVerlaufAction);
        guiCreated = true;
    }

    /**
     * Erzeugt das Panel fuer die Stammdaten einer Projektierung.
     */
    private AKJPanel createBasePanel(TextFieldMouseListener tfML) {
        AKJLabel lblVbz = internalSF.createLabel("vbz");
        AKJLabel lblProdukt = internalSF.createLabel("produkt");
        AKJLabel lblOeName = internalSF.createLabel("oe.name");
        AKJLabel lblAuftragId = internalSF.createLabel("auftrag.id");
        AKJLabel lblOrderNoOrig = internalSF.createLabel("order.no");
        AKJLabel lblVpnNr = internalSF.createLabel("vpn.nr");
        AKJLabel lblVorgabeAm = internalSF.createLabel("vorgabe.am");
        AKJLabel lblAnDispo = internalSF.createLabel("projektierung.an.dispo");
        AKJLabel lblBearbeiter = internalSF.createLabel("bearbeiter.am");
        AKJLabel lblKundeName = internalSF.createLabel("kunde.name");
        AKJLabel lblKundeVorname = internalSF.createLabel("kunde.vorname");
        AKJLabel lblBemerkungen = internalSF.createLabel("bemerkung");
        AKJLabel lblPreventCPSOrder = internalSF.createLabel("prevent.cps.provisioning.order");

        tfVbz = internalSF.createTextField("vbz", false);
        tfVbz.addMouseListener(tfML);
        tfProdukt = internalSF.createTextField("produkt", false);
        tfOeName = internalSF.createTextField("oe.name", false);
        tfAuftragId = internalSF.createFormattedTextField("auftrag.id", false);
        tfAuftragId.addMouseListener(tfML);
        tfOrderNoOrig = internalSF.createFormattedTextField("order.no", false);
        tfVpnNr = internalSF.createFormattedTextField("vpn.nr", false);
        dcVorgabeAm = internalSF.createDateComponent("vorgabe.am", false);
        dcAnDispo = internalSF.createDateComponent("projektierung.an.dispo", false);
        tfBearbeiter = internalSF.createTextField("bearbeiter.am", false);
        tfKundeName = internalSF.createTextField("kunde.name", false);
        tfKundeVorname = internalSF.createTextField("kunde.vorname", false);
        taBemerkungen = internalSF.createTextArea("bemerkung", false);
        taBemerkungen.setWrapStyleWord(true);
        taBemerkungen.setLineWrap(true);
        AKJScrollPane spBemerkungen = new AKJScrollPane(taBemerkungen);
        AKJCheckBox chbPreventCPSOrder = internalSF.createCheckBox("prevent.cps.provisioning.order", false);

        // @formatter:off
        AKJPanel base = new AKJPanel(new GridBagLayout());
        base.add(lblVbz             , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.NONE));
        base.add(tfVbz              , GBCFactory.createGBC(100,  0, 2, 0, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 4, 0, 1, 1, GridBagConstraints.NONE));
        base.add(lblVpnNr           , GBCFactory.createGBC(  0,  0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(new AKJPanel()     , GBCFactory.createGBC(  0,  0, 6, 0, 1, 1, GridBagConstraints.NONE));
        base.add(tfVpnNr            , GBCFactory.createGBC(100,  0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblProdukt         , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfProdukt          , GBCFactory.createGBC(100,  0, 2, 1, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblVorgabeAm       , GBCFactory.createGBC(  0,  0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(dcVorgabeAm        , GBCFactory.createGBC(100,  0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblOeName          , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfOeName           , GBCFactory.createGBC(100,  0, 2, 2, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblAuftragId       , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfAuftragId        , GBCFactory.createGBC(100,  0, 2, 3, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblAnDispo         , GBCFactory.createGBC(  0,  0, 5, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(dcAnDispo          , GBCFactory.createGBC(100,  0, 7, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblOrderNoOrig     , GBCFactory.createGBC(  0,  0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfOrderNoOrig      , GBCFactory.createGBC(100,  0, 2, 4, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblBearbeiter      , GBCFactory.createGBC(  0,  0, 5, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfBearbeiter       , GBCFactory.createGBC(100,  0, 7, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblKundeName       , GBCFactory.createGBC(  0,  0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfKundeName        , GBCFactory.createGBC(100,  0, 2, 5, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblPreventCPSOrder , GBCFactory.createGBC(  0,  0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(chbPreventCPSOrder , GBCFactory.createGBC(100,  0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblKundeVorname    , GBCFactory.createGBC(  0,  0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(tfKundeVorname     , GBCFactory.createGBC(100,  0, 2, 6, 2, 1, GridBagConstraints.HORIZONTAL));
        base.add(lblBemerkungen     , GBCFactory.createGBC(  0,  0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        base.add(new AKJPanel()     , GBCFactory.createGBC(  0,100, 0, 8, 1, 1, GridBagConstraints.VERTICAL));
        base.add(spBemerkungen      , GBCFactory.createGBC(100,  0, 2, 7, 6, 2, GridBagConstraints.BOTH));
        // @formatter:on
        return base;
    }

    /**
     * Gibt die Tabelle mit den Projektierung-Views zurueck.
     */
    @Override
    protected AKJTable getTable() {
        return tbViews;
    }

    /**
     * Initialisiert das Panel.
     */
    private void init() {
        try {
            // Lade Niederlassungen
            NiederlassungService niederlassungService = getCCService(NiederlassungService.class);
            if (showAbteilungsFilter) {
                List<Abteilung> abteilungen = niederlassungService.findAbteilungen4UniversalGui();
                cbAbteilung.addItems(abteilungen, true, Abteilung.class);

                Long abtIdToSelect = HurricanSystemRegistry.instance().getUserConfigAsLong(
                        HurricanGUIConstants.ABTEILUNG_4_PROJEKTIERUNG_GUI);
                if (abtIdToSelect != null) {
                    cbAbteilung.selectItem("getId", Abteilung.class, abtIdToSelect);
                    reloadData();
                }
            }

            List<Niederlassung> niederlassungen = niederlassungService.findNiederlassungen();
            cbNiederlassung.addItems(niederlassungen, true, Niederlassung.class);
            Niederlassung emptyItem = (Niederlassung) cbNiederlassung.getItemAt(0);
            if (emptyItem.getId() == null) {
                emptyItem.setName("Alle");
            }

            // Ermittle User
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService us = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            user = us.findUserBySessionId(sessionId);

            // Selektiere Niederlassung
            if (user != null && user.getNiederlassungId() != null) {
                cbNiederlassung.selectItem("getId", Niederlassung.class, user.getNiederlassungId());
            }

            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> amResponsibilityRefs = rs.findReferencesByType(Reference.REF_TYPE_AM_PART, Boolean.TRUE);
            Map<?, ?> amResponsibilityRefsMap = CollectionMapConverter.convert2Map(amResponsibilityRefs, "getId", null);
            tableModel.addReference(COL_AM_BEREICH, amResponsibilityRefsMap, "strValue");

            initDone = true;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * Filter TableModel nach Niederlassung
     */
    private void filterNiederlassung(Niederlassung niederlassung) {
        if (tableModel != null) {
            // Filter zuruecksetzen
            tableModel.removeFilter(FILTER_NIEDERLASSUNG);
            actView = null;
            if ((niederlassung != null) && (niederlassung.getId() != null)) {
                // Daten nach Niederlassung filtern
                tableModel.addFilter(new FilterOperator(
                        FILTER_NIEDERLASSUNG, FilterOperators.EQ, niederlassung.getName(), COL_NIEDERLASSUNG));
            }
        }
    }

    /**
     * Liefert selektierte Niederlassung
     */
    private Niederlassung getSelectedNiederlassung() {
        Object obj = (cbNiederlassung != null) ? cbNiederlassung.getSelectedItem() : null;
        return ((obj != null) && (obj instanceof Niederlassung)) ? (Niederlassung) obj : null;
    }

    private void reloadData() {
        clear();
        clearTable();

        if (abteilungId != null) {
            loadData();
        }
    }


    @Override
    public final void loadData() {
        // Nicht notwendig auf Buttons aufzupassen, da sich diese auf markierten Tabelleneintrag beziehen!
        final SwingWorker<List<ProjektierungsView>, Void> worker = new SwingWorker<List<ProjektierungsView>, Void>() {
            final Long localAbteilungId = abteilungId;
            final boolean localShowRL = showRL;

            @Override
            protected List<ProjektierungsView> doInBackground() throws Exception {
                BAService bas = getCCService(BAService.class);
                List<ProjektierungsView> views = (localAbteilungId != null)
                        ? bas.findProjektierungen4Abt(localAbteilungId, localShowRL)
                        : Collections.<ProjektierungsView>emptyList();

                for (ProjektierungsView view : views) {
                    Long projectResponsibleId = view.getProjectResponsibleId();

                    if (projectResponsibleId != null) {
                        AKUserService userService = getAuthenticationService(AKUserService.class);
                        AKUser akUser = userService.findById(projectResponsibleId);
                        String userName = akUser.getNameAndFirstName();

                        if (StringUtils.isNotEmpty(userName)) {
                            view.setProjectResponsibleName(userName);
                        }
                    }

                    VerlaufAbteilung vab = bas.findVerlaufAbteilung(view.getVerlaufId(), Abteilung.DISPO);
                    view.setProjAnDispo((vab != null) ? vab.getDatumAn() : null);
                }
                return views;
            }

            @Override
            protected void done() {
                try {
                    List<ProjektierungsView> views = get();
                    ObserverHelper.addObserver2Objects(AbstractProjektierungPanel.this, views);
                    tableModel.setData(views);
                    if (CollectionUtils.isEmpty(views)) {
                        MessageHelper.showInfoDialog(getMainFrame(),
                                "Es konnten keine Projektierungen ermittelt werden.", null, true);
                    }
                    else {
                        // Filter nach Niederlassung
                        filterNiederlassung(getSelectedNiederlassung());
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    refreshRowCount();
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        tableModel.setData(null);
        this.clear();
        showProgressBar("lade Projektierungen...");
        worker.execute();
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(final Object obj) {
        clear();
        if (obj instanceof ProjektierungsView) {
            actView = (ProjektierungsView) obj;

            // Nicht notwendig auf Buttons aufzupassen, da sich diese auf markierten Tabelleneintrag beziehen!
            final SwingWorker<WorkerResult, Void> worker = new SwingWorker<WorkerResult, Void>() {
                final ProjektierungsView localActView = actView;
                final boolean localShowESPanel = showESPanel;

                @Override
                protected WorkerResult doInBackground() throws Exception {
                    WorkerResult result = new WorkerResult();

                    BAService bas = getCCService(BAService.class);
                    result.vaDispo = bas.findVerlaufAbteilung(localActView.getVerlaufId(), Abteilung.DISPO);

                    CCAuftragService as = getCCService(CCAuftragService.class);
                    result.ad = as.findAuftragDatenByAuftragId(localActView.getAuftragId());

                    KundenService ks = getBillingService(KundenService.class);
                    result.kunde = ks.findKunde(localActView.getKundeNo());

                    // Endstellen laden
                    if (localShowESPanel) {
                        endstellenLaden(result, bas);
                    }
                    return result;
                }

                private void endstellenLaden(WorkerResult result, BAService bas)
                        throws ServiceNotFoundException, FindException {
                    EndstellenService esSrv = getCCService(EndstellenService.class);

                    Verlauf projektierung = bas.findVerlauf(localActView.getVerlaufId());
                    Set<Long> auftragIds = (projektierung != null) ? projektierung.getAllOrderIdsOfVerlauf() : null;

                    if (CollectionTools.isNotEmpty(auftragIds)) {
                        result.endstellen4Auftraege = new HashMap<>();
                        result.auftragIds = new ArrayList<>();
                        for (Long auftragId : auftragIds) {
                            List<Endstelle> endstellen = esSrv.findEndstellen4Auftrag(auftragId);
                            result.endstellen4Auftraege.put(auftragId, endstellen);
                            if (NumberTools.notEqual(auftragId, localActView.getAuftragId())) {
                                result.auftragIds.add(auftragId);
                            }
                        }
                        Collections.sort(result.auftragIds);
                        result.auftragIds.add(0, localActView.getAuftragId());
                    }
                }

                @Override
                protected void done() {
                    try {
                        tfVbz.setText(localActView.getVbz());
                        tfProdukt.setText(localActView.getProduktName());
                        tfOeName.setText(localActView.getOeName());
                        tfAuftragId.setValue(localActView.getAuftragId());
                        tfOrderNoOrig.setValue(localActView.getAuftragNoOrig());
                        tfVpnNr.setValue(localActView.getVpnNr());
                        dcVorgabeAm.setDate(localActView.getVorgabeAm());
                        tfBearbeiter.setText(localActView.getBearbeiterAm());

                        WorkerResult result = get();

                        dcAnDispo.setDate((result.vaDispo != null) ? result.vaDispo.getDatumAn() : null);
                        taBemerkungen.setText((result.ad != null) ? result.ad.getBemerkungen() : null);

                        Kunde kunde = result.kunde;
                        if (kunde != null) {
                            tfKundeName.setText(kunde.getName());
                            tfKundeVorname.setText(kunde.getVorname());
                        }

                        showDetails4Verlauf(localActView.getVerlaufId());
                        updateAuftragsPanel(localActView);

                        endstellen4Auftraege = result.endstellen4Auftraege;
                        navBar4Endstellen.setData(result.auftragIds);
                    }
                    catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                        MessageHelper.showErrorDialog(getMainFrame(), e);
                    }
                    finally {
                        setDefaultCursor();
                    }
                }
            };

            setWaitCursor();
            worker.execute();
        }
    }

    private void updateAuftragsPanel(ProjektierungsView details) {
        if ((details != null) && BooleanTools.nullToFalse(details.getHasSubOrders())) {
            tabPane.add("Aufträge", voPanel);
            voPanel.setVerlaufId(details.getVerlaufId());
        }
        else {
            tabPane.remove(voPanel);
            voPanel.setVerlaufId(null);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            try {
                setWaitCursor();
                showProgressBar("Öffne Auftrags-Daten...");
                AuftragDataFrame.openFrame((CCAuftragModel) selection);
            }
            finally {
                stopProgressBar();
                setDefaultCursor();
            }
        }
    }

    /**
     * Uebernimmt die aktuelle Projektierung
     */
    protected void projektierungUebernehmen() {
        VerlaufAbteilung verlAbt = null;
        VerlaufAbteilung verlaufAbteilung = null;
        String username = HurricanSystemRegistry.instance().getCurrentUserNameAndFirstName();

        try {

            BAService bas = getCCService(BAService.class);
            verlaufAbteilung = bas.findVerlaufAbteilung(getActView().getVerlaufAbtId());
        }
        catch (Exception e) {
            LOGGER.info(e.getMessage());
            // bei auftretenden Exceptions werden diese ignoriert, da die Übernahme des Verlaufs nicht von diesem Service abruf abhängt
        }

        if (hasBearbeiterChangedSinceLastRefresh(verlaufAbteilung)) {
            @SuppressWarnings("null")
            Integer messageReturn = MessageHelper.showYesNoCancelQuestion(getMainFrame(),
                    "Die Projektierung wird bereits durch " + verlaufAbteilung.getBearbeiter() + " bearbeitet. Möchten Sie die Bearbeitung fortsetzen?",
                    "Projektierung bearbeiten");

            // Wenn trotzdem die Bearbeitung übernommen werden soll, wird der Bearbeiter überschrieben
            if (messageReturn == JOptionPane.YES_OPTION) {
                verlAbt = super.verlaufUebernehmen(getActView());
            }
            else {
                getActView().setBearbeiter(verlaufAbteilung.getBearbeiter());
            }
        }
        else {
            verlAbt = super.verlaufUebernehmen(getActView());
        }

        if (verlAbt != null) {
            getActView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            if (NumberTools.equal(verlAbt.getVerlaufStatusId(), VerlaufStatus.STATUS_IN_BEARBEITUNG)) {
                getActView().setVerlaufAbtStatus("in Bearbeitung");
            }
            getActView().setBearbeiter(username);
            getActView().notifyObservers(true);
        }
    }

    private boolean hasBearbeiterChangedSinceLastRefresh(VerlaufAbteilung verlaufAbteilung) {
        return ((getActView().getBearbeiter() == null) && (verlaufAbteilung != null) && (verlaufAbteilung.getBearbeiter() != null))
                || (((getActView().getBearbeiter() != null) && (verlaufAbteilung != null) && (verlaufAbteilung.getBearbeiter() != null))
                && !getActView().getBearbeiter().equals(verlaufAbteilung.getBearbeiter()));
    }

    /**
     * Schliesst die aktuelle Projektierung fuer die Abteilung ab. Seit HUR-23094 werden die {@link
     * VerlaufAbteilungTechPredicate#TECH_ABTEILUNGEN} nicht mehr geprüft.
     */
    protected void projektierungAbschliessenTechnik() {
        try {
            if ((getActView() != null) && (getActView().getVerlaufAbtId() != null)) {
                if (NumberTools.equal(getActView().getVerlaufAbtStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
                    throw new HurricanGUIException("Projektierung ist bereits abgeschlossen!");
                }

                setWaitCursor();
                BAService bas = getCCService(BAService.class);
                Verlauf verlauf = bas.findVerlauf(getActView().getVerlaufId());
                VerlaufAbteilung va = bas.findVerlaufAbteilung(getActView().getVerlaufAbtId());
                if ((verlauf == null) || (va == null)) {
                    throw new HurricanGUIException("Verlaufs-Datensatz für die Abteilung wurde nicht gefunden!");
                }

                // ErledigenDialog oeffnen mit Abfrage von Bemerkung u. NotPossible
                BauauftragErledigenDialog dlg = new BauauftragErledigenDialog(
                        va, HurricanSystemRegistry.instance().getCurrentUser(),
                        getActView().getRealisierungstermin());
                Object value = DialogHelper.showDialog(this, dlg, true, true);

                if ((value instanceof Integer) && ((Integer) value == JOptionPane.OK_OPTION)) {
                    va.setBearbeiter(HurricanSystemRegistry.instance().getCurrentLoginName());
                    va.setDatumErledigt(new Date());
                    va.setAusgetragenAm(new Date());
                    va.setNotPossible(dlg.isNotPossible());
                    va.setNotPossibleReasonRefId(dlg.getNotPossibleReason());
                    va.setAusgetragenVon(HurricanSystemRegistry.instance().getCurrentLoginName());
                    va.setVerlaufStatusId(VerlaufStatus.STATUS_ERLEDIGT);
                    va.setBemerkung(dlg.getBemerkung());
                    bas.saveVerlaufAbteilung(va);

                    if (BooleanTools.nullToFalse(va.getNotPossible())) {
                        // Verlauf als NOT_POSSIBLE markieren, wenn Abteilung negativ abgeschlossen hat
                        verlauf.setNotPossible(Boolean.TRUE);
                    }
                    verlauf.setVerlaufStatusId(VerlaufStatus.RUECKLAEUFER_DISPO);
                    bas.saveVerlauf(verlauf);

                    getActView().setVerlaufAbtStatus("erledigt");
                    getActView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_ERLEDIGT);
                    getActView().setBearbeiter(va.getBearbeiter());
                    getActView().notifyObservers(true);
                }
            }
            else {
                throw new HurricanGUIException("Es wurde keine Verlaufs-ID angegeben. Verlauf kann nicht erledigt werden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Gibt die Endstellen-Liste zurueck - sofern sie geladen wurde.
     */
    protected List<Endstelle> getEndstellen() {
        if ((endstellen4Auftraege == null) || (actView == null)) {
            return null;
        }
        return endstellen4Auftraege.get(actView.getAuftragId());
    }

    /**
     * Druckt die Projtkierung fuer eine best. Abteilung aus.
     */
    protected void printProjektierung() {
        if ((actView != null) && (actView.getVerlaufId() != null)) {
            try {
                setWaitCursor();
                PrintVerlaufHelper helper = new PrintVerlaufHelper();
                helper.printVerlauf(actView.getVerlaufId(), true, true, false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
            }
        }
        else {
            MessageHelper.showInfoDialog(getMainFrame(), "Bitte wählen Sie zuerst eine Projektierung aus.");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        tableModel.fireTableDataChanged();
    }

    /**
     * Gibt das Panel zurueck, auf dem die Ableitungen noch spezielle GUI-Elemente platzieren koennen.
     */
    protected AKJPanel getChildPanel() {
        return childPanel;
    }

    /**
     * Gibt das aktuell angezeigte ProjektierungView-Objekt zurueck.
     */
    protected ProjektierungsView getActView() {
        return actView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("refresh".equals(e.getActionCommand())) {
            loadData();
        }
    }

    /**
     * Aktualisiert den Row-Count im Frame-Header.
     */
    protected void refreshRowCount() {
        setFrameTitle(StringUtils.trimToEmpty(getFrameTitle()) + " - Anzahl: " + tbViews.getRowCount());
    }

    /**
     * 'Loescht' den Inhalt aller Felder.
     */
    protected void clear() {
        GuiTools.cleanFields(detailPanel);
        actView = null;
        updateAuftragsPanel(null);
        endstellen4Auftraege = null;
        navBar4Endstellen.setData(null);
        if (tbMdlES != null) {
            tbMdlES.setData(null);
            tbMdlES.setEndstellen(null, null);
        }
    }

    private void clearTable() {
        tableModel.setData(null);
    }

    @Override
    public void tableFiltered() {
        refreshRowCount();
    }

    @Override
    public void showNavigationObject(Object obj, int number) throws PropertyVetoException {
        if (guiCreated) {
            if ((showESPanel) && (obj instanceof Long)) {
                try {
                    setWaitCursor();
                    Long auftragId = (Long) obj;
                    List<Endstelle> endstellen = endstellen4Auftraege.get(auftragId);
                    if ((endstellen != null) && (!endstellen.isEmpty())) {
                        Endstelle esA = null;
                        Endstelle esB = null;
                        for (Endstelle e : endstellen) {
                            if (e.isEndstelleA()) {
                                esA = e;
                            }
                            else if (e.isEndstelleB()) {
                                esB = e;
                            }
                        }
                        tbMdlES.setEndstellen(esA, esB);

                        List<Throwable> errors = tbMdlES.getErrors();
                        if ((errors != null) && (!errors.isEmpty())) {
                            MessageHelper.showErrorDialog(getMainFrame(), errors);
                        }

                    }
                    tfAuftragId4Endstellen.setValue(auftragId);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                }
            }
            else {
                tbMdlES.setEndstellen(null, null);
                tfAuftragId4Endstellen.setValue(null);
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cbNiederlassung && e.getStateChange() == ItemEvent.SELECTED) {
            niederlassung = getSelectedNiederlassung();
            filterNiederlassung(niederlassung);
            clear();
        }
        else if (e.getSource() == cbAbteilung && e.getStateChange() == ItemEvent.SELECTED) {
            abteilungId = (cbAbteilung.getSelectedItem() instanceof Abteilung)
                    ? ((Abteilung) cbAbteilung.getSelectedItem()).getId()
                    : null;

            if (initDone) {
                HurricanSystemRegistry.instance().setUserConfig(
                        HurricanGUIConstants.ABTEILUNG_4_PROJEKTIERUNG_GUI, abteilungId);
            }

            reloadData();
        }
    }

    private static class WorkerResult {
        Map<Long, List<Endstelle>> endstellen4Auftraege = null;
        VerlaufAbteilung vaDispo = null;
        List<Long> auftragIds = null;
        AuftragDaten ad = null;
        Kunde kunde = null;
    }

    /**
     * MouseListener fuer die TextFields, um in einen Auftrag zu 'springen'.
     */
    class TextFieldMouseListener extends MouseAdapter {
        private void openAuftrag(AKJFormattedTextField tfAuftragIdClicked) {
            Long auftragId = tfAuftragIdClicked.getValueAsLong(null);
            if (auftragId != null) {
                try {
                    setWaitCursor();
                    showProgressBar("Öffne Auftrags-Daten...");

                    Auftrag auftrag = new Auftrag();
                    auftrag.setAuftragId(auftragId);
                    AuftragDataFrame.openFrame(auftrag);
                }
                finally {
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Object source = e.getSource();
            if ((e.getClickCount() == 2) && (source != null) && (source instanceof AKJFormattedTextField)) {
                openAuftrag((AKJFormattedTextField) source);
            }
        }
    }

}
