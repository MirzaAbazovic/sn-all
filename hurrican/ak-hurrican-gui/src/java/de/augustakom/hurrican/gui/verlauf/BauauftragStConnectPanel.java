/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 11:16:33
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
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VPNKonfiguration;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufStConnectView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Panel fuer die Anzeige/Bearbeitung der Bauauftraege von ST Connect.
 *
 *
 */
public class BauauftragStConnectPanel
        extends AbstractBauauftragPanel<VerlaufStConnectView, StConnectDetailsWorkerResult, AKReferenceAwareTableModel<VerlaufStConnectView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragStConnectPanel.class);

    private static final String COL_CPS_TX_STATE = "CPS Status";
    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcGesamtRealisierung = null;
    private AKJTextField tfStatus = null;
    private AKJTextField tfBearbeiter = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJTextField tfPhysLtg = null;
    private AKJCheckBox cbKanalb = null;
    private AKJFormattedTextField tfAnzKanaele = null;
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
    private AKJTextField tfMaxBitrateA = null;
    private AKJTextField tfMaxBitrateB = null;
    private AKJTextField tfPhysikInfo = null;
    private AKJTextArea taBemerkung = null;

    private AKJButton btnUebernahme = null;
    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnErledigen = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnCPSTxHistory = null;
    private AKJButton btnCPSTxCreation = null;
    private AKJButton btnShowPorts = null;

    // Detail-Modelle
    private VPNKonfiguration vpnKonf = null;
    private VerbindungsBezeichnung vpnPhysVbz = null;
    private Endstelle endstelleA = null;
    private Endstelle endstelleB = null;
    private Ansprechpartner esAnspA = null;
    private Ansprechpartner esAnspB = null;
    private AuftragDaten auftragDaten = null;

    /**
     * Standardkonstruktor.
     */
    public BauauftragStConnectPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragStConnectPanel.xml", Abteilung.ST_CONNECT, false,
                COL_NIEDERLASSUNG);
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
        tableModel = new AKReferenceAwareTableModel<>(
                new String[] { "CPS", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Kunde",                 //  0- 4
                        "Produkt (Hurrican)", "Real.-Datum", "BA Status", "Bearbeiter ST-C",  //  5- 9
                        "Status ST-C", "Wiedervorlage", "Schnittstelle", "Leitung A", "Leitung B",       // 10-14
                        "DSLAM-Prof.", "VPN-Nr", "verschoben", COL_CPS_TX_STATE, COL_NIEDERLASSUNG },
                new String[] { "cpsProvisioning", "vbz", "auftragNoOrig", "anlass", "kundenName",
                        "produktName", "realisierungstermin", "verlaufStatus", "bearbeiterSDH",
                        "verlaufAbteilungStatus", "wiedervorlage", "schnittstelle", "leitungA", "leitungB",
                        "dslamProfile", "vpnNr", "verschoben", "cpsTxState", "niederlassung" },
                new Class[] { Boolean.class, String.class, Long.class, String.class, String.class,
                        String.class, Date.class, String.class, String.class,
                        String.class, Date.class, String.class, String.class, String.class,
                        String.class, String.class, Boolean.class, String.class, String.class }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 40, 125, 70, 95, 110, 110, 70, 70, 80, 80, 80, 75, 70, 70, 100, 55, 15, 70, 70 });

        createDetailPanel();
        AKAbstractAction erledigenAction = btnErledigen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnUebernahme, btnPrint, btnPrintCompact, btnShowPorts,
                btnErledigen, erledigenAction, btnBemerkungen, btnCPSTxHistory, btnCPSTxCreation);
    }

    /* Erzeugt das Detail-Panel. */
    private void createDetailPanel() {
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOeName = getSwingFactory().createLabel("oe.name");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblGesamtRealisierung = getSwingFactory().createLabel("gesamt.realisierung");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblStatus = getSwingFactory().createLabel("status.sdh");
        AKJLabel lblBearbeiter = getSwingFactory().createLabel("bearbeiter.sdh");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblPhysLtg = getSwingFactory().createLabel("phys.leitung");
        AKJLabel lblKanalb = getSwingFactory().createLabel("kanalbuendelung");
        AKJLabel lblAnzKanaele = getSwingFactory().createLabel("anz.kanaele");
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
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblMaxBitrateA = getSwingFactory().createLabel("max.bitrate.a");
        AKJLabel lblMaxBitrateB = getSwingFactory().createLabel("max.bitrate.b");

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcGesamtRealisierung = getSwingFactory().createDateComponent("gesamt.realisierung", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        tfStatus = getSwingFactory().createTextField("status.sdh", false);
        tfBearbeiter = getSwingFactory().createTextField("bearbeiter.sdh", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        tfPhysLtg = getSwingFactory().createTextField("phys.leitung", false);
        cbKanalb = getSwingFactory().createCheckBox("kanalbuendelung", false);
        tfAnzKanaele = getSwingFactory().createFormattedTextField("anz.kanaele", false);
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
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);
        tfMaxBitrateA = getSwingFactory().createTextField("max.bitrate.a", false);
        tfMaxBitrateA.setForeground(Color.RED);
        tfMaxBitrateA.setFontStyle(Font.BOLD);
        tfMaxBitrateB = getSwingFactory().createTextField("max.bitrate.b", false);
        tfMaxBitrateB.setForeground(Color.RED);
        tfMaxBitrateB.setFontStyle(Font.BOLD);

        btnUebernahme = getSwingFactory().createButton(BTN_UEBERNAHME, getActionListener());
        btnPrint = getSwingFactory().createButton(BTN_PRINT, getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton(BTN_BA_ERLEDIGEN, getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnCPSTxHistory = getSwingFactory().createButton(SHOW_CPS_TX_HISTORY, getActionListener());
        btnCPSTxCreation = getSwingFactory().createButton(CREATE_CPS_TX, getActionListener());
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
        left.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfStatus, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbeiter, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbeiter, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblPhysLtg, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfPhysLtg, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblKanalb, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(cbKanalb, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAnzKanaele, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAnzKanaele, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 6, 3, 1, GridBagConstraints.BOTH));

        AKJPanel mid2 = new AKJPanel(new GridBagLayout());
        mid2.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid2.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblSchnittst, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfSchnittstelle, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblLeitungA, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfLeitungA, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblLeitungB, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfLeitungB, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        mid2.add(lblEsNameA, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsNameA, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblEsOrtA, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsOrtA, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblEsAnspA, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsAnspA, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.NONE));
        mid2.add(lblEsNameB, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsNameB, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblEsOrtB, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsOrtB, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(lblEsAnspB, GBCFactory.createGBC(0, 0, 0, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(tfEsAnspB, GBCFactory.createGBC(100, 0, 2, 11, 1, 1, GridBagConstraints.HORIZONTAL));
        mid2.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 12, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblMaxBitrateA, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfMaxBitrateA, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMaxBitrateB, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfMaxBitrateB, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 2, 1, GridBagConstraints.VERTICAL));

        AKJPanel ldown = new AKJPanel(new GridBagLayout());
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        ldown.add(lblPhysikInfo, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        ldown.add(tfPhysikInfo, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        ldown.add(spBemerkung, GBCFactory.createGBC(100, 0, 2, 1, 1, 2, GridBagConstraints.HORIZONTAL));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left, GBCFactory.createGBC(40, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ldown, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid2, GBCFactory.createGBC(0, 0, 4, 0, 1, 2, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right, GBCFactory.createGBC(0, 100, 6, 0, 1, 2, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnUebernahme, btnPrint, btnPrintCompact, btnErledigen, btnBemerkungen,
                btnCPSTxHistory, btnShowPorts });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    public List<VerlaufStConnectView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufStConnectView> stConnectViews = (List) bas.findBAVerlaufViews4Abt(
                false, Abteilung.ST_CONNECT, false, realisierungFrom, realisierungTo);
        return stConnectViews;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufStConnectView> tableData) {
        try {
            loadCPSTxStateReferences(tableModel, COL_CPS_TX_STATE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        ObserverHelper.addObserver2Objects(this, tableData);
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
        else if (BTN_PRINT.equals(command)) {
            printBA((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null, true, false);
        }
        else if (BTN_PRINT_COMPACT.equals(command)) {
            printBA((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null, false, true);
        }
        else if (BTN_BEMERKUNGEN.equals(command)) {
            showBemerkungen((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null);
        }
        else if (SHOW_CPS_TX_HISTORY.equals(command)) {
            showCPSTxHistory(getSelectedView());
        }
        else if (CREATE_CPS_TX.equals(command)) {
            createCPSTx(getSelectedView());
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getSelectedView());
        }
    }

    /* Ordnet den ausgewaehlten Verlauf dem aktuellen Benutzer zu. */
    private void verlaufUebernehmen() {
        VerlaufAbteilung verlAbt = super.verlaufUebernehmen(getSelectedView().getBearbeiterSDH());
        if (verlAbt != null) {
            getSelectedView().setBearbeiterSDH(verlAbt.getBearbeiter());
            getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            getSelectedView().setVerlaufStatus("in Bearbeitung"); // hard-coded - kann nur dieser Wert sein und ist nur eine View
            getSelectedView().notifyObservers(true);
        }
    }

    /* Ausgewaehlten Verlauf erledigen. */
    private void verlaufErledigen() {
        if (getSelectedView() == null) {
            MessageHelper.showInfoDialog(getMainFrame(), NOTHING_SELECTED, null, true);
            return;
        }

        try {
            setWaitCursor();
            VerlaufAbteilung va = verlaufErledigen(getSelectedView());
            if (va != null) {
                getSelectedView().setBearbeiterSDH(va.getBearbeiter());
                getSelectedView().setVerlaufStatus("erledigt");
                getSelectedView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_ERLEDIGT);
                getSelectedView().setGuiFinished(true);
                getSelectedView().notifyObservers(true);
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

    @Override
    public StConnectDetailsWorkerResult loadDetails(VerlaufStConnectView selectedView) throws ServiceNotFoundException, FindException {
        StConnectDetailsWorkerResult result = new StConnectDetailsWorkerResult();

        // VPN-Daten ermitteln
        PhysikService ps = getCCService(PhysikService.class);
        if (selectedView.getVpnNr() != null) {
            VPNService vpns = getCCService(VPNService.class);
            result.vpnKonf = vpns.findVPNKonfiguration4Auftrag(selectedView.getAuftragId());
            if ((result.vpnKonf != null) && (result.vpnKonf.getPhysAuftragId() != null)) {
                // phys. VBZ von VPN ermitteln
                result.vpnPhysVbz = ps.findVerbindungsBezeichnungByAuftragId(vpnKonf.getPhysAuftragId());
            }
        }

        // ES-Daten (A+B) ermitteln und Carrierbestellungen ermitteln
        EndstellenService esSrv = getCCService(EndstellenService.class);
        AnsprechpartnerService anspSrv = getCCService(AnsprechpartnerService.class);
        CarrierService cs = getCCService(CarrierService.class);
        if (selectedView.getEsIdA() != null) {
            result.endstelleA = esSrv.findEndstelle(selectedView.getEsIdA());
            result.cbEsA = cs.findLastCB4Endstelle(selectedView.getEsIdA());
            if (result.endstelleA != null) {
                result.esAnspA = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_A, selectedView.getAuftragId());
            }
        }
        if (selectedView.getEsIdB() != null) {
            result.endstelleB = esSrv.findEndstelle(selectedView.getEsIdB());
            result.cbEsB = cs.findLastCB4Endstelle(selectedView.getEsIdB());
            if (result.endstelleB != null) {
                result.esAnspB = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, selectedView.getAuftragId());
            }
        }

        // Bemerkungen ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());

        return result;
    }

    /* (non-Javadoc)
     * @see de.augustakom.hurrican.gui.verlauf.AbstractBauauftragPanel#updateGuiByDetails(de.augustakom.hurrican.model.cc.view.AbstractBauauftragView, java.lang.Object)
     */
    @Override
    public void updateGuiByDetails(VerlaufStConnectView selectedView, StConnectDetailsWorkerResult result) {
        setSelectedView(selectedView);
        vpnKonf = result.vpnKonf;
        vpnPhysVbz = result.vpnPhysVbz;
        endstelleA = result.endstelleA;
        Carrierbestellung cbEsA = result.cbEsA;
        esAnspA = result.esAnspA;
        endstelleB = result.endstelleB;
        Carrierbestellung cbEsB = result.cbEsB;
        esAnspB = result.esAnspB;
        auftragDaten = result.auftragDaten;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcGesamtRealisierung.setDate(selectedView.getGesamtrealisierungstermin());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        tfPhysikInfo.setText(getPhysikInfo(selectedView));
        tfStatus.setText(selectedView.getVerlaufStatus());
        tfBearbeiter.setText(selectedView.getBearbeiterSDH());
        tfVbz.setText(selectedView.getVbz());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfVpnNr.setValue(selectedView.getVpnNr());
        tfSchnittstelle.setText(selectedView.getSchnittstelle());
        tfLeitungA.setText(selectedView.getLeitungA());
        tfLeitungB.setText(selectedView.getLeitungB());
        tfKundeName.setText(selectedView.getKundenName());

        if (vpnKonf != null) {
            cbKanalb.setSelected(vpnKonf.getKanalbuendelung());
            tfAnzKanaele.setValue(vpnKonf.getAnzahlKanaele());
        }

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

        if (cbEsA != null) {
            tfMaxBitrateA.setText(cbEsA.getMaxBruttoBitrate());
        }

        if (cbEsB != null) {
            tfMaxBitrateB.setText(cbEsB.getMaxBruttoBitrate());
        }

        tfPhysLtg.setText((vpnPhysVbz != null) ? vpnPhysVbz.getVbz() : null);
        taBemerkung.setText((auftragDaten != null) ? auftragDaten.getBemerkungen() : null);
        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
    }

    @Override
    public void clearDetails() {
        vpnKonf = null;
        vpnPhysVbz = null;
        endstelleA = null;
        endstelleB = null;
        esAnspA = null;
        esAnspB = null;
        auftragDaten = null;
        super.clearDetails();
    }

    @Override
    protected void enableButtons(boolean enable) {
        super.enableButtons(enable);
        btnCPSTxCreation.setEnabled(enable);
        if (enable) {
            manageGUI(btnCPSTxCreation);
        }
    }
}

class StConnectDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    AuftragDaten auftragDaten = null;
    Endstelle endstelleA = null;
    Carrierbestellung cbEsA = null;
    Ansprechpartner esAnspA = null;
    Endstelle endstelleB = null;
    Carrierbestellung cbEsB = null;
    Ansprechpartner esAnspB = null;
    VerbindungsBezeichnung vpnPhysVbz = null;
    VPNKonfiguration vpnKonf = null;
}
