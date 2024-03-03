/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.01.2009 12:42:12
 */
package de.augustakom.hurrican.gui.base.tree.hardware;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.AuftragNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.HwBaugruppeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.HwRackNode;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.cc.VerbindungsBezeichnung;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWDslam;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.temp.MassenbenachrichtigungDaten;
import de.augustakom.hurrican.model.exmodules.massenbenachrichtigung.TServiceExp;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.BAService;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.MassenbenachrichtigungService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel fuer die Darstellung von Auftrags-IDs fuer Massenbenachrichtigungen und zum Anstossen von
 * Massenbenachrichtigungen.
 *
 *
 */
public class MassenbenachrichtigungPanel extends AbstractServicePanel {
    private static final Logger LOGGER = Logger.getLogger(MassenbenachrichtigungPanel.class);

    private static final String AUFTRAEGE = "auftraege";
    private static final String MB_POT_AUFTRAEGE = "auftraege.pot";
    private static final String MB_GENERIEREN_FAX = "mb.generieren.fax";
    private static final String MB_GENERIEREN_EMAIL = "mb.generieren.email";
    private static final String MB_VORLAGE = "mb.vorlage";
    private static final String MB_BEGINN = "mb.beginn";
    private static final String MB_ENDE = "mb.ende";
    private static final String MB_TICKET = "mb.ticket";
    private static final String MB_TEXT = "mb.text";
    private static final String MB_IDS = "mb.ids";
    private static final String MB_START = "mb.start";
    private static final String MB_RESET_AUFTRAEGE = "mb.reset.auftraege";
    private static final String MB_DETERMINE_AUFTRAEGE = "mb.determine.auftraege";
    private static final String MB_AUFTRAEGE_UEBERNEHMEN = "mb.auftraege.uebernehmen";
    private static final String MB_AUFTRAEGE_UEBERNEHMEN_SELECTED = "mb.auftraege.uebernehmen.selected";


    private static final Map<String, String> VORLAGEN_MAPPING = new HashMap<String, String>();

    static {
        VORLAGEN_MAPPING.put("Terminvereinbarung geplante Arbeiten", "GA Terminvorschlag");
        VORLAGEN_MAPPING.put("Bestätigung der geplanten Arbeit", "GA Bestät. Termin");
        VORLAGEN_MAPPING.put("Störungsmeldung", "Erstbenachrichtigung");
        VORLAGEN_MAPPING.put("Funktion wiederhergestellt", "Funktion whgestellt");
        VORLAGEN_MAPPING.put("Standardfax", "Standardfax");
    }

    // CC-Services
    private CCAuftragService auftragService;
    private BAService baService;
    private PhysikService physikService;
    private ProduktService produktService;
    private MassenbenachrichtigungService massenbenachrichtigungService;

    private final Map<Long, MassenbenachrichtigungDaten> sdslAuftraege = new HashMap<Long, MassenbenachrichtigungDaten>();
    private final List<MassenbenachrichtigungDaten> useAuftraege = new ArrayList<MassenbenachrichtigungDaten>();
    private final List<TableData> tableData = new ArrayList<TableData>();

    private AKJTextField tfPotAuftraege;
    private AKJButton btnMbStart;
    private AKJButton btnMbResetAuftraege;
    private AKJButton btnMbDetermineAuftraege;
    private AKJButton btnMbAuftraegeUebernehmen;
    private AKJButton btnMbAuftraegeUebernehmenSel;
    private AKJCheckBox chbFax;
    private AKJCheckBox chbEmail;
    private AKJComboBox cbVorlage;
    private AKJDateComponent dcBeginn;
    private AKJDateComponent dcEnde;
    private AKJTextField tfTicket;
    private AKJTextArea taText;
    private AKJTextArea taIds;
    private AKJTable tbAuftraege;
    private MbAuftragTableModel tbModelAuftraege;


