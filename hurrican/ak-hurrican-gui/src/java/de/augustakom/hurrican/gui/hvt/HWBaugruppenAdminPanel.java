/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.2008 15:45:26
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
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppe;
import de.augustakom.hurrican.model.cc.hardware.HWBaugruppenTyp;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.augustakom.hurrican.model.cc.hardware.HWSubrack;
import de.augustakom.hurrican.service.cc.HWService;


/**
 * Admin-Panel fuer die Verwaltung von Baugruppen
 *
 *
 */
public class HWBaugruppenAdminPanel extends AbstractAdminPanel implements AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(HWBaugruppenAdminPanel.class);

    private static final String GENERATE_PORTS = "generate.ports";
    private static final String EDIT_BAUGRUPPE = "edit.baugruppe";

    private AKJTable tbBaugruppen = null;
    private HWBaugruppeTM tbModelBaugruppen = null;

    private HVTStandort hvtStandort = null;
    private List<HWBaugruppenTyp> hwBgTypen = null;
    private List<HWRack> hwRacks = null;
    private List<HWSubrack> hwSubracks = null;

    private EditDetailsAction editAction;

    /**
     * Konstruktor
     */
    public HWBaugruppenAdminPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelBaugruppen = new HWBaugruppeTM();
        tbBaugruppen = new AKJTable(tbModelBaugruppen, JTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbBaugruppen.attachSorter();
        AKJScrollPane tableSP = new AKJScrollPane(tbBaugruppen, new Dimension(800, 250));
        tbBaugruppen.fitTable(new int[] { 100, 80, 130, 100, 100, 100, 80, 60 });
        tbBaugruppen.addMouseListener(new AKTableDoubleClickMouseListener(this));

        editAction = new EditDetailsAction();
        editAction.setParentClass(this.getClass());
        tbBaugruppen.addPopupAction(editAction);

        GeneratePortsAction generatePortsAction = new GeneratePortsAction();
        generatePortsAction.setParentClass(this.getClass());
        tbBaugruppen.addPopupAction(generatePortsAction);

        manageGUI(editAction, generatePortsAction);

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
                hwSubracks = service.findSubracksForStandort(hvtStandort.getHvtIdStandort());
                hwBgTypen = service.findAllBaugruppenTypen();

                List<HWBaugruppe> bgs = service.findBaugruppen(hvtStandort.getHvtIdStandort(), Boolean.FALSE);
                tbModelBaugruppen.setData(bgs);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(this, e);
            }
        }
        else {
            hvtStandort = null;
            tbModelBaugruppen.setData(null);
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
                    this, new HWBaugruppenEditDialog(null, hvtStandort.getHvtIdStandort()), true, true);
            if ((result != null) && (result instanceof HWBaugruppe)) {
                HWBaugruppe hwBg = (HWBaugruppe) result;
                tbModelBaugruppen.addObject(hwBg);
                tbModelBaugruppen.fireTableDataChanged();
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
    class HWBaugruppeTM extends AKTableModel<HWBaugruppe> {
        static final int COL_TYPE = 0;
        static final int COL_MANUFACTURER = 1;
        static final int COL_RACK = 2;
        static final int COL_SUBRACK = 3;
        static final int COL_INV_NO = 4;
        static final int COL_MOD_NO = 5;
        static final int COL_BEMERKUNG = 6;
        static final int COL_EINGEBAUT = 7;

        static final int COL_COUNT = 8;

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
                    return "Baugruppen-Typ";
                case COL_MANUFACTURER:
                    return "Hersteller";
                case COL_RACK:
                    return "Rack";
                case COL_SUBRACK:
                    return "Subrack";
                case COL_INV_NO:
                    return "Inventar-Nummer";
                case COL_MOD_NO:
                    return "Modul-Nummer";
                case COL_BEMERKUNG:
                    return "Bemerkung";
                case COL_EINGEBAUT:
                    return "Eingebaut";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof HWBaugruppe) {
                HWBaugruppe bg = (HWBaugruppe) tmp;
                Predicate4BGType predTyp;
                HWBaugruppenTyp type;
                switch (column) {
                    case COL_TYPE:
                        predTyp = new Predicate4BGType(bg.getHwBaugruppenTyp());
                        type = (HWBaugruppenTyp) CollectionUtils.find(hwBgTypen, predTyp);
                        return (type != null) ? type.getName() : "";
                    case COL_MANUFACTURER:
                        predTyp = new Predicate4BGType(bg.getHwBaugruppenTyp());
                        type = (HWBaugruppenTyp) CollectionUtils.find(hwBgTypen, predTyp);
                        return ((type != null) && (type.getHvtTechnik() != null)) ? type.getHvtTechnik().getHersteller() : "";
                    case COL_RACK:
                        Predicate4Rack predRack = new Predicate4Rack(bg.getRackId());
                        HWRack rack = (HWRack) CollectionUtils.find(hwRacks, predRack);
                        return (rack != null) ? rack.getAnlagenBez() + " - " + rack.getGeraeteBez() : "";
                    case COL_SUBRACK:
                        Predicate4Subrack predSubrack = new Predicate4Subrack(bg.getSubrackId());
                        HWSubrack subrack = (HWSubrack) CollectionUtils.find(hwSubracks, predSubrack);
                        return (subrack != null) ? subrack.getDisplay() : "";
                    case COL_INV_NO:
                        return bg.getInventarNr();
                    case COL_MOD_NO:
                        return bg.getModNumber();
                    case COL_BEMERKUNG:
                        return bg.getBemerkung();
                    case COL_EINGEBAUT:
                        return bg.getEingebaut();
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
            if (columnIndex == COL_EINGEBAUT) {
                return Boolean.class;
            }
            else {
                return String.class;
            }
        }
    }


    /**
     * Predicate, um nach einen bestimmten Baugruppentyp zu suchen.
     */
    static class Predicate4BGType implements Predicate {
        private HWBaugruppenTyp bgType = null;

        public Predicate4BGType(HWBaugruppenTyp bgType) {
            this.bgType = bgType;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof HWBaugruppenTyp) {
                HWBaugruppenTyp view = (HWBaugruppenTyp) obj;
                if (!NumberTools.equal(view.getId(), bgType.getId())) {
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
    static class Predicate4Rack implements Predicate {
        private Long rackId = null;

        public Predicate4Rack(Long rackId) {
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
     * Predicate, um nach einen bestimmten Rack zu suchen.
     */
    static class Predicate4Subrack implements Predicate {
        private Long subrackId = null;

        public Predicate4Subrack(Long subrackId) {
            this.subrackId = subrackId;
        }

        /**
         * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
         */
        @Override
        public boolean evaluate(Object obj) {
            if (obj instanceof HWSubrack) {
                HWSubrack view = (HWSubrack) obj;
                if (!NumberTools.equal(view.getId(), subrackId)) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Action, die Daten einer Baugruppe zu editieren.
     */
    class EditDetailsAction extends AKAbstractAction {
        public EditDetailsAction() {
            setName("Baugruppe editieren...");
            setTooltip("Oeffnet einen Dialog, um die aktuelle Baugruppe zu editieren");
            setActionCommand(EDIT_BAUGRUPPE);
        }

        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Object tmp = ((AKMutableTableModel) tbBaugruppen.getModel()).getDataAtRow(tbBaugruppen.getSelectedRow());
                if (tmp instanceof HWBaugruppe) {
                    HWBaugruppenEditDialog dialog = new HWBaugruppenEditDialog((HWBaugruppe) tmp, hvtStandort.getHvtIdStandort());
                    Object value = DialogHelper.showDialog(getParent(), dialog, true, true);

                    if (value instanceof HWBaugruppe) {
                        HWService service = getCCService(HWService.class);
                        List<HWBaugruppe> bgs = service.findBaugruppen(hvtStandort.getHvtIdStandort(), Boolean.FALSE);
                        tbModelBaugruppen.setData(bgs);
                        tbModelBaugruppen.fireTableDataChanged();
                    }
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                MessageHelper.showErrorDialog(getMainFrame(), ex);
            }
        }
    }

    /**
     * Action, um einen Dialog fuer die Port-Generierung zu oeffnen.
     */
    class GeneratePortsAction extends AKAbstractAction {
        GeneratePortsAction() {
            setName("Ports generieren...");
            setTooltip("Öffnet einen Dialog, ueber den Ports generiert/importiert werden koennen");
            setActionCommand(GENERATE_PORTS);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object tmp = ((AKMutableTableModel) tbBaugruppen.getModel()).getDataAtRow(tbBaugruppen.getSelectedRow());
            if (tmp instanceof HWBaugruppe) {
                try {
                    PortGenerationDialog portDlg = new PortGenerationDialog((HWBaugruppe) tmp);
                    DialogHelper.showDialog(getParent(), portDlg, true, true);
                }
                catch (IllegalArgumentException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    MessageHelper.showErrorDialog(getMainFrame(), ex);
                }
            }
        }
    }
}
