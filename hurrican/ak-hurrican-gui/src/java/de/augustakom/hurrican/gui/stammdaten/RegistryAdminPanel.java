/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.11.2006 14:41:18
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
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModel;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Registry;
import de.augustakom.hurrican.service.cc.RegistryService;


/**
 * Panel fuer die Administration der Registry-Tabelle.
 *
 *
 */
public class RegistryAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(RegistryAdminPanel.class);

    private AKTableModel tbModelRegistry = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDesc = null;
    private AKJTextArea taStrVal = null;
    private AKJFormattedTextField tfIntVal = null;

    private Registry detail = null;

    /**
     * Standardkonstruktor
     */
    public RegistryAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/RegistryAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbModelRegistry = new AKTableModelXML(
                "de/augustakom/hurrican/gui/stammdaten/resources/RegistryTM.xml");
        AKJTable tbRegistry = new AKJTable(tbModelRegistry, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbRegistry.attachSorter();
        tbRegistry.addTableListener(this);

        tbRegistry.fitTable(new int[] { 50, 150, 170, 70, 170 });
        AKJScrollPane tableSP = new AKJScrollPane(tbRegistry, new Dimension(700, 300));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(tableSP, BorderLayout.CENTER);

        AKJLabel lblName = getSwingFactory().createLabel("name");
        AKJLabel lblDesc = getSwingFactory().createLabel("beschreibung");
        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblStrVal = getSwingFactory().createLabel("strval");
        AKJLabel lblIntVal = getSwingFactory().createLabel("intval");

        tfName = getSwingFactory().createTextField("name");
        taDesc = getSwingFactory().createTextArea("beschreibung");
        tfId = getSwingFactory().createFormattedTextField("id");
        taStrVal = getSwingFactory().createTextArea("strval");
        tfIntVal = getSwingFactory().createFormattedTextField("intval");
        AKJScrollPane spDesc = new AKJScrollPane(taDesc);
        AKJScrollPane spStrVal = new AKJScrollPane(taStrVal);

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfId, GBCFactory.createGBC(50, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 2, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfName, GBCFactory.createGBC(50, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblStrVal, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spStrVal, GBCFactory.createGBC(50, 50, 3, 4, 1, 2, GridBagConstraints.BOTH));
        dataPanel.add(lblIntVal, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfIntVal, GBCFactory.createGBC(50, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblDesc, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(spDesc, GBCFactory.createGBC(50, 50, 3, 8, 1, 2, GridBagConstraints.BOTH));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(50, 50, 4, 10, 1, 1, GridBagConstraints.BOTH));

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
        try {
            setWaitCursor();
            showProgressBar("laden...");

            RegistryService rs = getCCService(RegistryService.class);
            List<Registry> registry = rs.findRegistries();
            tbModelRegistry.setData(registry);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
        finally {
            setDefaultCursor();
            stopProgressBar();
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractAdminPanel#showDetails(java.lang.Object)
     */
    @Override
    public void showDetails(Object details) {
        if (details instanceof Registry) {
            this.detail = (Registry) details;
            Registry reg = (Registry) details;

            tfId.setValue(reg.getId());
            tfId.setEnabled(reg.getId() == null);
            tfName.setText(reg.getName());
            taDesc.setText(reg.getDescription());
            taStrVal.setText(reg.getStringValue());
            tfIntVal.setValue(reg.getIntValue());

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
                detail = new Registry();
            }

            detail.setId(tfId.getValueAsLong(null));
            detail.setName(tfName.getText());
            detail.setDescription(taDesc.getText());
            detail.setStringValue(taStrVal.getText());
            detail.setIntValue(tfIntVal.getValueAsInt(null));

            // Validierung
            if (detail.getId() == null) {
                throw new Exception("ID muss definiert werden!");
            }
            if (StringUtils.isEmpty(detail.getName())) {
                throw new Exception("Name muss definiert werden!");
            }

            // Speichern
            RegistryService service = getCCService(RegistryService.class);
            service.saveRegistry(detail);

            if (isNew) {
                tbModelRegistry.addObject(detail);
            }
            tbModelRegistry.fireTableDataChanged();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(this, e);
            this.detail = null;
        }
    }

    /* 'Loescht' die TextFields */
    private void clear() {
        GuiTools.cleanFields(this);
        tfId.setEnabled(true);
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

}
