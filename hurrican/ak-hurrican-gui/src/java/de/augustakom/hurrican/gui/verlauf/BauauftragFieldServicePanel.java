/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 15:05:20
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
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
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufFieldServiceView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Panel fuer die Anzeige/Bearbeitung der Bauauftraege von FieldService
 *
 *
 */
public class BauauftragFieldServicePanel
        extends AbstractBauauftragPanel<VerlaufFieldServiceView, FieldServiceDetailsWorkerResult,
        AKReflectionTableModel<VerlaufFieldServiceView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragFieldServicePanel.class);

    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcGesamtRealisierung = null;
    private AKJTextField tfStatus = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJTextField tfCarrier = null;
    private AKJDateComponent dcBereitst = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfLeitungA = null;
    private AKJTextField tfLeitungB = null;
    private AKJTextField tfSchnittstelle = null;
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

    private AKJButton btnUebernahme = null;
    private AKJButton btnAssignVerlauf = null;
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
     * Konstruktor fuer das Bauauftrags-Panel der Abteilung FieldService
     */
    public BauauftragFieldServicePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragFieldServicePanel.xml",
                Abteilung.FIELD_SERVICE, false, COL_NIEDERLASSUNG);
        createGUI();
        init();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        super.createGUI();
        tableModel = new AKReflectionTableModel<>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Kreuzung", "Produkt (Billing)", "Produkt (Hurrican)",  //  0-5
                        "Real.-Datum", "VPN_NR", "Bearbeiter", "BA Status", "Status FieldService", "Wiedervorlage",                  //  6-11
                        "Schnittstelle (B)", "Leitung A", "Leitung B", "ES-Ort (B)", "Strasse", "Kunde",           // 12-17
                        "verschoben", COL_NIEDERLASSUNG, "Cluster ID", "Anschlussart", "DPO-Chassis", "Gerätebez." }, // 18 - 23
                new String[] { "vbz", "auftragNoOrig", "anlass", "kreuzung", "oeName", "produktName",
                        "realisierungstermin", "vpnNr", "bearbeiter", "verlaufStatus", "verlaufAbteilungStatus", "wiedervorlage",
                        "schnittstelle", "leitungA", "leitungB", "endstelleOrtB", "endstelleB", "kundenName",
                        "verschoben", "niederlassung", "hvtClusterId", "anschlussArt", "dpoChassis", "geraeteBez" },
                new Class[] { String.class, Long.class, String.class, Boolean.class, String.class, String.class,
                        Date.class, Long.class, String.class, String.class, String.class, Date.class,
                        String.class, String.class, String.class, String.class, String.class, String.class,
                        Boolean.class, String.class, String.class, String.class, String.class, String.class }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 125, 80, 100, 30, 100, 100, 80, 65, 80, 80, 80, 80, 80, 80, 75, 100, 100, 100, 25, 70, 50, 100, 100 });

        ManuellVerteilenAction mvAction = new ManuellVerteilenAction("Nachverteilen");
        mvAction.setParentClassName(getClassName());
        getTable().addPopupAction(mvAction);

        createDetailPanel();

        relatedBAPnl = new RelatedBAPanel();
        internPnl = new AuftragInternVerlaufPanel();
        getTabbedPane().addTab("zugehoerige BAs", relatedBAPnl);
        getTabbedPane().addTab("interne Arbeit", internPnl);

        AKAbstractAction erledigenAction = btnErledigen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnErledigen, btnUebernahme, btnAssignVerlauf, btnRufnummern,
                btnPrint, btnPrintCompact, erledigenAction, btnBemerkungen, mvAction, btnShowPorts);
        guiCreated = true;
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
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblLeitungA = getSwingFactory().createLabel("ltg.a");
        AKJLabel lblLeitungB = getSwingFactory().createLabel("ltg.b");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
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
        AKJLabel lblGesamtRealisierung = getSwingFactory().createLabel("gesamt.realisierung");

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcGesamtRealisierung = getSwingFactory().createDateComponent("gesamt.realisierung", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        tfCarrier = getSwingFactory().createTextField("carrier", false);
        dcBereitst = getSwingFactory().createDateComponent("bereitstellung", false);
        tfStatus = getSwingFactory().createTextField("status", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);
        tfLeitungA = getSwingFactory().createTextField("ltg.a", false);
        tfLeitungB = getSwingFactory().createTextField("ltg.b", false);
        tfSchnittstelle = getSwingFactory().createTextField("schnittstelle", false);
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

        btnUebernahme = getSwingFactory().createButton(BTN_UEBERNAHME, getActionListener());
        btnAssignVerlauf = getSwingFactory().createButton("assign.verlauf", getActionListener());
        btnPrint = getSwingFactory().createButton(BTN_PRINT, getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton(BTN_BA_ERLEDIGEN, getActionListener());
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
        left.add(lblGesamtRealisierung, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcGesamtRealisierung, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblCarrier, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfCarrier, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBereitst, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitst, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfStatus, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblMontageAkom, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbMontageAkom, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblPortierung, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfPortierung, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblInstallTS, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfInstallTS, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblSchnittst, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSchnittstelle, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblLeitungA, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfLeitungA, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblLeitungB, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfLeitungB, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        right.add(lblEsNameA, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameA, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsOrtA, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsOrtA, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsAnspA, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsAnspA, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.NONE));
        right.add(lblEsNameB, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameB, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsOrtB, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsOrtB, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsAnspB, GBCFactory.createGBC(0, 0, 0, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsAnspB, GBCFactory.createGBC(100, 0, 2, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 12, 1, 1, GridBagConstraints.VERTICAL));

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

        addButtons(new AKJButton[] { btnUebernahme, btnAssignVerlauf, btnPrint, btnPrintCompact,
                btnErledigen, btnBemerkungen, btnRufnummern, btnShowPorts });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    public List<VerlaufFieldServiceView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufFieldServiceView> result = (List) bas.findBAVerlaufViews4Abt(
                false, abteilungId, false, realisierungFrom, realisierungTo);
        return result;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufFieldServiceView> tableData) {
        ObserverHelper.addObserver2Objects(BauauftragFieldServicePanel.this, tableData);
        tableModel.setData(tableData);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (BTN_UEBERNAHME.equals(command)) {
            verlaufUebernehmen();
        }
        else if (BTN_BA_ERLEDIGEN.equals(command)) {
            if (getTable().getSelectedRows().length <= 1) {
                verlaufErledigen();
            }
            else {
                verlaeufeErledigen();
            }
        }
        else if ("assign.verlauf".equals(command)) {
            assignVerlauf();
        }
        else if (BTN_PRINT.equals(command)) {
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
    public FieldServiceDetailsWorkerResult loadDetails(VerlaufFieldServiceView selectedView) throws ServiceNotFoundException, FindException {
        FieldServiceDetailsWorkerResult result = new FieldServiceDetailsWorkerResult();

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
        result.timeSlot = getTimeSlot(result.auftragDaten, selectedView);

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufFieldServiceView selectedView, FieldServiceDetailsWorkerResult result) {
        setSelectedView(selectedView);
        auftragDaten = result.auftragDaten;
        endstelleA = result.endstelleA;
        esAnspA = result.esAnspA;
        endstelleB = result.endstelleB;
        esAnspB = result.esAnspB;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcGesamtRealisierung.setDate(selectedView.getGesamtrealisierungstermin());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        tfStatus.setText(selectedView.getVerlaufStatus());
        tfVbz.setText(selectedView.getVbz());
        dcBereitst.setDate(selectedView.getCarrierBereitstellung());
        tfCarrier.setText(selectedView.getCarrier());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfSchnittstelle.setText(selectedView.getSchnittstelle());
        chbMontageAkom.setSelected(hasExternInstallation(selectedView));
        tfLeitungA.setText(selectedView.getLeitungA());
        tfLeitungB.setText(selectedView.getLeitungB());
        tfKundeName.setText(selectedView.getKundenName());
        tfPortierung.setText(selectedView.getPortierungsart());
        GuiTools.switchForegroundColor(tfPortierung, selectedView.getPortierungsartId(),
                portierung2Color, tfSchnittstelle.getForeground());
        tfVpnNr.setValue(selectedView.getVpnNr());
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

        relatedBAPnl.showRelatedBAs(selectedView);
        getTabbedPane().setIconAt(2,
                (relatedBAPnl.hasRelatedBAs()) ? getSwingFactory().createIcon("has.relations") : null);
        internPnl.setModel(selectedView);

        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());

        // TimeSlot laden
        tfInstallTS.setText(result.timeSlot);
    }

    /* (non-Javadoc)
     * @see de.augustakom.hurrican.gui.verlauf.ShowBauauftragDetails#clearDetails()
     */
    @Override
    public void clearDetails() {
        auftragDaten = null;
        endstelleA = null;
        endstelleB = null;
        esAnspA = null;
        esAnspB = null;
        if (guiCreated) {
            relatedBAPnl.clear();
            internPnl.setModel(null);
        }
        super.clearDetails();
    }

    /* Ordnet den ausgewaehlten Verlauf dem aktuellen Benutzer zu. */
    private void verlaufUebernehmen() {

        VerlaufAbteilung verlAbt = super.verlaufUebernehmen(getSelectedView().getBearbeiter());

        if (verlAbt != null) {
            getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            getSelectedView().setVerlaufStatus("in Bearbeitung"); // hard-coded - kann nur dieser Wert sein
            getSelectedView().setBearbeiter(verlAbt.getBearbeiter());
            getSelectedView().notifyObservers(true);
        }

    }

    /**
     * Verlauf einem Mitarbeiter zuweisen.
     */
    private void assignVerlauf() {
        VerlaufAbteilung verlAbt = assignVerlauf(getSelectedView(), abteilungId);
        if (verlAbt != null) {
            getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            getSelectedView().setBearbeiter(verlAbt.getBearbeiter());
            getSelectedView().notifyObservers(true);
        }
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
                if (NumberTools.isGreaterOrEqual(va.getVerlaufStatusId(), VerlaufStatus.STATUS_ERLEDIGT)) {
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

/**
 * value-Object Klasse zur Speicherung der Werte aus dem {@link SwingWorker#doInBackground()}
 */
class FieldServiceDetailsWorkerResult {
    AuftragDaten auftragDaten = null;
    Ansprechpartner esAnspB = null;
    Endstelle endstelleB = null;
    Ansprechpartner esAnspA = null;
    Endstelle endstelleA = null;
    List<Auftrag2TechLeistung> auftrag2TechLeistungen;
    String timeSlot = null;
}
