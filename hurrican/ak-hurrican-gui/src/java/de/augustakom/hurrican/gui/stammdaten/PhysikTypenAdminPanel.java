/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2004 14:41:18
 */
package de.augustakom.hurrican.gui.stammdaten;

import java.awt.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.HVTTechnik;
import de.augustakom.hurrican.model.cc.PhysikTyp;
import de.augustakom.hurrican.service.cc.HVTService;
import de.augustakom.hurrican.service.cc.PhysikService;


/**
 * Panel fuer die Administration der Physik-Typen.
 *
 *
 */
public class PhysikTypenAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(PhysikTypenAdminPanel.class);

    private PhysikTypenTM tbModelPT = null;

    private AKJTextField tfName = null;
    private AKJTextArea taDesc = null;
    private AKJComboBox cbHVTTechnik = null;

    private PhysikTyp detail = null;

    /**
     * Standardkonstruktor
     */
    public PhysikTypenAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/PhysikTypenAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelPT = new PhysikTypenTM();
        AKJTable tbPT = new AKJTable(tbModelPT, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbPT.addMouseListener(getTableListener());
        tbPT.addKeyListener(getTableListener());
        tbPT.fitTable(new int[] { 70, 200, 70 });
        AKJScrollPane tableSP = new AKJScrollPane(tbPT);
        tableSP.setPreferredSize(new Dimension(450, 150));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(tableSP, BorderLayout.CENTER);

        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblDesc = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblHVTTechnik = getSwingFactory().createLabel("hvttechnik");

        tfName = getSwingFactory().createTextField("name");
        taDesc = getSwingFactory().createTextArea("beschreibung");
        AKJScrollPane spDesc = new AKJScrollPane(taDesc);
        cbHVTTechnik = getSwingFactory().createComboBox("hvttechnik");
        cbHVTTechnik.setRenderer(new AKCustomListCellRenderer<>(HVTTechnik.class, HVTTechnik::getHersteller));

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfName, GBCFactory.createGBC(50, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblHVTTechnik, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbHVTTechnik, GBCFactory.createGBC(50, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblDesc, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spDesc, GBCFactory.createGBC(50, 50, 3, 3, 1, 2, GridBagConstraints.BOTH));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(50, 50, 4, 5, 1, 1, GridBagConstraints.BOTH));

        AKJSplitPane split = new AKJSplitPane(AKJSplitPane.VERTICAL_SPLIT);
        split.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        split.setTopComponent(tablePanel);
        split.setBottomComponent(dataPanel);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#loadData()
     */
    @Override
    public final void loadData() {
        final SwingWorker<Pair<List<PhysikTyp>, List<HVTTechnik>>, Void> worker
                = new SwingWorker<Pair<List<PhysikTyp>, List<HVTTechnik>>, Void>() {

            @Override
            protected Pair<List<PhysikTyp>, List<HVTTechnik>> doInBackground() throws Exception {
                PhysikService ps = getCCService(PhysikService.class);
                List<PhysikTyp> pts = ps.findPhysikTypen();

                HVTService service = getCCService(HVTService.class);
                List<HVTTechnik> hvtTecs = service.findHVTTechniken();

                return Pair.create(pts, hvtTecs);
            }

            @Override
            protected void done() {
                try {
                    Pair<List<PhysikTyp>, List<HVTTechnik>> pair = get();
                    tbModelPT.setData(pair.getFirst());
                    cbHVTTechnik.addItems(pair.getSecond(), true, HVTTechnik.class);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(getMainFrame(), e);
                }
                finally {
                    enableGuiComponents(true);
                    setDefaultCursor();
                    stopProgressBar();
                }
            }
        };

        setWaitCursor();
        showProgressBar("laden...");
        enableGuiComponents(false);
        worker.execute();
    }

    private void enableGuiComponents(boolean enable) {
        tfName.setEnabled(enable);
        taDesc.setEnabled(enable);
        cbHVTTechnik.setEnabled(enable);
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof PhysikTyp) {
            this.detail = (PhysikTyp) details;
            PhysikTyp pt = (PhysikTyp) details;
            tfName.setText(pt.getName());
            taDesc.setText(pt.getBeschreibung());
            cbHVTTechnik.selectItem("getId", HVTTechnik.class, detail.getHvtTechnikId());
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
                detail = new PhysikTyp();
            }

            detail.setName(tfName.getText());
            detail.setBeschreibung(taDesc.getText());
            detail.setHvtTechnikId((cbHVTTechnik.getSelectedItem() instanceof HVTTechnik)
                    ? ((HVTTechnik) cbHVTTechnik.getSelectedItem()).getId() : null);

            // Validierung
            if (StringUtils.isEmpty(detail.getName())) {
                throw new Exception("Name muss definiert werden!");
            }

            PhysikService service = getCCService(PhysikService.class);
            service.savePhysikTyp(detail);

            if (isNew) {
                tbModelPT.addObject(detail);
            }
            tbModelPT.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.detail = null;
        }
    }

    /* 'Loescht' die TextFields */
    private void clear() {
        tfName.setText("");
        taDesc.setText("");
        cbHVTTechnik.setSelectedIndex(-1);
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
     * TableModel fuer die PhysikTypen.
     */
    static class PhysikTypenTM extends AKTableModel<PhysikTyp> {
        static final int COL_ID = 0;
        static final int COL_NAME = 1;
        static final int COL_HVTTEC = 2;

        static final int COL_COUNT = 3;

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
                    return "Name";
                case COL_HVTTEC:
                    return "HVT-Technik";
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
            if (tmp instanceof PhysikTyp) {
                PhysikTyp art = (PhysikTyp) tmp;
                switch (column) {
                    case COL_ID:
                        return art.getId();
                    case COL_NAME:
                        return art.getName();
                    case COL_HVTTEC:
                        return art.getHvtTechnikId();
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


