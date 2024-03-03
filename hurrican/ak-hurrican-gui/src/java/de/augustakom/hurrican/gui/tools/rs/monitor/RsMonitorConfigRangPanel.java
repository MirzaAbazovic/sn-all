/*
 * Copyright (c) 2009 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2009 16:11:12
 */
package de.augustakom.hurrican.gui.tools.rs.monitor;

import java.awt.*;
import java.util.List;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.exceptions.AKGUIException;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.collections.CollectionMapConverter;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.model.cc.RSMonitorConfig;
import de.augustakom.hurrican.model.cc.RSMonitorRun;
import de.augustakom.hurrican.service.cc.MonitorService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Basis-Panel fuer die Konfiguration des Ressourcenmonitors.
 *
 *
 */
public class RsMonitorConfigRangPanel extends AbstractDataPanel implements AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(RsMonitorConfigRangPanel.class);

    private HVTStandort hvtStandort = null;
    private RangConfigTableModel tbMdlRang = null;
    private AKJTable tbEQ = null;
    private Map ptMap = null;

    private AKJButton btnAdd = null;
    private AKJButton btnDel = null;
    private AKJButton btnChange = null;

    /**
     * Default-Konstruktor.
     */
    public RsMonitorConfigRangPanel() {
        super("de/augustakom/hurrican/gui/tools/rs/monitor/resources/RsMonitorConfigEQPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    protected final void createGUI() {
        btnAdd = getSwingFactory().createButton("add", getActionListener(), null);
        btnDel = getSwingFactory().createButton("del", getActionListener(), null);
        btnChange = getSwingFactory().createButton("change", getActionListener(), null);

        AKJPanel btnPnl = new AKJPanel(new GridBagLayout());
        btnPnl.add(btnAdd, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnDel, GBCFactory.createGBC(0, 0, 0, 2, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 3, 1, 1, GridBagConstraints.VERTICAL));
        btnPnl.add(btnChange, GBCFactory.createGBC(0, 0, 0, 4, 1, 1, GridBagConstraints.NONE));
        btnPnl.add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 5, 1, 1, GridBagConstraints.VERTICAL));

        tbMdlRang = new RangConfigTableModel();
        tbEQ = new AKJTable(tbMdlRang);
        tbEQ.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbEQ.attachSorter();
        tbEQ.fitTable(new int[] { 80, 80, 80 });

        AKJScrollPane spTable = new AKJScrollPane(tbEQ);
        spTable.setPreferredSize(new Dimension(500, 300));

        this.setLayout(new BorderLayout());
        this.add(spTable, BorderLayout.WEST);
        this.add(btnPnl, BorderLayout.EAST);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            if (hvtStandort != null) {
                PhysikService ps = getCCService(PhysikService.class);
                List<PhysikTyp> physiktypen = ps.findPhysikTypen();
                ptMap = CollectionMapConverter.convert2Map(physiktypen, "getId", "getName");

                // Lade Konfiguration
                MonitorService ms = getCCService(MonitorService.class);
                List<RSMonitorConfig> list = ms.findMonitorConfig4HvtType(hvtStandort.getHvtIdStandort(), RSMonitorRun.RS_REF_TYPE_RANG_MONITOR);
                tbMdlRang.setData(list);
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getParent(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
        if ("add".equals(command)) {
            if (hvtStandort == null) {
                MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("Bitte zuerst einen HVT auswählen"));
                return;
            }
            RsMonitorConfigRangAnlegenDialog dlg = new RsMonitorConfigRangAnlegenDialog(hvtStandort.getId());
            DialogHelper.showDialog(getMainFrame(), dlg, true, true);
            loadData();
        }
        else if ("del".equals(command)) {
            RSMonitorConfig config = (RSMonitorConfig) tbMdlRang.getDataAtRow(tbEQ.getSelectedRow());
            if (config == null) {
                MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("Bitte zuerst einen Datensatz auswählen"));
                return;
            }
            try {
                MonitorService ms = getCCService(MonitorService.class);
                ms.deleteRsMonitorConfig(config.getId());
                loadData();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getParent(), e);
            }
        }
        else if ("change".equals(command)) {
            editConfig();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKSimpleModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
        if (model instanceof HVTStandort) {
            hvtStandort = (HVTStandort) model;
            loadData();
            enableButtons(Boolean.TRUE);
        }
        else if (model == null) {
            hvtStandort = null;
            tbMdlRang.setData(null);
            enableButtons(Boolean.FALSE);
        }
    }

    /*
     * De-/Aktiviere Button
     */
    private void enableButtons(Boolean enable) {
        btnAdd.setEnabled(enable);
        btnDel.setEnabled(enable);
        btnChange.setEnabled(enable);
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() throws AKGUIException {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
    }

    /* Funktion um selektierten Eintrag zu bearbeiten */
    private void editConfig() {
        RSMonitorConfig config = (RSMonitorConfig) tbMdlRang.getDataAtRow(tbEQ.getSelectedRow());
        if (config == null) {
            MessageHelper.showErrorDialog(getParent(), new HurricanGUIException("Bitte zuerst einen Datensatz auswählen"));
            return;
        }
        RsMonitorConfigRangAnlegenDialog dlg = new RsMonitorConfigRangAnlegenDialog(config);
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        if (btnChange.isEnabled()) {
            editConfig();
        }
    }

    /**
     * TableModel fuer die Darstellung der Konfiguration.
     *
     *
     */
    class RangConfigTableModel extends AKTableModel {

        protected static final int COL_PT = 0;
        protected static final int COL_PT_ADD = 1;
        protected static final int COL_SW = 2;
        protected static final int COL_ALARM = 3;

        private static final int COL_COUNT = 4;

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnCount()
         */
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnName(int)
         */
        public String getColumnName(int column) {
            switch (column) {
                case COL_PT:
                    return "Physiktyp";
                case COL_PT_ADD:
                    return "zugehöriger Physiktyp";
                case COL_SW:
                    return "Schwellwert";
                case COL_ALARM:
                    return "Alarmierung";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof RSMonitorConfig) {
                RSMonitorConfig view = (RSMonitorConfig) tmp;
                switch (column) {
                    case COL_PT:
                        return ptMap.get(view.getPhysiktyp());
                    case COL_PT_ADD:
                        return ptMap.get(view.getPhysiktypAdd());
                    case COL_SW:
                        return view.getMinCount();
                    case COL_ALARM:
                        return view.getAlarmierung();
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case COL_SW:
                    return Integer.class;
                case COL_ALARM:
                    return Boolean.class;
                default:
                    return String.class;
            }
        }
    }
}


