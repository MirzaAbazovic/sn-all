/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.01.2005 12:03:17
 */
package de.augustakom.hurrican.gui.verlauf;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.model.AKLoginContext;
import de.augustakom.common.gui.awt.GBCFactory;
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
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.common.gui.utils.ObserverHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.Abteilung;
import de.augustakom.hurrican.model.cc.Auftrag2TechLeistung;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.EndstelleLtgDaten;
import de.augustakom.hurrican.model.cc.ExtServiceProvider;
import de.augustakom.hurrican.model.cc.Niederlassung;
import de.augustakom.hurrican.model.cc.Schnittstelle;
import de.augustakom.hurrican.model.cc.VPN;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.temp.SelectAbteilung4BAModel;
import de.augustakom.hurrican.model.cc.view.VerlaufUniversalView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCLeistungsService;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ExtServiceProviderService;
import de.augustakom.hurrican.service.cc.NiederlassungService;
import de.augustakom.hurrican.service.cc.ProduktService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.VPNService;
import de.augustakom.hurrican.tools.predicate.VerlaufAbteilungTechPredicate;

/**
 * Panel fuer die Anzeige/Bearbeitung der Bauauftraege von DISPO bzw. Netzplanung.
 *
 *
 */
public class BauauftragDISPOPanel
        extends
        AbstractBauauftragPanel<VerlaufUniversalView, DispoDetailsWorkerResult, AKReflectionTableModel<VerlaufUniversalView>> {
    private static final Logger LOGGER = Logger.getLogger(BauauftragDISPOPanel.class);
    private static final long serialVersionUID = -6215196293570961387L;

    private static final String COL_NIEDERLASSUNG = "Niederlassung";

    // GUI-Komponenten
    private AbtTableModel tbMdlVerlaufAbt = null;
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
    private AKJDateComponent dcBaAnDispo = null;
    private AKJTextField tfBearbAm = null;
    private AKJTextField tfPortierung = null;
    private AKJTextArea taBemerkung = null;
    private AKJTextField tfPhysikInfo = null;
    private AKReferenceField rfInstall = null;
    private AKJTextField tfInstallTS = null;

    private AKJButton btnManuell = null;
    private AKJButton btnAuto = null;
    private AKJButton btnRueckruf = null;
    private AKJButton btnPrint = null;
    private AKJButton btnPrintCompact = null;
    private AKJButton btnBemerkungen = null;
    private AKJButton btnRufnummern = null;
    private AKJButton btnMoveToRL = null;
    private AKJButton btnShowPorts = null;

    private VPN vpn = null;
    private AuftragDaten auftragDaten = null;
    private Schnittstelle schnittstelleEsB = null;
    private Carrierbestellung carrierbestellung = null;
    private List<VerlaufAbteilung> vaVerteilt = null;

    private boolean guiCreated = false;

    /**
     * Konstruktor mit Angabe der Abteilungs-ID (Dispo od. NP) fuer die die zu verteilenden Bauauftraege angezeigt
     * werden sollen.
     */
    public BauauftragDISPOPanel(Long abteilungId) {
        super("de/augustakom/hurrican/gui/verlauf/resources/BauauftragDISPOPanel.xml", abteilungId, true,
                COL_NIEDERLASSUNG);
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
        String abteilung = (Abteilung.DISPO.equals(abteilungId) ? "DISPO" : "NP");
        tableModel = new AKReflectionTableModel<>(
                new String[] { VerbindungsBezeichnung.VBZ_BEZEICHNUNG, "Taifun A-Nr", "Anlass", "Kreuzung", // 0-4
                        "Produkt (Billing)", "Produkt (Hurrican)", "Real.-Datum", "TimeSlot", "BA-Hinweise", "Kunde", // 5-10
                        "Ort ES-B", "Strasse", "Anschlussart", "verschoben", COL_NIEDERLASSUNG, // 11-15
                        "Projektverantwortlicher", "BA Status", "Status " + abteilung, "Wiedervorlage", "Cluster-ID", // 16-20
                        "DPO-Chassis", "Gerätebez." }, // 21-22
                new String[] { "vbz", "auftragNoOrig", "anlass", "kreuzung", "oeName", "produktName",
                        "gesamtrealisierungstermin", "timeSlot.timeSlotToUseAsString", "baHinweise", "kundenName", "endstelleOrtB", "endstelleB",
                        "hvtAnschlussart", "verschoben", "niederlassung",
                        "projectResponsibleName", "verlaufStatus", "verlaufAbteilungStatus", "wiedervorlage",
                        "hvtClusterId", "dpoChassis", "geraeteBez" },
                new Class[] { String.class, Long.class, String.class, Boolean.class, String.class, String.class,
                        Date.class, String.class, String.class, String.class, String.class, String.class, String.class,
                        Boolean.class, String.class,
                        String.class, String.class, String.class, Date.class, String.class, String.class, String.class }
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
        getTable().fitTable(
                new int[] { 125, 70, 115, 40, 130, 130, 70, 110, 100, 100, 100, 120, 25, 70, 120, 70, 80, 80, 50, 100, 100 });

        createDetailPanel();
        manageGUI(btnRufnummern, btnBemerkungen, btnShowPorts, btnMoveToRL, btnPrint,
                btnPrintCompact, changeDateAction);
        manageGUI(getClassName(), btnManuell, btnAuto, btnMoveToRL, btnRueckruf);

        guiCreated = true;
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
        AKJLabel lblBaAnDispo = getSwingFactory().createLabel("ba.an.dispo");
        AKJLabel lblBearbAm = getSwingFactory().createLabel("bearbeiter.am");
        AKJLabel lblPortierung = getSwingFactory().createLabel("portierungsart");
        AKJLabel lblInstall = getSwingFactory().createLabel("installation");
        AKJLabel lblPhysikInfo = getSwingFactory().createLabel("physik.info");
        AKJLabel lblBemerkung = getSwingFactory().createLabel("bemerkung");
        AKJLabel lblAbteilung = getSwingFactory().createLabel("abteilungen");
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
        dcBaAnDispo = getSwingFactory().createDateComponent("ba.an.dispo", false);
        tfBearbAm = getSwingFactory().createTextField("bearbeiter.am", false);
        tfPortierung = getSwingFactory().createTextField("portierungsart", false);
        rfInstall = getSwingFactory().createReferenceField("installation");
        rfInstall.setEnabled(false);
        tfPhysikInfo = getSwingFactory().createTextField("physik.info", false);
        taBemerkung = getSwingFactory().createTextArea("bemerkung", false);
        taBemerkung.setLineWrap(true);
        tfInstallTS = getSwingFactory().createTextField("install.time.slot", false);

        String className = getClassName();
        btnManuell = getSwingFactory().createButton("manuell", getActionListener());
        btnManuell.setParentClassName(className);
        btnAuto = getSwingFactory().createButton("auto", getActionListener());
        btnAuto.setParentClassName(className);
        btnRueckruf = getSwingFactory().createButton("rueckruf", getActionListener());
        btnRueckruf.setParentClassName(className);
        btnPrint = getSwingFactory().createButton("print", getActionListener());
        btnPrintCompact = getSwingFactory().createButton(BTN_PRINT_COMPACT, getActionListener());
        btnBemerkungen = getSwingFactory().createButton(BTN_BEMERKUNGEN, getActionListener());
        btnRufnummern = getSwingFactory().createButton("rufnummern", getActionListener());
        btnMoveToRL = getSwingFactory().createButton("move.to.rl", getActionListener());
        btnMoveToRL.setParentClassName(className);
        btnShowPorts = getSwingFactory().createButton(BTN_SHOW_PORTS, getActionListener());

        tbMdlVerlaufAbt = new AbtTableModel();
        AKJTable tbAbtVerl = new AKJTable(tbMdlVerlaufAbt,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbAbtVerl.fitTable(new int[] { 65, 65, 70 });
        AKJScrollPane spTable = new AKJScrollPane(tbAbtVerl);
        spTable.setPreferredSize(new Dimension(135, 150));

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
        left.add(lblBereitst, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        left.add(dcBereitst, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel mid = new AKJPanel(new GridBagLayout());
        mid.add(lblVbz, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        mid.add(tfVbz, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblAuftragId, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfAuftragId, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblVpnNr, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfVpnNr, GBCFactory.createGBC(100, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblCarrierLbz, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfCarrierLbz, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(lblCarrierVtrNr, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        mid.add(tfCarrierVtrNr, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right = new AKJPanel(new GridBagLayout());
        right.add(lblKundeName, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        right.add(tfKundeName, GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblSchnittst, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfSchnittstelle, GBCFactory.createGBC(100, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        right.add(lblBaAnDispo, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(dcBaAnDispo, GBCFactory.createGBC(100, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblBearbAm, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfBearbAm, GBCFactory.createGBC(100, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        right.add(lblHvtAnsArt, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfHvtAnsArt, GBCFactory.createGBC(100, 0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblMontageAkom, GBCFactory.createGBC(0, 0, 0, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(chbMontageAkom, GBCFactory.createGBC(100, 0, 2, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblPortierung, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfPortierung, GBCFactory.createGBC(100, 0, 2, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInstall, GBCFactory.createGBC(0, 0, 0, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(rfInstall, GBCFactory.createGBC(100, 0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(lblInstallTS, GBCFactory.createGBC(0, 0, 0, 10, 1, 1, GridBagConstraints.HORIZONTAL));
        right.add(tfInstallTS, GBCFactory.createGBC(100, 0, 2, 10, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel right2 = new AKJPanel(new GridBagLayout());
        right2.add(lblAbteilung, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        right2.add(spTable, GBCFactory.createGBC(100, 0, 0, 2, 1, 2, GridBagConstraints.BOTH));
        right2.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 3, 1, 1, GridBagConstraints.VERTICAL));

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
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE));
        child.add(right2, GBCFactory.createGBC(0, 0, 6, 0, 1, 3, GridBagConstraints.VERTICAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 7, 2, 1, 1, GridBagConstraints.BOTH));

        AKJScrollPane spChild = new AKJScrollPane(child);
        spChild.setBorder(null);

        addButtons(new AKJButton[] { btnAuto, btnManuell, btnShowPorts,
                btnMoveToRL, btnRueckruf, btnPrint, btnPrintCompact, btnBemerkungen, btnRufnummern });
        getDetailPanel().setLayout(new BorderLayout());
        getDetailPanel().add(spChild, BorderLayout.CENTER);
    }

    /*
     * Gibt abhaengig von der Abteilung einen anderen Klassennamen zurueck, der als Parent-Name fuer GUI-Komponenten
     * verwendet werden kann.
     */
    @Override
    public String getClassName() {
        if (Abteilung.NP.equals(abteilungId)) {
            return this.getClass().getName() + ".NP";
        }
        return this.getClass().getName();
    }

    @Override
    public List<VerlaufUniversalView> loadTableData(Long abteilungId, Date realisierungFrom, Date realisierungTo)
            throws ServiceNotFoundException, FindException {
        BAService bas = getCCService(BAService.class);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<VerlaufUniversalView> dispoViews = (List) bas.findBAVerlaufViews4Abt(
                false, abteilungId, false, realisierungFrom, realisierungTo);

        Map<Long, String> projektleiterById = getProjektleiterById();
        for (VerlaufUniversalView view : dispoViews) {
            if (view.getProjectResponsibleId() != null) {
                Long projectResponsibleId = view.getProjectResponsibleId();
                if (projektleiterById.containsKey(projectResponsibleId)) {
                    view.setProjectResponsibleName(projektleiterById.get(projectResponsibleId));
                }
            }
        }

        return dispoViews;
    }

    @Override
    public void updateGuiByTableData(List<VerlaufUniversalView> tableData) {
        ObserverHelper.addObserver2Objects(this, tableData);
        tableModel.setData(tableData);
        ObserverHelper.addObserver2Objects((VerlaufTable) getTable(), tableData);

        try {
            rfInstall.setFindService(getCCService(QueryCCService.class));

            NiederlassungService ns = getCCService(NiederlassungService.class);
            ExtServiceProviderService es = getCCService(ExtServiceProviderService.class);
            tbMdlVerlaufAbt.setAbteilungen(ns.findAbteilungen());
            tbMdlVerlaufAbt.setNiederlassungen(ns.findNiederlassungen());
            tbMdlVerlaufAbt.setExternePartner(es.findAllServiceProvider());
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
        if ("manuell".equals(command)) {
            if (isVerlaufAtZentraleDispo(getSelectedView().getVerlaufId())) {
                anNetzplanungenVerteilen(getSelectedView());
            }
            else {
                baVerteilenManuell();
            }
        }
        else if ("auto".equals(command)) {
            baVerteilenAuto();
        }
        else if ("rueckruf".equals(command)) {
            verlaufRueckruf();
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
        else if ("move.to.rl".equals(command)) {
            moveToRL();
        }
        else if (BTN_SHOW_PORTS.equals(command)) {
            showPorts(getSelectedView());
        }
    }

    /* Manuelle Verteilung der Bauauftraege. */
    private void baVerteilenManuell() {
        if (verteilenErlaubt()) {
            try {
                setWaitCursor();

                // Warnung, falls Auftrag einem VPN zugeordnet ist
                if ((vpn != null) && (vpn.getVpnNr() != null) && (vpn.getVpnNr().intValue() > 0)) {
                    MessageHelper.showMessageDialog(getMainFrame(), "Es handelt sich um einen VPN-Auftrag!",
                            "ACHTUNG", JOptionPane.WARNING_MESSAGE);
                }

                VerlaufAbtAuswahlDialog dlg = new VerlaufAbtAuswahlDialog(true, getSelectedView().getVerlaufId());
                dlg.selectAbteilungen(getSelectedView());
                Object value = DialogHelper.showDialog(this, dlg, true, true);
                if (value instanceof List<?>) {
                    if (!((List<?>) value).isEmpty()) {
                        BAService bas = getCCService(BAService.class);

                        @SuppressWarnings("unchecked")
                        List<SelectAbteilung4BAModel> abtIds = (List<SelectAbteilung4BAModel>) value;
                        String installationsauftrag = null;
                        if (emailToExtServiceProviderWillBeSend(abtIds)) {
                            installationsauftrag = generateInstallationsauftrag(auftragDaten.getAuftragId());
                        }

                        Collection<Long> missingAbtIds = bas.getMissingAbteilungIds(
                                getSelectedView().getVerlaufId(), abtIds);
                        if (CollectionUtils.isNotEmpty(missingAbtIds) && !askUser4BauauftragDistribution(missingAbtIds)) {
                            return;
                        }

                        final long sessionId = HurricanSystemRegistry.instance().getSessionId();

                        final List<Pair<byte[], String>> attachmentWithFilename =
                                this.getAttachmentsWithFilename(installationsauftrag, getSelectedView().getVerlaufId(), sessionId);

                        vaVerteilt = bas.dispoVerteilenManuell(getSelectedView().getVerlaufId(),
                                getSelectedView().getVerlaufAbtId(),
                                abtIds, attachmentWithFilename, sessionId);

                        if ((vaVerteilt == null) || (vaVerteilt.isEmpty())) {
                            throw new HurricanGUIException("Der Bauauftrag wurde nicht verteilt!");
                        }
                        finishVerteilen(getSelectedView());
                    }
                    else {
                        MessageHelper.showInfoDialog(this,
                                "Der Bauauftrag wurde nicht verschickt, da keine Abteilung ausgewählt wurde.",
                                null, true);
                    }
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

    /* Automatische Verteilung der Bauauftraege. */
    private void baVerteilenAuto() {
        if (getTable().getSelectedRowCount() > 1) {
            baVerteilenAutoMultiselect();
            return;
        }
        if (verteilenErlaubt()) {
            try {
                setWaitCursor();

                if ((vpn != null) && (vpn.getVpnNr() != null) && (vpn.getVpnNr().intValue() > 0)) {
                    String title = "ACHTUNG !!!";
                    StringBuilder msg = new StringBuilder("Der Auftrag ist einem VPN zugeordnet.");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append("Sollte die Verteilung vom Standard abweichen");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append("Auftrag bitte manuell verteilen!");
                    msg.append(SystemUtils.LINE_SEPARATOR);
                    msg.append("Mit automatischer Verteilung fortfahren?");
                    int selection = MessageHelper.showConfirmDialog(this, msg.toString(), title,
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (selection == JOptionPane.NO_OPTION) {
                        baVerteilenManuell();
                        return;
                    }
                }

                BAService bas = getCCService(BAService.class);
                vaVerteilt = bas.dispoBAVerteilenAuto(getSelectedView().getVerlaufId(),
                        HurricanSystemRegistry.instance().getSessionId());

                if ((vaVerteilt == null) || (vaVerteilt.isEmpty())) {
                    throw new HurricanGUIException("Bauaufträge wurden aus unbekanntem Grund nicht verteilt!");
                }
                finishVerteilen(getSelectedView());
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

    private void baVerteilenAutoMultiselect() {
        setWaitCursor();
        try {
            VPNService vpns = getCCService(VPNService.class);
            int[] rows = getTable().getSelectedRows();
            for (int currRow : rows) {
                @SuppressWarnings("unchecked")
                VerlaufUniversalView data = ((AKMutableTableModel<VerlaufUniversalView>) getTable().getModel())
                        .getDataAtRow(currRow);
                if (!checkVPNMultiselect(vpns, data)) {
                    return;
                }
            }

            BAService bas = getCCService(BAService.class);
            for (int currRow : rows) {
                @SuppressWarnings("unchecked")
                VerlaufUniversalView data = ((AKMutableTableModel<VerlaufUniversalView>) getTable().getModel())
                        .getDataAtRow(currRow);
                List<VerlaufAbteilung> verlaufAbteilung = bas.dispoBAVerteilenAuto(data.getVerlaufId(),
                        HurricanSystemRegistry.instance().getSessionId());

                if ((verlaufAbteilung == null) || (verlaufAbteilung.isEmpty())) {
                    throw new HurricanGUIException("Bauaufträge wurden aus unbekanntem Grund nicht verteilt!");
                }
                finishVerteilen(data);

            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
        }
    }

    private boolean checkVPNMultiselect(VPNService vpns, VerlaufUniversalView dispoView)
            throws FindException {
        // prüfen, ob VPN dabei ist
        VPN currVpn = vpns.findVPNByAuftragId(dispoView.getAuftragId());
        if ((currVpn != null) && (currVpn.getVpnNr() != null)
                && (currVpn.getVpnNr() > 0L)) {
            String title = "ACHTUNG !!!";
            StringBuilder msg = new StringBuilder(
                    "Der Auftrag " + dispoView.getAuftragId() + " ist einem VPN zugeordnet.");
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append("Sollte die Verteilung vom Standard abweichen");
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append("Auftrag bitte manuell verteilen!");
            msg.append(SystemUtils.LINE_SEPARATOR);
            msg.append("Mit automatischer Verteilung fortfahren?");
            int selection = MessageHelper.showConfirmDialog(this, msg.toString(),
                    title, JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (selection == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        return true;
    }

    /* Fuehrt abschliessende Arbeiten nach dem Verteilen eines BAs durch. */
    private void finishVerteilen(VerlaufUniversalView verlauf) {
        verlauf.setVerlaufAbtStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
        verlauf.setGuiFinished(true);
        verlauf.notifyObservers(true);
    }

    /* Versucht, den aktuellen Verlauf zurueck zu rufen. */
    private void verlaufRueckruf() {
        if (getSelectedView() == null) {
            MessageHelper.showInfoDialog(this, "Bitte wählen Sie zuerst einen Bauauftrag aus.", null, true);
            return;
        }

        try {
            setWaitCursor();
            BAService bas = getCCService(BAService.class);
            bas.dispoVerlaufRueckruf(getSelectedView().getVerlaufId(), HurricanSystemRegistry.instance().getSessionId());
            getSelectedView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_IM_UMLAUF);
            getSelectedView().setGuiFinished(false);
            getSelectedView().notifyObservers(true);

            MessageHelper.showMessageDialog(this, "Verlauf wurde erfolgreich zurück gerufen.",
                    "Rückruf", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Verschiebt den aktuellen Bauauftrag in die Ruecklaeufer. */
    private void moveToRL() {
        if (verteilenErlaubt()) {
            int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                    getSwingFactory().getText("move.to.rl.msg"), getSwingFactory().getText("move.to.rl.title"));
            if (option == JOptionPane.YES_OPTION) {
                try {
                    AKLoginContext logCtx = (AKLoginContext) HurricanSystemRegistry.instance()
                            .getValue(HurricanSystemRegistry.REGKEY_LOGIN_CONTEXT);
                    if (logCtx != null) {
                        BAService bas = getCCService(BAService.class);
                        Verlauf verlauf = bas.findVerlauf(getSelectedView().getVerlaufId());
                        verlauf.setVerlaufStatusId(VerlaufStatus.RUECKLAEUFER_DISPO);

                        VerlaufAbteilung va = bas.findVerlaufAbteilung(getSelectedView().getVerlaufAbtId());
                        va.setVerlaufStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                        va.setBearbeiter(logCtx.getUser().getLoginName());

                        bas.saveVerlaufAbteilung(va);
                        bas.saveVerlauf(verlauf);

                        getSelectedView().setVerlaufAbtStatusId(VerlaufStatus.STATUS_IN_BEARBEITUNG);
                        getSelectedView().notifyObservers(true);
                    }
                    else {
                        throw new HurricanGUIException("Aktueller Benutzer konnte nicht ermittelt werden!");
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(this, e);
                }
            }
        }
    }

    /* Ueberprueft, ob die Bauauftraege verteilt werden duerfen. */
    private boolean verteilenErlaubt() {
        if (getSelectedView() == null) {
            MessageHelper.showInfoDialog(this, "Bitte wählen Sie zuerst einen Bauauftrag aus.", null, true);
            return false;
        }

        if (NumberTools.equal(getSelectedView().getVerlaufAbtStatusId(), VerlaufStatus.STATUS_IN_BEARBEITUNG)) {
            MessageHelper.showInfoDialog(this, "Bauauftrag wurde bereits verteilt!", null, true);
            return false;
        }

        return true;
    }

    @Override
    public DispoDetailsWorkerResult loadDetails(VerlaufUniversalView selectedView) throws ServiceNotFoundException,
            FindException {
        DispoDetailsWorkerResult result = new DispoDetailsWorkerResult();

        // AuftragDaten ermitteln
        CCAuftragService as = getCCService(CCAuftragService.class);
        result.auftragDaten = as.findAuftragDatenByAuftragId(selectedView.getAuftragId());

        // Abteilungen ermitteln, an die der BA verteilt wurde
        BAService bas = getCCService(BAService.class);
        result.vaVerteilt = bas.findVerlaufAbteilungen(selectedView.getVerlaufId());

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

        result.timeSlot = getTimeSlot(result.auftragDaten, selectedView);

        return result;
    }

    @Override
    public void updateGuiByDetails(VerlaufUniversalView selectedView, DispoDetailsWorkerResult result) {
        setSelectedView(selectedView);
        auftragDaten = result.auftragDaten;
        vaVerteilt = result.vaVerteilt;
        vpn = result.vpn;
        schnittstelleEsB = result.schnittstelleEsB;
        carrierbestellung = result.carrierbestellung;

        tfAnlass.setText(selectedView.getAnlass());
        tfOeName.setText(selectedView.getOeName());
        tfProdukt.setText(selectedView.getProduktName());
        dcRealisierung.setDate(selectedView.getRealisierungstermin());
        dcRealisierung.setForeground((selectedView.isVerschoben()) ? Color.red : Color.black);
        dcBaAnDispo.setDate(selectedView.getDatumAnAbteilung());
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
        rfInstall.setReferenceId(selectedView.getInstallationRefId());

        CollectionUtils.filter(vaVerteilt, new VerlaufAbteilungTechPredicate());
        tbMdlVerlaufAbt.setData(vaVerteilt);

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
        vaVerteilt = null;
        if (guiCreated) {
            tbMdlVerlaufAbt.setData(null);
        }
        super.clearDetails();
    }


    /**
     * 'Fragt' den User, ob er den BA trotz erkannter Differenz in den Abteilungen (gegenueber den zum BA gehoerenden
     * techn. Leistungen) verteilen soll
     * @param abteilungDifference
     * @return
     */
    private boolean askUser4BauauftragDistribution(Collection<Long> abteilungDifference) {
        StringBuilder sb = new StringBuilder();
        try {
            NiederlassungService ns = getCCService(NiederlassungService.class);
            List<Abteilung> abteilungen = ns.findAbteilungen(abteilungDifference);
            if ((abteilungen != null) && (!abteilungen.isEmpty())) {
                for (Abteilung abt : abteilungen) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(abt.getName());
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        int opt = MessageHelper.showConfirmDialog(getMainFrame(),
                getSwingFactory().getText("verteilung.abt.diff.msg", sb.toString()),
                getSwingFactory().getText("verteilung.abt.diff.title"), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return (opt == JOptionPane.YES_OPTION);
    }

    /**
     * TableModel fuer die Darstellung des Abteilungsnamens sowie der Niederlassung von Abteilungs-Verlaeufen.
     *
     *
     */
    static class AbtTableModel extends AKTableModel<VerlaufAbteilung> {

        private static final int COL_ABTEILUNG = 0;
        private static final int COL_NIEDERLASSUNG = 1;
        private static final int COL_EXTERN = 2;

        private static final int COL_COUNT = 3;
        private static final long serialVersionUID = -1149818890445753511L;

        private Map<Long, Abteilung> abtMap = null;
        private Map<Long, Niederlassung> nlMap = null;
        private Map<Long, ExtServiceProvider> extMap = null;

        /**
         * Default-Konstruktor.
         */
        public AbtTableModel() {
            super();
            abtMap = new HashMap<>();
            nlMap = new HashMap<>();
            extMap = new HashMap<>();
        }

        /**
         * Uebergibt dem TableModel eine Liste mit allen verfuegbaren Abteilungen.
         *
         * @param abteilungen
         */
        public void setAbteilungen(List<Abteilung> abteilungen) {
            CollectionMapConverter.convert2Map(abteilungen, abtMap, "getId", null);
        }

        public void setNiederlassungen(List<Niederlassung> niederlassungen) {
            CollectionMapConverter.convert2Map(niederlassungen, nlMap, "getId", null);
        }

        public void setExternePartner(List<ExtServiceProvider> extServProv) {
            CollectionMapConverter.convert2Map(extServProv, extMap, "getId", null);
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_ABTEILUNG:
                    return "Abteilung";
                case COL_NIEDERLASSUNG:
                    return "Niederlassung";
                case COL_EXTERN:
                    return "Externer Partner";
                default:
                    return super.getColumnName(column);
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            VerlaufAbteilung va = getDataAtRow(row);
            if (va != null) {
                switch (column) {
                    case COL_ABTEILUNG:
                        Abteilung abt = abtMap.get(va.getAbteilungId());
                        return abt.getName();
                    case COL_NIEDERLASSUNG:
                        Niederlassung nl = nlMap.get(va.getNiederlassungId());
                        return nl.getName();
                    case COL_EXTERN:
                        ExtServiceProvider sp = extMap.get(va.getExtServiceProviderId());
                        return (sp != null) ? sp.getName() : null;
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

    }

}

/**
 * value-Object Klasse zur Speicherung der Werte aus dem {@link SwingWorker#doInBackground()}
 */
class DispoDetailsWorkerResult {
    Carrierbestellung carrierbestellung = null;
    Schnittstelle schnittstelleEsB = null;
    VPN vpn = null;
    String timeSlot = null;
    List<VerlaufAbteilung> vaVerteilt = null;
    AuftragDaten auftragDaten = null;
    List<Auftrag2TechLeistung> auftrag2TechLeistungen = null;
}
