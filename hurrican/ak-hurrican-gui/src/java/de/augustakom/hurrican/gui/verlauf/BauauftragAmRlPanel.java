/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.01.2005 13:11:22
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.commons.collections.CollectionUtils;
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
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.view.VerlaufAmRlView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungNegPredicate;


/**
 * Panel fuer die Bauauftrags-Ruecklaeufer von AM.
 *
 *
 */
public class BauauftragAmRlPanel
        extends AbstractBauauftragPanel<VerlaufAmRlView, AMRlDetailsWorkerResult, AKReferenceAwareTableModel<VerlaufAmRlView>> {

    private static final long serialVersionUID = -2523298993289053728L;

    private static final Logger LOGGER = Logger.getLogger(BauauftragAmRlPanel.class);

    private static final String COL_NIEDERLASSUNG = "Niederlassung";
    private static final String EMPTY_STRING = " ";
    private String baAbgeschlossen = null;

    // GUI-Komponenten
    private VerlaufAbtTableModel tbMdlVerlaufAbt = null;
    private AKJTextField tfAnlass = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcInbetrieb = null;
    private AKJTextField tfBearbAm = null;
    private AKJTextField tfBearbBA = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfOrderNo = null;
    private AKJFormattedTextField tfBuendelNr = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfHvtAnsArt = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfSchnittstelle = null;
    private AKJLabel lblStatus = null;

    private AKJButton btnBemerkungen = null;
    private AKJButton btnAbschliessen = null;
    private AKJButton btnShowPorts = null;

    // Modelle
    private List<Abteilung> abteilungen = null;
    private List<Niederlassung> niederlassungen = null;
    private Map<Long, Reference> amResponsibilityRefsMap = null;
    private VPN vpn = null;
    private Schnittstelle schnittstelleEsB = null;

    private boolean guiCreated = false;

    /**
     * Default-Konstruktor.
     */
    public BauauftragAmRlPanel() {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragAmRlPanel.xml", Abteilung.AM, false,
                COL_NIEDERLASSUNG);
        loadDefaultData();
        createGUI();
        init();
        loadData();
    }

    @Override
    protected AKJTable createTable() {
        return new VerlaufAmTable();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        super.createGUI();
        tableModel = new AKReferenceAwareTableModel<>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Anlass", "Produkt (Hurrican)", "Real-Datum", "Inbetriebn.", // 0- 4
                        "Order__No", "Tech. Auftragsnr.", "Buendel-Nr", "VPN", "Kunde",                      // 5- 9
                        "Bearbeiter BA", "BA Status", "Bearbeiter AM", "verschoben", COL_NIEDERLASSUNG, "AM Bereich " },     //10-14
                new String[] { "vbz", "anlass", "produktName", "realisierungstermin", "inbetriebnahme",
                        "auftragNoOrig", "auftragId", "buendelNr", "vpnNr", "kundenName",
                        "bearbeiter", "verlaufStatus", "bearbeiterAm", "verschoben", "niederlassung", "amResponsibility" },
                new Class[] { String.class, String.class, String.class, Date.class, Date.class,
                        Long.class, Long.class, Integer.class, Integer.class, String.class,
                        String.class, String.class, String.class, Boolean.class, String.class, String.class }
        );
        tableModel.addFilterTableModelListener(this);
        tableModel.addReference(15, amResponsibilityRefsMap, "strValue");
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 125, 110, 100, 70, 70, 65, 65, 65, 50, 120, 80, 70, 80, 25, 70, 50 });

        createDetailPanel();
        AKAbstractAction erledigenAction = btnAbschliessen.createAction();
        getTable().addPopupAction(erledigenAction);
        manageGUI(btnBemerkungen, btnAbschliessen, btnShowPorts, erledigenAction);
        guiCreated = true;
    }

    /* Erzeugt das Detail-Panel. */
    private void createDetailPanel() {
        baAbgeschlossen = getSwingFactory().getText("abgeschlossen");
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOrderNo = getSwingFactory().createLabel("order.no");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblInbetrieb = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblBearbAm = getSwingFactory().createLabel("bearbeiter.am");
        AKJLabel lblBearbBA = getSwingFactory().createLabel("bearbeiter.ba");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblBuendelNr = getSwingFactory().createLabel("buendel.nr");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblMontageAkom = getSwingFactory().createLabel("montage.akom");
        AKJLabel lblHvtAnsArt = getSwingFactory().createLabel("hvt.anschlussart");
        AKJLabel lblKundeName = getSwingFactory().createLabel("kunde.name");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
        lblStatus = new AKJLabel(EMPTY_STRING);
        lblStatus.setForeground(Color.red);

        tfAnlass = getSwingFactory().createTextField("anlass", false);
        tfOrderNo = getSwingFactory().createFormattedTextField("order.no", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        dcInbetrieb = getSwingFactory().createDateComponent("inbetriebnahme", false);
        tfBearbAm = getSwingFactory().createTextField("bearbeiter.am", false);
        tfBearbBA = getSwingFactory().createTextField("bearbeiter.ba", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfBuendelNr = getSwingFactory().createFormattedTextField("buendel.nr", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        chbMontageAkom = getSwingFactory().createCheckBox("montage.akom", false);
        tfHvtAnsArt = getSwingFactory().createTextField("hvt.anschlussart", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);
        tfSchnittstelle = getSwingFactory().createTextField("schnittstelle", false);

        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnAbschliessen = getSwingFactory().createButton("ba.abschliessen", getActionListener());
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        AKJPanel left = new AKJPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(260, 140));
        left.add(lblAnlass, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        left.add(tfAnlass, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblOrderNo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfOrderNo, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblProdukt, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfProdukt, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblRealisierung, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcRealisierung, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblInbetrieb, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcInbetrieb, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbAm, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbAm, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbBA, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbBA, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBuendelNr, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBuendelNr, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 8, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblHvtAnsArt, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfHvtAnsArt, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblMontageAkom, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(chbMontageAkom, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblSchnittst, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfSchnittstelle, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlVerlaufAbt = new VerlaufAbtTableModel();
        tbMdlVerlaufAbt.setAbteilungen(abteilungen);
        tbMdlVerlaufAbt.setNiederlassungen(niederlassungen);
        AKJTable tbAbtVerl = new AKJTable(tbMdlVerlaufAbt,
                AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbAbtVerl.fitTable(new int[] { 60, 55, 70, 70, 70, 95 });
        TableColumnModel tbCM = tbAbtVerl.getColumnModel();
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AUSGETRAGEN).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AB).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_REAL).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AN).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        AKJScrollPane spTable = new AKJScrollPane(tbAbtVerl);
        spTable.setPreferredSize(new Dimension(370, 180));

        AKJPanel tbPnl = new AKJPanel(new GridBagLayout());
        tbPnl.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.bauauftraege")));
        tbPnl.add(spTable, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left, GBCFactory.createGBC(0, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid, GBCFactory.createGBC(0, 100, 2, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(tbPnl, GBCFactory.createGBC(0, 0, 4, 0, 1, 2, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnAbschliessen, btnBemerkungen, btnShowPorts });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }


    /* Laedt die Standard-Daten fuer das Panel. */
    private void loadDefaultData() {
        try {
            NiederlassungService nls = getCCService(NiederlassungService.class);
            abteilungen = nls.findAbteilungen4Ba();
            niederlassungen = nls.findNiederlassungen();

            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> amResponsibilityRefs = rs.findReferencesByType(Reference.REF_TYPE_AM_PART, Boolean.TRUE);
            amResponsibilityRefsMap = CollectionMapConverter.convert2Map(amResponsibilityRefs, "getId", null);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<VerlaufAmRlView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufAmRlView> result = (List) bas.findBAVerlaufViews4Abt(false, Abteilung.AM, true, realisierungFrom, realisierungTo);
        return result;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufAmRlView> tableData) {
        ObserverHelper.addObserver2Objects(this, tableData);
        tableModel.setData(tableData);
        ObserverHelper.addObserver2Objects((VerlaufAmTable) getTable(), tableData);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (BTN_BEMERKUNGEN.equals(command)) {
            showBemerkungen((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null);
        }
        else if ("ba.abschliessen".equals(command)) {
            verlaufErledigen();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getSelectedView());
        }
    }

    /* Schliesst den aktuellen Verlauf ab. */
    private void verlaufErledigen() {
        if (getSelectedView() == null) {
            MessageHelper.showInfoDialog(this, NOTHING_SELECTED, null, true);
            return;
        }
        else if (getSelectedView().isGuiFinished()) {
            MessageHelper.showInfoDialog(this, "Bauauftrag wurde bereits abgeschlossen.", null, true);
            return;
        }

        try {
            setWaitCursor();
            BAService bas = getCCService(BAService.class);
            Verlauf result = bas.amBaAbschliessen(
                    getSelectedView().getVerlaufId(), HurricanSystemRegistry.instance().getSessionId());

            getSelectedView().setGuiFinished(true);
            getSelectedView().notifyObservers(true);

            // Hinweis, dass Verlauf negativ --> Auftragsstatus auf Ursprung!!!
            if (BooleanTools.nullToFalse(result.getNotPossible())) {
                MessageHelper.showInfoDialog(getMainFrame(),
                        "Der Bauauftrag wurde als nicht realisierbar gekennzeichnet!\n" +
                                "Status des Auftrags wurde auf den Ursprungswert zurück gesetzt und " +
                                "der Bauauftrag beendet."
                );
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

    @Override
    public AMRlDetailsWorkerResult loadDetails(VerlaufAmRlView selectedView) throws ServiceNotFoundException, FindException {
        AMRlDetailsWorkerResult result = new AMRlDetailsWorkerResult();

        BAService bas = getCCService(BAService.class);
        result.verlaufAbts = bas.findVerlaufAbteilungen(selectedView.getVerlaufId());
        // Abteilung AM aus der Liste entfernen.
        CollectionUtils.filter(result.verlaufAbts, new VerlaufAbteilungNegPredicate(Abteilung.AM));

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
            }
        }

        CCLeistungsService ccLeistungsService = getCCService(CCLeistungsService.class);
        result.auftrag2TechLeistungen = ccLeistungsService.findAuftrag2TechLeistungen4Verlauf(selectedView.getVerlaufId());

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufAmRlView selectedView, AMRlDetailsWorkerResult result) {
        setSelectedView(selectedView);
        tbMdlVerlaufAbt.setData(result.verlaufAbts);
        vpn = result.vpn;
        schnittstelleEsB = result.schnittstelleEsB;

        tfAnlass.setText(selectedView.getAnlass());
        tfOrderNo.setValue(selectedView.getAuftragNoOrig());
        tfProdukt.setText(selectedView.getProduktName());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        dcInbetrieb.setDate(selectedView.getInbetriebnahme());
        tfVbz.setText(selectedView.getVbz());
        chbMontageAkom.setSelected(hasExternInstallation(selectedView));
        tfHvtAnsArt.setText(selectedView.getHvtAnschlussart());
        tfAuftragId.setValue(selectedView.getAuftragId());
        tfSchnittstelle.setText((schnittstelleEsB != null) ? schnittstelleEsB.getSchnittstelle() : null);
        tfKundeName.setText(selectedView.getKundenName());
        tfVpnNr.setValue((vpn != null) ? vpn.getVpnNr() : null);
        tfBuendelNr.setValue(selectedView.getBuendelNr());
        tfBearbAm.setText(selectedView.getBearbeiterAm());
        tfBearbBA.setText(selectedView.getBearbeiter());

        lblStatus.setText((selectedView.isGuiFinished()) ? baAbgeschlossen : EMPTY_STRING);
        lblStatus.repaint();

        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
    }

    @Override
    public void clearDetails() {
        vpn = null;
        schnittstelleEsB = null;

        if (guiCreated) {
            tbMdlVerlaufAbt.setData(null);
            lblStatus.setText(EMPTY_STRING);
        }
        super.clearDetails();
    }
}

/**
 * value-Object Klasse zur Speicherung der Werte aus dem {@link SwingWorker#doInBackground()}
 */
class AMRlDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    Schnittstelle schnittstelleEsB = null;
    VPN vpn = null;
    List<VerlaufAbteilung> verlaufAbts = null;
}