    public MassenbenachrichtigungPanel(Collection<DynamicTreeNode> nodes) {
        super("de/augustakom/hurrican/gui/base/tree/hardware/resources/MassenbenachrichtigungPanel.xml");
        try {
            auftragService = getCCService(CCAuftragService.class);
            baService = getCCService(BAService.class);
            physikService = getCCService(PhysikService.class);
            produktService = getCCService(ProduktService.class);
            massenbenachrichtigungService = getCCService(MassenbenachrichtigungService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
        createGUI();
        loadData(nodes);
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblMbPotAuftraege = getSwingFactory().createLabel(MB_POT_AUFTRAEGE);
        AKJLabel lblMbBeginn = getSwingFactory().createLabel(MB_BEGINN);
        AKJLabel lblMbEnde = getSwingFactory().createLabel(MB_ENDE);
        AKJLabel lblMbGenerierenFax = getSwingFactory().createLabel(MB_GENERIEREN_FAX);
        AKJLabel lblMbGenerierenEmail = getSwingFactory().createLabel(MB_GENERIEREN_EMAIL);
        AKJLabel lblMbVorlage = getSwingFactory().createLabel(MB_VORLAGE);
        AKJLabel lblMbTicket = getSwingFactory().createLabel(MB_TICKET);
        AKJLabel lblMbText = getSwingFactory().createLabel(MB_TEXT);
        AKJLabel lblMbIds = getSwingFactory().createLabel(MB_IDS);
        AKJLabel lblAuftraege = getSwingFactory().createLabel(AUFTRAEGE);

        tfPotAuftraege = getSwingFactory().createTextField(MB_POT_AUFTRAEGE);
        dcBeginn = getSwingFactory().createDateComponent(MB_BEGINN);
        dcEnde = getSwingFactory().createDateComponent(MB_ENDE);

        chbFax = getSwingFactory().createCheckBox(MB_GENERIEREN_FAX);
        chbEmail = getSwingFactory().createCheckBox(MB_GENERIEREN_EMAIL);
        cbVorlage = getSwingFactory().createComboBox(MB_VORLAGE, VORLAGEN_MAPPING.keySet().toArray(new String[VORLAGEN_MAPPING.size()]));
        tfTicket = getSwingFactory().createTextField(MB_TICKET);
        taText = getSwingFactory().createTextArea(MB_TEXT);
        AKJScrollPane spText = new AKJScrollPane(taText);
        taIds = getSwingFactory().createTextArea(MB_IDS);
        AKJScrollPane spIds = new AKJScrollPane(taIds);
        btnMbStart = getSwingFactory().createButton(MB_START, getActionListener());
        btnMbResetAuftraege = getSwingFactory().createButton(MB_RESET_AUFTRAEGE, getActionListener());
        btnMbDetermineAuftraege = getSwingFactory().createButton(MB_DETERMINE_AUFTRAEGE, getActionListener());
        btnMbAuftraegeUebernehmen = getSwingFactory().createButton(MB_AUFTRAEGE_UEBERNEHMEN, getActionListener());
        btnMbAuftraegeUebernehmenSel = getSwingFactory().createButton(MB_AUFTRAEGE_UEBERNEHMEN_SELECTED, getActionListener());

        tbModelAuftraege = new MbAuftragTableModel();
        tbModelAuftraege.setData(tableData);
        tbAuftraege = new AKJTable(tbModelAuftraege,
                JTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbAuftraege.attachSorter();
        tbAuftraege.fitTable(new int[] { 60, 60, 100, 20, 20, 20, 60, 100, 150, 250, 100, 100, 150, 100, 100, 150, 100, 100, 150 });
        AKJScrollPane spAuftraege = new AKJScrollPane(tbAuftraege);
        spAuftraege.setPreferredSize(new Dimension(600, 200));

        JPanel panelOne = new JPanel();
        panelOne.setLayout(new GridBagLayout());
        panelOne.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.title.1")));

        JPanel panelOneTop = new JPanel();
        JPanel panelOneBottom = new JPanel();
        panelOneTop.setLayout(new GridBagLayout());
        panelOneBottom.setLayout(new GridBagLayout());

        panelOneTop.add(lblMbPotAuftraege, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(new JPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        panelOneTop.add(tfPotAuftraege, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(new JPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        panelOneTop.add(lblMbBeginn, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(dcBeginn, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(btnMbDetermineAuftraege, GBCFactory.createGBC(0, 0, 4, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(lblMbEnde, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(dcEnde, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(btnMbResetAuftraege, GBCFactory.createGBC(0, 0, 4, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(lblAuftraege, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneTop.add(spAuftraege, GBCFactory.createGBC(0, 0, 0, 4, 5, 1, GridBagConstraints.BOTH));
        panelOneBottom.add(btnMbAuftraegeUebernehmen, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOneBottom.add(btnMbAuftraegeUebernehmenSel, GBCFactory.createGBC(100, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        panelOne.add(panelOneTop, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelOne.add(panelOneBottom, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        JPanel panelTwo = new JPanel();
        panelTwo.setLayout(new GridBagLayout());
        panelTwo.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("border.title.2")));

        panelTwo.add(lblMbGenerierenFax, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(new JPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        panelTwo.add(chbFax, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(new JPanel(), GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        panelTwo.add(lblMbIds, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(lblMbGenerierenEmail, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(chbEmail, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(spIds, GBCFactory.createGBC(0, 0, 4, 1, 1, 5, GridBagConstraints.BOTH));
        panelTwo.add(lblMbVorlage, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(cbVorlage, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(lblMbTicket, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(tfTicket, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(lblMbText, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        panelTwo.add(spText, GBCFactory.createGBC(0, 100, 0, 5, 3, 1, GridBagConstraints.BOTH));
        panelTwo.add(btnMbStart, GBCFactory.createGBC(0, 0, 0, 6, 5, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(panelOne, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(panelTwo, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));

        manageGUI(btnMbStart);

        tfPotAuftraege.setEnabled(false);
        setFieldsEnabled(false, false, false);
    }


    private void loadData(final Collection<DynamicTreeNode> nodes) {
        setFieldsEnabled(false, false, false);
        setWaitCursor();
        showProgressBar("suchen...");
        final SwingWorker<Map<Long, MassenbenachrichtigungDaten>, Void> worker = new SwingWorker<Map<Long, MassenbenachrichtigungDaten>, Void>() {

            @Override
            protected Map<Long, MassenbenachrichtigungDaten> doInBackground() throws Exception {
                Set<MassenbenachrichtigungDaten> mbAuftraege = new HashSet<MassenbenachrichtigungDaten>();
                for (DynamicTreeNode node : nodes) {
                    nodeDfs(node, mbAuftraege);
                }

                List<Produkt> produkte = produktService.findProdukte(false);
                final Map<Long, Produkt> produktMap = new HashMap<Long, Produkt>();
                for (Produkt produkt : produkte) {
                    produktMap.put(produkt.getId(), produkt);
                }

                Map<Long, MassenbenachrichtigungDaten> sdslAuftraegeByAuftragNoOrig = new HashMap<Long, MassenbenachrichtigungDaten>(mbAuftraege.size());
                for (MassenbenachrichtigungDaten mbAuftragsDaten : mbAuftraege) {
                    Produkt produkt = produktMap.get(mbAuftragsDaten.getAuftragDaten().getProdId());
                    if ((produkt != null) && ProduktGruppe.AK_SDSL.equals(produkt.getProduktGruppeId())) {
                        MassenbenachrichtigungDaten old = sdslAuftraegeByAuftragNoOrig.put(mbAuftragsDaten.getAuftragDaten().getAuftragNoOrig(), mbAuftragsDaten);
                        // Falls vierdraht Auftrag sollte der zweite Auftrag nicht doppelt erscheinen.
                        // Wir wollen aber nicht den Auftrag mit der 4-Draht-Option haben.
                        if ((old != null) && BooleanTools.nullToFalse(produkt.getIsVierDraht())) {
                            sdslAuftraegeByAuftragNoOrig.put(mbAuftragsDaten.getAuftragDaten().getAuftragNoOrig(), old);
                        }
                    }
                }
                return sdslAuftraegeByAuftragNoOrig;
            }

            @Override
            protected void done() {
                try {
                    Map<Long, MassenbenachrichtigungDaten> sdslAuftraegeByAuftragNoOrig = get();
                    sdslAuftraege.clear();
                    for (MassenbenachrichtigungDaten mbAuftragsDaten : sdslAuftraegeByAuftragNoOrig.values()) {
                        sdslAuftraege.put(mbAuftragsDaten.getAuftragDaten().getAuftragId(), mbAuftragsDaten);
                    }

                    tfPotAuftraege.setText(sdslAuftraege.size());
                    setFieldsEnabled(true, false, true);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                }
            }


        };
        worker.execute();
    }


    /**
     * Macht eine DFS auf den Child-Nodes und sucht die Auftraege heraus.
     */
    private void nodeDfs(DynamicTreeNode node, Set<MassenbenachrichtigungDaten> auftraege) {
        if (node instanceof AuftragNode) {
            EquipmentNode portNode = node.getParentOfType(EquipmentNode.class);
            HwBaugruppeNode baugruppenNode = portNode.getParentOfType(HwBaugruppeNode.class);
            if (baugruppenNode != null) {
                HwRackNode rackNode = baugruppenNode.getParentOfType(HwRackNode.class);

                MassenbenachrichtigungDaten auftragsDaten = new MassenbenachrichtigungDaten();
                auftragsDaten.setAuftragDaten(((AuftragNode) node).getAuftragDaten());
                auftragsDaten.setRack(rackNode.getRackModel().getGeraeteBez());
                auftragsDaten.setKarte(baugruppenNode.getBaugruppe().getModNumber());
                try {
                    auftragsDaten.setPort(Long.valueOf(
                            portNode.getEquipment().getHwEQN().substring(portNode.getEquipment().getHwEQN().lastIndexOf(Equipment.HW_EQN_SEPARATOR) + 1)));
                }
                catch (Exception e) {
                    LOGGER.debug("nodeDfs() - HWEQN: " + portNode.getEquipment().getHwEQN() + " - could not extract port");
                    // leave: auftragsDaten.port = null
                }

                auftraege.add(auftragsDaten);
            }
        }
        else {
            if (node instanceof HwRackNode) {
                HWRack rackModel = ((HwRackNode) node).getRackModel();
                if (!(rackModel instanceof HWDslam)) {
                    return;
                }
            }
            if (node instanceof HwBaugruppeNode) {
                HWBaugruppenTyp baugruppenTyp = ((HwBaugruppeNode) node).getBaugruppe().getHwBaugruppenTyp();
                if (!HWBaugruppenTyp.HW_SCHNITTSTELLE_SDSL.equals(baugruppenTyp.getHwSchnittstelleName())) {
                    return;
                }
            }
            node.loadChildren();
            @SuppressWarnings("unchecked")
            Enumeration<DynamicTreeNode> children = node.children();
            while (children.hasMoreElements()) {
                nodeDfs(children.nextElement(), auftraege);
            }
        }
    }


    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (MB_START.equals(command)) {
            triggerMassenbenachrichtigung();
        }
        if (MB_RESET_AUFTRAEGE.equals(command)) {
            setFieldsEnabled(true, false, true);
            useAuftraege.clear();
            tbModelAuftraege.fireTableDataChanged();
        }
        if (MB_DETERMINE_AUFTRAEGE.equals(command)) {
            determineAuftraege();
        }
        if (MB_AUFTRAEGE_UEBERNEHMEN.equals(command)) {
            StringBuilder auftraege = new StringBuilder();
            String lineSep = System.getProperty("line.separator");
            for (MassenbenachrichtigungDaten auftrag : useAuftraege) {
                auftraege.append(auftrag.getAuftragDaten().getAuftragId());
                auftraege.append(lineSep);
            }
            taIds.setText(auftraege.toString());
        }
        if (MB_AUFTRAEGE_UEBERNEHMEN_SELECTED.equals(command)) {
            StringBuilder auftraege = new StringBuilder();
            String lineSep = System.getProperty("line.separator");
            for (int row : tbAuftraege.getSelectedRows()) {
                TableData data = tbModelAuftraege.getDataAtRow(row);
                auftraege.append(data.hurricanId);
                auftraege.append(lineSep);
            }
            taIds.setText(auftraege.toString());
        }
    }


    private static class DetermineAuftraegeResult {
        private List<TableData> tableData;
        private List<MassenbenachrichtigungDaten> useAuftraege;
    }

    /**
     * Ermittelt die Auftraege fuer die angegebenen Daten
     */
    private void determineAuftraege() {
        final FormData formData = checkFormFields(true);
        if (formData == null) {
            return;
        }
        setFieldsEnabled(false, false, false);
        setWaitCursor();
        showProgressBar("filtern...");
        useAuftraege.clear();
        tableData.clear();

        final SwingWorker<DetermineAuftraegeResult, Void> worker = new SwingWorker<DetermineAuftraegeResult, Void>() {

            final Collection<MassenbenachrichtigungDaten> mbAuftragsDatenValues = sdslAuftraege.values();

            @Override
            protected DetermineAuftraegeResult doInBackground() throws Exception {
                DetermineAuftraegeResult wResult = new DetermineAuftraegeResult();
                wResult.tableData = new ArrayList<TableData>();
                wResult.useAuftraege = new ArrayList<MassenbenachrichtigungDaten>();

                List<AuftragStatus> auftragStati = auftragService.findAuftragStati();
                Map<Long, AuftragStatus> auftragStatiMap = new HashMap<Long, AuftragStatus>();
                for (AuftragStatus auftragStatus : auftragStati) {
                    auftragStatiMap.put(auftragStatus.getId(), auftragStatus);
                }

                for (MassenbenachrichtigungDaten mbAuftragsDaten : mbAuftragsDatenValues) {
                    AuftragDaten auftragDaten = mbAuftragsDaten.getAuftragDaten();
                    if ((auftragDaten.getKuendigung() != null) && auftragDaten.getKuendigung().before(formData.start)) {
                        continue;
                    }
                    if (auftragDaten.getInbetriebnahme() != null) {
                        if (auftragDaten.getInbetriebnahme().after(formData.end)) {
                            continue;
                        }
                    }
                    else {
                        Verlauf verlauf = null;
                        try {
                            verlauf = baService.findActVerlauf4Auftrag(auftragDaten.getAuftragId(), false);
                        }
                        catch (FindException e) {
                            LOGGER.warn("triggerMassenbenachrichtigung() - Error finding Verlauf for Auftrag " + auftragDaten.getAuftragId());
                        }
                        if ((verlauf == null) || (verlauf.getRealisierungstermin() == null) ||
                                verlauf.getRealisierungstermin().after(formData.end)) {
                            continue;
                        }
                    }
                    // Alles ok - Massenbenachrichtigung sollte fuer Auftrag erstellt werden
                    TServiceExp serviceExp = massenbenachrichtigungService.createFromAuftrag(auftragDaten);

                    VerbindungsBezeichnung verbindungsBezeichnung = physikService.findVerbindungsBezeichnungByAuftragId(auftragDaten.getAuftragId());
                    wResult.useAuftraege.add(mbAuftragsDaten);
                    wResult.tableData.add(new TableData(auftragDaten.getAuftragId(), auftragDaten.getAuftragNoOrig(),
                            verbindungsBezeichnung, auftragStatiMap.get(auftragDaten.getAuftragStatusId()).getStatusText(), serviceExp));
                }
                return wResult;
            }

            @Override
            protected void done() {
                try {
                    DetermineAuftraegeResult wResult = get();
                    tableData.addAll(wResult.tableData);
                    useAuftraege.addAll(wResult.useAuftraege);
                    tbModelAuftraege.setData(tableData);
                }
                catch (Exception e) {
                    useAuftraege.clear();
                    tableData.clear();
                    MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Fehler bei der Ermittlung der relevanten Aufträge", e), false);
                }
                finally {
                    setDefaultCursor();
                    stopProgressBar();
                    tbModelAuftraege.fireTableDataChanged();
                    setFieldsEnabled(false, true, true);
                }
            }

        };
        worker.execute();
    }


    /**
     * Erstellt Massenbenachrichtigungen fuer die ermittelten Auftraege zum gesetzten Datum
     */
    private void triggerMassenbenachrichtigung() {
        final FormData formData = checkFormFields(false);
        if (formData == null) {
            return;
        }
        int dialogResult = MessageHelper.showConfirmDialog(this, "Massenbenachrichtigung für angegebene Aufträge starten?",
                "Massenbenachrichtigung", JOptionPane.OK_CANCEL_OPTION);
        if (JOptionPane.OK_OPTION == dialogResult) {
            String auftraegeText = taIds.getText();
            if (!StringUtils.isBlank(auftraegeText)) {
                setFieldsEnabled(false, false, false);
                setWaitCursor();
                showProgressBar("Massenbenachrichtigung...");
                final List<MassenbenachrichtigungDaten> auftragsDaten = new ArrayList<MassenbenachrichtigungDaten>();
                final String[] auftraege = auftraegeText.split(System.getProperty("line.separator"));
                for (String stringId : auftraege) {
                    Long id = Long.valueOf(stringId);
                    MassenbenachrichtigungDaten mbAuftragsDaten = sdslAuftraege.get(id);
                    if (mbAuftragsDaten == null) {
                        throw new NullPointerException("ID ist nicht Teil der ermittelten Aufträge");
                    }
                    auftragsDaten.add(mbAuftragsDaten);
                }

                final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        massenbenachrichtigungService.triggerMassenbenachrichtigung(auftragsDaten, formData.vorlage,
                                formData.fax, formData.email, formData.ticket, formData.text,
                                formData.start, formData.end, HurricanSystemRegistry.instance().getSessionId());
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            MessageHelper.showMessageDialog(getMainFrame(), "Massenbenachrichtigung wurde angestossen");
                        }
                        catch (Exception e) {
                            LOGGER.error("triggerMassenbenachrichtigung() - Error triggering Massenbenachrichtigung", e);
                            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Fehler bei der Ermittlung der relevanten Aufträge", e), false);
                        }
                        finally {
                            setDefaultCursor();
                            stopProgressBar();
                            setFieldsEnabled(false, true, true);
                        }
                    }


                };
                worker.execute();
            }
            else {
                MessageHelper.showInfoDialog(getMainFrame(), "Keine Auftragsnummern eingegeben!", null, true);
            }
        }
    }


    private void setFieldsEnabled(boolean step1, boolean step2, boolean input) {
        chbFax.setEnabled(input);
        chbEmail.setEnabled(input);
        cbVorlage.setEnabled(input);
        tfTicket.setEnabled(input);
        taText.setEnabled(input);
        dcBeginn.setEnabled(step1);
        dcEnde.setEnabled(step1);
        btnMbDetermineAuftraege.setEnabled(step1);
        btnMbResetAuftraege.setEnabled(step2);
        btnMbAuftraegeUebernehmen.setEnabled(step2);
        btnMbAuftraegeUebernehmenSel.setEnabled(step2);
        btnMbStart.setEnabled(input);
    }


    private FormData checkFormFields(boolean onlyDates) {
        FormData formData = new FormData();
        formData.start = dcBeginn.getDate(null);
        formData.end = dcEnde.getDate(null);
        formData.vorlage = VORLAGEN_MAPPING.get(cbVorlage.getSelectedItem());
        formData.fax = chbFax.isSelectedBoolean();
        formData.email = chbEmail.isSelectedBoolean();
        formData.ticket = tfTicket.getText();
        formData.text = taText.getText();

        if (formData.start == null) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Es wurden kein gültiges Start-Datum angegeben."), false);
            return null;
        }
        if (formData.end == null) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Es wurden kein gültiges Ende-Datum angegeben."), false);
            return null;
        }
        if (formData.start.after(formData.end)) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Das Start-Datum muss vor dem Ende-Datum liegen."), false);
            return null;
        }
        if (!onlyDates) {
            if (StringUtils.isEmpty(formData.vorlage)) {
                MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Eine Vorlage muss ausgewählt werden."), false);
                return null;
            }
            if (StringUtils.isEmpty(formData.ticket)) {
                MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Eine Ticket-Nummer muss angegeben werden."), false);
                return null;
            }
            if (StringUtils.isEmpty(formData.text)) {
                MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Ein Text muss angegeben werden."), false);
                return null;
            }
            if (!Boolean.TRUE.equals(formData.fax) && !Boolean.TRUE.equals(formData.email)) {
                MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Email und/oder Fax sollte ausgewählt werden."), false);
                return null;
            }
        }
        return formData;
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }


    /**
     * Klasse fuer die Form-Daten
     */
    private static class FormData {
        Date start;
        Date end;
        String vorlage;
        Boolean fax;
        Boolean email;
        String ticket;
        String text;
    }

    private static class TableData {
        final Long hurricanId;
        final Long taifunId;
        final VerbindungsBezeichnung verbindungsBezeichnung;
        final String status;
        final TServiceExp serviceExp;

        public TableData(Long hurricanId, Long taifunId, VerbindungsBezeichnung verbindungsBezeichnung, String status,
                TServiceExp serviceExp) {
            this.hurricanId = hurricanId;
            this.taifunId = taifunId;
            this.verbindungsBezeichnung = verbindungsBezeichnung;
            this.status = status;
            this.serviceExp = serviceExp;
        }
    }


    /**
     * TableModel fuer die HVT-Standorte.
     */
    private static class MbAuftragTableModel extends AKTableModel<TableData> {
        static final int COL_HUR_ID = 0;
        static final int COL_TAIFUN_ID = 1;
        static final int COL_VBZ = 2;
        static final int COL_CUST_IDENT = 3;
        static final int COL_KOU_PRODUCT = 4;
        static final int COL_KOU_TYPE = 5;
        static final int COL_UNIQUE_CODE = 6;
        static final int COL_STATUS = 7;
        static final int COL_CUSTOMER = 8;
        static final int COL_CUSTOMER_ADDRESS = 9;
        static final int COL_CONTACT_1 = 10;
        static final int COL_CONTACT_1_FAX = 11;
        static final int COL_CONTACT_1_MAIL = 12;
        static final int COL_CONTACT_2 = 13;
        static final int COL_CONTACT_2_FAX = 14;
        static final int COL_CONTACT_2_MAIL = 15;
        static final int COL_CONTACT_3 = 16;
        static final int COL_CONTACT_3_FAX = 17;
        static final int COL_CONTACT_3_MAIL = 18;

        static final int COL_COUNT = 19;

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
                case COL_HUR_ID:
                    return "Technische Auftragsnr.";
                case COL_TAIFUN_ID:
                    return "Billing Auftragsnr.";
                case COL_VBZ:
                    return VerbindungsBezeichnung.VBZ_BEZEICHNUNG;
                case COL_CUST_IDENT:
                    return "Nutzerbezeichnung";
                case COL_KOU_PRODUCT:
                    return "Nutzungsart Produktgruppe";
                case COL_KOU_TYPE:
                    return "Nutzungsart Typ";
                case COL_UNIQUE_CODE:
                    return "Eindeutige Codierung";
                case COL_STATUS:
                    return "Status";
                case COL_CUSTOMER:
                    return "Kunde";
                case COL_CUSTOMER_ADDRESS:
                    return "Adresse Kunde";
                case COL_CONTACT_1:
                    return "Kontakt 1";
                case COL_CONTACT_1_FAX:
                    return "Kontakt 1 Fax";
                case COL_CONTACT_1_MAIL:
                    return "Kontakt 1 Mail";
                case COL_CONTACT_2:
                    return "Kontakt 2";
                case COL_CONTACT_2_FAX:
                    return "Kontakt 2 Fax";
                case COL_CONTACT_2_MAIL:
                    return "Kontakt 2 Mail";
                case COL_CONTACT_3:
                    return "Kontakt 3";
                case COL_CONTACT_3_FAX:
                    return "Kontakt 3 Fax";
                case COL_CONTACT_3_MAIL:
                    return "Kontakt 3 Mail";
                default:
                    return " ";
            }
        }


        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            TableData data = getDataAtRow(row);
            switch (column) {
                case COL_HUR_ID:
                    return data.hurricanId;
                case COL_TAIFUN_ID:
                    return data.taifunId;
                case COL_VBZ:
                    return data.verbindungsBezeichnung.getVbz();
                case COL_CUST_IDENT:
                    return data.verbindungsBezeichnung.getCustomerIdent();
                case COL_KOU_PRODUCT:
                    return data.verbindungsBezeichnung.getKindOfUseProduct();
                case COL_KOU_TYPE:
                    return data.verbindungsBezeichnung.getKindOfUseType();
                case COL_UNIQUE_CODE:
                    return data.verbindungsBezeichnung.getUniqueCode();
                case COL_STATUS:
                    return data.status;
                case COL_CUSTOMER:
                    return data.serviceExp.getTsxKunde();
                case COL_CUSTOMER_ADDRESS:
                    return String.format("%s%s%s%s%s",
                            format(data.serviceExp.getTsxPlz(), " "),
                            format(data.serviceExp.getTsxOrt(), ", "),
                            format(data.serviceExp.getTsxStrasse(), " "),
                            format(data.serviceExp.getTsxHausnr(), " "),
                            format(data.serviceExp.getTsxWohnung(), " "));
                case COL_CONTACT_1:
                    return data.serviceExp.getTsxAnspr1();
                case COL_CONTACT_1_FAX:
                    return data.serviceExp.getTsxAnsprFax1();
                case COL_CONTACT_1_MAIL:
                    return data.serviceExp.getTsxAnsprEmail1();
                case COL_CONTACT_2:
                    return data.serviceExp.getTsxAnspr2();
                case COL_CONTACT_2_FAX:
                    return data.serviceExp.getTsxAnsprFax2();
                case COL_CONTACT_2_MAIL:
                    return data.serviceExp.getTsxAnsprEmail2();
                case COL_CONTACT_3:
                    return data.serviceExp.getTsxAnspr3();
                case COL_CONTACT_3_FAX:
                    return data.serviceExp.getTsxAnsprFax3();
                case COL_CONTACT_3_MAIL:
                    return data.serviceExp.getTsxAnsprEmail3();
                default:
                    return null;
            }
        }

        private String format(String s, String separator) {
            if (s == null) {
                return "";
            }
            return s + separator;
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
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case COL_HUR_ID:
                    return Long.class;
                case COL_TAIFUN_ID:
                    return Long.class;
                default:
                    return String.class;
            }
        }
    }
}
