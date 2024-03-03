/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.10.2009 09:42:53
 */

package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKManageableComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKDateSelectionDialog;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJDateComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.AKTableSingleClickMouseListener;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.BrasPool;
import de.augustakom.hurrican.model.cc.EQCrossConnection;
import de.augustakom.hurrican.model.cc.Equipment;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.EQCrossConnectionService;
import de.augustakom.hurrican.service.cc.RangierungsService;
import de.augustakom.hurrican.service.cc.ReferenceService;

/**
 * Dialog zur Bearbeitung der Cross Connections eines Ports.
 *
 *
 */
public class CrossConnectionDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, AKObjectSelectionListener {

    private static final String NEW_CROSSCONNECTION = "new.crossconnection";
    private static final String DELETE_CROSSCONNECTION = "delete.crossconnection";

    private static final Logger LOGGER = Logger.getLogger(CrossConnectionDialog.class);

    // Inner Classes

    /**
     * Action Listener of the drop down field of the brasPool selection.
     */
    private class BrasPoolComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (selectedCrossConnection == null) {
                return;
            }
            BrasPool brasPool = (BrasPool) cbBrasPool.getSelectedItem();
            if ((brasPool == null) || (brasPool.getId() == null)) {
                cleanBrasFields();
                setBrasFieldsEndabled(true);
                return;
            }

            // fill bras fields with values from bras pool
            updateTfVcRange(brasPool);
            cbBrasPool.selectItem(brasPool);
            tfBrasOuter.setValue(brasPool.getVp());

            tfNasIdentifier.setText(brasPool.getNasIdentifier());
            tfPort.setValue(brasPool.getPort());
            tfSlot.setValue(brasPool.getSlot());
            tfBackupNasIdentifier.setText(brasPool.getBackupNasIdentifier());
            tfBackupPort.setValue(brasPool.getBackupPort());
            tfBackupSlot.setValue(brasPool.getBackupSlot());

