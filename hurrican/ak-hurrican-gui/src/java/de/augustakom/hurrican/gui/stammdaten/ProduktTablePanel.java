/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2004 09:23:52
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.DateTableCellRenderer;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.service.cc.ProduktService;


/**
 * Panel fuer die Uebersicht aller definierten Produkte.
 *
 *
 */
public class ProduktTablePanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(ProduktTablePanel.class);

    private AbstractAdminPanel detailPanel = null;
    private AKJTable tbProdukte = null;
    private ProduktTableModel tbMdlProdukte = null;

    /**
     * Konstruktor
     */
    public ProduktTablePanel(AbstractAdminPanel detailPanel) {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ProduktTablePanel.xml");
        this.detailPanel = detailPanel;
        createGUI();
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
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlProdukte = new ProduktTableModel();
        tbProdukte = new AKJTable(tbMdlProdukte, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbProdukte.attachSorter();
        tbProdukte.addMouseListener(getTableListener());
        tbProdukte.addKeyListener(getTableListener());
        tbProdukte.setDefaultRenderer(Date.class, new DateTableCellRenderer(DateTools.PATTERN_DAY_MONTH_YEAR));
        tbProdukte.fitTable(new int[] { 50, 100, 150, 70, 80, 70, 60, 100, 50, 50 });
        AKJScrollPane spProdukte = new AKJScrollPane(tbProdukte);
        spProdukte.setPreferredSize(new Dimension(700, 220));

        AKAbstractAction createMatrixAction = (AKAbstractAction) getSwingFactory().createAction("create.matrix");
        AKAbstractAction konfigAction = (AKAbstractAction) getSwingFactory().createAction("open.prod2prod.konfig");
        AKAbstractAction prod2egAction = (AKAbstractAction) getSwingFactory().createAction("open.prod2eg.konfig");

        tbProdukte.addPopupAction(createMatrixAction);
        tbProdukte.addPopupSeparator();
        tbProdukte.addPopupAction(konfigAction);
        tbProdukte.addPopupAction(prod2egAction);

        manageGUI(createMatrixAction, konfigAction, prod2egAction);

        this.setLayout(new GridBagLayout());
        this.add(spProdukte, GBCFactory.createGBC(100, 100, 0, 0, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        try {
            setWaitCursor();
            tbProdukte.setWaitCursor();
            showProgressBar("laden...");

            ProduktService service = getCCService(ProduktService.class);
            List<Produkt> produkte = service.findProdukte(false);
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
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        detailPanel.showDetails(details);
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

    /* TableModel fuer die Darstellung der Produkte */
    static class ProduktTableModel extends AKTableModel<Produkt> {
        private static final int COL_ID = 0;
        private static final int COL_PROD_NR = 1;
        private static final int COL_ANSCHLUSSART = 2;
        private static final int COL_GUELTIG_VON = 3;
        private static final int COL_GUELTIG_BIS = 4;
        private static final int COL_ACC_VORSATZ = 5;
        private static final int COL_EL_VERLAUF = 6;
        private static final int COL_ENDSTELLENTYP = 7;
        private static final int COL_KIND_OF_USE_PRODUCT = 8;
        private static final int COL_KIND_OF_USE_TYPE = 9;
        private static final int COL_KIND_OF_USE_TYPE_VPN = 10;

        private static final int COL_COUNT = 11;

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
                case COL_PROD_NR:
                    return "Produkt-Nr";
                case COL_ANSCHLUSSART:
                    return "Anschlussart";
                case COL_GUELTIG_VON:
                    return "von";
                case COL_GUELTIG_BIS:
                    return "bis";
                case COL_ACC_VORSATZ:
                    return "Acc-Vorsatz";
                case COL_EL_VERLAUF:
                    return "el-Verlauf";
                case COL_ENDSTELLENTYP:
                    return "Endstellen-Typ";
                case COL_KIND_OF_USE_PRODUCT:
                    return "Produkt";
                case COL_KIND_OF_USE_TYPE:
                    return "Typ";
                case COL_KIND_OF_USE_TYPE_VPN:
                    return "Typ VPN";
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
            if (o instanceof Produkt) {
                Produkt p = (Produkt) o;
                switch (column) {
                    case COL_ID:
                        return p.getId();
                    case COL_PROD_NR:
                        return p.getProduktNr();
                    case COL_ANSCHLUSSART:
                        return p.getAnschlussart();
                    case COL_GUELTIG_VON:
                        return p.getGueltigVon();
                    case COL_GUELTIG_BIS:
                        return p.getGueltigBis();
                    case COL_ACC_VORSATZ:
                        return p.getAccountVorsatz();
                    case COL_EL_VERLAUF:
                        return p.getElVerlauf();
                    case COL_ENDSTELLENTYP:
                        Integer esTyp = p.getEndstellenTyp();
                        if (Produkt.ES_TYP_KEINE_ENDSTELLEN.equals(esTyp)) {
                            return "keine Endstellen";
                        }
                        else if (Produkt.ES_TYP_NUR_B.equals(esTyp)) {
                            return "nur Endstelle B";
                        }
                        else if (Produkt.ES_TYP_A_UND_B.equals(esTyp)) {
                            return "Endstellen A+B";
                        }
                        return null;
                    case COL_KIND_OF_USE_PRODUCT:
                        return p.getVbzKindOfUseProduct();
                    case COL_KIND_OF_USE_TYPE:
                        return p.getVbzKindOfUseType();
                    case COL_KIND_OF_USE_TYPE_VPN:
                        return p.getVbzKindOfUseTypeVpn();
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
            if (columnIndex == COL_EL_VERLAUF) {
                return Boolean.class;
            }
            else if ((columnIndex == COL_GUELTIG_VON) || (columnIndex == COL_GUELTIG_BIS)) {
                return Date.class;
            }
            else if (columnIndex == COL_ID) {
                return Long.class;
            }
            return super.getColumnClass(columnIndex);
        }
    }

}


