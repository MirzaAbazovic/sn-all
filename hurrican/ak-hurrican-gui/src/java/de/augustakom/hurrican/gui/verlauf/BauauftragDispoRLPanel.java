/**
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.01.2005 11:42:09
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
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.BAVerlaufAnlass;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.view.VerlaufDispoRLView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungPredicate;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;


/**
 * Panel fuer die Darstellung der BA-Ruecklaeufer fuer DISPO bzw. Netzplanung.
 *
 *
 */
public class BauauftragDispoRLPanel
        extends AbstractBauauftragPanel<VerlaufDispoRLView, DispoRlDetailsWorkerResult, AKReflectionTableModel<VerlaufDispoRLView>> {

    private static final Logger LOGGER = Logger.getLogger(BauauftragDispoRLPanel.class);
    private static final long serialVersionUID = -8439807343706227082L;

    private static final String EMPTY_STRING = " ";
    private static final String BTN_ABSCHLIESSEN = "abschliessen";
    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    // GUI-Komponenten
    private VerlaufAbtTableModel tbMdlVerlaufAbt = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnAbschliessen = null;
    private AKJButton btnAbschlManuell = null;
    private AKJButton btnStorno = null;
    private AKJButton btnShowPorts = null;
    private AKJTextField tfAnlass = null;
    private AKJTextField tfOeName = null;
    private AKJTextField tfProdukt = null;
    private AKJDateComponent dcRealisierung = null;
    private AKJDateComponent dcInbetrieb = null;
    private AKJTextField tfBearbAm = null;
    private AKJTextField tfVbz = null;
    private AKJFormattedTextField tfAuftragId = null;
    private AKJFormattedTextField tfVpnNr = null;
    private AKJCheckBox chbMontageAkom = null;
    private AKJTextField tfHvtAnsArt = null;
    private AKJTextField tfKundeName = null;
    private AKJTextField tfSchnittstelle = null;
    private AKJLabel lblStatus = null;
    private AKJTextField tfInstallTS = null;

    private String baStorniert = null;
    private String baAbgeschlossen = null;

    // Modelle
    private List<Abteilung> abteilungen = null;
    private List<Niederlassung> niederlassungen = null;
    private VerlaufAbteilung vaAm = null;
    private VPN vpn = null;
    private Schnittstelle schnittstelleEsB = null;

    private boolean guiCreated = false;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID (Dispo oder Netzplanung) fuer die die BA-Ruecklaeufer angezeigt werden
     * sollen.
     *
     * @param abteilungId
     */
    public BauauftragDispoRLPanel(Long abteilungId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragDispoRLPanel.xml", abteilungId, true,
                COL_NIEDERLASSUNG);
        loadDefaultData();
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
        String abteilung = (Abteilung.DISPO.equals(abteilungId) ? "DISPO" : "NP");
        tableModel = new AKReflectionTableModel<>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Produkt (Billing)", "Produkt (Hurrican)", "Real.-Datum",  // 0-5
                        "Inbetriebnahme", "Kunde", "ES-B Ort", "Strasse", "Bearbeiter AM", "techn. erledigt", "verschoben",                 // 6-11
                        "Verteiler", COL_NIEDERLASSUNG, "Projektverantwortlicher", "BA Status", "Status " + abteilung, "Wiedervorlage", "Cluster-ID" },
                new String[] { "vbz", "auftragNoOrig", "anlass", "oeName", "produktName", "gesamtrealisierungstermin",
                        "inbetriebnahme", "kundenName", "endstelleOrtB", "endstelleB", "bearbeiterAm", "erledigt", "verschoben",
                        "verteiler", "niederlassung", "projectResponsible", "verlaufStatus", "verlaufAbteilungStatus", "wiedervorlage", "hvtClusterId" },
                new Class[] { String.class, Long.class, String.class, String.class, String.class, Date.class,
                        Date.class, String.class, String.class, String.class, String.class, Boolean.class, Boolean.class,
                        String.class, String.class, String.class, String.class, String.class, Date.class, String.class }
        );

        tableModel.addFilterTableModelListener(this);
        getTable().setModel(tableModel);
        getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        OpenTerminverschiebungAction changeDateAction = new OpenTerminverschiebungAction();
        changeDateAction.setParentClass(this.getClass());
        getTable().addPopupAction(changeDateAction);
        getTable().attachSorter();
        getTable().fitTable(new int[] { 125, 70, 115, 130, 130, 70, 70, 150, 100, 100, 80, 40, 25, 80, 70, 120, 70, 80, 80, 50 });

        ManuellVerteilenAction mvAction = new ManuellVerteilenAction("Nachverteilen");
        mvAction.setParentClassName(getClassName());
        getTable().addPopupAction(mvAction);

        ObserveVerlaufAction ovAction = new ObserveVerlaufAction();
        ovAction.setParentClassName(getClassName());
        getTable().addPopupAction(ovAction);

        createDetailPanel();
        manageGUI(btnBemerkungen, btnShowPorts, changeDateAction);
        manageGUI(getClassName(), btnAbschliessen, btnAbschlManuell, btnStorno, mvAction, ovAction);
        guiCreated = true;
    }

    /**
     * Erzeugt das Detail-Panel.
     */
    private void createDetailPanel() {
        AKJLabel lblAnlass = getSwingFactory().createLabel("anlass");
        AKJLabel lblOeName = getSwingFactory().createLabel("oe.name");
        AKJLabel lblProdukt = getSwingFactory().createLabel("produkt");
        AKJLabel lblRealisierung = getSwingFactory().createLabel("realisierungstermin");
        AKJLabel lblInbetrieb = getSwingFactory().createLabel("inbetriebnahme");
        AKJLabel lblBearbAm = getSwingFactory().createLabel("bearbeiter");
        AKJLabel lblInstallTS = getSwingFactory().createLabel("install.time.slot");
        AKJLabel lblVbz = getSwingFactory().createLabel("vbz");
        AKJLabel lblAuftragId = getSwingFactory().createLabel("auftrag.id");
        AKJLabel lblVpnNr = getSwingFactory().createLabel("vpn.nr");
        AKJLabel lblHvtAnsArt = getSwingFactory().createLabel("hvt.anschlussart");
        AKJLabel lblMontageAkom = getSwingFactory().createLabel("montage.akom");
        AKJLabel lblSchnittst = getSwingFactory().createLabel("schnittstelle");
        AKJLabel lblKunde = getSwingFactory().createLabel("kunde.name");
        lblStatus = new AKJLabel(EMPTY_STRING);
        lblStatus.setForeground(Color.red);

        baStorniert = getSwingFactory().getText("storniert");
        baAbgeschlossen = getSwingFactory().getText("abgeschlossen");
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        tfAnlass = getSwingFactory().createTextField("anlass", false);
        tfOeName = getSwingFactory().createTextField("oe.name", false);
        tfProdukt = getSwingFactory().createTextField("produkt", false);
        dcRealisierung = getSwingFactory().createDateComponent("realisierungstermin", false);
        dcInbetrieb = getSwingFactory().createDateComponent("inbetriebnahme", false);
        tfBearbAm = getSwingFactory().createTextField("bearbeiter", false);
        tfInstallTS = getSwingFactory().createTextField("install.time.slot", false);
        tfVbz = getSwingFactory().createTextField("vbz", false);
        tfAuftragId = getSwingFactory().createFormattedTextField("auftrag.id", false);
        tfVpnNr = getSwingFactory().createFormattedTextField("vpn.nr", false);
        tfHvtAnsArt = getSwingFactory().createTextField("hvt.anschlussart", false);
        chbMontageAkom = getSwingFactory().createCheckBox("montage.akom", false);
        tfSchnittstelle = getSwingFactory().createTextField("schnittstelle", false);
        tfKundeName = getSwingFactory().createTextField("kunde.name", false);

        String className = getClassName();
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnAbschliessen = getSwingFactory().createButton(BTN_ABSCHLIESSEN, getActionListener());
        btnAbschliessen.setParentClassName(className);
        btnAbschlManuell = getSwingFactory().createButton("abschluss.manuell", getActionListener());
        btnAbschlManuell.setParentClassName(className);
        btnStorno = getSwingFactory().createButton("storno", getActionListener());
        btnStorno.setParentClassName(className);

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
        left.add(lblInbetrieb, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcInbetrieb, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblBearbAm, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfBearbAm, GBCFactory.createGBC(100, 0, 2, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblInstallTS, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(tfInstallTS, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(lblStatus, GBCFactory.createGBC(0, 0, 0, 7, 3, 1, GridBagConstraints.HORIZONTAL));

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
        mid.add(lblKunde, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlVerlaufAbt = new VerlaufAbtTableModel();
        tbMdlVerlaufAbt.setAbteilungen(abteilungen);
        tbMdlVerlaufAbt.setNiederlassungen(niederlassungen);
        VerlaufAbtTable tbAbtVerl = new VerlaufAbtTable(tbMdlVerlaufAbt,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbAbtVerl.fitTable(new int[] { 60, 55, 70, 70, 70, 95, 70 });
        TableColumnModel tbCM = tbAbtVerl.getColumnModel();
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AUSGETRAGEN).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DATE_TIME));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AB).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_REAL).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbCM.getColumn(VerlaufAbtTableModel.COL_DATUM_AN).setCellRenderer(new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        AKJScrollPane spTable = new AKJScrollPane(tbAbtVerl);
        spTable.setPreferredSize(new Dimension(390, 150));

        AKJPanel tbPnl = new AKJPanel(new GridBagLayout());
        tbPnl.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.bauauftraege")));
        tbPnl.add(spTable, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));

        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(left, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        child.add(mid, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        child.add(tbPnl, GBCFactory.createGBC(0, 0, 4, 0, 1, 2, GridBagConstraints.BOTH));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 5, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnAbschliessen, btnAbschlManuell, btnStorno, btnBemerkungen, btnShowPorts });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    /**
     * Gibt abhaengig von der Abteilung einen anderen Klassennamen zurueck, der als Parent-Name fuer GUI-Komponenten
     * verwendet werden kann.
     */
    @Override
    public String getClassName() {
        if (Abteilung.NP.equals(abteilungId)) {
            StringBuilder name = new StringBuilder(this.getClass().getName());
            name.append(".NP");
            return name.toString();
        }
        return this.getClass().getName();
    }

    /**
     * Laedt die Standard-Daten fuer das Panel.
     */
    private void loadDefaultData() {
        try {
            NiederlassungService nls = getCCService(NiederlassungService.class);
            abteilungen = nls.findAbteilungen4Ba();
            niederlassungen = nls.findNiederlassungen();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public List<VerlaufDispoRLView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {

        BAService bas = getCCService(BAService.class);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufDispoRLView> dispoRLViews = (List) bas.findBAVerlaufViews4Abt(
                false, abteilungId, true, realisierungFrom, realisierungTo);

        Map<Long, String> projektleiterById = getProjektleiterById();
        for (VerlaufDispoRLView view : dispoRLViews) {
            if (view.getProjectResponsibleId() != null) {
                Long projectResponsibleId = view.getProjectResponsibleId();
                if (projektleiterById.containsKey(projectResponsibleId)) {
                    view.setProjectResponsible(projektleiterById.get(projectResponsibleId));
                }
            }
        }
        return dispoRLViews;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufDispoRLView> tableData) {
        ObserverHelper.addObserver2Objects(this, tableData);
        tableModel.setData(tableData);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (BTN_ABSCHLIESSEN.equals(command)) {
            if (getTable().getSelectedRows().length <= 1) {
                verlaufAbschliessen(getSelectedView());
            }
            else {
                verlaeufeAbschliessen();
            }
        }
        else if ("abschluss.manuell".equals(command)) {
            verlaufAbschliessenManuell(getSelectedView());
        }
        else if ("storno".equals(command)) {
            verlaufStorno(getSelectedView());
        }
        else if (BTN_BEMERKUNGEN.equals(command)) {
            showBemerkungen((getSelectedView() != null) ? getSelectedView().getVerlaufId() : null);
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getSelectedView());
        }
    }

    /**
     * Ueberprueft, ob der aktuelle Bauauftrag von allen (techn.) Abteilungen abgeschlossen ist
     */
    private boolean isBAFinished(VerlaufDispoRLView verlaufView) throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);
        List<VerlaufAbteilung> verlAbts = bas.findVerlaufAbteilungen(verlaufView.getVerlaufId());
        List<Long> abtIdsNotFinished = new ArrayList<>();
        for (VerlaufAbteilung va : verlAbts) {
            if (!NumberTools.isIn(va.getAbteilungId(),
                    new Number[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP }) &&
                    (va.getDatumErledigt() == null)) {
                abtIdsNotFinished.add(va.getAbteilungId());
            }
        }

        if (!abtIdsNotFinished.isEmpty()) {
            MessageHelper.showInfoDialog(this, getSwingFactory().getText("cannot.finish.msg"),
                    getSwingFactory().getText("cannot.finish.title"), null, true);
            return false;
        }

        return true;
    }

    /**
     * Schliesst den aktuellen Verlauf ab.
     */
    private void verlaufAbschliessen(VerlaufDispoRLView verlaufView) {
        if (!canFinish(verlaufView)) {
            return;
        }
        try {
            setWaitCursor();
            if (!isBAFinished(verlaufView)) {
                return;
            }

            Date inbetriebnahme = (verlaufView.getInbetriebnahme() != null)
                    ? verlaufView.getInbetriebnahme() : getTechDatumErledigt();
            if (inbetriebnahme == null) {
                inbetriebnahme = ask4Inbetriebnahme(verlaufView.getRealisierungstermin());
                if (inbetriebnahme == null) {
                    MessageHelper.showInfoDialog(this,
                            getSwingFactory().getText("ba.not.finished"), null, true);
                    return;
                }
            }

            BAService bas = getCCService(BAService.class);
            bas.dispoVerlaufAbschluss(verlaufView.getVerlaufId(), verlaufView.getVerlaufAbtId(), inbetriebnahme,
                    HurricanSystemRegistry.instance().getSessionId(), ask4RLAM(verlaufView));
            setAbgeschlossen(verlaufView);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Schliesst alle markierten Verläufe ab
     */
    private void verlaeufeAbschliessen() {
        try {
            setWaitCursor();

            List<VerlaufDispoRLView> toClose = new ArrayList<>();
            for (int row : getTable().getSelectedRows()) {
                toClose.add((VerlaufDispoRLView) ((AKMutableTableModel<?>) getTable().getModel()).getDataAtRow(row));
            }
            if (CollectionTools.isEmpty(toClose)) {
                throw new HurricanGUIException("Es wurde kein Verlauf angegeben. Verlauf kann nicht erledigt werden!");
            }

            Date definedRealDate = null;
            for (VerlaufDispoRLView verlauf : toClose) {
                if (definedRealDate == null) {
                    definedRealDate = verlauf.getRealisierungstermin();
                }
                else if (!DateTools.isDateEqual(definedRealDate, verlauf.getRealisierungstermin())) {
                    throw new HurricanGUIException("Es koennen nur Verlaeufe mit gleichem vorgegebenen " +
                            "Realisierungsdatum zusammen abgeschlossen werden!");
                }
            }

            checkAutoAbschliessen4Verlaeufe(toClose);

            for (VerlaufDispoRLView verlaufView : toClose) {
                if (verlaufView.getInbetriebnahme() == null) {
                    verlaufView.setInbetriebnahme(verlaufView.getRealisierungstermin());
                }
                verlaufAbschliessen(verlaufView);
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
     * Prüft, ob das automatische Abschließen der übergebenen Bauauträge nicht erlaubt ist, ansonsten wird eine
     * Exception geworfen
     *
     * @throws HurricanGUIException falls das automatische Abschließen der übergebenen Bauauträge nicht erlaubt ist
     */
    private void checkAutoAbschliessen4Verlaeufe(List<VerlaufDispoRLView> toClose)
            throws ServiceNotFoundException, FindException, HurricanGUIException {

        BAService baService = getCCService(BAService.class);
        for (VerlaufDispoRLView verlaufView : toClose) {
            List<VerlaufAbteilung> vas = baService.findVerlaufAbteilungen(verlaufView.getVerlaufId());
            for (VerlaufAbteilung verlaufAbteilung : vas) {
                if (!NumberTools.isIn(verlaufAbteilung.getAbteilungId(),
                        new Number[] { Abteilung.AM, Abteilung.DISPO, Abteilung.NP })) {
                    if (verlaufAbteilung.getDatumErledigt() == null) {
                        throw new HurricanGUIException(String.format("Baufauftrag mit der Verb.Bez %s kann nicht abgeschlossen werden"
                                + ", da Abteilung %s noch nicht erledigt hat"
                                , verlaufView.getVbz()
                                , getCCService(NiederlassungService.class).findAbteilung(verlaufAbteilung.getAbteilungId()).getName()));
                    }
                    if (!DateTools.isDateEqual(verlaufAbteilung.getDatumErledigt(), verlaufAbteilung.getRealisierungsdatum())) {
                        throw new HurricanGUIException(String.format("Baufauftrag mit der Verb.Bez %s kann nicht abgeschlossen werden"
                                + ", da für in Abteilung %s \"Datum erledigt\" und \"Realisierungsdatum\" abweichen"
                                , verlaufView.getVbz()
                                , getCCService(NiederlassungService.class).findAbteilung(verlaufAbteilung.getAbteilungId()).getName()));
                    }
                }
            }
        }
    }

    /**
     * Schliesst den aktuellen Verlauf ab. Das Inbetriebnahmedatum wird in dieser Methode immer dann abgefragt, wenn es
     * sich nicht um einen Kuendigungs-Bauauftrag handelt.!
     */
    private void verlaufAbschliessenManuell(VerlaufDispoRLView verlaufView) {
        if (!canFinish(verlaufView)) { return; }
        try {
            setWaitCursor();
            if (!isBAFinished(verlaufView)) {
                return;
            }

            Date inbetriebnahme = null;
            if (NumberTools.notEqual(verlaufView.getAnlassId(), BAVerlaufAnlass.KUENDIGUNG)) {
                inbetriebnahme = ask4Inbetriebnahme(verlaufView.getRealisierungstermin());
                if (inbetriebnahme == null) {
                    MessageHelper.showInfoDialog(this,
                            getSwingFactory().getText("ba.not.finished"), null, true);
                    return;
                }
            }

            BAService bas = getCCService(BAService.class);
            bas.dispoVerlaufAbschluss(verlaufView.getVerlaufId(), verlaufView.getVerlaufAbtId(), inbetriebnahme,
                    HurricanSystemRegistry.instance().getSessionId(), ask4RLAM(verlaufView));
            setAbgeschlossen(verlaufView);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * 'Fragt', ob der Bauauftrag zurueck an AM gehen soll. <br> Die Frage wird nur dann gestellt, wenn es sich bei der
     * aktuellen Abteilung um 'NP' handelt.
     *
     * @return true wenn der Bauauftrag an die Ruecklaeufer AM gehen soll.
     */
    private Boolean ask4RLAM(VerlaufDispoRLView verlaufView) {
        if (NumberTools.equal(abteilungId, Abteilung.NP)) {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    StringTools.formatString(getSwingFactory().getText("ask.rl.am.msg"),
                            new Object[] { String.format("%s", verlaufView.getAuftragId()), verlaufView.getVbz() }),
                    getSwingFactory().getText("ask.rl.am.title")
            );
            return (option == JOptionPane.YES_OPTION);
        }
        return null;
    }

    /**
     * Fragt den User nach dem Inbetriebnahmedatum.
     */
    private Date ask4Inbetriebnahme(Date defaultDate) {
        AKDateSelectionDialog dlg = new AKDateSelectionDialog(
                getSwingFactory().getText("ask.inbetriebnahme.title"),
                getSwingFactory().getText("ask.inbetriebnahme.msg"),
                getSwingFactory().getText("ask.inbetriebnahme.label"));
        dlg.showDate(defaultDate);
        Object result = DialogHelper.showDialog(this, dlg, true, true);
        return ((result instanceof Date) ? (Date) result : null);
    }

    /**
     * Setzt den aktuellen BA auf 'abgeschlossen'.
     */
    private void setAbgeschlossen(VerlaufDispoRLView verlaufView) {
        verlaufView.setAbgeschlossen(true);
        verlaufView.setGuiFinished(true);
        verlaufView.notifyObservers(true);
    }

    /**
     * Ueberprueft, ob ein BA selektiert ist, der abgeschlossen werden kann.
     */
    private boolean canFinish(VerlaufDispoRLView verlaufView) {
        if (verlaufView == null) {
            MessageHelper.showInfoDialog(this, NOTHING_SELECTED, null, true);
            return false;
        }
        else if (verlaufView.isAbgeschlossen() || verlaufView.isStorniert()) {
            MessageHelper.showInfoDialog(this, "Bauauftrag wurde bereits abgeschlossen oder storniert!", null, true);
            return false;
        }
        return true;
    }

    /**
     * Ueberprueft, ob alle techn. Abteilungen das gleiche Erledigt-Datum eingetragen haben. Ist dies der Fall, wird das
     * Datum zurueck gegeben - sonst <code>null</code>.
     */
    private Date getTechDatumErledigt() {
        if (tbMdlVerlaufAbt.getRowCount() == 1) {
            return (tbMdlVerlaufAbt.getDataAtRow(0)).getDatumErledigt();
        }
        try {
            VerlaufAbteilung tmp = tbMdlVerlaufAbt.getDataAtRow(0);
            if (tmp == null) {
                return null;
            }

            Date dateRef = tmp.getDatumErledigt();
            if (dateRef != null) {
                for (int i = 1; i < tbMdlVerlaufAbt.getRowCount(); i++) {
                    VerlaufAbteilung va = tbMdlVerlaufAbt.getDataAtRow(i);
                    Date toCheck = va.getDatumErledigt();
                    if (!dateRef.equals(toCheck)) {
                        return null;
                    }
                }
                return dateRef;
            }
        }
        catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Storniert den aktuellen Verlauf.
     */
    private void verlaufStorno(VerlaufDispoRLView verlaufView) {
        if (verlaufView == null) {
            MessageHelper.showInfoDialog(this, NOTHING_SELECTED, null, true);
            return;
        }
        else if (verlaufView.isAbgeschlossen() || verlaufView.isStorniert()) {
            MessageHelper.showInfoDialog(this,
                    "Bauauftrag wurde bereits storniert oder abgeschlossen und kann deshalb nicht mehr storniert werden!",
                    null, true);
            return;
        }

        int option = MessageHelper.showYesNoQuestion(this,
                "Soll der Bauauftrag wirklich storniert werden?\n" +
                        "Wenn ja, dies bitte auch mit den Abteilungen absprechen.", "Bauauftrag-Storno"
        );
        if (option == JOptionPane.YES_OPTION) {
            try {
                BAService bas = getCCService(BAService.class);
                AKWarnings warnings = bas.dispoVerlaufStorno(verlaufView.getVerlaufId(), HurricanSystemRegistry.instance().getSessionId());
                if ((warnings != null) && warnings.isNotEmpty()) {
                    MessageHelper.showInfoDialog(getMainFrame(), warnings.getWarningsAsText(), null, true);
                }
                verlaufView.setStorniert(true);
                verlaufView.notifyObservers(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
    }

    @Override
    public DispoRlDetailsWorkerResult loadDetails(VerlaufDispoRLView selectedView) throws ServiceNotFoundException, FindException {
        DispoRlDetailsWorkerResult result = new DispoRlDetailsWorkerResult();

        BAService bas = getCCService(BAService.class);
        result.verlaufAbts = bas.findVerlaufAbteilungen(selectedView.getVerlaufId());

        // Verlauf von AM suchen
        result.vaAm = (VerlaufAbteilung) CollectionUtils.find(result.verlaufAbts, new VerlaufAbteilungPredicate(Abteilung.AM));

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

        // TimeSlot ermitteln
        CCAuftragService auftragService = getCCService(CCAuftragService.class);
        result.timeSlot = getTimeSlot(auftragService.findAuftragDatenByAuftragId(selectedView.getAuftragId()), selectedView);

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufDispoRLView selectedView, DispoRlDetailsWorkerResult result) {
        setSelectedView(selectedView);
        vaAm = result.vaAm;
        vpn = result.vpn;
        schnittstelleEsB = result.schnittstelleEsB;

        // alle nicht-technischen Abteilungen aus der Liste filtern.
        CollectionUtils.filter(result.verlaufAbts, new VerlaufAbteilungTechPredicate());
        tbMdlVerlaufAbt.setData(result.verlaufAbts);

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
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
        tfBearbAm.setText((vaAm != null) ? vaAm.getBearbeiter() : null);

        // Installationstermin ermitteln
        tfInstallTS.setText(result.timeSlot);

        if (selectedView.isAbgeschlossen()) {
            lblStatus.setText(baAbgeschlossen);
        }
        else if (selectedView.isStorniert()) {
            lblStatus.setText(baStorniert);
        }
        else {
            lblStatus.setText(EMPTY_STRING);
        }
        lblStatus.repaint();
        showAuftragTechLeistungen(selectedView.getVerlaufId(), result.auftrag2TechLeistungen);
        showBemerkungenInPanel(selectedView.getVerlaufId());
    }

    @Override
    public void clearDetails() {
        vpn = null;
        schnittstelleEsB = null;
        vaAm = null;
        if (guiCreated) {
            tbMdlVerlaufAbt.setData(null);
            lblStatus.setText(EMPTY_STRING);
        }
        super.clearDetails();
    }
}

class DispoRlDetailsWorkerResult {
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
    Schnittstelle schnittstelleEsB = null;
    VPN vpn = null;
    VerlaufAbteilung vaAm = null;
    List<VerlaufAbteilung> verlaufAbts = null;
    String timeSlot = null;
}
