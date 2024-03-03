/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 15:33:32
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTGruppe;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.service.cc.HVTService;


/**
 * Panel fuer die Administration der HVT-Gruppen.
 *
 *
 */
public class HVTGruppenAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(HVTGruppenAdminPanel.class);

    private AKJTable tbHVT = null;
    private HVTGruppenTM tbModelHVT = null;
    private boolean loaded = false;

    private HVTGruppePanel hvtGruppePanel = null;
    protected HVTStandortAdminPanel hvtStandortAdminPanel = null;

    protected void setHvtStandortAdminPanel(HVTStandortAdminPanel hvtStandortAdminPanel) {
        this.hvtStandortAdminPanel = hvtStandortAdminPanel;
    }

    /**
     * Standardkonstruktor
     */
    public HVTGruppenAdminPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelHVT = new HVTGruppenTM();
        tbHVT = new AKJTable(tbModelHVT, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbHVT.attachSorter();
        tbHVT.addMouseListener(getTableListener());
        tbHVT.addKeyListener(getTableListener());
        AKJScrollPane tableSP = new AKJScrollPane(tbHVT);
        tableSP.setPreferredSize(new Dimension(750, 270));
        fitTable();

        hvtGruppePanel = new HVTGruppePanel(true);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tableSP);
        split.setBottomComponent(hvtGruppePanel);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /* Passt die Spaltenbreiten der Tabelle an. */
    private void fitTable() {
        TableColumn column;
        for (int i = 0; i < tbHVT.getColumnCount(); i++) {
            column = tbHVT.getColumnModel().getColumn(i);
            if ((i >= HVTGruppenTM.COL_MON) && (i <= HVTGruppenTM.COL_FRE)) {
                column.setPreferredWidth(50);
            }
            else if ((i == HVTGruppenTM.COL_ORTSTEIL) || (i == HVTGruppenTM.COL_STRASSE)) {
                column.setPreferredWidth(150);
            }
            else {
                column.setPreferredWidth(60);
            }
        }
    }

    public void updateData(Map<Long, HVTGruppe> hvtGruppenMap, List<HVTGruppe> hvtGruppenList, List<HVTStandort> hvtStandorts) {
        tbModelHVT.setData(hvtGruppenList);
    }

    public void addToGruppenList(HVTGruppe hvtGruppe) {
        // no duplicates please
        if (tbModelHVT.getData() == null) {
            tbModelHVT.addObject(hvtGruppe);
        }
        else if (!tbModelHVT.getData().contains(hvtGruppe)) {
            tbModelHVT.addObject(hvtGruppe);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        // done in HVTAdminSearchPanel by button click
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof HVTGruppe) {
            hvtGruppePanel.setModel((HVTGruppe) details);
        }
        else {
            hvtGruppePanel.setModel(null);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        hvtGruppePanel.setModel(null);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            HVTGruppe toSave = (HVTGruppe) hvtGruppePanel.getModel();
            boolean isNew = toSave.getId() == null;

            HVTService service = getCCService(HVTService.class);
            service.saveHVTGruppe(toSave);

            if (isNew) {
                tbModelHVT.addObject(toSave);
            }
            hvtStandortAdminPanel.hvtStandortDetailAdminPanel.refreshHVTGruppen();
            tbModelHVT.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    @SuppressWarnings("squid:UnusedProtectedMethod")
    protected void execute(String command) {
        // not needed for this panel
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
        // not needed for this panel
    }

    /**
     * TableModel fuer die HVT-Gruppen.
     */
    static class HVTGruppenTM extends AKTableModel<HVTGruppe> {
        static final int COL_ID = 0;
        static final int COL_ONKZ = 1;
        static final int COL_ORTSTEIL = 2;
        static final int COL_STRASSE = 3;
        static final int COL_SWITCH = 4;
        static final int COL_MON = 5;
        static final int COL_DIE = 6;
        static final int COL_MIT = 7;
        static final int COL_DON = 8;
        static final int COL_FRE = 9;

        static final int COL_COUNT = 10;

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
                case COL_ID:
                    return "ID";
                case COL_ONKZ:
                    return "ONKZ";
                case COL_ORTSTEIL:
                    return "HVT-Name";
                case COL_STRASSE:
                    return "Strasse";
                case COL_SWITCH:
                    return "Switch";
                case COL_MON:
                    return "Montag";
                case COL_DIE:
                    return "Dienstag";
                case COL_MIT:
                    return "Mittwoch";
                case COL_DON:
                    return "Donnerstag";
                case COL_FRE:
                    return "Freitag";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            HVTGruppe hvt = getDataAtRow(row);
            if (hvt != null) {
                switch (column) {
                    case COL_ID:
                        return hvt.getId();
                    case COL_ONKZ:
                        return hvt.getOnkz();
                    case COL_ORTSTEIL:
                        return hvt.getOrtsteil();
                    case COL_STRASSE:
                        return hvt.getStreetAndHouseNum();
                    case COL_SWITCH:
                        return (hvt.getHwSwitch() != null) ? hvt.getHwSwitch().getName() : null;
                    case COL_MON:
                        return hvt.getMontag();
                    case COL_DIE:
                        return hvt.getDienstag();
                    case COL_MIT:
                        return hvt.getMittwoch();
                    case COL_DON:
                        return hvt.getDonnerstag();
                    case COL_FRE:
                        return hvt.getFreitag();
                    default:
                        return null;
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
            if ((columnIndex >= COL_MON) && (columnIndex <= COL_FRE)) {
                return Boolean.class;
            }
            else if (columnIndex == COL_ID) {
                return Long.class;
            }

            return super.getColumnClass(columnIndex);
        }
    }

}


