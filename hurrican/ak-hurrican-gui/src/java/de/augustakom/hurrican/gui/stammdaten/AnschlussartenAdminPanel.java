/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 13:19:09
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Anschlussart;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel fuer die Administration der Anschlussarten.
 *
 *
 */
public class AnschlussartenAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(AnschlussartenAdminPanel.class);

    private AKJTable tbArten = null;
    private AnschlussartenTM tbModelArten = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;

    private Anschlussart detail = null;

    /**
     * Standardkonstruktor
     */
    public AnschlussartenAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/AnschlussartenAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelArten = new AnschlussartenTM();
        tbArten = new AKJTable(tbModelArten);
        tbArten.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbArten.setAutoResizeMode(AKJTable.AUTO_RESIZE_OFF);
        tbArten.addMouseListener(getTableListener());
        tbArten.addKeyListener(getTableListener());
        AKJScrollPane tableSP = new AKJScrollPane(tbArten);
        tableSP.setPreferredSize(new Dimension(400, 150));
        fitTable();

        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblName = getSwingFactory().createLabel("name");

        tfId = getSwingFactory().createFormattedTextField("id");
        tfName = getSwingFactory().createTextField("name");

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(20, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfName, GBCFactory.createGBC(20, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(80, 0, 4, 3, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(tableSP, BorderLayout.CENTER);

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tablePanel);
        split.setBottomComponent(dataPanel);
        split.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /* Passt die Spaltenbreiten der Tabelle an. */
    private void fitTable() {
        TableColumn column;
        for (int i = 0; i < tbArten.getColumnCount(); i++) {
            column = tbArten.getColumnModel().getColumn(i);
            if (i == AnschlussartenTM.COL_NAME) {
                column.setPreferredWidth(250);
            }
            else {
                column.setPreferredWidth(50);
            }
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        final SwingWorker<List<Anschlussart>, Void> worker = new SwingWorker<List<Anschlussart>, Void>() {

            @Override
            protected List<Anschlussart> doInBackground() throws Exception {
                PhysikService service = getCCService(PhysikService.class);
                return service.findAnschlussarten();
            }

            @Override
            protected void done() {
                try {
                    tbModelArten.setData(get());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiComponents(true);
                    stopProgressBar();
                    setDefaultCursor();
                }
            }
        };
        setWaitCursor();
        showProgressBar("laden...");
        enableGuiComponents(false);
        worker.execute();
    }

    private void enableGuiComponents(boolean enable) {
        tfId.setEnabled(enable);
        tfName.setEnabled(enable);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof Anschlussart) {
            this.detail = (Anschlussart) details;
            Anschlussart a = (Anschlussart) details;
            tfId.setValue(a.getId());
            tfName.setText(a.getAnschlussart());
        }
        else {
            this.detail = null;
            clear();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#createNew()
     */
    @Override
    public void createNew() {
        this.detail = null;
        clear();
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#saveData()
     */
    @Override
    public void saveData() {
        try {
            boolean isNew = false;
            if (detail == null) {
                isNew = true;
                detail = new Anschlussart();
            }

            detail.setId(tfId.getValueAsLong(null));
            detail.setAnschlussart(tfName.getText());
            if (StringUtils.isEmpty(detail.getAnschlussart())) {
                throw new Exception("Anschlussart muss definiert werden!");
            }

            PhysikService service = getCCService(PhysikService.class);
            service.saveAnschlussart(detail);

            if (isNew) {
                tbModelArten.addObject(detail);
            }
            tbModelArten.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.detail = null;
        }
    }

    /* 'Loescht' die TextFields */
    private void clear() {
        tfId.setText("");
        tfName.setText("");
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
     * TableModel fuer die Anschlussarten.
     */
    static class AnschlussartenTM extends AKTableModel<Anschlussart> {
        static final int COL_ID = 0;
        static final int COL_NAME = 1;
        static final int COL_COUNT = 2;

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
                case COL_NAME:
                    return "Anschlussart";
                default:
                    return " ";
            }
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        @Override
        public Object getValueAt(int row, int column) {
            Anschlussart art = getDataAtRow(row);
            if (art != null) {
                switch (column) {
                    case COL_ID:
                        return art.getId();
                    case COL_NAME:
                        return art.getAnschlussart();
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


