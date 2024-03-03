/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 09:02:33
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
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Ansprechpartner;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufStVoiceView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.AnsprechpartnerService;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.VPNService;


/**
 * Panel fuer die Anzeige/Bearbeitung der Bauauftraege von ST Voice.
 *
 *
 */
public class BauauftragStVoicePanel
        extends AbstractBauauftragPanel<VerlaufStVoiceView, StVoiceDetailsWorkerResult, AKReferenceAwareTableModel<VerlaufStVoiceView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragStVoicePanel.class);

    private static final String COL_CPS_TX_STATE = "CPS Status";
    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJTextField tfCarrier = null;
    private AKJDateComponent dcBereitst = null;
    private AKJDateComponent dcBaAnStConnect = null;
    private AKJDateComponent dcBaAnFieldService = null;
    private AKJDateComponent dcGesamtRealisierung = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfPortierung = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfSchnittstelle = null;
    private AKJTextField tfEsNameA = null;
    private AKJTextField tfEsSwitchA = null;
    private AKJTextField tfEsNameB = null;
    private AKJTextField tfEsSwitchB = null;
    private AKJTextField tfEsOrtB = null;
    private AKJTextField tfEsAnspB = null;
    private AKJTextField tfPhysikInfo = null;
    private AKJTextArea taBemerkung = null;

    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnErledigen = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnUebernahme = null;
    private AKJButton btnRufnummern = null;
    private AKJButton btnCPSTxHistory = null;
    private AKJButton btnCPSTxCreation = null;
    private AKJButton btnShowPorts = null;

    private VPN vpn = null;
    private Endstelle endstelleA = null;
    private Endstelle endstelleB = null;
    private Ansprechpartner esAnspB = null;
    private AuftragDaten auftragDaten = null;
    private Schnittstelle schnittstelleEsB = null;
    private List<VerlaufAbteilung> verlaufAbts = null;

    private boolean guiCreated = false;

    /**
     * Standardkonstruktor.
     */
    public BauauftragStVoicePanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragStVoicePanel.xml", Abteilung.ST_VOICE, true, COL_NIEDERLASSUNG);
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
                new String[] { "CPS", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Produkt (Billing)", //  0- 4
                        "Produkt (Hurrican)", "Real.-Datum", "Switch", "Haupt-RN", "Carrier",  //  5- 9
                        "Kunde", "VoIP", "verschoben", COL_CPS_TX_STATE, COL_NIEDERLASSUNG,    // 10-14
                        "Bearbeiter ST-V", "BA Status", "Status ST-V", "Wiedervorlage", },     // 15-18
                new String[] { "cpsProvisioning", "vbz", "auftragNoOrig", "anlass", "oeName",
                        "produktName", "realisierungstermin", "switchEsB", "hauptRN", "carrier",
                        "kundenName", "voIP", "verschoben", "cpsTxState", "niederlassung",
                        "bearbeiterSTVoice", "verlaufStatus", "verlaufAbteilungStatus", "wiedervorlage" },
                new Class[] { Boolean.class, String.class, Long.class, String.class, String.class,
                        String.class, Date.class, String.class, String.class, String.class,
                        String.class, Boolean.class, Boolean.class, String.class, String.class,
                        String.class, String.class, String.class, Date.class }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 40, 125, 70, 100, 120, 110, 80, 80, 90, 60, 120, 45, 65, 70, 70, 80, 70, 80, 80 });

        createDetailPanel();

        AKAbstractAction erledigenAction = btnErledigen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnUebernahme, btnRufnummern, btnPrint, btnPrintCompact,
                erledigenAction, btnErledigen, btnBemerkungen, btnCPSTxHistory, btnCPSTxCreation, btnShowPorts);
        guiCreated = true;
    }

    /* Erzeugt das Detail-Panel fuer die Verlaufs-Ansicht. */
    private void createDetailPanel() {
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOeName = getSwingFactory().createLabel("oe.name");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblCarrier = getSwingFactory().createLabel("carrier");
        AKJLabel lblBereitst = getSwingFactory().createLabel("bereitstellung");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
        AKJLabel lblEsNameA = getSwingFactory().createLabel("es.a.name");
        AKJLabel lblEsSwitchA = getSwingFactory().createLabel("es.a.switch");
        AKJLabel lblEsNameB = getSwingFactory().createLabel("es.b.name");
        AKJLabel lblEsSwitchB = getSwingFactory().createLabel("es.b.switch");
        AKJLabel lblEsOrtB = getSwingFactory().createLabel("es.b.ort");
        AKJLabel lblEsAnspB = getSwingFactory().createLabel("es.b.ansp");
        AKJLabel lblBaAnStConnect = getSwingFactory().createLabel("ba.an.stconnect");
        AKJLabel lblBaAnFieldService = getSwingFactory().createLabel("ba.an.fieldservice");
        AKJLabel lblMontageAkom = getSwingFactory().createLabel("montage.akom");
        AKJLabel lblPortierung = getSwingFactory().createLabel("portierungsart");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblGesamtRealisierung = getSwingFactory().createLabel("gesamt.realisierung");

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        tfCarrier = getSwingFactory().createTextField("carrier", false);
        dcBereitst = getSwingFactory().createDateComponent("bereitstellung", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);
        tfSchnittstelle = getSwingFactory().createTextField("schnittstelle", false);
        tfEsNameA = getSwingFactory().createTextField("es.a.name", false);
        tfEsSwitchA = getSwingFactory().createTextField("es.a.switch", false);
        tfEsNameB = getSwingFactory().createTextField("es.b.name", false);
        tfEsSwitchB = getSwingFactory().createTextField("es.b.switch", false);
        tfEsOrtB = getSwingFactory().createTextField("es.b.ort", false);
        tfEsAnspB = getSwingFactory().createTextField("es.b.ansp", false);
        dcBaAnStConnect = getSwingFactory().createDateComponent("ba.an.stconnect", false);
        dcBaAnFieldService = getSwingFactory().createDateComponent("ba.an.fieldservice", false);
        dcGesamtRealisierung = getSwingFactory().createDateComponent("gesamt.realisierung", false);
        chbMontageAkom = getSwingFactory().createCheckBox("montage.akom", false);
        tfPortierung = getSwingFactory().createTextField("portierungsart", false);
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);

        btnPrint = getSwingFactory().createButton(BTN_PRINT, getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton(BTN_BA_ERLEDIGEN, getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnUebernahme = getSwingFactory().createButton(BTN_UEBERNAHME, getActionListener());
        btnRufnummern = getSwingFactory().createButton("rufnummern", getActionListener());
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
        mid.add(lblSchnittst, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfSchnittstelle, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblMontageAkom, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbMontageAkom, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblPortierung, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfPortierung, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 0, 6, 3, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        right.add(lblEsNameA, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameA, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsSwitchA, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsSwitchA, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsNameB, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsNameB, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsSwitchB, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsSwitchB, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsOrtB, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsOrtB, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblEsAnspB, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfEsAnspB, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.NONE));
        right.add(lblBaAnStConnect, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcBaAnStConnect, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblBaAnFieldService, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcBaAnFieldService, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));
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
        child.add(mid, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(ldown, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right, GBCFactory.createGBC(0, 0, 4, 0, 1, 3, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 2, 1, 1, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnPrint, btnPrintCompact, btnErledigen, btnShowPorts,
                btnBemerkungen, btnUebernahme, btnRufnummern, btnCPSTxHistory, btnCPSTxCreation });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    public List<VerlaufStVoiceView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufStVoiceView> result = (List) bas.findBAVerlaufViews4Abt(
                false, Abteilung.ST_VOICE, false, realisierungFrom, realisierungTo);
        return result;
    }


    @Override
    public void updateGuiByTableData(List<VerlaufStVoiceView> tableData) {
        try {
            loadCPSTxStateReferences(tableModel, COL_CPS_TX_STATE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }

        ObserverHelper.addObserver2Objects(this, tableData);
        tableModel.setData(tableData);
        LOGGER.debug(".... data-size: " + tableData.size());
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
        else if ("rufnummern".equals(command)) {
            showRufnummern(getSelectedView());
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
    public StVoiceDetailsWorkerResult loadDetails(VerlaufStVoiceView selectedView)
            throws ServiceNotFoundException, FindException {
        StVoiceDetailsWorkerResult result = new StVoiceDetailsWorkerResult();

        EndstellenService esSrv = getCCService(EndstellenService.class);

        // VPN-Daten ermitteln
        VPNService vpns = getCCService(VPNService.class);
        result.vpn = vpns.findVPNByAuftragId(selectedView.getAuftragId());

        // ES-Daten (A+B) ermitteln
        if (selectedView.getEsIdA() != null) {
            result.endstelleA = esSrv.findEndstelle(selectedView.getEsIdA());
        }
        if (selectedView.getEsIdB() != null) {
            result.endstelleB = esSrv.findEndstelle(selectedView.getEsIdB());
            if (endstelleB != null) {
                AnsprechpartnerService anspSrv = getCCService(AnsprechpartnerService.class);
                result.esAnspB = anspSrv.findPreferredAnsprechpartner(Ansprechpartner.Typ.ENDSTELLE_B, selectedView.getAuftragId());

                EndstelleLtgDaten ltgDaten = esSrv.findESLtgDaten4ES(endstelleB.getId());
                if ((ltgDaten != null) && (ltgDaten.getSchnittstelleId() != null)) {
                    ProduktService prs = getCCService(ProduktService.class);
                    result.schnittstelleEsB = prs.findSchnittstelle(ltgDaten.getSchnittstelleId());
                }
            }
        }

        // AuftragDaten ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        // alle anderen Verlaufs-Eintraege ermitteln
        BAService bas = getCCService(BAService.class);
        result.verlaufAbts = bas.findVerlaufAbteilungen(selectedView.getVerlaufId());

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufStVoiceView selectedView, StVoiceDetailsWorkerResult result) {
        setSelectedView(selectedView);
        vpn = result.vpn;
        endstelleA = result.endstelleA;
        endstelleB = result.endstelleB;
        esAnspB = result.esAnspB;
        schnittstelleEsB = result.schnittstelleEsB;
        auftragDaten = result.auftragDaten;
        verlaufAbts = result.verlaufAbts;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcGesamtRealisierung.setDate(selectedView.getGesamtrealisierungstermin());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        tfVbz.setText(selectedView.getVbz());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfEsSwitchA.setText(selectedView.getSwitchEsA());
        tfEsSwitchB.setText(selectedView.getSwitchEsB());
        tfKundeName.setText(selectedView.getKundenName());
        chbMontageAkom.setSelected(hasExternInstallation(selectedView));
        tfCarrier.setText(selectedView.getCarrier());
        dcBereitst.setDate(selectedView.getCarrierBereitstellung());
        tfVpnNr.setValue((vpn != null) ? vpn.getVpnNr() : null);
        tfEsNameA.setText((endstelleA != null) ? endstelleA.getName() : null);
        tfSchnittstelle.setText((schnittstelleEsB != null) ? schnittstelleEsB.getSchnittstelle() : null);
        tfPhysikInfo.setText(getPhysikInfo(selectedView));
        taBemerkung.setText((auftragDaten != null) ? auftragDaten.getBemerkungen() : null);
        tfPortierung.setText(selectedView.getPortierungsart());
        GuiTools.switchForegroundColor(tfPortierung, selectedView.getPortierungsartId(),
                portierung2Color, tfSchnittstelle.getForeground());

        if (endstelleB != null) {
            tfEsNameB.setText(endstelleB.getName());
            tfEsOrtB.setText(endstelleB.getOrt());
            tfEsAnspB.setText((esAnspB != null) ? esAnspB.getDisplayText() : null);
        }

        if (verlaufAbts != null) {
            for (VerlaufAbteilung va : verlaufAbts) {
                if (NumberTools.equal(va.getAbteilungId(), Abteilung.ST_CONNECT)) {
                    dcBaAnStConnect.setDate(va.getDatumAn());
                }
                else if (NumberTools.equal(va.getAbteilungId(), Abteilung.FIELD_SERVICE)) {
                    dcBaAnFieldService.setDate(va.getDatumAn());
                }
            }
        }
        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
    }

    @Override
    public void clearDetails() {
        vpn = null;
        endstelleA = null;
        endstelleB = null;
        esAnspB = null;
        auftragDaten = null;
        schnittstelleEsB = null;
        verlaufAbts = null;
        if (guiCreated) {
            tfPortierung.setBackground(tfSchnittstelle.getBackground());
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
        VerlaufAbteilung verlAbt = verlaufUebernehmen(getSelectedView().getBearbeiterSTVoice());
        if (verlAbt != null) {
            getSelectedView().setBearbeiterSTVoice(verlAbt.getBearbeiter());
            getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
            getSelectedView().setVerlaufStatus("in Bearbeitung"); // hard-coded - kann nur dieser Wert sein und ist nur eine View
            getSelectedView().notifyObservers(true);
        }
    }
}

class StVoiceDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    List<VerlaufAbteilung> verlaufAbts = null;
    AuftragDaten auftragDaten = null;
    Schnittstelle schnittstelleEsB = null;
    Ansprechpartner esAnspB = null;
    Endstelle endstelleB = null;
    Endstelle endstelleA = null;
    VPN vpn = null;
}
