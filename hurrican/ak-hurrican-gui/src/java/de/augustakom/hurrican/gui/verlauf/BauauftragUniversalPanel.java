/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
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
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.VPNService;

/**
 * Panel fuer eine abteilungs-uebergreifende Bauauftrags-GUI. <br/>
 * Die gewuenschte Abteilung kann ueber eine ComboBox selektiert werden; durch die Selektion werden die
 * Bauauftrags-Datensaetze geladen.
 */
public class BauauftragUniversalPanel extends
        AbstractBauauftragPanel<VerlaufUniversalView, UniversalDetailsWorkerResult, AKReferenceAwareTableModel<VerlaufUniversalView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragUniversalPanel.class);

    private static final long serialVersionUID = 3463799318327918209L;

    private static final String COL_CPS_TX_STATE = "CPS Status";
    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcBereitst = null;
    private AKJTextField tfCarrierLbz = null;
    private AKJTextField tfCarrierVtrNr = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfHvtAnsArt = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfSchnittstelle = null;
    private AKJDateComponent dcBaAnAbteilung = null;
    private AKJTextField tfBearbAm = null;
    private AKJTextField tfPortierung = null;
    private AKJTextArea taBemerkung = null;
    private AKJTextField tfPhysikInfo = null;
    private AKReferenceField rfInstall = null;
    private AKJTextField tfInstallTS = null;

    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnErledigen = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnUebernahme = null;
    private AKJButton btnCPSTxHistory = null;
    private AKJButton btnCPSTxCreation = null;
    private AKJButton btnShowPorts = null;

    private AuftragDaten auftragDaten = null;
    private VPN vpn = null;
    private Schnittstelle schnittstelleEsB = null;
    private Carrierbestellung carrierbestellung = null;


    /**
     * Default-Konstruktor.
     */
    public BauauftragUniversalPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragUniversalPanel.xml", false, COL_NIEDERLASSUNG);
        createGUI();
        init();
        loadData();
    }

    @Override
    public void init() {
        super.init();
        try {
            rfInstall.setFindService(getCCService(QueryCCService.class));
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    protected final void createGUI() {
        super.createGUI();
        tableModel = new AKReferenceAwareTableModel<>(
                new String[] { "CPS", VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Kreuzung",
                        "Produkt (Billing)", "Produkt (Hurrican)",
                        "Real.-Datum", "TimeSlot", "BA-Hinweise", "Kunde", "Ort ES-B", "Strasse", "Anschlussart",
                        "verschoben", COL_NIEDERLASSUNG, COL_CPS_TX_STATE, "VPN-Nr.",
                        "Projektverantwortlicher", "BA Status", "Bearbeiter", "Status", "Wiedervorlage", "Cluster-ID" },
                new String[] { "cpsProvisioning", "vbz", "auftragNoOrig", "anlass", "kreuzung", "oeName", "produktName",
                        "gesamtrealisierungstermin", "timeSlot.timeSlotToUseAsString", "baHinweise", "kundenName", "endstelleOrtB",
                        "endstelleB", "hvtAnschlussart", "verschoben", "niederlassung", "cpsTxState", "vpnNr",
                        "projectResponsibleName", "verlaufStatus", "bearbeiterAbteilung", "verlaufAbteilungStatus",
                        "wiedervorlage", "hvtClusterId" },
                new Class[] { Boolean.class, String.class, Long.class, String.class, Boolean.class, String.class, String.class,
                        Date.class, String.class, String.class, String.class, String.class, String.class, String.class,
                        Boolean.class, String.class, String.class, Long.class,
                        String.class, String.class, String.class, String.class, Date.class, String.class }
        );
        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        OpenTerminverschiebungAction changeDateAction = new OpenTerminverschiebungAction();
        changeDateAction.setParentClass(this.getClass());
        getTable().addPopupAction(changeDateAction);
        if (Abteilung.DISPO.equals(abteilungId)) {
            AssignBAToNetzplanungAction assign2NetzplanungAction = new AssignBAToNetzplanungAction();
            assign2NetzplanungAction.setParentClass(this.getClass());
            getTable().addPopupAction(assign2NetzplanungAction);
        }
        getTable().attachSorter();
        getTable().fitTable(new int[] {
                40, 125, 70, 115, 40, 130, 130, 70, 110, 100, 100, 100, 120, 25, 70, 70, 120, 70, 80, 80, 80, 50 });

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
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblBereitst = getSwingFactory().createLabel("bereitstellung");
        AKJLabel lblCarrierLbz = getSwingFactory().createLabel("carrier.lbz");
        AKJLabel lblCarrierVtrNr = getSwingFactory().createLabel("carrier.vtr.nr");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblMontageAkom = getSwingFactory().createLabel("montage.akom");
        AKJLabel lblHvtAnsArt = getSwingFactory().createLabel("hvt.anschlussart");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
        AKJLabel lblBaAnAbteilung = getSwingFactory().createLabel("ba.an.abteilung");
        AKJLabel lblBearbAm = getSwingFactory().createLabel("bearbeiter.am");
        AKJLabel lblPortierung = getSwingFactory().createLabel("portierungsart");
        AKJLabel lblInstall = getSwingFactory().createLabel("installation");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblInstallTS = getSwingFactory().createLabel("install.time.slot");

        tfAnlass = getSwingFactory().createTextField("anlasss", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        dcBereitst = getSwingFactory().createDateComponent("bereitstellung", false);
        tfCarrierLbz = getSwingFactory().createTextField("carrier.lbz", false);
        tfCarrierVtrNr = getSwingFactory().createTextField("carrier.vtr.nr", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        chbMontageAkom = getSwingFactory().createCheckBox("montage.akom", false);
        tfHvtAnsArt = getSwingFactory().createTextField("hvt.anschlussart", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);
        tfSchnittstelle = getSwingFactory().createTextField("schnittstelle", false);
        dcBaAnAbteilung = getSwingFactory().createDateComponent("ba.an.abteilung", false);
        tfBearbAm = getSwingFactory().createTextField("bearbeiter.am", false);
        tfPortierung = getSwingFactory().createTextField("portierungsart", false);
        rfInstall = getSwingFactory().createReferenceField("installation");
        rfInstall.setEnabled(false);
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);
        tfInstallTS = getSwingFactory().createTextField("install.time.slot", false);

        btnPrint = getSwingFactory().createButton(BTN_PRINT, getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnErledigen = getSwingFactory().createButton(BTN_BA_ERLEDIGEN, getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnUebernahme = getSwingFactory().createButton(BTN_UEBERNAHME, getActionListener());
        btnCPSTxHistory = getSwingFactory().createButton(SHOW_CPS_TX_HISTORY, getActionListener());
        btnCPSTxCreation = getSwingFactory().createButton(CREATE_CPS_TX, getActionListener());
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        // @formatter:off
        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.add(lblAnlass      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel() , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAnlass       , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOeName      , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOeName       , GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt     , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProdukt      , GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRealisierung, GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcRealisierung , GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBereitst    , GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitst     , GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz          , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel()  , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz           , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId    , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId     , GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr        , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr         , GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblCarrierLbz   , GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfCarrierLbz    , GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblCarrierVtrNr , GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfCarrierVtrNr  , GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblKundeName      , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()    , GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfKundeName       , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblSchnittst      , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSchnittstelle   , GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()    , GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        right.add(lblBaAnAbteilung  , GBCFactory.createGBC(  0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcBaAnAbteilung   , GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblBearbAm        , GBCFactory.createGBC(  0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfBearbAm         , GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel()    , GBCFactory.createGBC(  0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        right.add(lblHvtAnsArt      , GBCFactory.createGBC(  0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfHvtAnsArt       , GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMontageAkom    , GBCFactory.createGBC(  0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbMontageAkom    , GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblPortierung     , GBCFactory.createGBC(  0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfPortierung      , GBCFactory.createGBC(100, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInstall        , GBCFactory.createGBC(  0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rfInstall         , GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInstallTS      , GBCFactory.createGBC(  0, 0, 0,10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfInstallTS       , GBCFactory.createGBC(100, 0, 2,10, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel ldown = new AKJPanel(new GridBagLayout());
        AKJScrollPane spBemerkung = new AKJScrollPane(taBemerkung);
        ldown.add(lblPhysikInfo , GBCFactory.createGBC(  0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        ldown.add(tfPhysikInfo  , GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(lblBemerkung  , GBCFactory.createGBC(  0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        ldown.add(new AKJPanel(), GBCFactory.createGBC(  0, 0, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        ldown.add(spBemerkung   , GBCFactory.createGBC(100, 0, 2, 1, 1, 2, GridBagConstraints.HORIZONTAL));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left          , GBCFactory.createGBC(10, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC( 0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid           , GBCFactory.createGBC( 0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(ldown         , GBCFactory.createGBC( 0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC( 0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right         , GBCFactory.createGBC( 0, 0, 4, 0, 1, 3, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC( 0, 0, 5, 2, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnPrint, btnPrintCompact, btnErledigen, btnShowPorts,
                btnBemerkungen, btnUebernahme, btnCPSTxHistory, btnCPSTxCreation });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    @Override
    public List<VerlaufUniversalView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        if (abteilungId != null) {
            BAService bas = getCCService(BAService.class);
            @SuppressWarnings("unchecked")
            List<VerlaufUniversalView> universalViews = (List) bas.findBAVerlaufViews4Abt(
                    true, getAbteilungId(), false, realisierungFrom, realisierungTo);
            return universalViews;
        }
        return Collections.emptyList();
    }

    @Override
    public void updateGuiByTableData(List<VerlaufUniversalView> tableData) {
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
    public UniversalDetailsWorkerResult loadDetails(VerlaufUniversalView selectedView) throws ServiceNotFoundException, FindException {
        UniversalDetailsWorkerResult result = new UniversalDetailsWorkerResult();

        // AuftragDaten ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        // VPN-Daten ermitteln
        VPNService vpns = getCCService(VPNService.class);
        result.vpn = vpns.findVPNByAuftragId(selectedView.getAuftragId());

        // Schnittstelle ermitteln
        EndstellenService esSrv = getCCService(EndstellenService.class);
        if (selectedView.getEsIdB() != null) {
            Endstelle endstelleB = esSrv.findEndstelle(selectedView.getEsIdB());
            if (endstelleB != null) {
                EndstelleLtgDaten ltgDaten = esSrv.findESLtgDaten4ES(endstelleB.getId());
                if ((ltgDaten != null) && (ltgDaten.getSchnittstelleId() != null)) {
                    ProduktService prs = getCCService(ProduktService.class);
                    result.schnittstelleEsB = prs.findSchnittstelle(ltgDaten.getSchnittstelleId());
                }

                // Carrierbestellung ermitteln
                CarrierService cs = getCCService(CarrierService.class);
                List<Carrierbestellung> cbs = cs.findCBs4Endstelle(endstelleB.getId());
                result.carrierbestellung = ((cbs != null) && (!cbs.isEmpty())) ? cbs.get(cbs.size() - 1) : null;
            }
        }

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView
                .getVerlaufId());

        result.timeSlot = selectedView.getTimeSlot().getTimeSlotToUseAsString();

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufUniversalView selectedView, UniversalDetailsWorkerResult result) {
        setSelectedView(selectedView);
        auftragDaten = result.auftragDaten;
        vpn = result.vpn;
        schnittstelleEsB = result.schnittstelleEsB;
        carrierbestellung = result.carrierbestellung;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        dcBaAnAbteilung.setDate(selectedView.getDatumAnAbteilung());
        tfPhysikInfo.setText(getPhysikInfo(selectedView));
        tfVbz.setText(selectedView.getVbz());
        chbMontageAkom.setSelected(hasExternInstallation(selectedView));
        tfHvtAnsArt.setText(selectedView.getHvtAnschlussart());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfSchnittstelle.setText((schnittstelleEsB != null) ? schnittstelleEsB.getSchnittstelle() : null);
        tfKundeName.setText(selectedView.getKundenName());
        tfPortierung.setText(selectedView.getPortierungsart());
        GuiTools.switchForegroundColor(tfPortierung, selectedView.getPortierungsartId(),
                portierung2Color, tfSchnittstelle.getForeground());
        tfVpnNr.setValue((vpn != null) ? vpn.getVpnNr() : null);
        taBemerkung.setText((auftragDaten != null) ? auftragDaten.getBemerkungen() : null);
        tfBearbAm.setText((auftragDaten != null) ? auftragDaten.getBearbeiter() : null);
        if (selectedView.getInstallationRefId() != null) {
            rfInstall.setReferenceId(selectedView.getInstallationRefId());
        }

        // Installationstermin ermitteln
        tfInstallTS.setText(result.timeSlot);

        if (carrierbestellung != null) {
            dcBereitst.setDate(carrierbestellung.getBereitstellungAm());
            tfCarrierLbz.setText(carrierbestellung.getLbz());
            tfCarrierVtrNr.setText(carrierbestellung.getVtrNr());
        }
        showBemerkungenInPanel(selectedView.getVerlaufId());
        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
    }

    @Override
    public void clearDetails() {
        vpn = null;
        auftragDaten = null;
        schnittstelleEsB = null;
        carrierbestellung = null;
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
        if (getSelectedView() != null) {
            VerlaufAbteilung verlAbt = verlaufUebernehmen(getSelectedView().getBearbeiterAbteilung());
            if (verlAbt != null) {
                getSelectedView().setBearbeiterAbteilung(verlAbt.getBearbeiter());
                getSelectedView().setVerlaufAbtStatusId(verlAbt.getVerlaufStatusId());
                getSelectedView().setVerlaufStatus("in Bearbeitung"); // hard-coded - kann nur dieser Wert sein und ist nur eine View
                getSelectedView().notifyObservers(true);
            }
        }
    }
}

class UniversalDetailsWorkerResult {
    Carrierbestellung carrierbestellung = null;
    Schnittstelle schnittstelleEsB = null;
    VPN vpn = null;
    String timeSlot = null;
    AuftragDaten auftragDaten = null;
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
}
