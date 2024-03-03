/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.02.2010 14:56:28
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKModelOwner;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSaveableAware;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJOptionDialog;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKReferenceField;
import de.augustakom.common.gui.swing.AKReferenceFieldEvent;
import de.augustakom.common.gui.swing.AKReferenceFieldObserver;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.auftrag.shared.AuftragHousingKeyTableModel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.CCAddress;
import de.augustakom.hurrican.model.cc.housing.HousingBuilding;
import de.augustakom.hurrican.model.cc.housing.HousingFloor;
import de.augustakom.hurrican.model.cc.housing.HousingRoom;
import de.augustakom.hurrican.model.cc.view.AuftragHousingKeyView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.HousingService;


/**
 * Auftrags-Panel fuer Housing-Daten.
 *
 *
 */
public class AuftragHousingPanel extends AbstractAuftragPanel implements AKModelOwner,
        AKSaveableAware, AKDataLoaderComponent, AKReferenceFieldObserver, ItemListener, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragHousingPanel.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragHousingPanel.xml";

    private static final String WATCH_AUFTRAG_HOUSING = "auftrag.housing";

    private static final String TITLE_CIRCUIT = "title.circuit";
    private static final String TITLE_BUILDING = "title.building";
    private static final String TITLE_KEYS = "title.keys";

    private static final String COUNTER = "counter";
    private static final String COUNTER_NUMBER = "counter.number";
    private static final String COUNTER_START = "counter.start";
    private static final String COUNTER_END = "counter.end";

    private static final String COUNTER_2 = "counter2";
    private static final String COUNTER_2_NUMBER = "counter2.number";
    private static final String COUNTER_2_START = "counter2.start";
    private static final String COUNTER_2_END = "counter2.end";

    private static final String COUNTER_3 = "counter3";
    private static final String COUNTER_3_NUMBER = "counter3.number";
    private static final String COUNTER_3_START = "counter3.start";
    private static final String COUNTER_3_END = "counter3.end";

    private static final String COUNTER_4 = "counter4";
    private static final String COUNTER_4_NUMBER = "counter4.number";
    private static final String COUNTER_4_START = "counter4.start";
    private static final String COUNTER_4_END = "counter4.end";

    private static final String SAFEGUARD = "safeguard";
    private static final String CIRCUIT_CAPACITY = "circuit.capacity";
    private static final String CIRCUIT_COUNT = "circuit.count";
    private static final String RACK_UNITS = "rack.units";
    private static final String RACK = "rack";
    private static final String PARCEL = "parcel";
    private static final String ROOM = "room";
    private static final String FLOOR = "floor";
    private static final String BUILDING = "building";
    private static final String BUILDING_STREET = "building.street";
    private static final String BUILDING_CITY = "building.city";

    private static final String TF_COUNTER_NUMBER = "tfCounterNumber";
    private static final String TF_COUNTER_START = "tfCounterStart";
    private static final String TF_COUNTER_END = "tfCounterEnd";

    private static final Integer CIRCUIT_FIELD_COUNT = 4;
    private static final Boolean ENABLED = Boolean.TRUE;
    private static final Boolean DISABLED = Boolean.FALSE;

    private static final String ADD_KEY = "add.key";
    private static final String REMOVE_KEY = "remove.key";
    private static final long serialVersionUID = 1917991875147110337L;

    private AuftragHousingKeyTableModel auftragHousingKeyTableModel;


    // GUI Objekte
    private AKReferenceField rfBuilding;
    private AKJTextField tfBuildingStreet;
    private AKJTextField tfBuildingCity;
    private AKReferenceField rfFloor;
    private AKReferenceField rfRoom;
    private AKReferenceField rfParcel;
    private AKJTextField tfRack;
    private AKJFormattedTextField tfRackUnits;
    private AKJComboBox cbCircuitCount;
    private AKJComboBox cbCapacity;
    private AKJFormattedTextField tfSafeguard;

    private AKJFormattedTextField tfCounterNumber;
    private AKJFormattedTextField tfCounterStart;
    private AKJFormattedTextField tfCounterEnd;

    private AKJFormattedTextField tfCounterNumber2;
    private AKJFormattedTextField tfCounterStart2;
    private AKJFormattedTextField tfCounterEnd2;

    private AKJFormattedTextField tfCounterNumber3;
    private AKJFormattedTextField tfCounterStart3;
    private AKJFormattedTextField tfCounterEnd3;

    private AKJFormattedTextField tfCounterNumber4;
    private AKJFormattedTextField tfCounterStart4;
    private AKJFormattedTextField tfCounterEnd4;

    private AKJTable auftragHousingKeyTable;

    private final List<AKManageableComponent> manageableComponents = new ArrayList<>();

    // Modelle
    private CCAuftragModel auftragModel = null;
    private AuftragHousing auftragHousing = null;

    // Services
    private HousingService housingService;


    /**
     * Default-Const.
     */
    public AuftragHousingPanel() {
        super(RESOURCE);
        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        AKJLabel lblBuilding = getSwingFactory().createLabel(BUILDING);
        AKJLabel lblBuildingStreet = getSwingFactory().createLabel(BUILDING_STREET);
        AKJLabel lblBuildingCity = getSwingFactory().createLabel(BUILDING_CITY);
        AKJLabel lblFloor = getSwingFactory().createLabel(FLOOR);
        AKJLabel lblRoom = getSwingFactory().createLabel(ROOM);
        AKJLabel lblParcel = getSwingFactory().createLabel(PARCEL);
        AKJLabel lblRack = getSwingFactory().createLabel(RACK);
        AKJLabel lblRackUnits = getSwingFactory().createLabel(RACK_UNITS);
        AKJLabel lblCircuitCount = getSwingFactory().createLabel(CIRCUIT_COUNT);
        AKJLabel lblCapacity = getSwingFactory().createLabel(CIRCUIT_CAPACITY);
        AKJLabel lblSafeguard = getSwingFactory().createLabel(SAFEGUARD);

        AKJLabel lblCounter = getSwingFactory().createLabel(COUNTER, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblCounter2 = getSwingFactory().createLabel(COUNTER_2, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblCounter3 = getSwingFactory().createLabel(COUNTER_3, AKJLabel.LEFT, Font.BOLD);
        AKJLabel lblCounter4 = getSwingFactory().createLabel(COUNTER_4, AKJLabel.LEFT, Font.BOLD);

        AKJLabel lblCounterNumber = getSwingFactory().createLabel(COUNTER_NUMBER);
        AKJLabel lblCounterStart = getSwingFactory().createLabel(COUNTER_START);
        AKJLabel lblCounterEnd = getSwingFactory().createLabel(COUNTER_END);

        AKJLabel lblCounterNumber2 = getSwingFactory().createLabel(COUNTER_2_NUMBER);
        AKJLabel lblCounterStart2 = getSwingFactory().createLabel(COUNTER_2_START);
        AKJLabel lblCounterEnd2 = getSwingFactory().createLabel(COUNTER_2_END);

        AKJLabel lblCounterNumber3 = getSwingFactory().createLabel(COUNTER_3_NUMBER);
        AKJLabel lblCounterStart3 = getSwingFactory().createLabel(COUNTER_3_START);
        AKJLabel lblCounterEnd3 = getSwingFactory().createLabel(COUNTER_3_END);

        AKJLabel lblCounterNumber4 = getSwingFactory().createLabel(COUNTER_4_NUMBER);
        AKJLabel lblCounterStart4 = getSwingFactory().createLabel(COUNTER_4_START);
        AKJLabel lblCounterEnd4 = getSwingFactory().createLabel(COUNTER_4_END);

        rfBuilding = getSwingFactory().createReferenceField(BUILDING);
        rfBuilding.addObserver(this);
        tfBuildingStreet = getSwingFactory().createTextField(BUILDING_STREET, false);
        tfBuildingCity = getSwingFactory().createTextField(BUILDING_CITY, false);
        rfFloor = getSwingFactory().createReferenceField(FLOOR);
        rfFloor.addObserver(this);
        rfRoom = getSwingFactory().createReferenceField(ROOM);
        rfRoom.addObserver(this);
        rfParcel = getSwingFactory().createReferenceField(PARCEL);
        rfParcel.addObserver(this);
        tfRack = getSwingFactory().createTextField(RACK);
        tfRackUnits = getSwingFactory().createFormattedTextField(RACK_UNITS);
        cbCircuitCount = getSwingFactory().createComboBox(CIRCUIT_COUNT);
        cbCapacity = getSwingFactory().createComboBox(CIRCUIT_CAPACITY);
        tfSafeguard = getSwingFactory().createFormattedTextField(SAFEGUARD);

        tfCounterNumber = getSwingFactory().createFormattedTextField(COUNTER_NUMBER);
        tfCounterStart = getSwingFactory().createFormattedTextField(COUNTER_START);
        tfCounterEnd = getSwingFactory().createFormattedTextField(COUNTER_END);

        tfCounterNumber2 = getSwingFactory().createFormattedTextField(COUNTER_2_NUMBER);
        tfCounterStart2 = getSwingFactory().createFormattedTextField(COUNTER_2_START);
        tfCounterEnd2 = getSwingFactory().createFormattedTextField(COUNTER_2_END);

        tfCounterNumber3 = getSwingFactory().createFormattedTextField(COUNTER_3_NUMBER);
        tfCounterStart3 = getSwingFactory().createFormattedTextField(COUNTER_3_START);
        tfCounterEnd3 = getSwingFactory().createFormattedTextField(COUNTER_3_END);

        tfCounterNumber4 = getSwingFactory().createFormattedTextField(COUNTER_4_NUMBER);
        tfCounterStart4 = getSwingFactory().createFormattedTextField(COUNTER_4_START);
        tfCounterEnd4 = getSwingFactory().createFormattedTextField(COUNTER_4_END);
        cbCircuitCount.addItemListener(this);

        AKJButton btnAddKey = getSwingFactory().createButton(ADD_KEY, getActionListener(), null);
        AKJButton btnRemoveKey = getSwingFactory().createButton(REMOVE_KEY, getActionListener(), null);

        // @formatter:off
        // Gebaeude
        AKJPanel bdPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(TITLE_BUILDING));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(lblBuilding       , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(rfBuilding        , GBCFactory.createGBC(  0,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 4, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(lblFloor          , GBCFactory.createGBC(  0,  0, 5, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 6, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(rfFloor           , GBCFactory.createGBC(  0,  0, 7, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0, 8, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(lblRoom           , GBCFactory.createGBC(  0,  0, 9, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0,10, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(rfRoom            , GBCFactory.createGBC(  0,  0,11, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0,12, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(lblParcel         , GBCFactory.createGBC(  0,  0,13, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(  0,  0,14, 1, 1, 1, GridBagConstraints.NONE));
        bdPnl.add(rfParcel          , GBCFactory.createGBC(  0,  0,15, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(lblBuildingStreet , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(tfBuildingStreet  , GBCFactory.createGBC(  0,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(lblBuildingCity   , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(tfBuildingCity    , GBCFactory.createGBC(  0,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(lblRack           , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(tfRack            , GBCFactory.createGBC(  0,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(lblRackUnits      , GBCFactory.createGBC(  0,  0, 5, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(tfRackUnits       , GBCFactory.createGBC(  0,  0, 7, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bdPnl.add(new AKJPanel()    , GBCFactory.createGBC(100,100,16, 5, 1, 1, GridBagConstraints.BOTH));

        // Stromkreis
        AKJPanel cirPnl = new AKJPanel(new GridBagLayout(), getSwingFactory().getText(TITLE_CIRCUIT));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        cirPnl.add(lblCircuitCount  , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(cbCircuitCount   , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCapacity      , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(cbCapacity       , GBCFactory.createGBC(  0,  0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblSafeguard     , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfSafeguard      , GBCFactory.createGBC(  0,  0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 6, 4, 1, 1, GridBagConstraints.BOTH));

        cirPnl.add(lblCounter       , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterNumber , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterNumber  , GBCFactory.createGBC(  0,  0, 2, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterStart  , GBCFactory.createGBC(  0,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterStart   , GBCFactory.createGBC(  0,  0, 4, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterEnd    , GBCFactory.createGBC(  0,  0, 5, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterEnd     , GBCFactory.createGBC(  0,  0, 6, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 6, 7, 1, 1, GridBagConstraints.BOTH));

        cirPnl.add(lblCounter2      , GBCFactory.createGBC(  0,  0, 1, 8, 3, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterNumber2, GBCFactory.createGBC(  0,  0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterNumber2 , GBCFactory.createGBC(  0,  0, 2, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterStart2 , GBCFactory.createGBC(  0,  0, 3, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterStart2  , GBCFactory.createGBC(  0,  0, 4, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterEnd2   , GBCFactory.createGBC(  0,  0, 5, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterEnd2    , GBCFactory.createGBC(  0,  0, 6, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 6, 10, 1, 1, GridBagConstraints.BOTH));

        cirPnl.add(lblCounter3      , GBCFactory.createGBC(  0,  0, 1, 11, 3, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterNumber3, GBCFactory.createGBC(  0,  0, 1, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterNumber3 , GBCFactory.createGBC(  0,  0, 2, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterStart3 , GBCFactory.createGBC(  0,  0, 3, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterStart3  , GBCFactory.createGBC(  0,  0, 4, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterEnd3   , GBCFactory.createGBC(  0,  0, 5, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterEnd3    , GBCFactory.createGBC(  0,  0, 6, 12, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 6, 13, 1, 1, GridBagConstraints.BOTH));

        cirPnl.add(lblCounter4      , GBCFactory.createGBC(  0,  0, 1, 14, 3, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterNumber4, GBCFactory.createGBC(  0,  0, 1, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterNumber4 , GBCFactory.createGBC(  0,  0, 2, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterStart4 , GBCFactory.createGBC(  0,  0, 3, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterStart4  , GBCFactory.createGBC(  0,  0, 4, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(lblCounterEnd4   , GBCFactory.createGBC(  0,  0, 5, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(tfCounterEnd4    , GBCFactory.createGBC(  0,  0, 6, 15, 1, 1, GridBagConstraints.HORIZONTAL));
        cirPnl.add(new AKJPanel()   , GBCFactory.createGBC(100,100, 6, 16, 1, 1, GridBagConstraints.BOTH));

        // Schluessel

        AKJPanel keyPanel = new AKJPanel(new BorderLayout(), getSwingFactory().getText(TITLE_KEYS));
        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAddKey     , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE, new Insets(2,2,10,2)));
        btnPnl.add(btnRemoveKey  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(  0,100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        keyPanel.add(btnPnl, BorderLayout.WEST);
        // @formatter:on

        auftragHousingKeyTableModel = new AuftragHousingKeyTableModel();
        auftragHousingKeyTable = new AKJTable(auftragHousingKeyTableModel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);

        auftragHousingKeyTable.addMouseListener(new AKTableDoubleClickMouseListener(this));
        auftragHousingKeyTable.attachSorter();
        auftragHousingKeyTable.fitTable(new int[] { 85, 150, 150, 85 });
        AKJScrollPane spKeys = new AKJScrollPane(auftragHousingKeyTable, new Dimension(480, 150));

        keyPanel.add(spKeys, BorderLayout.CENTER);

        manageableComponents.add(btnAddKey);
        manageableComponents.add(btnRemoveKey);
        manageGUI(manageableComponents.toArray(new AKManageableComponent[manageableComponents.size()]));

        // @formatter:off
        this.setLayout(new GridBagLayout());
        this.add(bdPnl              , GBCFactory.createGBC(    0,   0, 0, 0, 4, 1, GridBagConstraints.HORIZONTAL));
        this.add(cirPnl             , GBCFactory.createGBC(  100,   0, 0, 1, 3, 1, GridBagConstraints.BOTH));
        this.add(keyPanel           , GBCFactory.createGBC(    0,   0, 3, 1, 1, 1, GridBagConstraints.BOTH));
        this.add(new AKJPanel()     , GBCFactory.createGBC(  100, 100, 4, 5, 1, 1, GridBagConstraints.VERTICAL));
        // @formatter:on

        GuiTools.enableContainerComponents(this, false);
        rfBuilding.setEnabled(true);
    }

    @Override
    public final void loadData() {
        try {
            housingService = getCCService(HousingService.class);

            List<HousingBuilding> buildings = housingService.findHousingBuildings();
            rfBuilding.setReferenceList(buildings);

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void setModel(Observable model) throws AKGUIException {
        try {
            if (model instanceof CCAuftragModel) {
                this.auftragModel = (CCAuftragModel) model;
            }
            else {
                this.auftragModel = null;
            }

            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new AKGUIException("Error loading housing data: " + e.getMessage(), e);
        }
    }

    @Override
    public void readModel() throws AKGUIException {
        GuiTools.cleanFields(this);
        auftragHousing = null;
        if (auftragModel != null) {
            try {
                auftragHousing = housingService.findAuftragHousing(auftragModel.getAuftragId());
                List<AuftragHousingKeyView> keys = housingService.findHousingKeys(auftragModel.getAuftragId());
                auftragHousingKeyTableModel.setData(keys);

                addObjectToWatch(WATCH_AUFTRAG_HOUSING, auftragHousing);
                showValues();

                rfBuilding.setEnabled(true);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new AKGUIException("Fehler bei der Ermittlung der Housing-Daten: " + e.getMessage(), e);
            }
        }
        else {
            rfBuilding.setEnabled(false);
        }
    }

    /**
     * Zeigt die Werte des aktuellen Housing-Objekts in der GUI an.
     */
    private void showValues() {
        if (auftragHousing != null) {
            rfBuilding.setReferenceId(auftragHousing.getBuildingId());
            rfFloor.setReferenceId(auftragHousing.getFloorId());
            rfRoom.setReferenceId(auftragHousing.getRoomId());
            rfParcel.setReferenceId(auftragHousing.getParcelId());
            tfRack.setText(auftragHousing.getRack());
            tfRackUnits.setValue(auftragHousing.getRackUnits());
            cbCapacity.selectItemWithValue(auftragHousing.getElectricCircuitCapacity());
            cbCircuitCount.selectItemWithValue(auftragHousing.getElectricCircuitCount());
            tfSafeguard.setValue(auftragHousing.getElectricSafeguard());

            tfCounterNumber.setValue(auftragHousing.getElectricCounterNumber());
            tfCounterStart.setValue(auftragHousing.getElectricCounterStart());
            tfCounterEnd.setValue(auftragHousing.getElectricCounterEnd());

            tfCounterNumber2.setValue(auftragHousing.getElectricCounterNumber2());
            tfCounterStart2.setValue(auftragHousing.getElectricCounterStart2());
            tfCounterEnd2.setValue(auftragHousing.getElectricCounterEnd2());

            tfCounterNumber3.setValue(auftragHousing.getElectricCounterNumber3());
            tfCounterStart3.setValue(auftragHousing.getElectricCounterStart3());
            tfCounterEnd3.setValue(auftragHousing.getElectricCounterEnd3());

            tfCounterNumber4.setValue(auftragHousing.getElectricCounterNumber4());
            tfCounterStart4.setValue(auftragHousing.getElectricCounterStart4());
            tfCounterEnd4.setValue(auftragHousing.getElectricCounterEnd4());
        }
    }

    @Override
    protected void execute(String command) {
        if (ADD_KEY.equals(command)) {
            AuftragHousingKeyDialog dlg = new AuftragHousingKeyDialog(null, auftragHousing);
            DialogHelper.showDialog(this, dlg, true, true);
        }
        else if (REMOVE_KEY.equals(command)) {
            removeKey();
        }

        // aktualisieren
        try {
            readModel();
        }
        catch (Exception e) {
            MessageHelper.showMessageDialog(this, "Die Liste konnte nicht aktualisiert werden!");
        }

    }


    private void removeKey() {
        try {
            int row = auftragHousingKeyTable.getSelectedRow();
            if (row != -1) {
                @SuppressWarnings("rawtypes")
                AuftragHousingKeyView selection = (AuftragHousingKeyView) ((AKMutableTableModel) auftragHousingKeyTable.getModel()).getDataAtRow(row);
                if (selection != null) {
                    int option = MessageHelper.showYesNoQuestion(getMainFrame(),
                            getSwingFactory().getText("delete.msg"), getSwingFactory().getText("delete.title"));
                    if (option == AKJOptionDialog.YES_OPTION) {
                        AuftragHousingKey key = housingService.findHousingKey(selection.getAuftragHousingKeyId());
                        housingService.deleteAuftragHousingKey(key);
                    }
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }


    @Override
    public boolean hasModelChanged() {
        getModel();
        return hasChanged(WATCH_AUFTRAG_HOUSING, auftragHousing);
    }

    @Override
    public void saveModel() throws AKGUIException {
        if (hasModelChanged() && (auftragHousing != null)) {
            try {
                housingService.saveAuftragHousing(auftragHousing);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    @Override
    public Object getModel() {
        if (rfBuilding.getReferenceId() != null) {
            if (auftragHousing == null) {
                auftragHousing = new AuftragHousing();
                auftragHousing.setAuftragId(auftragModel.getAuftragId());
            }

            auftragHousing.setBuildingId(rfBuilding.getReferenceIdAs(Long.class));
            auftragHousing.setFloorId(rfFloor.getReferenceIdAs(Long.class));
            auftragHousing.setRoomId(rfRoom.getReferenceIdAs(Long.class));
            auftragHousing.setParcelId(rfParcel.getReferenceIdAs(Long.class));
            auftragHousing.setRack(tfRack.getText(null));
            auftragHousing.setRackUnits(tfRackUnits.getValueAsLong(null));
            auftragHousing.setElectricCircuitCount((Long) cbCircuitCount.getSelectedItemValue());
            auftragHousing.setElectricCircuitCapacity((Float) cbCapacity.getSelectedItemValue());
            auftragHousing.setElectricSafeguard(tfSafeguard.getValueAsLong(null));

            auftragHousing.setElectricCounterNumber(tfCounterNumber.getText());
            auftragHousing.setElectricCounterStart(tfCounterStart.getValueAsDouble(null));
            auftragHousing.setElectricCounterEnd(tfCounterEnd.getValueAsDouble(null));

            auftragHousing.setElectricCounterNumber2(tfCounterNumber2.getText());
            auftragHousing.setElectricCounterStart2(tfCounterStart2.getValueAsDouble(null));
            auftragHousing.setElectricCounterEnd2(tfCounterEnd2.getValueAsDouble(null));

            auftragHousing.setElectricCounterNumber3(tfCounterNumber3.getText());
            auftragHousing.setElectricCounterStart3(tfCounterStart3.getValueAsDouble(null));
            auftragHousing.setElectricCounterEnd3(tfCounterEnd3.getValueAsDouble(null));

            auftragHousing.setElectricCounterNumber4(tfCounterNumber4.getText());
            auftragHousing.setElectricCounterStart4(tfCounterStart4.getValueAsDouble(null));
            auftragHousing.setElectricCounterEnd4(tfCounterEnd4.getValueAsDouble(null));
        }
        return auftragHousing;
    }

    @Override
    public void setSaveable(boolean saveable) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void update(AKReferenceFieldEvent akReferenceFieldEvent) throws Exception {
        if ((akReferenceFieldEvent == rfBuilding) && (rfBuilding.getReferenceId() != null)) {
            GuiTools.enableContainerComponents(this, true);
            tfBuildingStreet.setEditable(false);
            tfBuildingCity.setEditable(false);

            HousingBuilding building = (HousingBuilding) rfBuilding.getReferenceObject();
            rfFloor.clearReference();
            rfFloor.setReferenceList((building != null) ? building.getFloors() : null);
            rfRoom.clearReference();
            rfRoom.setReferenceList(null);
            rfParcel.clearReference();
            rfParcel.setReferenceList(null);

            if ((building != null) && (building.getAddress() != null)) {
                CCAddress address = building.getAddress();
                tfBuildingStreet.setText(address.getCombinedStreetData());
                tfBuildingCity.setText(address.getCombinedPlzOrtData());
            }
            else {
                tfBuildingStreet.setText("");
                tfBuildingCity.setText("");
            }
        }
        else if (akReferenceFieldEvent == rfFloor) {
            HousingFloor floor = (HousingFloor) rfFloor.getReferenceObject();
            rfRoom.clearReference();
            rfRoom.setReferenceList((floor != null) ? floor.getRooms() : null);
            rfParcel.clearReference();
            rfParcel.setReferenceList(null);
        }
        else if (akReferenceFieldEvent == rfRoom) {
            HousingRoom room = (HousingRoom) rfRoom.getReferenceObject();
            rfParcel.clearReference();
            rfParcel.setReferenceList((room != null) ? room.getParcels() : null);
        }
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object selectedItem = e.getItem();

            if (selectedItem != null) {
                String selectedItemString = selectedItem.toString().trim();
                Integer circuitCount = StringUtils.isEmpty(selectedItemString) ? Integer.valueOf(0) : Integer.valueOf(selectedItem.toString().trim());

                for (int i = 1; i <= AuftragHousingPanel.CIRCUIT_FIELD_COUNT; i++) {
                    setEnabledCounterField(AuftragHousingPanel.TF_COUNTER_NUMBER, i, circuitCount);
                    setEnabledCounterField(AuftragHousingPanel.TF_COUNTER_START, i, circuitCount);
                    setEnabledCounterField(AuftragHousingPanel.TF_COUNTER_END, i, circuitCount);
                }
            }
        }
    }

    /**
     * aktiviert/deaktiviert die Textfelder der Zaehlerstaende
     *
     * @param name         Name der Komponente
     * @param i            Zaehler
     * @param circuitCount Anzahl der Stromkreise
     */
    private void setEnabledCounterField(String name, int i, Integer circuitCount) {
        StringBuilder fieldNameBuffer = new StringBuilder(name);

        if (i > 1) {
            fieldNameBuffer.append(i);
        }

        if (getTextField(fieldNameBuffer.toString()) != null) {
            if (i <= circuitCount) {
                getTextField(fieldNameBuffer.toString()).setEnabled(ENABLED);
            }
            else {
                getTextField(fieldNameBuffer.toString()).setEnabled(DISABLED);
            }
        }
    }

    /**
     * Ermittelt das Textfeld in Abhaengigkeit des Namens
     *
     * @param fieldName Name der Komponente
     * @return Textfeld
     */
    private AKJFormattedTextField getTextField(String fieldName) {
        AKJFormattedTextField textField = null;

        try {
            Field field = this.getClass().getDeclaredField(fieldName);

            if ((field != null) && field.getType().equals(AKJFormattedTextField.class)) {
                textField = (AKJFormattedTextField) field.get(this);
            }
        }
        catch (SecurityException | IllegalArgumentException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        return textField;
    }

    @Override
    public void objectSelected(Object selection) {
        try {
            if (selection instanceof AuftragHousingKeyView) {
                AuftragHousingKeyView view = (AuftragHousingKeyView) selection;
                if (StringUtils.isNotBlank(view.getTransponderGroupDescription())) {
                    throw new HurricanGUIException("Transponder-Gruppen bitte über den Kunden bearbeiten!");
                }

                AuftragHousingKey key = housingService.findHousingKey(view.getAuftragHousingKeyId());
                showDialog(key);
            }
        }
        catch (Exception e) {
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * Oeffnet den Transponder-Dialog
     */
    private void showDialog(AuftragHousingKey auftragHousingKey) {
        try {
            AuftragHousingKeyDialog dlg = new AuftragHousingKeyDialog(auftragHousingKey, auftragHousing);
            DialogHelper.showDialog(this, dlg, true, true);
            readModel();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

}
