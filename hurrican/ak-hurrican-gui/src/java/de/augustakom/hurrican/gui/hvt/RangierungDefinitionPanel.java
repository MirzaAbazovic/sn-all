/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.10.2007 13:11:11
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReflectionTableModel;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.iface.ISimpleFindService;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.common.tools.messages.AKMessages;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.innenauftrag.IAServiceRoomDefinitionDialog;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.EqStatus;
import de.augustakom.hurrican.model.cc.EqVerwendung;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RangSchnittstelle;
import de.augustakom.hurrican.model.cc.RangierungsAuftrag;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.model.cc.Uebertragungsverfahren;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.innenauftrag.IA;
import de.augustakom.hurrican.model.cc.tal.ProduktDtag;
import de.augustakom.hurrican.model.cc.view.EquipmentInOutView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CounterService;
import de.augustakom.hurrican.service.cc.HWService;
import de.augustakom.hurrican.service.cc.PhysikService;
import de.augustakom.hurrican.service.cc.QueryCCService;
import de.augustakom.hurrican.service.cc.RangierungAdminService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * Panel fuer die Definition von Rangierungen.
 *
 *
 */
public class RangierungDefinitionPanel extends AbstractServicePanel implements AKDataLoaderComponent {

    private static final String EQ_IDS_DSLAM_OUT = "eqIdsDSLAMOut";
    private static final String EQ_IDS_DSLAM_IN = "eqIdsDSLAMIn";
    private static final String EQ_IDS_EWSD = "eqIdsEWSD";
    private static final String TYPE = "type";
    private static final String UETV2 = "uetv";
    private static final String RANG_SS_TYPE = "rangSSType";
    private static final String EQ_IDS_CARRIER = "eqIdsCarrier";
    private static final String EQ_IDS_PDH_IN = "eqIdsPDHIn";
    private static final String EQ_IDS_PDH_OUT = "eqIdsPDHOut";
    private static final String EQ_IDS_SDH = "eqIdsSDH";
    private static final String TYPE_SDH_PDH = "SDH/PDH";
    private static final String TYPE_DSL_TELEFONIE = "DSL/Telefonie";

    private static final Logger LOGGER = Logger.getLogger(RangierungDefinitionPanel.class);

    // GUI-Komponenten
    private AKJTable tbEWSD = null;
    private EquipmentWithBaugruppentypTableModel tbMdlEWSD = null;
    private AKJTable tbSDH = null;
    private EquipmentWithBaugruppentypTableModel tbMdlSDH = null;
    private AKJLabel lblCountEWSD = null;
    private AKJLabel lblCountSDH = null;
    private AKJTable tbDSLAM = null;
    private EquipmentWithBaugruppentypTableModel tbMdlDSLAM = null;
    private AKJTable tbPDHIn = null;
    private EquipmentWithBaugruppentypTableModel tbMdlPDHIn = null;
    private AKJTable tbPDHOut = null;
    private AKReflectionTableModel<Equipment> tbMdlPDHOut = null;
    private AKJLabel lblCountDSLAM = null;
    private AKJLabel lblCountPDHIn = null;
    private AKJLabel lblCountPDHOut = null;
    private AKJTable tbCarrier = null;
    private AKJTable tbCarrierPDH = null;
    private AKReflectionTableModel<Equipment> tbMdlCarrier = null;
    private AKJLabel lblCountCarrier = null;
    private AKJLabel lblCountCarrierPDH = null;
    private AKJComboBox cbRangSSType = null;
    private AKJComboBox cbUETV = null;
    private AKJComboBox cbRangType = null;
    private AKJPanel eqPnlDSL;
    private AKJPanel eqPnlPDH;

    // Modelle
    private RangierungsAuftrag rangierungsAuftrag = null;
    private PhysikTyp ptParent = null;
    private PhysikTyp ptChild = null;

    // sonstiges
    private boolean useDSLAM = false;
    private boolean useEWSD = false;
    private boolean useCarrier = false;
    private boolean useSDHPDH = false;
    private boolean dslamEqInOutView = false;

    private final List<Map<String, Object>> rangierungen = new ArrayList<Map<String, Object>>();
    private final List<Long> usedEqIDs = new ArrayList<Long>();

