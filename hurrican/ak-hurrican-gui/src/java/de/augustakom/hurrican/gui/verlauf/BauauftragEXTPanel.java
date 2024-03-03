/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2009 10:05:20
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.IServiceLocator;
import de.augustakom.common.service.iface.IServiceLocatorNames;
import de.augustakom.common.service.locator.ServiceLocatorRegistry;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufEXTView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;


/**
 * Panel fuer die Anzeige/Bearbeitung der Bauauftraege von Extern.
 *
 *
 */
public class BauauftragEXTPanel
        extends AbstractBauauftragPanel<VerlaufEXTView, EXTDetailsWorkerResult, AKReflectionTableModel<VerlaufEXTView>> {
    private static final Logger LOGGER = Logger.getLogger(BauauftragEXTPanel.class);

    private static final long serialVersionUID = 5554610836325413075L;

    private static final String COL_NIEDERLASSUNG = "Niederlassung";
    private static final String COL_EXT_SERVICE_PROVIDER = "Dienstleister";
    private static final String FILTER_EXT_SERVICE_PROVIDER = "externer Service-Provider";

    // GUI-Komponenten
    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJTextField tfStatus = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJTextField tfCarrier = null;
    private AKJDateComponent dcBereitst = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfExtServiceProvider = null;
    private AKJTextField tfEsNameA = null;
    private AKJTextField tfEsOrtA = null;
    private AKJTextField tfEsAnspA = null;
    private AKJTextField tfEsNameB = null;
    private AKJTextField tfEsOrtB = null;
    private AKJTextField tfEsAnspB = null;
    private AKJTextField tfPortierung = null;
    private AKJTextField tfInstallTS = null;
    private AKJTextField tfPhysikInfo = null;
    private AKJTextArea taBemerkung = null;
    private RelatedBAPanel relatedBAPnl = null;
    private AuftragInternVerlaufPanel internPnl = null;
    private AKJComboBox cbExtern = null;

    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnErledigen = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnRufnummern = null;
    private AKJButton btnShowPorts = null;

    private AuftragDaten auftragDaten = null;
    private Endstelle endstelleA = null;
    private Endstelle endstelleB = null;
    private Ansprechpartner esAnspA = null;
    private Ansprechpartner esAnspB = null;

    private boolean guiCreated = false;

    /**
     * Konstruktor fuer das Bauauftrags-Panel der Abteilung EXTERN.
     */
    public BauauftragEXTPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragEXTPanel.xml", Abteilung.EXTERN, false,
                COL_NIEDERLASSUNG, FilterEXTEventListener.class);
        createGUI();
        init();
        loadData();
    }

    @Override
    protected final void createGUI() {
        super.createGUI();
        tableModel = new AKReflectionTableModel<>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Kreuzung", "Produkt (Billing)", "Produkt (Hurrican)",  //  0-5
                        "Real.-Datum", "TimeSlot", "BA Status", "Status Extern", "Wiedervorlage", "ES-Ort (B)", "ES-Strasse (B)",                 //  6-11
                        "Kunde", "verschoben", COL_NIEDERLASSUNG, COL_EXT_SERVICE_PROVIDER, "Cluster-ID", "Gerätebez."},
                new String[] { "vbz", "auftragNoOrig", "anlass", "kreuzung", "oeName", "produktName",
                        "realisierungstermin", "timeSlot.timeSlotToUseAsString", "verlaufStatus", "verlaufAbteilungStatus", "wiedervorlage", "endstelleOrtB", "endstelleStrasseB",
                        "kundenName", "verschoben", "niederlassung", "extServiceProviderName", "hvtClusterId", "geraeteBez" },
                new Class[] { String.class, Long.class, String.class, Boolean.class, String.class, String.class,
                        Date.class, String.class, String.class, String.class, Date.class, String.class, String.class,
                        String.class, Boolean.class, String.class, String.class, String.class, String.class  }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 125, 80, 100, 40, 100, 100, 80, 80, 80, 80, 80, 80, 80, 25, 70, 100, 50, 100 });

        createDetailPanel();

        relatedBAPnl = new RelatedBAPanel();
        internPnl = new AuftragInternVerlaufPanel();
        getTabbedPane().addTab("zugehoerige BAs", relatedBAPnl);
        getTabbedPane().addTab("interne Arbeit", internPnl);

        AKAbstractAction erledigenAction = btnErledigen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnRufnummern, btnPrint, btnPrintCompact, erledigenAction, btnBemerkungen, btnErledigen, btnShowPorts);
        guiCreated = true;
    }

    @Override
    protected int childComponents2FilterPanel(AKJPanel filterPanel, int nextGridBagXIdxIn) {
        int nextGridBagXIdx = nextGridBagXIdxIn;
        AKJLabel lblExtern = new AKJLabel("Dienstleister:");
        cbExtern = new AKJComboBox(ExtServiceProvider.class, "getName");
        cbExtern.addItemListener(getFilterEventListener(this));

        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(lblExtern, GBCFactory.createGBC(0, 0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.NONE));
        filterPanel.add(cbExtern, GBCFactory.createGBC(50, 0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        filterPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, nextGridBagXIdx++, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        return nextGridBagXIdx;
    }

    /* Erzeugt das Detail-Panel. */
    private void createDetailPanel() {
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOeName = getSwingFactory().createLabel("oe.name");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblCarrier = getSwingFactory().createLabel("carrier");
        AKJLabel lblBereitst = getSwingFactory().createLabel("bereitstellung");
        AKJLabel lblStatus = getSwingFactory().createLabel("status");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblExtServiceProvider = getSwingFactory().createLabel("ext.service.provider.name");
        AKJLabel lblEsNameA = getSwingFactory().createLabel("es.a.name");
        AKJLabel lblEsOrtA = getSwingFactory().createLabel("es.a.ort");
        AKJLabel lblEsAnspA = getSwingFactory().createLabel("es.a.ansp");
        AKJLabel lblEsNameB = getSwingFactory().createLabel("es.b.name");
        AKJLabel lblEsOrtB = getSwingFactory().createLabel("es.b.ort");
        AKJLabel lblEsAnspB = getSwingFactory().createLabel("es.b.ansp");
        AKJLabel lblMontageAkom = getSwingFactory().createLabel("montage.akom");
        AKJLabel lblPortierung = getSwingFactory().createLabel("portierungsart");
        AKJLabel lblInstallTS = getSwingFactory().createLabel("install.time.slot");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        tfCarrier = getSwingFactory().createTextField("carrier", false);
        dcBereitst = getSwingFactory().createDateComponent("bereitstellung", false);
        tfStatus = getSwingFactory().createTextField("status", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);
        tfExtServiceProvider = getSwingFactory().createTextField("ext.service.provider.name", false);
        tfEsNameA = getSwingFactory().createTextField("es.a.name", false);
        tfEsOrtA = getSwingFactory().createTextField("es.a.ort", false);
        tfEsAnspA = getSwingFactory().createTextField("es.a.ansp", false);
        tfEsNameB = getSwingFactory().createTextField("es.b.name", false);
        tfEsOrtB = getSwingFactory().createTextField("es.b.ort", false);
        tfEsAnspB = getSwingFactory().createTextField("es.b.ansp", false);
        chbMontageAkom = getSwingFactory().createCheckBox("montage.akom", false);
        tfPortierung = getSwingFactory().createTextField("portierungsart", false);
        tfInstallTS = getSwingFactory().createTextField("install.time.slot", false);
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);

        btnPrint = getSwingFactory().createButton("print", getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton("ba.erledigen.ext", getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnRufnummern = getSwingFactory().createButton("rufnummern", getActionListener());
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblAnlass, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAnlass, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOeName, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOeName, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProdukt, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRealisierung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcRealisierung, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCarrier, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfCarrier, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBereitst, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitst, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfStatus, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblMontageAkom, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbMontageAkom, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblPortierung, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfPortierung, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblInstallTS, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfInstallTS, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblExtServiceProvider, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfExtServiceProvider, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        right.add(lblEsNameA, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameA, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsOrtA, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsOrtA, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsAnspA, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsAnspA, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.NONE));
        right.add(lblEsNameB, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameB, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsOrtB, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsOrtB, GBCFactory.createGBC(100, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsAnspB, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsAnspB, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 10, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel ldown = new AKJPanel(new GridBagLayout());
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        ldown.add(lblPhysikInfo, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        ldown.add(tfPhysikInfo, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        ldown.add(spBemerkung, GBCFactory.createGBC(100, 0, 2, 1, 1, 2, GridBagConstraints.HORIZONTAL));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left, GBCFactory.createGBC(10, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ldown, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right, GBCFactory.createGBC(0, 0, 4, 0, 1, 3, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 2, 1, 1, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnPrint, btnPrintCompact, btnErledigen, btnBemerkungen, btnRufnummern, btnShowPorts });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    protected final void init() {
        super.init();
        try {
            // Lade Externe Partner
            ExtServiceProviderService es = getCCService(ExtServiceProviderService.class);
            List<ExtServiceProvider> provider = es.findAllServiceProvider();
            cbExtern.addItems(provider, Boolean.TRUE, ExtServiceProvider.class);

            selectExtServiceProvider();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public List<VerlaufEXTView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        return (List) bas.findBAVerlaufViews4Abt(
                false, Abteilung.EXTERN, false, realisierungFrom, realisierungTo);
    }

    @Override
    public void updateGuiByTableData(List<VerlaufEXTView> tableData) {
        ObserverHelper.addObserver2Objects(this, tableData);
        tableModel.setData(tableData);
        filterExtServiceProvider(getSelectedExtServiceProvider());
    }

    private void filterExtServiceProvider(ExtServiceProvider extServiceProvider) {
        if (tableModel != null) {
            tableModel.removeFilter(FILTER_EXT_SERVICE_PROVIDER);
            setSelectedView(null);
            if ((extServiceProvider != null) && (extServiceProvider.getId() != null)) {
                Optional<Integer> index = tableModel.getColumnIndex(COL_EXT_SERVICE_PROVIDER);
                if (index.isPresent()) {
                    tableModel.addFilter(new FilterOperator(FILTER_EXT_SERVICE_PROVIDER, FilterOperators.EQ,
                            extServiceProvider.getName(), index.get()));
                } else {
                    MessageHelper.showMessageDialog(getMainFrame(), String.format("Für die Spalte %s konnte kein "
                                    + "Index ermittelt werden. Daher ist kein Filter gesetzt.",
                            COL_EXT_SERVICE_PROVIDER), "Niederlassung nicht gefiltert", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private ExtServiceProvider getSelectedExtServiceProvider() {
        Object obj = cbExtern.getSelectedItem();
        if ((obj != null) && (obj instanceof ExtServiceProvider)) {
            return (ExtServiceProvider) obj;
        }
        return null;
    }


    protected class FilterEXTEventListener extends FilterEventListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            super.itemStateChanged(e);
            if ((e.getSource() == cbExtern) && (e.getStateChange() == ItemEvent.SELECTED)) {
                filterExtServiceProvider(getSelectedExtServiceProvider());
            }
        }
    }

    private void selectExtServiceProvider() {
        try {
            // Ermittle User
            Long sessionId = HurricanSystemRegistry.instance().getSessionId();
            IServiceLocator authSL = ServiceLocatorRegistry.instance().getServiceLocator(
                    IServiceLocatorNames.AUTHENTICATION_SERVICE);
            AKUserService us = (AKUserService) authSL.getService(AKAuthenticationServiceNames.USER_SERVICE, null);
            AKUser user = us.findUserBySessionId(sessionId);

            // Selektiere Externer Partner
            if ((user != null) && (user.getExtServiceProviderId() != null)) {
                ExtServiceProviderService extServiceProvService = getCCService(ExtServiceProviderService.class);
                ExtServiceProvider sp = extServiceProvService.findById(user.getExtServiceProviderId());
                if (sp != null) {
                    cbExtern.selectItem(sp.getName());
                    cbExtern.setEnabled(false);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("ba.erledigen.ext".equals(command)) {
            if (getTable().getSelectedRows().length <= 1) {
                verlaufErledigen();
            }
            else {
                MessageHelper.showInfoDialog(this, "Bitte nur einen Bauauftrag auswählen.");
            }
        }
        else if ("print".equals(command)) {
            printBA((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null, true, false);
        }
        else if (BTN_PRINT_COMPACT.equals(command)) {
            printBA((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null, false, true);
        }
        else if ("rufnummern".equals(command)) {
            showRufnummern(getSelectedView());
        }
        else if (BTN_BEMERKUNGEN.equals(command)) {
            showBemerkungen((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null);
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getSelectedView());
        }
    }

    @Override
    public EXTDetailsWorkerResult loadDetails(VerlaufEXTView selectedView) throws ServiceNotFoundException, FindException {

        EXTDetailsWorkerResult result = new EXTDetailsWorkerResult();

        // Endstellen ermitteln
        EndstellenService esSrv = getCCService(EndstellenService.class);
        AnsprechpartnerService anspSrv = getCCService(AnsprechpartnerService.class);
        if (selectedView.getEsIdA() != null) {
            result.endstelleA = esSrv.findEndstelle(selectedView.getEsIdA());
            if (result.endstelleA != null) {
                result.esAnspA = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_A, selectedView.getAuftragId());
            }
        }
        if (selectedView.getEsIdB() != null) {
            result.endstelleB = esSrv.findEndstelle(selectedView.getEsIdB());
            if (result.endstelleB != null) {
                result.esAnspB = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, selectedView.getAuftragId());
            }
        }

        // AuftragDaten ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());
        result.timeSlot = getTimeSlot(result.auftragDaten, selectedView);

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufEXTView selectedView, EXTDetailsWorkerResult result) {
        setSelectedView(selectedView);
        endstelleA = result.endstelleA;
        esAnspA = result.esAnspA;
        endstelleB = result.endstelleB;
        esAnspB = result.esAnspB;
        auftragDaten = result.auftragDaten;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        tfStatus.setText(selectedView.getVerlaufStatus());
        tfVbz.setText(selectedView.getVbz());
        dcBereitst.setDate(selectedView.getCarrierBereitstellung());
        tfCarrier.setText(selectedView.getCarrier());
        tfAuftragId.setValue(selectedView.getAuftragId());
        chbMontageAkom.setSelected(hasExternInstallation(selectedView));
        tfKundeName.setText(selectedView.getKundenName());
        tfPortierung.setText(selectedView.getPortierungsart());
        tfExtServiceProvider.setText(selectedView.getExtServiceProviderName());
        tfPhysikInfo.setText(getPhysikInfo(selectedView));
        taBemerkung.setText((auftragDaten != null) ? auftragDaten.getBemerkungen() : null);

        if (endstelleA != null) {
            tfEsNameA.setText(endstelleA.getName());
            tfEsOrtA.setText(endstelleA.getOrt());
            tfEsAnspA.setText((esAnspA != null) ? esAnspA.getDisplayText() : null);
        }

        if (endstelleB != null) {
            tfEsNameB.setText(endstelleB.getName());
            tfEsOrtB.setText(endstelleB.getOrt());
            tfEsAnspB.setText((esAnspB != null) ? esAnspB.getDisplayText() : null);
        }

        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
        relatedBAPnl.showRelatedBAs(selectedView);
        getTabbedPane().setIconAt(2, (relatedBAPnl.hasRelatedBAs()) ? getSwingFactory().createIcon("has.relations") : null);
        internPnl.setModel(selectedView);
        tfInstallTS.setText(result.timeSlot);
    }

    @Override
    public void clearDetails() {
        auftragDaten = null;
        endstelleA = null;
        endstelleB = null;
        esAnspA = null;
        esAnspB = null;
        if (guiCreated) {
            relatedBAPnl.clear();
        }
        super.clearDetails();
    }

    /* Setzt den aktuell ausgewaehlten Verlauf auf 'erledigt'. */
    private void verlaufErledigen() {
        if (getSelectedView() == null) {
            MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
            return;
        }

        try {
            setWaitCursor();

            if ((getSelectedView() != null) && (getSelectedView().getVerlaufAbtId() != null)) {
                BAService bas = getCCService(BAService.class);
                VerlaufAbteilung va = bas.findVerlaufAbteilung(getSelectedView().getVerlaufAbtId());

                // Pruefe, ob BA bereits abgeschlossen
                if (NumberTools.equal(va.getVerlaufStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
                    MessageHelper.showInfoDialog(this, "Bauauftrag wurde bereits abschlossen.");
                    return;
                }

                va = verlaufErledigen(getSelectedView());

                if (va != null) {
                    getSelectedView().setVerlaufStatus("erledigt");
                    getSelectedView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_ERLEDIGT);
                    getSelectedView().setGuiFinished(true);
                    getSelectedView().notifyObservers(true);
                }
            }
            else {
                throw new HurricanGUIException("Es wurde keine Verlaufs-ID angegeben. Verlauf kann nicht erledigt werden.");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }
}

class EXTDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    AuftragDaten auftragDaten = null;
    Ansprechpartner esAnspA = null;
    Endstelle endstelleA = null;
    Ansprechpartner esAnspB = null;
    Endstelle endstelleB = null;
    String timeSlot = null;
}