            try {
                Date from = selectedCrossConnection.getGueltigVon();
                Date till = selectedCrossConnection.getGueltigBis();
                Set<Integer> skips = findSkipsForBrasPool(brasPool, from, till);
                Integer brasVC = crossConnectionService.getVcFromPool(brasPool, from, till, skips);
                if (brasVC == null) {
                    throw new HurricanGUIException(
                            "Vom BRAS Pool konnte kein freier Wert mehr ermittelt werden!");
                }

                tfBrasInner.setValue(brasVC);
                setBrasFieldsEndabled(false);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
        }
    }

    /**
     * Action Listener on the 'Show only valid Cross Connections' checkbox.
     */
    private class ShowOnlyValidCrossConnectionsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            updateCrossConnectionsTableModel();
        }
    }

    /**
     * Action Listener on the 'CC Default' checkbox.
     */
    private class DefaultCcsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            updateOverwritten();
        }
    }

    /**
     * Calculates the values that should be skipped for the calculation of a free brasInner for the given time interval.
     * The skipped brasInner are calculated from the field 'allCrossConnections'.
     */
    private Set<Integer> findSkipsForBrasPool(BrasPool brasPool, Date from, Date till) {
        if (brasPool == null) {
            throw new IllegalArgumentException("The given brasPool may not be null");
        }
        Set<Integer> skipValues = new HashSet<Integer>();
        if (brasPool.getId() == null) {
            return skipValues;
        }

        for (EQCrossConnection cc : allCrossConnections) {
            if (brasPool.getId().equals(cc.getBrasPoolId())
                    && (DateTools.isDateBetween(cc.getGueltigVon(), from, till)
                    || DateTools.isDateBetween(cc.getGueltigBis(), from, till))) {
                Integer brasInner = cc.getBrasInner();
                if (brasInner != null) {
                    skipValues.add(brasInner);
                }
            }
        }
        return skipValues;
    }

    // Dialog Data

    /**
     * Port of the Cross Connection
     */
    private final Equipment eqInPort;

    /**
     * Port of the Cross Connection
     */
    private final Long eqInPortId; //

    /**
     * All Cross connection of the port (allCrossConnections = savedCrossConnections + newCrossConnections)
     */
    private List<EQCrossConnection> allCrossConnections;

    /**
     * Cross connections that should be saved when the user clicks on save
     */
    private List<EQCrossConnection> newCrossConnections;

    /**
     * The currently selected Cross connections
     */
    private EQCrossConnection selectedCrossConnection;

    /**
     * All types of Cross connections.
     */
    private List<Reference> ccTypes;

    /**
     * The current user's login name
     */
    private String currentUser;

    /**
     * List of all Bras Pools
     */
    private List<BrasPool> allBrasPools;

    // GUI Elements
    private AKJTable tbCrossConnections;
    private AKReferenceAwareTableModel<EQCrossConnection> tbModelCrossConnections;
    private AKJButton btnNewCrossConnection;
    private AKJButton btnDeleteCrossConnection;
    private AKJCheckBox cbShowOnlyCurrentlyValid;
    private AKJComboBox cbType;
    private AKJComboBox cbBrasPool;
    private AKJFormattedTextField tfNtOuter;
    private AKJFormattedTextField tfNtInner;
    private AKJFormattedTextField tfLtOuter;
    private AKJFormattedTextField tfLtInner;
    private AKJFormattedTextField tfBrasOuter;
    private AKJTextField tfVcRange;
    private AKJFormattedTextField tfBrasInner;
    private AKJDateComponent dcGueltigVon;
    private AKJDateComponent dcGueltigBis;

    private AKJTextField tfNasIdentifier;
    private AKJFormattedTextField tfPort;
    private AKJFormattedTextField tfSlot;
    private AKJTextField tfBackupNasIdentifier;
    private AKJFormattedTextField tfBackupPort;
    private AKJFormattedTextField tfBackupSlot;

    private final List<AKManageableComponent> manageableComponents = new ArrayList<AKManageableComponent>();
    private AKJCheckBox cbDefaultCcs;


    // Used Services
    private EQCrossConnectionService crossConnectionService;
    private RangierungsService rangierungsService;
    private ReferenceService referenceService;
    private final Long auftragId;
    private boolean editingEnabled;

    // GUI Logic

    public CrossConnectionDialog(Equipment eqInPort, Long auftragId) {
        super("de/augustakom/hurrican/gui/auftrag/resources/CrossConnectionDialog.xml");
        this.eqInPort = eqInPort;
        this.auftragId = auftragId;
        this.eqInPortId = eqInPort.getId();

        initServices();
        createGUI();
        loadData();
    }

    private void initServices() {
        try {
            crossConnectionService = getCCService(EQCrossConnectionService.class);
            rangierungsService = getCCService(RangierungsService.class);
            referenceService = getCCService(ReferenceService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        configureButton(CMD_SAVE, getSwingFactory().getText("ok.text"),
                getSwingFactory().getText("ok.tooltip"), true, true);

        tbModelCrossConnections = new AKReferenceAwareTableModel<EQCrossConnection>(
                new String[] { "Typ", "BrasOuter", "BrasInner", "NtOuter", "NtInner", "LtOuter", "LtInner", "Gültig von", "Gültig bis", "Bearbeiter" },
                new String[] { "crossConnectionTypeRefId", "brasOuter", "brasInner", "ntOuter", "ntInner", "ltOuter", "ltInner", "gueltigVon", "gueltigBis", "userW" },
                new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Date.class, Date.class, String.class });
        tbCrossConnections = new AKJTable(tbModelCrossConnections, AKJTable.AUTO_RESIZE_ALL_COLUMNS, ListSelectionModel.SINGLE_SELECTION);
        tbCrossConnections.addMouseListener(new AKTableSingleClickMouseListener(this));

        btnNewCrossConnection = getSwingFactory().createButton(NEW_CROSSCONNECTION, getActionListener(), null);
        manageableComponents.add(btnNewCrossConnection);

        btnDeleteCrossConnection = getSwingFactory().createButton(DELETE_CROSSCONNECTION, getActionListener(), null);
        manageableComponents.add(btnDeleteCrossConnection);

        cbShowOnlyCurrentlyValid = getSwingFactory().createCheckBox("showOnlyCurrentlyValid");
        cbShowOnlyCurrentlyValid.addActionListener(new ShowOnlyValidCrossConnectionsActionListener());

        cbDefaultCcs = getSwingFactory().createCheckBox("defaultCcs");
        cbDefaultCcs.addActionListener(new DefaultCcsActionListener());

        AKJLabel labelType = getSwingFactory().createLabel("type");
        AKJLabel labelBrasPool = getSwingFactory().createLabel("brasPool");
        AKJLabel labelBrasOuter = getSwingFactory().createLabel("brasOuter");
        AKJLabel labelVcRange = getSwingFactory().createLabel("vcRange");
        AKJLabel labelBrasInner = getSwingFactory().createLabel("brasInner");
        AKJLabel labelNtOuter = getSwingFactory().createLabel("ntOuter");
        AKJLabel labelNtInner = getSwingFactory().createLabel("ntInner");
        AKJLabel labelLtOuter = getSwingFactory().createLabel("ltOuter");
        AKJLabel labelLtInner = getSwingFactory().createLabel("ltInner");
        AKJLabel labelGueltigVon = getSwingFactory().createLabel("gueltigVon");
        AKJLabel labelGueltigBis = getSwingFactory().createLabel("gueltigBis");
        AKJLabel labelNasIdentifier = getSwingFactory().createLabel("nasIdentifier");
        AKJLabel labelPort = getSwingFactory().createLabel("port");
        AKJLabel labelSlot = getSwingFactory().createLabel("slot");
        AKJLabel labelBackupNasIdentifier = getSwingFactory().createLabel("backupNasIdentifier");
        AKJLabel labelBackupPort = getSwingFactory().createLabel("backupPort");
        AKJLabel labelBackupSlot = getSwingFactory().createLabel("backupSlot");

        cbType = getSwingFactory().createComboBox("type", new AKCustomListCellRenderer<>(Reference.class, Reference::getGuiText));
        cbBrasPool = getSwingFactory().createComboBox("brasPool", new AKCustomListCellRenderer<>(BrasPool.class, BrasPool::getName));
        cbBrasPool.addActionListener(new BrasPoolComboBoxActionListener());
        tfBrasOuter = getSwingFactory().createFormattedTextField("brasOuter");
        tfVcRange = getSwingFactory().createTextField("vcRange");
        tfVcRange.setEditable(false);
        tfBrasInner = getSwingFactory().createFormattedTextField("brasInner");
        tfNtOuter = getSwingFactory().createFormattedTextField("ntOuter");
        tfNtInner = getSwingFactory().createFormattedTextField("ntInner");
        tfLtOuter = getSwingFactory().createFormattedTextField("ltOuter");
        tfLtInner = getSwingFactory().createFormattedTextField("ltInner");
        dcGueltigVon = getSwingFactory().createDateComponent("gueltigVon");
        dcGueltigBis = getSwingFactory().createDateComponent("gueltigBis");
        tfNasIdentifier = getSwingFactory().createTextField("nasIdentifier");
        tfPort = getSwingFactory().createFormattedTextField("port");
        tfSlot = getSwingFactory().createFormattedTextField("slot");
        tfBackupNasIdentifier = getSwingFactory().createTextField("backupNasIdentifier");
        tfBackupPort = getSwingFactory().createFormattedTextField("backupPort");
        tfBackupSlot = getSwingFactory().createFormattedTextField("backupSlot");

        setAddDeleteButtonsEnabled(false);
        setCCFieldsInEditCrossConnectionPanelEnabled(false);
        setBrasFieldsEndabled(false);

        tfNasIdentifier.setEnabled(false);
        tfPort.setEditable(false);
        tfSlot.setEnabled(false);
        tfBackupNasIdentifier.setEnabled(false);
        tfBackupPort.setEditable(false);
        tfBackupSlot.setEnabled(false);

        AKJScrollPane crossConnectionsPane = new AKJScrollPane(tbCrossConnections, new Dimension(800, 150));
        AKJPanel crossConnectionsPanel = new AKJPanel(new GridBagLayout(), "Übersicht Cross Connections");
        crossConnectionsPanel.add(btnNewCrossConnection, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        crossConnectionsPanel.add(btnDeleteCrossConnection, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        crossConnectionsPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));
        crossConnectionsPanel.add(crossConnectionsPane, GBCFactory.createGBC(100, 100, 1, 0, 2, 4, GridBagConstraints.BOTH));
        crossConnectionsPanel.add(cbDefaultCcs, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.NONE));
        crossConnectionsPanel.add(cbShowOnlyCurrentlyValid, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.NONE));

        AKJPanel bLeft = new AKJPanel(new GridBagLayout());
        bLeft.setPreferredSize(new Dimension(225, 150));
        bLeft.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        bLeft.add(labelType, GBCFactory.createGBC(75, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(cbType, GBCFactory.createGBC(150, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(labelBrasPool, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(cbBrasPool, GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(labelBrasOuter, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfBrasOuter, GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(labelVcRange, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfVcRange, GBCFactory.createGBC(0, 0, 2, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(labelBrasInner, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(tfBrasInner, GBCFactory.createGBC(0, 0, 2, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bLeft.add(new AKJPanel(), GBCFactory.createGBC(0, 100, 3, 5, 1, 1, GridBagConstraints.VERTICAL));

        AKJPanel bMiddle = new AKJPanel(new GridBagLayout());
        bMiddle.setPreferredSize(new Dimension(225, 150));
        bMiddle.add(labelNtOuter, GBCFactory.createGBC(50, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(tfNtOuter, GBCFactory.createGBC(175, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(labelNtInner, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(tfNtInner, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(labelLtOuter, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(tfLtOuter, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(labelLtInner, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(tfLtInner, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(labelGueltigVon, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(dcGueltigVon, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(labelGueltigBis, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        bMiddle.add(dcGueltigBis, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel bRight = new AKJPanel(new GridBagLayout());
        bRight.setPreferredSize(new Dimension(225, 150));
        bRight.add(labelNasIdentifier, GBCFactory.createGBC(50, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfNasIdentifier, GBCFactory.createGBC(175, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(labelPort, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfPort, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(labelSlot, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfSlot, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(labelBackupNasIdentifier, GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfBackupNasIdentifier, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(labelBackupPort, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfBackupPort, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(labelBackupSlot, GBCFactory.createGBC(0, 0, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        bRight.add(tfBackupSlot, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel editCrossConnectionPanel = new AKJPanel(new GridBagLayout());
        editCrossConnectionPanel.setBorder(BorderFactory.createTitledBorder("Cross Connection bearbeiten"));
        editCrossConnectionPanel.add(bLeft, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        editCrossConnectionPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        editCrossConnectionPanel.add(bMiddle, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.NONE));
        editCrossConnectionPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 3, 1, 1, 1, GridBagConstraints.BOTH));
        editCrossConnectionPanel.add(bRight, GBCFactory.createGBC(0, 0, 4, 0, 1, 1, GridBagConstraints.NONE));
        editCrossConnectionPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 5, 1, 1, 1, GridBagConstraints.BOTH));

        AKJPanel child = getChildPanel();
        child.setLayout(new GridBagLayout());
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(crossConnectionsPanel, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.VERTICAL));
        child.add(editCrossConnectionPanel, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 3, 1, 1, GridBagConstraints.BOTH));
    }


    @Override
    public final void loadData() {
        try {
            /*
      Cross connection of the port that have been already saved (= Cross Connection with an Id)
     */
            List<EQCrossConnection> savedCrossConnections = crossConnectionService.findEQCrossConnections(eqInPortId);
            newCrossConnections = new ArrayList<EQCrossConnection>();
            allCrossConnections = new ArrayList<EQCrossConnection>(savedCrossConnections);
            currentUser = HurricanSystemRegistry.instance().getCurrentUser().getLoginName();

            allBrasPools = crossConnectionService.getAllBrasPools();
            cbBrasPool.addItems(allBrasPools, true, BrasPool.class);

            ccTypes = referenceService.findReferencesByType(Reference.REF_TYPE_XCONN_TYPES, Boolean.TRUE);
            cbType.addItems(ccTypes, true, Reference.class);

            Map<Long, Reference> referenceMap = new HashMap<Long, Reference>();
            CollectionMapConverter.convert2Map(ccTypes, referenceMap, "getId", null);
            tbModelCrossConnections.addReference(0, referenceMap, "strValue");

            updateCrossConnectionsTableModel();
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }


    private void updateCrossConnectionsTableModel() {
        tbCrossConnections.removeAll();
        if (cbShowOnlyCurrentlyValid.isSelected()) {
            Date actualDate = DateTools.getActualSQLDate();
            List<EQCrossConnection> ccsToShow = new ArrayList<EQCrossConnection>();
            for (EQCrossConnection cc : allCrossConnections) {
                if (DateTools.isDateBetween(actualDate, cc.getGueltigVon(), cc.getGueltigBis()) ||
                        DateTools.isDateEqual(actualDate, cc.getGueltigBis())) {
                    ccsToShow.add(cc);
                }
            }
            tbModelCrossConnections.setData(ccsToShow);
        }
        else {
            tbModelCrossConnections.setData(allCrossConnections);
        }
        if (Boolean.TRUE.equals(eqInPort.getManualConfiguration())) {
            cbDefaultCcs.setSelected(false);
            setAddDeleteButtonsEnabled(true);
            setBrasFieldsEndabled((selectedCrossConnection != null) && (selectedCrossConnection.getBrasPoolId() == null));
            setCCFieldsInEditCrossConnectionPanelEnabled(selectedCrossConnection != null);
        }
        else {
            cbDefaultCcs.setSelected(true);
            setAddDeleteButtonsEnabled(false);
            setBrasFieldsEndabled(false);
            setCCFieldsInEditCrossConnectionPanelEnabled(false);
        }
        tbModelCrossConnections.fireTableDataChanged();
    }


    private void updateOverwritten() {
        if (cbDefaultCcs.isSelected()) {
            addCalculatedCrossConnections();
        }
        else {
            eqInPort.setManualConfiguration(Boolean.TRUE);
            setAddDeleteButtonsEnabled(true);
            if (selectedCrossConnection != null) {
                setCCFieldsInEditCrossConnectionPanelEnabled(true);
                setBrasFieldsEndabled(selectedCrossConnection.getBrasPoolId() == null);
            }
        }
    }


    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof EQCrossConnection) {
            selectCrossConnection((EQCrossConnection) selection);
        }
    }


    private void selectCrossConnection(EQCrossConnection selectedCC) {
        if (editingEnabled) {
            updateSelectedCrossConnectionFromEditPanel();
        }
        selectedCrossConnection = selectedCC;
        updateEditCrossConnectionPanel();
    }


    private void updateEditCrossConnectionPanel() {
        if (selectedCrossConnection != null) {
            cbType.setSelectedIndex(0);
            for (Reference ref : ccTypes) {
                if (ref.getId().equals(selectedCrossConnection.getCrossConnectionTypeRefId())) {
                    cbType.setSelectedItem(ref);
                }
            }

            cbBrasPool.setSelectedIndex(0);
            boolean hasBrasPoolSelected = false;
            for (BrasPool brasPool : allBrasPools) {
                if (brasPool.getId().equals(selectedCrossConnection.getBrasPoolId())) {
                    cbBrasPool.setSelectedItem(brasPool);
                    updateTfVcRange(brasPool);
                    setBrasFieldsEndabled(false);
                    hasBrasPoolSelected = true;
                    tfNasIdentifier.setText(brasPool.getNasIdentifier());
                    tfPort.setValue(brasPool.getPort());
                    tfSlot.setValue(brasPool.getSlot());
                    tfBackupNasIdentifier.setText(brasPool.getBackupNasIdentifier());
                    tfBackupPort.setValue(brasPool.getBackupPort());
                    tfBackupSlot.setValue(brasPool.getBackupSlot());
                    break;
                }
            }

            if (!hasBrasPoolSelected) {
                cleanBrasFields();
                setBrasFieldsEndabled(true);
            }

            tfNtOuter.setValue(selectedCrossConnection.getNtOuter());
            tfNtInner.setValue(selectedCrossConnection.getNtInner());
            tfLtOuter.setValue(selectedCrossConnection.getLtOuter());
            tfLtInner.setValue(selectedCrossConnection.getLtInner());
            tfBrasOuter.setValue(selectedCrossConnection.getBrasOuter());
            tfBrasInner.setValue(selectedCrossConnection.getBrasInner());
            dcGueltigVon.setDate(selectedCrossConnection.getGueltigVon());
            dcGueltigBis.setDate(selectedCrossConnection.getGueltigBis());

            setCCFieldsInEditCrossConnectionPanelEnabled(true);
        }
        else {
            resetFieldsInEditCrossConnectionPanel();
        }
    }


    private void updateTfVcRange(BrasPool brasPool) {
        if ((brasPool != null) && (brasPool.getVcMin() != null) && (brasPool.getVcMax() != null)) {
            tfVcRange.setText(brasPool.getVcMin().toString() + " - " + brasPool.getVcMax().toString());
        }
        else {
            tfVcRange.setText("");
        }
    }


    private void updateSelectedCrossConnectionFromEditPanel() {
        if (selectedCrossConnection != null) {
            selectedCrossConnection.setCrossConnectionType((Reference) cbType.getSelectedItem());
            selectedCrossConnection.setBrasPool((BrasPool) cbBrasPool.getSelectedItem());
            selectedCrossConnection.setBrasOuter(tfBrasOuter.getValueAsInt(null));
            selectedCrossConnection.setBrasInner(tfBrasInner.getValueAsInt(null));
            selectedCrossConnection.setNtOuter(tfNtOuter.getValueAsInt(null));
            selectedCrossConnection.setNtInner(tfNtInner.getValueAsInt(null));
            selectedCrossConnection.setLtOuter(tfLtOuter.getValueAsInt(null));
            selectedCrossConnection.setLtInner(tfLtInner.getValueAsInt(null));
            selectedCrossConnection.setGueltigVon(dcGueltigVon.getDate(null));
            selectedCrossConnection.setGueltigBis(dcGueltigBis.getDate(null));

            tbModelCrossConnections.fireTableRowsUpdated(0, tbModelCrossConnections.getRowCount());
        }
    }


    private void resetFieldsInEditCrossConnectionPanel() {
        cbType.setSelectedIndex(0);
        cbBrasPool.setSelectedIndex(0);
        tfNtOuter.setValue(null);
        tfVcRange.setText("");
        tfNtInner.setValue(null);
        tfLtOuter.setValue(null);
        tfLtInner.setValue(null);
        tfBrasOuter.setValue(null);
        tfBrasInner.setValue(null);
        dcGueltigVon.setDate(null);
        dcGueltigBis.setDate(null);
        tfNasIdentifier.setText("");
        tfPort.setValue(null);
        tfSlot.setValue(null);
        tfBackupNasIdentifier.setText("");
        tfBackupPort.setValue(null);
        tfBackupSlot.setValue(null);
        selectedCrossConnection = null;
        setCCFieldsInEditCrossConnectionPanelEnabled(false);
        setBrasFieldsEndabled(false);
        setAddDeleteButtonsEnabled(false);
    }


    private void setAddDeleteButtonsEnabled(boolean enabled) {
        if (!enabled || (enabled && Boolean.TRUE.equals(eqInPort.getManualConfiguration()))) {
            btnNewCrossConnection.setEnabled(enabled);
            btnDeleteCrossConnection.setEnabled(enabled);
        }
    }


    private void setBrasFieldsEndabled(boolean enabled) {
        if (!enabled || (enabled && Boolean.TRUE.equals(eqInPort.getManualConfiguration()))) {
            tfBrasOuter.setEnabled(enabled);
            tfBrasInner.setEnabled(enabled);
        }
    }

    private void cleanBrasFields() {
        tfBrasOuter.setValue(null);
        tfBrasInner.setValue(null);
        tfVcRange.setText("");
        tfNasIdentifier.setText("");
        tfPort.setValue(null);
        tfSlot.setValue(null);
        tfBackupNasIdentifier.setText("");
        tfBackupPort.setValue(null);
        tfBackupSlot.setValue(null);
    }


    private void setCCFieldsInEditCrossConnectionPanelEnabled(boolean enabled) {
        editingEnabled = enabled;
        if (!enabled || (enabled && Boolean.TRUE.equals(eqInPort.getManualConfiguration()))) {
            cbType.setEnabled(enabled);
            cbBrasPool.setEnabled(enabled);
            tfNtOuter.setEnabled(enabled);
            tfNtInner.setEnabled(enabled);
            tfLtOuter.setEnabled(enabled);
            tfLtInner.setEnabled(enabled);
            dcGueltigVon.setEnabled(enabled);
            dcGueltigBis.setEnabled(enabled);
        }
    }


    @Override
    protected void execute(String command) {
        if (NEW_CROSSCONNECTION.equals(command)) {
            createNewCrossConnection();
        }
        else if (DELETE_CROSSCONNECTION.equals(command)) {
            deleteSelectedCrossConnection();
        }
    }


    private void createNewCrossConnection() {
        updateSelectedCrossConnectionFromEditPanel();
        EQCrossConnection newCrossConnection = new EQCrossConnection();
        newCrossConnection.setUserW(currentUser);
        newCrossConnection.setGueltigVon(DateTools.getActualSQLDate());
        newCrossConnection.setGueltigBis(DateTools.getHurricanEndDate());
        newCrossConnection.setEquipmentId(eqInPortId);

        newCrossConnections.add(newCrossConnection);
        allCrossConnections.add(newCrossConnection);
        selectedCrossConnection = newCrossConnection;

        resetFieldsInEditCrossConnectionPanel();
        updateCrossConnectionsTableModel();
        tbCrossConnections.selectAndScrollToLastRow();
        updateEditCrossConnectionPanel();
    }


    private void addCalculatedCrossConnections() {
        try {
            updateSelectedCrossConnectionFromEditPanel();
            resetFieldsInEditCrossConnectionPanel();
            AKDateSelectionDialog dateSelectionDialog = new AKDateSelectionDialog(
                    "Gültigkeit", "Ab wann sollen die Default Cross Connections gültig sein?", "Gültig ab*:");
            Object result = DialogHelper.showDialog(this, dateSelectionDialog, true, true);
            if (result instanceof Date) {
                Date selectedDate = new java.sql.Date(((Date) result).getTime());
                Date gueltigBis = new java.sql.Date(DateTools.changeDate(selectedDate, Calendar.DATE, -1).getTime());

                for (EQCrossConnection cc : allCrossConnections) {
                    if (DateTools.isDateAfterOrEqual(cc.getGueltigBis(), selectedDate)) {
                        cc.setGueltigBis(gueltigBis);
                        if (DateTools.isDateAfter(cc.getGueltigVon(), gueltigBis)) {
                            cc.setGueltigVon(gueltigBis);
                        }
                    }

                }

                List<EQCrossConnection> calculatedCrossConnections = crossConnectionService.calculateDefaultCcs(
                        eqInPort, currentUser, selectedDate, auftragId);

                newCrossConnections.addAll(calculatedCrossConnections);
                allCrossConnections.addAll(calculatedCrossConnections);

                eqInPort.setManualConfiguration(Boolean.FALSE);
            }

            updateCrossConnectionsTableModel();
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }


    private void deleteSelectedCrossConnection() {
        EQCrossConnection crossConnectionToDelete = tbModelCrossConnections.getDataAtRow(tbCrossConnections.getSelectedRow());
        if (crossConnectionToDelete == null) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Es wurde keine Cross Connection ausgewählt."), false);
            return;
        }
        if (!newCrossConnections.contains(crossConnectionToDelete)) {
            MessageHelper.showErrorDialog(getMainFrame(), new HurricanGUIException("Nur noch nicht gespeicherte Cross Connections dürfen gelöscht werden."), false);
            return;
        }
        newCrossConnections.remove(crossConnectionToDelete);
        allCrossConnections.remove(crossConnectionToDelete);
        selectedCrossConnection = null;
        resetFieldsInEditCrossConnectionPanel();
        updateCrossConnectionsTableModel();
    }


    @Override
    protected void doSave() {
        try {
            updateSelectedCrossConnectionFromEditPanel();
            crossConnectionService.saveEQCrossConnections(allCrossConnections);

            // Save manualConfiguration flag of port
            rangierungsService.saveEquipment(eqInPort);

            prepare4Close();
            setValue(allCrossConnections);
        }
        catch (Exception e) {
            resetFieldsInEditCrossConnectionPanel();
            updateCrossConnectionsTableModel();
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        // do nothing
    }

}