    /**
     * Konstruktor fuer das Panel mit Angabe des zugehoerigen Rangierungs-Auftrags.
     *
     * @param rangierungsAuftrag
     */
    public RangierungDefinitionPanel(RangierungsAuftrag rangierungsAuftrag) {
        super("de/augustakom/hurrican/gui/hvt/resources/RangierungDefinitionPanel.xml");
        this.rangierungsAuftrag = rangierungsAuftrag;
        if (this.rangierungsAuftrag == null) {
            throw new IllegalArgumentException("Rangierungsauftrag muss angegeben werden!");
        }
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblEWSD = getSwingFactory().createLabel("eq.ewsd");
        AKJLabel lblDSLAM = getSwingFactory().createLabel("eq.dslam");
        AKJLabel lblSDH = getSwingFactory().createLabel("eq.sdh");
        AKJLabel lblPDHIn = getSwingFactory().createLabel("eq.pdh.in");
        AKJLabel lblPDHOut = getSwingFactory().createLabel("eq.pdh.out");
        AKJLabel lblCarrier = getSwingFactory().createLabel("eq.carrier");
        AKJLabel lblCarrierPDH = getSwingFactory().createLabel("eq.carrier");
        AKJLabel lblRangSSType = getSwingFactory().createLabel("eq.rang.ss.type");
        AKJLabel lblRangType = getSwingFactory().createLabel("eq.rang.type");
        AKJLabel lblUETV = getSwingFactory().createLabel("eq.uetv");
        lblCountEWSD = getSwingFactory().createLabel("eq.ewsd.count");
        lblCountDSLAM = getSwingFactory().createLabel("eq.dslam.count");
        lblCountSDH = getSwingFactory().createLabel("eq.sdh.count");
        lblCountPDHIn = getSwingFactory().createLabel("eq.pdh.in.count");
        lblCountPDHOut = getSwingFactory().createLabel("eq.pdh.out.count");
        lblCountCarrier = getSwingFactory().createLabel("eq.carrier.count");
        lblCountCarrierPDH = getSwingFactory().createLabel("eq.carrier.count");

        cbRangSSType = getSwingFactory().createComboBox("eq.rang.ss.type",
                new AKCustomListCellRenderer<>(ProduktDtag.class, ProduktDtag::getRangSsType));
        cbUETV = getSwingFactory().createComboBox("eq.uetv",
                new AKCustomListCellRenderer<>(Uebertragungsverfahren.class, Uebertragungsverfahren::name));
        cbRangType = getSwingFactory().createComboBox("eq.rang.type",
                new String[] { TYPE_DSL_TELEFONIE, TYPE_SDH_PDH });
        cbRangType.addActionListener(getActionListener());
        AKJButton btnAdd = getSwingFactory().createButton("add.rangierung", getActionListener());
        AKJButton btnCreate = getSwingFactory().createButton("create.rangierungen", getActionListener());
        AKJButton btnReset = getSwingFactory().createButton("reset.rangierungen", getActionListener());
        AKJButton btnDefineBG = getSwingFactory().createButton("define.bg", getActionListener());
        AKJButton btnRefresh = getSwingFactory().createButton("refresh", getActionListener());

        Dimension lsDim = new Dimension(300, 300);
        tbMdlEWSD = new EquipmentWithBaugruppentypTableModel();
        tbEWSD = new AKJTable(tbMdlEWSD);
        ListSelectionModel ewsdLSM = new DefaultListSelectionModel();
        ewsdLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbEWSD));
        tbEWSD.setSelectionModel(ewsdLSM);
        AKJScrollPane spEWSD = new AKJScrollPane(tbEWSD, lsDim);

        tbMdlDSLAM = new EquipmentWithBaugruppentypTableModel();
        tbDSLAM = new AKJTable(tbMdlDSLAM);
        ListSelectionModel dslamLSM = new DefaultListSelectionModel();
        dslamLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbDSLAM));
        tbDSLAM.setSelectionModel(dslamLSM);
        AKJScrollPane spDSLAM = new AKJScrollPane(tbDSLAM, lsDim);

        tbMdlCarrier = new AKReflectionTableModel<Equipment>(
                new String[] { "Stifte", "Verwendung", "KVZ-Nr" },
                new String[] { "einbau1", Equipment.VERWENDUNG, Equipment.KVZ_NUMMER },
                new Class[] { String.class, EqVerwendung.class, String.class });
        tbCarrier = new AKJTable(tbMdlCarrier);
        ListSelectionModel carrierLSM = new DefaultListSelectionModel();
        carrierLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbCarrier));
        tbCarrier.setSelectionModel(carrierLSM);
        AKJScrollPane spCarrier = new AKJScrollPane(tbCarrier, lsDim);

        Dimension smallerDim = new Dimension(218, 300);
        tbMdlSDH = new EquipmentWithBaugruppentypTableModel();
        tbSDH = new AKJTable(tbMdlSDH);
        ListSelectionModel sdhLSM = new DefaultListSelectionModel();
        sdhLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbSDH));
        tbSDH.setSelectionModel(sdhLSM);
        AKJScrollPane spSDH = new AKJScrollPane(tbSDH, smallerDim);

        tbMdlPDHIn = new EquipmentWithBaugruppentypTableModel();
        tbPDHIn = new AKJTable(tbMdlPDHIn);
        ListSelectionModel pdhInLSM = new DefaultListSelectionModel();
        pdhInLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbPDHIn));
        tbPDHIn.setSelectionModel(pdhInLSM);
        AKJScrollPane spPDHIn = new AKJScrollPane(tbPDHIn, smallerDim);

        tbMdlPDHOut = new AKReflectionTableModel<Equipment>(
                new String[] { "Stifte" }, new String[] { "einbau1" }, new Class[] { String.class });
        tbPDHOut = new AKJTable(tbMdlPDHOut);
        ListSelectionModel pdhOutLSM = new DefaultListSelectionModel();
        pdhOutLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbPDHOut));
        tbPDHOut.setSelectionModel(pdhOutLSM);
        AKJScrollPane spPDHOut = new AKJScrollPane(tbPDHOut, smallerDim);

        tbCarrierPDH = new AKJTable(tbMdlCarrier);
        ListSelectionModel carrierPDHLSM = new DefaultListSelectionModel();
        carrierPDHLSM.addListSelectionListener(new EqTableSelectionChangeListener(tbCarrierPDH));
        tbCarrierPDH.setSelectionModel(carrierPDHLSM);
        AKJScrollPane spCarrierPDH = new AKJScrollPane(tbCarrierPDH, smallerDim);

        AKJPanel switchPanel = new AKJPanel(new GridBagLayout(), "Rangierung");
        switchPanel.add(lblRangType, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        switchPanel.add(cbRangType, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        eqPnlDSL = new AKJPanel(new GridBagLayout(), "Equipments");
        eqPnlDSL.add(lblEWSD, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(lblCountEWSD, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, 10));
        eqPnlDSL.add(lblDSLAM, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(lblCountDSLAM, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE, 10));
        eqPnlDSL.add(lblCarrier, GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(lblCountCarrier, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlDSL.add(spEWSD, GBCFactory.createGBC(0, 100, 0, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlDSL.add(spDSLAM, GBCFactory.createGBC(0, 100, 3, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlDSL.add(spCarrier, GBCFactory.createGBC(0, 100, 6, 1, 2, 1, GridBagConstraints.VERTICAL));

        eqPnlPDH = new AKJPanel(new GridBagLayout(), "Equipments (SDH/PDH)");
        eqPnlPDH.add(lblSDH, GBCFactory.createGBC(100, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(lblCountSDH, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE, 10));
        eqPnlPDH.add(lblPDHOut, GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(lblCountPDHOut, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 5, 0, 1, 1, GridBagConstraints.NONE, 10));
        eqPnlPDH.add(lblPDHIn, GBCFactory.createGBC(100, 0, 6, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(lblCountPDHIn, GBCFactory.createGBC(0, 0, 7, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 8, 0, 1, 1, GridBagConstraints.NONE, 10));
        eqPnlPDH.add(lblCarrierPDH, GBCFactory.createGBC(100, 0, 9, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(lblCountCarrierPDH, GBCFactory.createGBC(0, 0, 10, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        eqPnlPDH.add(spSDH, GBCFactory.createGBC(0, 100, 0, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlPDH.add(spPDHOut, GBCFactory.createGBC(0, 100, 3, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlPDH.add(spPDHIn, GBCFactory.createGBC(0, 100, 6, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlPDH.add(spCarrierPDH, GBCFactory.createGBC(0, 100, 9, 1, 2, 1, GridBagConstraints.VERTICAL));
        eqPnlPDH.setVisible(false);

        AKJPanel detPnl = new AKJPanel(new GridBagLayout(), "Detailangaben");
        detPnl.add(lblRangSSType, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        detPnl.add(cbRangSSType, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(lblUETV, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(cbUETV, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        detPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel fctPnl = new AKJPanel(new GridBagLayout(), "Funktionen");
        fctPnl.add(btnAdd, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        fctPnl.add(btnCreate, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.NONE));
        fctPnl.add(btnReset, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.NONE));
        fctPnl.add(btnDefineBG, GBCFactory.createGBC(0, 0, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        fctPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 7, 1, 1, GridBagConstraints.VERTICAL));
        fctPnl.add(btnRefresh, GBCFactory.createGBC(0, 0, 0, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(eqPnlDSL, GBCFactory.createGBC(0, 100, 0, 0, 1, 3, GridBagConstraints.VERTICAL));
        this.add(eqPnlPDH, GBCFactory.createGBC(0, 100, 0, 0, 1, 3, GridBagConstraints.VERTICAL));
        this.add(switchPanel, GBCFactory.createGBC(0, 10, 1, 0, 1, 1, GridBagConstraints.BOTH, 15));
        this.add(detPnl, GBCFactory.createGBC(0, 10, 1, 1, 1, 1, GridBagConstraints.BOTH, 15));
        this.add(fctPnl, GBCFactory.createGBC(0, 80, 1, 2, 1, 1, GridBagConstraints.BOTH, 15));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        manageGUI(btnCreate, btnDefineBG);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            tbMdlEWSD.setData(null);
            tbMdlDSLAM.setData(null);
            tbMdlCarrier.setData(null);
            tbMdlSDH.setData(null);
            tbMdlPDHIn.setData(null);
            tbMdlPDHOut.setData(null);
            ptParent = null;
            ptChild = null;

            ISimpleFindService sfs = getCCService(QueryCCService.class);
            List<ProduktDtag> rangSSTypes = sfs.findAll(ProduktDtag.class);
            cbRangSSType.addItems(rangSSTypes, true, ProduktDtag.class, true);

            Reference refEx = new Reference();
            refEx.setType(Reference.REF_TYPE_EQ_UETV);
            cbUETV.addItems(EnumSet.allOf(Uebertragungsverfahren.class), true, Uebertragungsverfahren.class, true);

            PhysikService ps = getCCService(PhysikService.class);
            if (rangierungsAuftrag.getPhysiktypParent() != null) {
                ptParent = ps.findPhysikTyp(rangierungsAuftrag.getPhysiktypParent());
            }
            if (rangierungsAuftrag.getPhysiktypChild() != null) {
                ptChild = ps.findPhysikTyp(rangierungsAuftrag.getPhysiktypChild());
            }

            validateEquipments();
            loadEquipments4EWSD();
            loadEquipments4DSLAM();
            loadEquipments4Carrier();
            if (useSDHPDH) {
                loadEquipments4Table(tbMdlSDH, Equipment.HW_SCHNITTSTELLE_SDH, new String[] { "hwEQN" });
                loadEquipments4Table(tbMdlPDHIn, Equipment.HW_SCHNITTSTELLE_PDH_IN, new String[] { "hwEQN" });
                loadEquipments4Table(tbMdlPDHOut, Equipment.HW_SCHNITTSTELLE_PDH_OUT, new String[] { "rangLeiste1", "rangStift1" });
                cbRangType.setSelectedItem(TYPE_SDH_PDH);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void loadEquipments4Table(EquipmentWithBaugruppentypTableModel tableModel, String type, String[] orderParams) throws HurricanGUIException {
        try {
            List<Equipment> eqs = loadEquipments(type, orderParams);
            tableModel.setData(eqs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Beim Laden der Equipments " + type + " ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    private void loadEquipments4Table(AKTableModel<Equipment> tableModel, String type, String[] orderParams) throws HurricanGUIException {
        try {
            List<Equipment> eqs = loadEquipments(type, orderParams);
            tableModel.setData(eqs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Beim Laden der Equipments " + type + " ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    private List<Equipment> loadEquipments(String type, String[] orderParams) throws ServiceNotFoundException, FindException {
        Equipment example = new Equipment();
        example.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
        example.setStatus(EqStatus.frei);
        example.setHwSchnittstelle(type);
        RangierungsService rs = getCCService(RangierungsService.class);
        List<Equipment> eqs = rs.findEquipments(example, orderParams);
        return eqs;
    }

    /* Laedt die Equipments der EWSD-Seite. */
    private void loadEquipments4EWSD() throws HurricanGUIException {
        if (!useEWSD) { return; }
        try {
            Equipment example = new Equipment();
            example.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
            example.setStatus(EqStatus.frei);
            if ((ptChild != null) && ptChild.isPhone()) {
                example.setHwSchnittstelle(ptChild.getHwSchnittstelle());
            }
            else if (ptParent.isPhone()) {
                example.setHwSchnittstelle(ptParent.getHwSchnittstelle());
            }

            if (StringUtils.isBlank(example.getHwSchnittstelle())) {
                throw new HurricanGUIException("Filter fuer HW_SCHNITTSTELLE ist nicht definiert!");
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            List<Equipment> eqs = rs.findEquipments(example, "hwEQN");
            tbMdlEWSD.setData(eqs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Beim Laden der Equipments der EWSD-Seite ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /* Laedt die Equipments der DSLAM-Seite. */
    private void loadEquipments4DSLAM() throws HurricanGUIException {
        if (!useDSLAM) { return; }
        try {
            RangierungsService rs = getCCService(RangierungsService.class);

            Equipment example = new Equipment();
            example.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
            example.setStatus(EqStatus.frei);

            if ((ptChild != null) && ptChild.isPhone()) {
                dslamEqInOutView = true;
                example.setHwSchnittstelle(ptParent.getHwSchnittstelle());

                List<EquipmentInOutView> eqs = rs.findEqInOutViews(example);
                tbMdlDSLAM.setData(eqs);
            }
            else {
                dslamEqInOutView = false;
                example.setHwSchnittstelle(ptParent.getHwSchnittstelle());

                List<Equipment> eqs = rs.findEquipments(example, "hwEQN");
                tbMdlDSLAM.setData(eqs);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Beim Laden der Equipments der DSLAM-Seite ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /* Laedt die Equipments der Carrier-Seite. */
    private void loadEquipments4Carrier() throws HurricanGUIException {
        if (!useCarrier) { return; }
        try {
            Equipment example = new Equipment();
            example.setHvtIdStandort(rangierungsAuftrag.getHvtStandortId());
            example.setStatus(EqStatus.frei);
            if (useSDHPDH) {
                example.setVerwendung(EqVerwendung.CONNECT);
            }
            else {
                if ((ptChild != null) && ptChild.isPhone()) {
                    example.setRangSchnittstelle(RangSchnittstelle.H);
                }
                else {
                    if (ptParent.isPhone()) {
                        example.setRangSchnittstelle(RangSchnittstelle.N);
                    }
                    else {
                        example.setRangSchnittstelle(RangSchnittstelle.H);
                    }
                }

                if (example.getRangSchnittstelle() == null) {
                    throw new HurricanGUIException("Filter fuer RANG_SCHNITTSTELLE (N or H) ist nicht definiert!");
                }
            }

            RangierungsService rs = getCCService(RangierungsService.class);
            List<Equipment> eqs = rs.findEquipments(example, "rangBucht", "rangLeiste1", "rangStift1");
            if (!useSDHPDH) {
                CollectionUtils.filter(eqs, new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        return ((Equipment) object).getVerwendung() != EqVerwendung.CONNECT;
                    }
                });
            }
            tbMdlCarrier.setData(eqs);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new HurricanGUIException(
                    "Beim Laden der Equipments der Carrier-Seite ist ein Fehler aufgetreten: " + e.getMessage(), e);
        }
    }

    /*
     * Abhaengig vom Rangierungs-Auftrag bzw. den Physiktypen wird ermittelt, welche
     * Equipments benoetigt werden.
     * - zwei Physiktypen --> alle Equipments laden
     * - ein Physiktyp
     *    - ist nicht 2H/4H  --> DSLAM sperren, EWSD u. Carrier laden
     *    - ist 2H oder 4H   --> EWSD u. DSLAM sperren, Carrier laden
     *    - ist DSLonly      --> EWSD sperren, DSLAM u. Carrier laden
     */
    private void validateEquipments() {
        useCarrier = false;
        useDSLAM = false;
        useEWSD = false;
        useSDHPDH = false;
        try {
            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            boolean[] validated = ras.validateNeededEquipments(rangierungsAuftrag);
            useEWSD = validated[0];
            useDSLAM = validated[1];
            useCarrier = validated[2];
            useSDHPDH = validated[3];
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
        if ("create.rangierungen".equals(command)) {
            createRangierungen();
        }
        else if ("reset.rangierungen".equals(command)) {
            resetRangierung();
        }
        else if ("add.rangierung".equals(command)) {
            addRangierung();
        }
        else if ("define.bg".equals(command)) {
            defineBG();
        }
        else if ("refresh".equals(command)) {
            loadData();
        }
        else if ("eq.rang.type".equals(command)) {
            switchRangType();
        }
    }

    private void resetRangierung() {
        rangierungen.clear();
        usedEqIDs.clear();
        tbMdlCarrier.enableAllRows();
        tbMdlDSLAM.enableAllRows();
        tbMdlEWSD.enableAllRows();
        tbMdlPDHIn.enableAllRows();
        tbMdlPDHOut.enableAllRows();
        tbMdlSDH.enableAllRows();
        loadData();
    }

    private void switchRangType() {
        String selection = (String) cbRangType.getSelectedItem();
        if (TYPE_DSL_TELEFONIE.equals(selection)) {
            eqPnlPDH.setVisible(false);
            eqPnlDSL.setVisible(true);
        }
        else if (TYPE_SDH_PDH.equals(selection)) {
            eqPnlDSL.setVisible(false);
            eqPnlPDH.setVisible(true);
        }
    }

    private void addRangierung() {
        String selection = (String) cbRangType.getSelectedItem();
        if (TYPE_DSL_TELEFONIE.equals(selection)) {
            addDSLRangierung();
        }
        else if (TYPE_SDH_PDH.equals(selection)) {
            addPDHRangierung();
        }
    }

    private void addDSLRangierung() {
        List<Long> eqIdsEWSD = (useEWSD) ? getIdValuesFromSelected(tbEWSD) : null;
        List<Long> eqIdsCarrier = (useCarrier) ? getIdValuesFromSelected(tbCarrier) : null;
        List<Long> eqIdsDSLAMIn = null;
        List<Long> eqIdsDSLAMOut = null;
        if (useDSLAM) {
            if (dslamEqInOutView) {
                int[] rows = tbDSLAM.getSelectedRows();
                eqIdsDSLAMIn = new ArrayList<Long>();
                eqIdsDSLAMOut = new ArrayList<Long>();
                for (int row : rows) {
                    Object obj = ((AKTableModel<?>) tbDSLAM.getModel()).getDataAtRow(row);
                    if (obj instanceof EquipmentWithBaugruppenTypView) {
                        EquipmentWithBaugruppenTypView view = (EquipmentWithBaugruppenTypView) obj;
                        eqIdsDSLAMIn.add(view.getEqID());
                        eqIdsDSLAMOut.add(view.getEqIDOut());
                    }
                }
            }
            else {
                eqIdsDSLAMOut = getIdValuesFromSelected(tbDSLAM);
            }
        }

        if (useDSLAM && CollectionTools.isEmpty(eqIdsDSLAMOut)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Keine DSLAM Ports ausgewählt!", null, true);
            return;
        }

        if (useEWSD && CollectionTools.isEmpty(eqIdsEWSD)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Keine EWSD Ports ausgewählt!", null, true);
            return;
        }

        if (useCarrier && CollectionTools.isEmpty(eqIdsCarrier)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Keine Carrier Ports ausgewählt!", null, true);
            return;
        }

        if (CollectionUtils.containsAny(usedEqIDs, eqIdsCarrier)
                || ((eqIdsEWSD != null) && CollectionUtils.containsAny(usedEqIDs, eqIdsEWSD))
                || ((eqIdsDSLAMIn != null) && CollectionUtils.containsAny(usedEqIDs, eqIdsDSLAMIn))
                || ((eqIdsDSLAMOut != null) && CollectionUtils.containsAny(usedEqIDs, eqIdsDSLAMOut))) {
            MessageHelper.showInfoDialog(getMainFrame(), "Sie können keine Equipments mehreren Rangierungen zuordnen", null, true);
            return;
        }

        int eqIdsCarrierSize = (eqIdsCarrier != null) ? eqIdsCarrier.size() : -1;
        if (((eqIdsEWSD != null) && (eqIdsEWSD.size() != eqIdsEWSD.size()))
                || ((eqIdsDSLAMIn != null) && (eqIdsDSLAMIn.size() != eqIdsCarrierSize))
                || ((eqIdsDSLAMOut != null) && (eqIdsDSLAMOut.size() != eqIdsCarrierSize))) {
            MessageHelper.showInfoDialog(getMainFrame(), "Die Anzahl der definierten Equipments stimmt nicht ueberein!", null, true);
            return;
        }

        String rangSSType = (String) cbRangSSType.getSelectedItemValue("getRangSsType", String.class);
        String uetv = (cbUETV.getSelectedItem() instanceof Uebertragungsverfahren)
                ? ((Uebertragungsverfahren) cbUETV.getSelectedItem()).name() : null;

        if ((rangSSType == null) || (uetv == null)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Rang-SS-Typ und UETV müssen definiert sein", null, true);
            return;
        }

        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsEWSD);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsCarrier);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsDSLAMIn);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsDSLAMOut);

        hideSelected(tbCarrier, eqIdsCarrier, "id");
        hideSelected(tbEWSD, eqIdsEWSD, "eqID");
        hideSelected(tbDSLAM, eqIdsDSLAMIn, "eqID");

        // Rangierung vormerken zum speichern
        Map<String, Object> rangierung = new HashMap<String, Object>();
        rangierung.put(TYPE, TYPE_DSL_TELEFONIE);
        rangierung.put(UETV2, uetv);
        rangierung.put(RANG_SS_TYPE, rangSSType);
        rangierung.put(EQ_IDS_EWSD, eqIdsEWSD);
        rangierung.put(EQ_IDS_CARRIER, eqIdsCarrier);
        rangierung.put(EQ_IDS_DSLAM_IN, eqIdsDSLAMIn);
        rangierung.put(EQ_IDS_DSLAM_OUT, eqIdsDSLAMOut);
        rangierungen.add(rangierung);
    }

    private void hideSelected(AKJTable table, List<Long> ids, String idProperty) {
        if ((ids == null) || ids.isEmpty()) {
            return;
        }
        AKReflectionTableModel<?> model = (AKReflectionTableModel<?>) table.getModel();
        for (Long id : ids) {
            model.disableRow(model.findRow(idProperty, id));
        }
        table.clearSelection();
    }

    private void addPDHRangierung() {
        List<Long> eqIdsCarrier = getIdValuesFromSelected(tbCarrierPDH);
        List<Long> eqIdsSDH = getIdValuesFromSelected(tbSDH);
        List<Long> eqIdsPDHOut = getIdValuesFromSelected(tbPDHOut);
        List<Long> eqIdsPDHIn = getIdValuesFromSelected(tbPDHIn);

        if (CollectionUtils.containsAny(usedEqIDs, eqIdsCarrier) || CollectionUtils.containsAny(usedEqIDs, eqIdsSDH) ||
                CollectionUtils.containsAny(usedEqIDs, eqIdsPDHOut) || CollectionUtils.containsAny(usedEqIDs, eqIdsPDHIn)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Sie können keine Equipments mehreren Rangierungen zuordnen", null, true);
            return;
        }

        int carrierSize = eqIdsCarrier.size();
        int sdhSize = eqIdsSDH.size();
        if ((carrierSize != eqIdsPDHIn.size()) || (eqIdsPDHOut.size() != sdhSize)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Die Anzahl der definierten Equipments stimmt nicht ueberein!", null, true);
            return;
        }
        if (((carrierSize == 4) && (sdhSize == 3)) || (sdhSize > carrierSize)) {
            MessageHelper.showInfoDialog(getMainFrame(), "Die Anzahl der definierten Equipments stimmt nicht ueberein!", null, true);
            return;
        }

        String rangSSType = (String) cbRangSSType.getSelectedItemValue("getRangSsType", String.class);
        Uebertragungsverfahren uetv = (Uebertragungsverfahren) cbUETV.getSelectedItem();

        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsSDH);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsCarrier);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsPDHOut);
        CollectionTools.addAllIgnoreNull(usedEqIDs, eqIdsPDHIn);

        hideSelected(tbCarrierPDH, eqIdsCarrier, "id");
        hideSelected(tbSDH, eqIdsSDH, "eqID");
        hideSelected(tbPDHOut, eqIdsPDHOut, "id");
        hideSelected(tbPDHIn, eqIdsPDHIn, "eqID");

        // Rangierung vormerken zum speichern
        Map<String, Object> rangierung = new HashMap<String, Object>();
        rangierung.put(TYPE, TYPE_SDH_PDH);
        rangierung.put(UETV2, uetv);
        rangierung.put(RANG_SS_TYPE, rangSSType);
        rangierung.put(EQ_IDS_SDH, eqIdsSDH);
        rangierung.put(EQ_IDS_PDH_OUT, eqIdsPDHOut);
        rangierung.put(EQ_IDS_PDH_IN, eqIdsPDHIn);
        rangierung.put(EQ_IDS_CARRIER, eqIdsCarrier);
        rangierungen.add(rangierung);
    }

    /*
     * Erzeugt aus den selektierten Equipments die entsprechenden Rangierungen. <br>
     */
    private void createRangierungen() {
        try {
            setWaitCursor();
            showProgressBar("Rangierungen definieren...");

            if (!checkPortCount()) {
                MessageHelper.showInfoDialog(getMainFrame(), "Die Anzahl der Equipments entspricht nicht der vorgegebenen Port-Anzahl.", null, true);
                return;
            }

            // Baugruppen ermitteln und pruefen ob eingebaut; falls nicht: IA-Nummerndialog oeffnen
            String iaNumber = null;

            RangierungAdminService ras = getCCService(RangierungAdminService.class);
            AKMessages messages = new AKMessages();
            for (Map<String, Object> rangierung : rangierungen) {
                Uebertragungsverfahren uetv = (rangierung.get(UETV2) instanceof Uebertragungsverfahren)
                        ? (Uebertragungsverfahren) rangierung.get(UETV2) : Uebertragungsverfahren.valueOf((String) rangierung.get(UETV2));
                String rangSSType = (String) rangierung.get(RANG_SS_TYPE);
                if (TYPE_DSL_TELEFONIE.equals(rangierung.get(TYPE))) {
                    if (iaNumber == null) {
                        iaNumber = createIANumber(usedEqIDs);
                    }
                    List<Long> eqIdsEWSD = (List<Long>) rangierung.get(EQ_IDS_EWSD);
                    List<Long> eqIdsCarrier = (List<Long>) rangierung.get(EQ_IDS_CARRIER);
                    List<Long> eqIdsDSLAMIn = (List<Long>) rangierung.get(EQ_IDS_DSLAM_IN);
                    List<Long> eqIdsDSLAMOut = (List<Long>) rangierung.get(EQ_IDS_DSLAM_OUT);

                    messages.addAKMessages(ras.createRangierungen(
                            rangierungsAuftrag.getId(), eqIdsEWSD, eqIdsCarrier, eqIdsDSLAMIn,
                            eqIdsDSLAMOut, rangSSType, uetv, iaNumber, HurricanSystemRegistry.instance().getSessionId()));
                }
                else if (TYPE_SDH_PDH.equals(rangierung.get(TYPE))) {
                    List<Long> eqIdsSDH = (List<Long>) rangierung.get(EQ_IDS_SDH);
                    List<Long> eqIdsCarrier = (List<Long>) rangierung.get(EQ_IDS_CARRIER);
                    List<Long> eqIdsPDHIn = (List<Long>) rangierung.get(EQ_IDS_PDH_IN);
                    List<Long> eqIdsPDHOut = (List<Long>) rangierung.get(EQ_IDS_PDH_OUT);

                    messages.addAKMessages(ras.createSDHPDHRangierung(
                            rangierungsAuftrag.getId(), eqIdsSDH, eqIdsCarrier, eqIdsPDHIn,
                            eqIdsPDHOut, rangSSType, uetv, HurricanSystemRegistry.instance().getSessionId()));
                }
            }

            String msgTxt = null;
            if (messages.isNotEmpty()) {
                msgTxt = messages.getMessagesAsText();
            }

            StringBuilder info = new StringBuilder("Die Rangierungen wurden erzeugt.");
            if (msgTxt != null) {
                info.append(SystemUtils.LINE_SEPARATOR);
                info.append("Informationen / Warnungen:").append(SystemUtils.LINE_SEPARATOR);
                info.append(msgTxt);
            }
            MessageHelper.showInfoDialog(getMainFrame(), info.toString(), null, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            stopProgressBar();
            setDefaultCursor();
        }
    }

    private boolean checkPortCount() {
        List<Long> carrierEqIDs = new ArrayList<Long>();
        for (Map<String, Object> rang : rangierungen) {
            carrierEqIDs.addAll((Collection<Long>) rang.get(EQ_IDS_CARRIER));
        }
        return (carrierEqIDs.size() == rangierungsAuftrag.getAnzahlPorts());
    }

    /*
     * Ueberprueft, ob Baugruppen noch nicht eingebaut sind.
     * Ist dies der Fall, wird eine IA-Nummer ueber einen Dialog generiert
     * und diese zurueck gegeben.
     * @param usedEqIDs Liste mit den fuer die Rangierungen zu verwendenden Equipment-IDs
     * @return
     */
    private String createIANumber(List<Long> usedEqIDs) {
        try {
            RangierungsService rs = getCCService(RangierungsService.class);
            HWService hws = getCCService(HWService.class);

            Map<Long, Object> bgIDMap = new HashMap<Long, Object>();

            for (Long eqId : usedEqIDs) {
                Equipment eq = rs.findEquipment(eqId);
                if (eq.getHwBaugruppenId() != null) {
                    bgIDMap.put(eq.getHwBaugruppenId(), null);
                }
            }

            boolean createIA = false;
            Iterator<Long> bgIdIt = bgIDMap.keySet().iterator();
            while (bgIdIt.hasNext()) {
                Long bgId = bgIdIt.next();
                HWBaugruppe hwBaugruppe = hws.findBaugruppe(bgId);
                if (!BooleanTools.nullToFalse(hwBaugruppe.getEingebaut())) {
                    createIA = true;
                    break;
                }
            }

            if (createIA) {
                CounterService cs = getCCService(CounterService.class);
                Integer count = cs.getNewIntValue("innenauftrag");

                String iaCounter = "" + count;
                iaCounter = StringTools.fillToSize(iaCounter, IA.NUMBER_LENGTH_BETRIEBSRAUM, '0', true);

                IAServiceRoomDefinitionDialog dlg = new IAServiceRoomDefinitionDialog(iaCounter);
                Object resultArray = DialogHelper.showDialog(getMainFrame(), dlg, true, true);

                if (resultArray instanceof String[]) {
                    return ((String[]) resultArray)[0];
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /*
     * Oeffnet einen Dialog, ueber den die noch nicht definierten EWSD-Baugruppen
     * einem Typ (UK0 oder AB) zugewiesen werden koennen.
     */
    private void defineBG() {
        try {
            RangierungDefineEWSDBGDialog dlg =
                    new RangierungDefineEWSDBGDialog(rangierungsAuftrag.getHvtStandortId());
            DialogHelper.showDialog(this, dlg, true, true);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /* Ermittelt eine Liste mit den Equipment-IDs der selektierten Objekte. */
    private List<Long> getIdValuesFromSelected(AKJTable table) {
        int[] rows = table.getSelectedRows();
        List<Long> ids = new ArrayList<Long>();
        for (int row : rows) {
            Object obj = ((AKTableModel<?>) table.getModel()).getDataAtRow(row);
            if (obj instanceof Equipment) {
                ids.add(((Equipment) obj).getId());
            }
            else if (obj instanceof EquipmentWithBaugruppenTypView) {
                ids.add(((EquipmentWithBaugruppenTypView) obj).getEqID());
            }
        }

        return ids;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /*
     * TableModel fuer die DSLAM-Equipments. Abhaengig von den uebergebenen
     * Daten wird als Bean-Klasse Equipment oder EquipmentInOutView verwendet.
     */
    class EquipmentWithBaugruppentypTableModel extends AKReflectionTableModel<EquipmentWithBaugruppenTypView> {
        /**
         * @param beanClass
         * @param columnNames
         * @param propertyNames
         * @param classTypes
         */
        public EquipmentWithBaugruppentypTableModel() {
            super(
                    new String[] { "HW_EQN", "Baugruppentyp" },
                    new String[] { "hwEQN", "bgType" },
                    new Class[] { String.class, String.class });
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void setData(Collection data) {
            List<EquipmentWithBaugruppenTypView> result = new ArrayList<EquipmentWithBaugruppenTypView>();
            if (CollectionTools.isNotEmpty(data)) {
                try {
                    RangierungsService rs = getCCService(RangierungsService.class);
                    HWService hwService = getCCService(HWService.class);
                    for (Object value : data) {
                        Equipment equipment = null;
                        if (value instanceof Equipment) {
                            equipment = (Equipment) value;
                        }
                        else if (value instanceof EquipmentInOutView) {
                            equipment = rs.findEquipment(((EquipmentInOutView) value).getEqIdIn());
                        }
                        if (equipment != null) {
                            HWBaugruppe bg = hwService.findBaugruppe(equipment.getHwBaugruppenId());
                            EquipmentWithBaugruppenTypView view = new EquipmentWithBaugruppenTypView(equipment.getHwEQN(),
                                    bg.getHwBaugruppenTyp().getName(), equipment.getId());
                            if (value instanceof EquipmentInOutView) {
                                view.eqIDOut = ((EquipmentInOutView) value).getEqIdOut();
                            }
                            result.add(view);
                        }
                    }
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
            super.setData(result);
        }
    }

    public static class EquipmentWithBaugruppenTypView {
        private final String hwEQN;
        private final String bgType;
        private final Long eqID;
        private Long eqIDOut;

        public EquipmentWithBaugruppenTypView(String hwEQN, String bgType, Long eqID) {
            this.hwEQN = hwEQN;
            this.bgType = bgType;
            this.eqID = eqID;
        }

        public String getHwEQN() {
            return hwEQN;
        }

        public String getBgType() {
            return bgType;
        }

        public Long getEqID() {
            return eqID;
        }

        public Long getEqIDOut() {
            return eqIDOut;
        }
    }

    /* Listener fuer die Tabellen, um die Anzahl der selektierten Zeilen in einem Label anzuzeigen. */
    class EqTableSelectionChangeListener implements ListSelectionListener {
        private AKJTable table = null;

        /**
         * Default-Const.
         */
        public EqTableSelectionChangeListener(AKJTable table) {
            super();
            this.table = table;
        }

        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int count = table.getSelectedRowCount();
            String countTxt = "" + count;
            if (table == tbEWSD) {
                lblCountEWSD.setText(countTxt);
            }
            else if (table == tbDSLAM) {
                lblCountDSLAM.setText(countTxt);
            }
            else if (table == tbCarrier) {
                lblCountCarrier.setText(countTxt);
            }
            else if (table == tbSDH) {
                lblCountSDH.setText(countTxt);
            }
            else if (table == tbPDHIn) {
                lblCountPDHIn.setText(countTxt);
            }
            else if (table == tbPDHOut) {
                lblCountPDHOut.setText(countTxt);
            }
            else if (table == tbCarrierPDH) {
                lblCountCarrierPDH.setText(countTxt);
            }
        }
    }
}


