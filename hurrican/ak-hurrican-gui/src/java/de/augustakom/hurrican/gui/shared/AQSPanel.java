/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 08:19:02
 */
package de.augustakom.hurrican.gui.shared;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.iface.AKObjectSelectionListener;
import de.augustakom.common.gui.iface.AKSimpleModelOwner;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableDoubleClickMouseListener;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.auftrag.AuftragDataFrame;
import de.augustakom.hurrican.gui.base.AbstractServicePanel;
import de.augustakom.hurrican.model.cc.GeoId;
import de.augustakom.hurrican.model.cc.view.AQSView;
import de.augustakom.hurrican.model.shared.iface.CCAuftragModel;
import de.augustakom.hurrican.service.cc.CarrierService;


/**
 * Panel fuer die Anzeige aller bisher eingetragenen Aderquerschnitte fuer eine Strasse.
 *
 *
 */
public class AQSPanel extends AbstractServicePanel implements AKDataLoaderComponent, AKSimpleModelOwner, AKObjectSelectionListener {
    private static final Logger LOGGER = Logger.getLogger(AQSPanel.class);

    private GeoId geoId = null;

    private AKJTable tbAQS = null;
    private AQSTableModel tbMdlAQS = null;

    /**
     * Konstruktor
     */
    public AQSPanel() {
        super(null);
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlAQS = new AQSTableModel();
        tbAQS = new AKJTable(tbMdlAQS, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tbAQS.addMouseListener(new AKTableDoubleClickMouseListener(this));
        tbAQS.attachSorter();
        tbAQS.fitTable(new int[] { 200, 100, 100, 200 });

        AKJScrollPane spAQS = new AKJScrollPane(tbAQS);
        spAQS.setPreferredSize(new Dimension(425, 150));

        this.setLayout(new BorderLayout());
        this.add(spAQS, BorderLayout.CENTER);
    }

    @Override
    public Object getModel() {
        return geoId;
    }

    @Override
    public void setModel(Observable model) {
        this.geoId = (model instanceof GeoId) ? (GeoId) model : null;
        loadData();
    }

    @Override
    public final void loadData() {
        if (geoId != null) {
            try {
                setWaitCursor();
                tbAQS.setWaitCursor();
                showProgressBar("laden...");

                CarrierService carrierService = getCCService(CarrierService.class);
                List<AQSView> result = carrierService.findAqsLL4GeoId(geoId.getId());
                tbMdlAQS.setData(result);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(getMainFrame(), e);
            }
            finally {
                setDefaultCursor();
                tbAQS.setDefaultCursor();
                stopProgressBar();
            }
        }
        else {
            tbMdlAQS.setData(null);
        }
    }

    @Override
    protected void execute(String command) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void objectSelected(Object selection) {
        if (selection instanceof CCAuftragModel) {
            AuftragDataFrame.openFrame((CCAuftragModel) selection);
        }
    }


    /**
     * TableModel fuer die Aderquerschnitte.
     */
    static class AQSTableModel extends AKTableModel<AQSView> {
        private static final int COL_ENDSTELLE = 0;
        private static final int COL_AQS = 1;
        private static final int COL_LL = 2;
        private static final int COL_AUFTRAG = 3;


        private static final int COL_COUNT = 4;

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
                case COL_ENDSTELLE:
                    return "Endstelle";
                case COL_AQS:
                    return "Aderquerschnitte";
                case COL_LL:
                    return "Leitungslängen";
                case COL_AUFTRAG:
                    return "Techn. Auftrags-Nr";
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
            if (o instanceof AQSView) {
                AQSView view = (AQSView) o;
                switch (column) {
                    case COL_ENDSTELLE:
                        return view.getEndstelle();
                    case COL_AQS:
                        return view.getAqs();
                    case COL_LL:
                        return view.getLl();
                    case COL_AUFTRAG:
                        return view.getAuftragId();
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


