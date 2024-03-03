/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.01.2005 14:50:33
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
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufStOnlineView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;


/**
 * Bauauftrags-Panel fuer die Ansicht/Bearbeitung der ST Online Bauauftraege.
 *
 *
 */
public class BauauftragStOnlinePanel
        extends AbstractBauauftragPanel<VerlaufStOnlineView, StOnlineDetailsWorkerResult, AKReferenceAwareTableModel<VerlaufStOnlineView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragStOnlinePanel.class);

    private static final String COL_CPS_TX_STATE = "CPS Status";
    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcGesamtRealisierung = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfPhysikInfo = null;
    private AKJTextArea taBemerkung = null;
    private AKJLabel lblErledigt = null;
    private AKJTextField tfLeitungA = null;
    private AKJTextField tfLeitungB = null;
    private AKJTextField tfSchnittstelle = null;
    private AKJTextField tfEsNameA = null;
    private AKJTextField tfEsOrtA = null;
    private AKJTextField tfEsAnspA = null;
    private AKJTextField tfEsNameB = null;
    private AKJTextField tfEsOrtB = null;
    private AKJTextField tfEsAnspB = null;

    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnErledigen = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnUebernahme = null;
    private AKJButton btnCPSTxHistory = null;
    private AKJButton btnCPSTxCreation = null;
    private AKJButton btnShowPorts = null;

    private AuftragDaten auftragDaten = null;
    private Endstelle endstelleA = null;
    private Endstelle endstelleB = null;
    private Ansprechpartner esAnspA = null;
    private Ansprechpartner esAnspB = null;

    private boolean guiCreated = false;


    /**
     * Default-Konstruktor.
     */
    public BauauftragStOnlinePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragStOnlinePanel.xml", Abteilung.ST_ONLINE, false, COL_NIEDERLASSUNG);
        createGUI();
        init();
        loadData();
    }

    @Override
    protected AKJTable createTable() {
        return new VerlaufTable(true, true);
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        super.createGUI();
        tableModel = new AKReferenceAwareTableModel<>(
                new String[] { "CPS", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun. A-Nr", "Tech. Auftragsnr.", "Anlass", //  0- 4
                        "Produkt (Hurrican)", "Real.-Datum", "BA-Hinweise",  "Kunde", "verschoben", "DSLAM -Prof.",   //  5- 10
                        "VPN-Nr.", "Schnittstelle", "BA Status", "Bearbeiter ST-O", "Status ST-O",    // 11-15
                        "Wiedervorlage", COL_CPS_TX_STATE, COL_NIEDERLASSUNG },                           // 16-18
                new String[] { "cpsProvisioning", "vbz", "auftragNoOrig", "auftragId", "anlass",
                        "produktName", "realisierungstermin", "baHinweise", "kundenName", "verschoben", "dslamProfile",
                        "vpnNr", "schnittstelle", "verlaufStatus", "bearbeiterIPS", "verlaufAbteilungStatus",
                        "wiedervorlage", "cpsTxState", "niederlassung" },
                new Class[] { Boolean.class, String.class, Long.class, Long.class, String.class,
                        String.class, Date.class, String.class, String.class, Boolean.class, String.class,
                        String.class, String.class, String.class, String.class, String.class,
                        Date.class, String.class, String.class }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 40, 125, 70, 70, 95, 110, 70, 100, 100, 50, 100, 55, 75, 70, 80, 80, 70, 70, 70 });

        createDetailPanel();
        AKAbstractAction erledigenAction = btnErledigen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnUebernahme, btnPrint, btnPrintCompact, btnShowPorts,
                btnErledigen, erledigenAction, btnBemerkungen, btnCPSTxHistory, btnCPSTxCreation);
        guiCreated = true;
    }

    /* Erzeugt das Detail-Panel. */
    private void createDetailPanel() {
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOeName = getSwingFactory().createLabel("oe.name");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblGesamtRealisierung = getSwingFactory().createLabel("gesamt.realisierung");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblLeitungA = getSwingFactory().createLabel("ltg.a");
        AKJLabel lblLeitungB = getSwingFactory().createLabel("ltg.b");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
        AKJLabel lblEsNameA = getSwingFactory().createLabel("es.a.name");
        AKJLabel lblEsOrtA = getSwingFactory().createLabel("es.a.ort");
        AKJLabel lblEsAnspA = getSwingFactory().createLabel("es.a.ansp");
        AKJLabel lblEsNameB = getSwingFactory().createLabel("es.b.name");
        AKJLabel lblEsOrtB = getSwingFactory().createLabel("es.b.ort");
        AKJLabel lblEsAnspB = getSwingFactory().createLabel("es.b.ansp");
        lblErledigt = getSwingFactory().createLabel("erledigt");
        lblErledigt.setForeground(Color.red);
        lblErledigt.setVisible(false);

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        dcGesamtRealisierung = getSwingFactory().createDateComponent("gesamt.realisierung", false);
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
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);

        btnPrint = getSwingFactory().createButton(BTN_PRINT, getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton(BTN_BA_ERLEDIGEN, getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnUebernahme = getSwingFactory().createButton(BTN_UEBERNAHME, getActionListener());
        btnCPSTxHistory = getSwingFactory().createButton(SHOW_CPS_TX_HISTORY, getActionListener());
        btnCPSTxCreation = getSwingFactory().createButton(CREATE_CPS_TX, getActionListener());
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(250, 130));
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

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 3, 2, GridBagConstraints.VERTICAL));

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

        AKJPanel lDown = new AKJPanel(new GridBagLayout());
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        lDown.add(lblPhysikInfo, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        lDown.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        lDown.add(tfPhysikInfo, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        lDown.add(lblBemerkung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        lDown.add(spBemerkung, GBCFactory.createGBC(100, 0, 2, 1, 1, 2, GridBagConstraints.HORIZONTAL));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left, GBCFactory.createGBC(10, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(lDown, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right, GBCFactory.createGBC(0, 0, 4, 0, 1, 3, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 2, 1, 1, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnPrint, btnPrintCompact, btnErledigen, btnShowPorts,
                btnBemerkungen, btnUebernahme, btnCPSTxHistory, btnCPSTxCreation });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    public List<VerlaufStOnlineView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        @SuppressWarnings("unchecked")
        List<VerlaufStOnlineView> stOnlineViews = (List) bas.findBAVerlaufViews4Abt(
                false, Abteilung.ST_ONLINE, false, realisierungFrom, realisierungTo);
        return stOnlineViews;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufStOnlineView> tableData) {
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
        if (BTN_BEMERKUNGEN.equals(command)) {
            showBemerkungen((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null);
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
        else if (BTN_UEBERNAHME.equals(command)) {
            verlaufUebernehmen();
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

    @Override
    public StOnlineDetailsWorkerResult loadDetails(VerlaufStOnlineView selectedView) throws ServiceNotFoundException, FindException {
        StOnlineDetailsWorkerResult result = new StOnlineDetailsWorkerResult();

        // AuftragDaten ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        EndstellenService esSrv = getCCService(EndstellenService.class);
        AnsprechpartnerService anspSrv = getCCService(AnsprechpartnerService.class);

        // ES-Daten (A+B) ermitteln
        result.endstelleA = esSrv.findEndstelle4Auftrag(selectedView.getAuftragId(), Endstelle.ENDSTELLEN_TYP_A);
        if (result.endstelleA != null) {
            result.esAnspA = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_A, selectedView.getAuftragId());
        }

        result.endstelleB = esSrv.findEndstelle4Auftrag(selectedView.getAuftragId(),
                Endstelle.ENDSTELLEN_TYP_B);
        if (result.endstelleB != null) {
            result.esAnspB = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, selectedView.getAuftragId());
        }

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufStOnlineView selectedView, StOnlineDetailsWorkerResult result) {
        setSelectedView(selectedView);
        auftragDaten = result.auftragDaten;
        endstelleA = result.endstelleA;
        esAnspA = result.esAnspA;
        endstelleB = result.endstelleB;
        esAnspB = result.esAnspB;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(getOeName(selectedView));
        tfProdukt.setText(selectedView.getProduktName());
        dcGesamtRealisierung.setDate(selectedView.getGesamtrealisierungstermin());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        tfVbz.setText(selectedView.getVbz());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfKundeName.setText(selectedView.getKundenName());
        tfSchnittstelle.setText(selectedView.getSchnittstelle());
        tfLeitungA.setText(selectedView.getLeitungA());
        tfLeitungB.setText(selectedView.getLeitungB());
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

        lblErledigt.setVisible(selectedView.isErledigt());

        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
    }

    @Override
    public void clearDetails() {
        auftragDaten = null;
        endstelleB = null;
        endstelleA = null;
        esAnspA = null;
        esAnspB = null;
        if (guiCreated) {
            lblErledigt.setVisible(false);
        }
        super.clearDetails();
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
                getSelectedView().setErledigt(true);
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

    /* Ordnet den ausgewaehlten Verlauf dem aktuellen Benutzer zu. */
    private void verlaufUebernehmen() {
        VerlaufAbteilung verlAbt = verlaufUebernehmen(getSelectedView().getBearbeiterIPS());
        if (verlAbt != null) {
            getSelectedView().setBearbeiterIPS(verlAbt.getBearbeiter());
            getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            getSelectedView().setVerlaufStatus("in Bearbeitung"); // hard-coded - kann nur dieser Wert sein und ist nur eine View
            getSelectedView().notifyObservers(true);
        }
    }
}

class StOnlineDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    Ansprechpartner esAnspB = null;
    Endstelle endstelleB = null;
    Ansprechpartner esAnspA = null;
    Endstelle endstelleA = null;
    AuftragDaten auftragDaten = null;
}
