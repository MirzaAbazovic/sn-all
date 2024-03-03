/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.02.2006 15:04:12
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.service.billing.OEService;


/**
 * Panel für die Übersicht aller Produkte aus dem Billing-System
 *
 *
 */
public class ProduktBillingTablePanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(ProduktBillingTablePanel.class);

    private int strategy = OEService.FIND_STRATEGY_ALL;

    private AbstractAdminPanel detailPanel = null;
    private AKJTable tbProdukte = null;
    private ProduktBillingTableModel tbMdlProdukte = null;

    /**
     * Konstruktor mit Angabe des Detail-Panels.
     *
     * @param detailPanel
     */
    public ProduktBillingTablePanel(AbstractAdminPanel detailPanel) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktBillingTablePanel.xml");
        this.detailPanel = detailPanel;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJButton btnFilterAll = getSwingFactory().createButton("flt.all", getActionListener());
        AKJButton btnFilterDn = getSwingFactory().createButton("flt.dn", getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());

        btnPanel.add(btnFilterAll, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnFilterDn, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        tbMdlProdukte = new ProduktBillingTableModel();
        tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdukte.attachSorter();
        tbProdukte.addMouseListener(getTableListener());
        tbProdukte.addKeyListener(getTableListener());
        tbProdukte.setDefaultRenderer(Date.class, new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbProdukte.fitTable(new int[] { 50, 250, 250, 70, 70 });
        AKJScrollPane spProdukte = new AKJScrollPane(tbProdukte);
        spProdukte.setPreferredSize(new Dimension(700, 250));

        this.setLayout(new BorderLayout());
        this.add(btnPanel, BorderLayout.NORTH);
        this.add(spProdukte, BorderLayout.CENTER);
    }


    /**
     * Gibt die Tabelle zurueck.
     *
     * @return
     */
    protected AKJTable getProdukteTable() {
        return tbProdukte;
    }

    /**
     * @see de.augustakom.common.gui.swing.table.AKTableOwner#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        detailPanel.showDetails(details);
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            tbProdukte.setWaitCursor();
            showProgressBar("laden...");
            OEService service = getBillingService(OEService.class);
            List<OE> produkte = service.findOEByOeTyp(OE.OE_OETYP_PRODUKT, strategy);
            tbMdlProdukte.setData(produkte);
        }
        catch (Exception e) {
            stopProgressBar();
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            tbProdukte.setDefaultCursor();
            stopProgressBar();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        LOGGER.info(this.getClass().getName() + ".createNew not implemented!");
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        LOGGER.info(this.getClass().getName() + ".saveData not implemented!");
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if ("flt.all".equals(command)) {
            strategy = OEService.FIND_STRATEGY_ALL;
            loadData();
        }
        else if ("flt.dn".equals(command)) {
            strategy = OEService.FIND_STRATEGY_HAS_DN;
            loadData();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
    }

    /* TableModel fuer die Darstellung der Produkte */
    static class ProduktBillingTableModel extends AKTableModel<OE> {
        private static final int COL_OE__NO = 0;
        private static final int COL_NAME = 1;
        private static final int COL_BEZEICHNUNG_D = 2;
        private static final int COL_GUELTIG_VON = 3;
        private static final int COL_GUELTIG_BIS = 4;

        private static final int COL_COUNT = 5;

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
                case COL_OE__NO:
                    return "OE__NO";
                case COL_NAME:
                    return "Name";
                case COL_BEZEICHNUNG_D:
                    return "Bezeichnung_D";
                case COL_GUELTIG_VON:
                    return "von";
                case COL_GUELTIG_BIS:
                    return "bis";
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
            if (o instanceof OE) {
                OE p = (OE) o;
                switch (column) {
                    case COL_OE__NO:
                        return p.getOeNoOrig();
                    case COL_NAME:
                        return p.getName();
                    case COL_BEZEICHNUNG_D:
                        return p.getRechnungstext();
                    case COL_GUELTIG_VON:
                        return p.getGueltigVon();
                    case COL_GUELTIG_BIS:
                        return p.getGueltigBis();
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

            if ((columnIndex == COL_GUELTIG_VON) || (columnIndex == COL_GUELTIG_BIS)) {
                return Date.class;
            }
            else if (columnIndex == COL_OE__NO) {
                return Long.class;
            }
            return super.getColumnClass(columnIndex);
        }
    }

}


