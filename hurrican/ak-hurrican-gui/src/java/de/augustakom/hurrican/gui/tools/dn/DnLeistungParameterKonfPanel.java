/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.02.2006 17:19:58
 */
package de.augustakom.hurrican.gui.tools.dn;

import java.awt.*;
import java.util.List;
import java.util.*;
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
import de.augustakom.common.gui.swing.table.AKTableListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableOwner;
import de.augustakom.hurrican.gui.base.AbstractDataPanel;
import de.augustakom.hurrican.model.cc.dn.Leistung4Dn;
import de.augustakom.hurrican.service.cc.CCRufnummernService;

/**
 * Panel zur Definition von Leistungs-Parametern.
 *
 *
 */
public class DnLeistungParameterKonfPanel extends AbstractDataPanel implements AKTableOwner,
        AKObjectSelectionListener, AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(DnLeistungParameterKonfPanel.class);

    private LeistungTableModel tbmDnLeistung = null;

    /* Konfigurationspanels */
    private DnLeistungParameterPanel panelParameter = null;

    /**
     * Default-Const.
     */
    public DnLeistungParameterKonfPanel() {
        super("de/augustakom/hurrican/gui/tools/dn/resources/DnLeistungParameterKonfPanel.xml");
        createGUI();
        loadData();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnNewLeistung = getSwingFactory().createButton("new.leistung", getActionListener());
        btnNewLeistung.setBorder(null);
        AKJButton btnDeleteLeistung = getSwingFactory().createButton("delete.leistung", getActionListener());
        btnDeleteLeistung.setBorder(null);

        tbmDnLeistung = new LeistungTableModel();
        AKJTable tbLeistung = new AKJTable(tbmDnLeistung, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbLeistung.attachSorter();
        tbLeistung.addMouseListener(new AKTableListener(this));
        tbLeistung.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbLeistung.addKeyListener(new AKTableListener(this));
        tbLeistung.fitTable(new int[] { 20, 200, 200, 200, 200 });
        AKJScrollPane spLeistung = new AKJScrollPane(tbLeistung);
        spLeistung.setPreferredSize(new Dimension(830, 250));

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Leistungen"));

        top.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        top.add(btnNewLeistung, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(spLeistung, GBCFactory.createGBC(0, 0, 0, 1, 3, 1, GridBagConstraints.HORIZONTAL));

        panelParameter = new DnLeistungParameterPanel();
        panelParameter.setBorder(BorderFactory.createTitledBorder("Parameter"));

        AKJPanel bottom = new AKJPanel(new GridBagLayout());
        bottom.add(panelParameter, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        bottom.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.NONE));

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(bottom, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    public final void loadData() {
        try {
            setWaitCursor();
            showProgressBar("laden...");

            CCRufnummernService service = getCCService(CCRufnummernService.class);
            List<Leistung4Dn> leistung = service.findLeistungen();
            tbmDnLeistung.setData(leistung);
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

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("new.leistung".equals(command)) {
            try {
                Leistung4Dn leistung4Dn = new Leistung4Dn();
                objectSelected(leistung4Dn);
                loadData();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
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

    /**
     * @see de.augustakom.common.gui.iface.AKModelOwner#setModel(java.util.Observable)
     */
    public void setModel(Observable model) throws AKGUIException {
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
    public void update(Observable arg0, Object arg1) {
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    public void showDetails(Object details) {
        if (details instanceof Leistung4Dn) {
            panelParameter.setModel((Observable) details);
        }
    }

    /**
     * @see de.augustakom.common.gui.iface.AKObjectSelectionListener#objectSelected(java.lang.Object)
     */
    public void objectSelected(Object selection) {
        DnLeistungLeistungDialog dlg = new DnLeistungLeistungDialog((Leistung4Dn) selection);
        DialogHelper.showDialog(getMainFrame(), dlg, true, true);
    }

    /* TableModel fuer die Darstellung der Leistung */
    static class LeistungTableModel extends AKTableModel<Leistung4Dn> {
        public static final int COL_ID = 0;
        public static final int COL_LEISTUNG = 1;
        public static final int COL_BESCHREIBUNG = 2;
        public static final int COL_EXTERN_LEISTUNG_NO = 3;
        public static final int COL_EXTERN_SONSTIGES_NO = 4;

        public static final int COL_COUNT = 5;

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
                case COL_LEISTUNG:
                    return "Leistung";
                case COL_BESCHREIBUNG:
                    return "Beschreibung";
                case COL_EXTERN_LEISTUNG_NO:
                    return "extern Leistung";
                case COL_EXTERN_SONSTIGES_NO:
                    return "extern Sonstiges";
                default:
                    return "";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object o = getDataAtRow(row);
            if (o instanceof Leistung4Dn) {
                Leistung4Dn l = (Leistung4Dn) o;
                switch (column) {
                    case COL_ID:
                        return l.getId();
                    case COL_LEISTUNG:
                        return l.getLeistung();
                    case COL_BESCHREIBUNG:
                        return l.getBeschreibung();
                    case COL_EXTERN_LEISTUNG_NO:
                        return l.getExternLeistungNo();
                    case COL_EXTERN_SONSTIGES_NO:
                        return l.getExternSonstigesNo();
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
    }

}


