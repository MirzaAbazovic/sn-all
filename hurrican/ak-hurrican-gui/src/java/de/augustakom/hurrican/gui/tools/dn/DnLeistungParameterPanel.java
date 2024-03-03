/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.2005 16:40:35
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
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
import de.augustakom.hurrican.model.cc.dn.Leistung2Parameter;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.model.cc.dn.LeistungParameter;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Panel fuer die Darstellung, Neuanlage und Aenderung von Parametern zu Rufnummer-Leistungen.
 *
 *
 */
public class DnLeistungParameterPanel extends AbstractDataPanel implements AKDataLoaderComponent, AKObjectSelectionListener {

    private static final Logger LOGGER = Logger.getLogger(DnLeistungParameterPanel.class);
    private Leistung2ParameterTableModel tbMdlLeistung2ParameterModel = null;

    /* Model aus übergeordnetem Panel */
    private Leistung4Dn model = null;

    /* Map mit allen Paramtern, die der aktuellen Leistung zugeordnet sind.
     * Es wird lediglich der Key der Map verwendet (Key = ID des Parameters).
     */
    private Map leistung2Parameter = null;

    /**
     * Default-Const.
     */
    public DnLeistungParameterPanel() {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungParameterPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    public final void createGUI() {
        AKJButton bSave = getSwingFactory().createButton("save", getActionListener(), null);
        AKJButton bNew = getSwingFactory().createButton("new", getActionListener(), null);
        tbMdlLeistung2ParameterModel = new Leistung2ParameterTableModel();
        AKJTable tbLeistung2Paramter = new AKJTable(tbMdlLeistung2ParameterModel, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLeistung2Paramter.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbLeistung2Paramter.fitTable(new int[] { 20, 350, 60, 60 });
        AKJScrollPane spLp = new AKJScrollPane(tbLeistung2Paramter);
        spLp.setPreferredSize(new Dimension(550, 200));

        this.setLayout(new GridBagLayout());
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(bSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 0, 1, 0, GridBagConstraints.NONE));
        this.add(bNew, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.NONE));
        this.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(spLp, GBCFactory.createGBC(0, 0, 0, 1, 5, 1, GridBagConstraints.NONE));
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    protected void execute(String command) {
        try {
            if ("save".equals(command)) {
                saveModel();
            }
            else if ("new".equals(command)) {
                LeistungParameter leistungParameter = new LeistungParameter();
                objectSelected(leistungParameter);
                loadData();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        DnLeistungParameterDialog dlg = new DnLeistungParameterDialog((LeistungParameter) selection);
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            CCRufnummernService service = getCCService(CCRufnummernService.class);
            List dnParameter = service.findAllParameter();
            tbMdlLeistung2ParameterModel.setData(dnParameter);

            leistung2Parameter = new HashMap();
            if (this.model != null) {
                CCRufnummernService serviceDn = getCCService(CCRufnummernService.class);
                List p2Leistung = serviceDn.findLeistung2Parameter(model.getId());
                if (p2Leistung != null) {
                    for (Iterator iter = p2Leistung.iterator(); iter.hasNext(); ) {
                        Leistung2Parameter element = (Leistung2Parameter) iter.next();
                        leistung2Parameter.put(element.getLeistungId(), true);
                    }
                }
                tbMdlLeistung2ParameterModel.fireTableDataChanged();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#readModel()
     */
    public void readModel() {
        if (this.model == null) { return; }
        leistung2Parameter.clear();
        try {
            CCRufnummernService serviceDn = getCCService(CCRufnummernService.class);
            List<Leistung2Parameter> param2LeistungIds = serviceDn.findLeistung2Parameter(model.getId());
            CollectionMapConverter.convert2Map(param2LeistungIds, leistung2Parameter, "getParameterId", null);
            tbMdlLeistung2ParameterModel.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#saveModel()
     */
    public void saveModel() throws AKGUIException {
        if (this.model == null) { return; }
        CCRufnummernService serviceDn;
        try {
            serviceDn = getCCService(CCRufnummernService.class);
            Collection l2p = (leistung2Parameter != null) ? leistung2Parameter.keySet() : new HashSet();
            serviceDn.saveLeistung2Parameter(model.getId(), l2p);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) {
        if (model instanceof Leistung4Dn) {
            this.model = (Leistung4Dn) model;
            readModel();
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#getModel()
     */
    public Object getModel() {
        return null;
    }

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#hasModelChanged()
     */
    public boolean hasModelChanged() {
        return false;
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

    /*
     * TableModel fuer die Darstellung der Leistungs-Parameter.
     */
    class Leistung2ParameterTableModel extends AKTableModel {
        static final int COL_ID = 0;
        static final int COL_BESCHREIBUNG = 1;
        static final int COL_MEHRFACH = 2;
        static final int COL_MEHRFACH_IMS = 3;
        static final int COL_USE = 4;

        static final int COL_COUNT = 5;

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int column) {
            switch (column) {
                case COL_ID:
                    return "ID";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
                case COL_MEHRFACH:
                    return "Mehrfach";
                case COL_MEHRFACH_IMS:
                    return "Mehrfach IMS";
                case COL_USE:
                    return "verwenden";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof LeistungParameter) {
                LeistungParameter lp = (LeistungParameter) o;
                switch (column) {
                    case COL_ID:
                        return lp.getId();
                    case COL_BESCHREIBUNG:
                        return lp.getLeistungParameterBeschreibung();
                    case COL_MEHRFACH:
                        return lp.getLeistungParameterMehrfach();
                    case COL_MEHRFACH_IMS:
                        return lp.getLeistungParameterMehrfachIms();
                    case COL_USE:
                        return (leistung2Parameter != null && leistung2Parameter.containsKey(lp.getId()))
                                ? Boolean.TRUE : Boolean.FALSE;
                    default:
                        break;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object aValue, int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof LeistungParameter) {
                LeistungParameter lp = (LeistungParameter) o;
                if (leistung2Parameter == null) {
                    leistung2Parameter = new HashMap();
                }

                if (aValue instanceof Boolean) {
                    if (((Boolean) aValue).booleanValue()) {
                        leistung2Parameter.put(lp.getId(), null);
                    }
                    else {
                        leistung2Parameter.remove(lp.getId());
                    }
                }
            }
        }

        /**
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int column) {
            return (column == COL_USE) ? true : false;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_USE) ? Boolean.class : super.getColumnClass(columnIndex);
        }
    }
}

