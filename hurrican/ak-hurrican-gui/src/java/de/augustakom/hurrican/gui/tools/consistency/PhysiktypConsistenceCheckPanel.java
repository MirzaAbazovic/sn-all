/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.08.2005 13:57:51
 */
package de.augustakom.hurrican.gui.tools.consistency;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.MapTools;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.RangierungsService;


/**
 * ConsistenceCheck-Panel, um die Physiktypen von Auftraegen zu pruefen.
 *
 *
 */
public class PhysiktypConsistenceCheckPanel extends AbstractConsistenceCheckPanel<Void, Map<Long, String>> {

    private static final Logger LOGGER = Logger.getLogger(PhysiktypConsistenceCheckPanel.class);

    private static final int INCREMENT = 100;


    private AKJFormattedTextField tfStartId = null;
    private AKJFormattedTextField tfEndId = null;
    private PhysiktypConsistenceTableModel tbMdlResult = null;
    private Map<Long, String> invalid = null;

    public PhysiktypConsistenceCheckPanel() {
        super("de/augustakom/hurrican/gui/tools/consistency/resources/PhysiktypConsistenceCheckPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblStartId = getSwingFactory().createLabel("start.id");
        AKJLabel lblEndId = getSwingFactory().createLabel("end.id");
        tfStartId = getSwingFactory().createFormattedTextField("start.id");
        tfEndId = getSwingFactory().createFormattedTextField("end.id");

        tbMdlResult = new PhysiktypConsistenceTableModel();
        AKJTable tbResult = new AKJTable(tbMdlResult, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbResult.attachSorter();
        tbResult.fitTable(new int[] { 100, 500 });

        AKJPanel top = new AKJPanel(new GridBagLayout());
        top.add(lblStartId, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        top.add(tfStartId, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(lblEndId, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 20, 2, 2)));
        top.add(tfEndId, GBCFactory.createGBC(0, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL, new Insets(2, 10, 2, 2)));
        top.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 4, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(top, BorderLayout.NORTH);
        getChildPanel().add(new AKJScrollPane(tbResult), BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.tools.consistency.AbstractConsistenceCheckPanel#executeCheckConsistence()
     */
    // Suppress Warnings wegen publish(Map<_,_>, ...) mit Generics!
    @SuppressWarnings("unchecked")
    @Override
    public void executeCheckConsistence() {
        swingWorker = new SwingWorker<Void, Map<Long, String>>() {

            // Initialize required data from panel in the constructor since this should be done in the EDT thread.
            final Long start = tfStartId.getValueAsLong(null);
            final Long end = tfEndId.getValueAsLong(null);

            @Override
            public Void doInBackground() throws Exception {
                long from = (start == null) ? 1 : start;
                long to = (end == null) ? getCCService(CCAuftragService.class).findMaxAuftragId() : end;
                if (to == 0) {
                    throw new Exception("Die höchste Auftrags-ID konnte nicht ermittelt werden!");
                }

                RangierungsService rs = getCCService(RangierungsService.class);
                for (long i = from; i <= to; i += INCREMENT) {
                    if (isCancelled()) {
                        return null;
                    }
                    Map<Long, String> result = rs
                            .checkPhysiktypen(i, ((i + INCREMENT) < to) ? (i + INCREMENT) - 1 : to);
                    publish(result);
                }
                return null;
            }

            @Override
            protected void process(List<Map<Long, String>> chunks) {
                for (Map<Long, String> map : chunks) {
                    if ((map != null) && !map.isEmpty()) {
                        tbMdlResult.addObjects(MapTools.getKeys(map));
                        invalid.putAll(map);
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                }
                catch (CancellationException e) {
                    LOGGER.info(e.getMessage(), e);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiButtonsForSwingWorkerInProgress(false);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };

        setWaitCursor();
        showProgressBar("Physiktypen pruefen...");
        enableGuiButtonsForSwingWorkerInProgress(true);
        invalid = new HashMap<Long, String>();
        tbMdlResult.setData(null);
        swingWorker.execute();
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

    /*
     * TableModel fuer die Darstellung der Auftraege mit ungueltigen Physiktyp.
     */
    class PhysiktypConsistenceTableModel extends AKTableModel {
        private static final int COL_ID = 0;
        private static final int COL_MESSAGE = 1;

        private static final int COL_COUNT = 2;

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return COL_COUNT;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case COL_ID:
                    return "Auftrags-ID";
                case COL_MESSAGE:
                    return "Message";
                default:
                    return null;
            }
        }

        /**
         * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Object tmp = getDataAtRow(row);
            if (tmp instanceof Long) {
                Long aId = (Long) tmp;
                switch (column) {
                    case COL_ID:
                        return aId;
                    case COL_MESSAGE:
                        return MapUtils.getString(invalid, aId);
                    default:
                        return null;
                }
            }
            return null;
        }

        /**
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == COL_ID) ? Long.class : String.class;
        }
    }
}


