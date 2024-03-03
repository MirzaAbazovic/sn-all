/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.10.2009 11:36:34
 */
package de.augustakom.hurrican.gui.hvt;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKMutableTableModel;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.NumberTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrackTyp;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Admin-Panel fuer die Verwaltung von Subracks
 *
 *
 */
public class HWSubrackAdminPanel extends AbstractAdminPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HWSubrackAdminPanel.class);

    private AKJTable tbSubracks = null;
    private AKTableModel<HWSubrack> tbModelSubracks = null;

    private HVTStandort hvtStandort = null;
    private List<HWSubrackTyp> hwSubrackTypen = null;
    private List<HWRack> hwRacks = null;

    private EditDetailsAction editAction;

    /**
     * Konstruktor
     */
    public HWSubrackAdminPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelSubracks = new HWSubrackTM();
        tbSubracks = new AKJTable(tbModelSubracks, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        AKJScrollPane tableSP = new AKJScrollPane(tbSubracks, new Dimension(600, 250));
        tbSubracks.fitTable(new int[] { 100, 130, 100, 120, 120 });
        tbSubracks.addMouseListener(new AKTableDoubleClickMouseListener(this));
        editAction = new EditDetailsAction();
        editAction.setParentClass(this.getClass());
        tbSubracks.addPopupAction(editAction);

        manageGUI(editAction);

        this.setLayout(new BorderLayout());
        this.add(tableSP, BorderLayout.WEST);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof HVTStandort) {
            try {
                hvtStandort = (HVTStandort) details;
                HWService service = getCCService(HWService.class);
                hwRacks = service.findRacks(hvtStandort.getHvtIdStandort());
                hwSubrackTypen = service.findAllSubrackTypes();

                List<HWSubrack> subracks = service.findSubracksForStandort(hvtStandort.getHvtIdStandort());
                tbModelSubracks.setData(subracks);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            hvtStandort = null;
            tbModelSubracks.setData(null);
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        if (hvtStandort != null) {
            Object result = DialogHelper.showDialog(
                    this, new HWSubrackEditDialog(null, hvtStandort.getHvtIdStandort()), true, true);
            if ((result != null) && (result instanceof HWSubrack)) {
                HWSubrack hwSubrack = (HWSubrack) result;
                tbModelSubracks.addObject(hwSubrack);
                tbModelSubracks.fireTableDataChanged();
            }
        }
        else {
            MessageHelper.showInfoDialog(this, "Bitte zuerst einen HVT-Standort auswählen!");
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    @Override
    public void objectSelected(Object selection) {
        if (editAction.isEnabled()) {
            editAction.actionPerformed(null);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }


    /**
     * TableModel fuer die HVT-Standorte.
     */
    private class HWSubrackTM extends AKTableModel<HWSubrack> {
        static final int COL_TYPE = 0;
        static final int COL_RACK = 1;
        static final int COL_MOD_NO = 2;
        static final int COL_BG_COUNT = 3;
        static final int COL_PORT_COUNT = 4;
        static final int COL_COUNT = 5;

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
                case COL_TYPE:
                    return "Subrack-Typ";
                case COL_RACK:
                    return "Rack";
                case COL_MOD_NO:
                    return "Modul-Nummer";
                case COL_BG_COUNT:
                    return "Baugruppen";
                case COL_PORT_COUNT:
                    return "Ports pro Baugruppe";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object obj = getDataAtRow(row);
            if (obj instanceof HWSubrack) {
                HWSubrack subRack = (HWSubrack) obj;
                switch (column) {
                    case COL_TYPE:
                        Predicate4SubrackType pred1 = new Predicate4SubrackType();
                        pred1.setPredicateValues(subRack.getSubrackTyp().getId());
                        HWSubrackTyp type = (HWSubrackTyp) CollectionUtils.find(hwSubrackTypen, pred1);
                        return (type != null) ? type.getName() : "";
                    case COL_RACK:
                        Predicate4Rack pred2 = new Predicate4Rack();
                        pred2.setPredicateValues(subRack.getRackId());
                        HWRack rack = (HWRack) CollectionUtils.find(hwRacks, pred2);
                        return (rack != null) ? rack.getAnlagenBez() : "";
                    case COL_MOD_NO:
                        return subRack.getModNumber();
                    case COL_BG_COUNT:
                        return subRack.getSubrackTyp().getBgCount();
                    case COL_PORT_COUNT:
                        return subRack.getSubrackTyp().getPortCount();
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
            if ((columnIndex == COL_BG_COUNT) || (columnIndex == COL_PORT_COUNT)) {
                return Integer.class;
            }
            else {
                return String.class;
            }
        }
    }

    /**
     * Predicate, um nach einen bestimmten Subrack-Typ zu suchen.
     */
    private static class Predicate4SubrackType implements Predicate {

        private Long subrackTypeId = null;

        /**
         * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
         *
         * @param id
         */
        public void setPredicateValues(Long subrackTypeId) {
            this.subrackTypeId = subrackTypeId;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof HWSubrackTyp) {
                HWSubrackTyp view = (HWSubrackTyp) obj;
                if (!NumberTools.equal(view.getId(), subrackTypeId)) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }

    /**
     * Predicate, um nach einen bestimmten Rack zu suchen.
     */
    private static class Predicate4Rack implements Predicate {

        private Long rackId = null;

        /**
         * Uebergibt dem Predicate die Werte, nach denen gefiltert werden soll.
         *
         * @param rackId
         */
        public void setPredicateValues(Long rackId) {
            this.rackId = rackId;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof HWRack) {
                HWRack view = (HWRack) obj;
                if (!NumberTools.equal(view.getId(), rackId)) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }

    /**
     * Action, die Daten eines Subracks zu editieren.
     */
    private class EditDetailsAction extends AKAbstractAction {
        public EditDetailsAction() {
            setName("Subrack editieren");
            setTooltip("Subrack editieren");
            setActionCommand("edit.subrack");
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object tmp = ((AKMutableTableModel) tbSubracks.getModel()).getDataAtRow(tbSubracks.getSelectedRow());
                if (tmp instanceof HWSubrack) {
                    HWSubrackEditDialog dialog = new HWSubrackEditDialog((HWSubrack) tmp, hvtStandort.getHvtIdStandort());
                    Object value = DialogHelper.showDialog(getParent(), dialog, true, true);

                    if (value instanceof HWSubrack) {
                        HWService service = getCCService(HWService.class);
                        List<HWSubrack> subracks = service.findSubracksForStandort(hvtStandort.getHvtIdStandort());
                        tbModelSubracks.setData(subracks);
                        tbModelSubracks.fireTableDataChanged();
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }
}


