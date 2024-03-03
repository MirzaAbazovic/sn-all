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
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJSplitPane;
import de.augustakom.common.gui.swing.AKJTable;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.gui.swing.table.AKTableModelXML;
import de.augustakom.common.gui.utils.GuiTools;
import de.augustakom.hurrican.gui.base.AbstractAdminPanel;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.cc.ReferenceService;


/**
 * Panel fuer die Administration der Reference-Tabelle.
 *
 *
 */
public class ReferenceAdminPanel extends AbstractAdminPanel {

    private static final Logger LOGGER = Logger.getLogger(ReferenceAdminPanel.class);

    private AKTableModelXML tbMdlReference = null;

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfType = null;
    private AKJTextField tfStrVal = null;
    private AKJFormattedTextField tfIntVal = null;
    private AKJFormattedTextField tfFloatVal = null;
    private AKJFormattedTextField tfUnitId = null;
    private AKJFormattedTextField tfOrderNo = null;
    private AKJCheckBox cbGuiVis = null;

    private Reference detail = null;

    /**
     * Standardkonstruktor
     */
    public ReferenceAdminPanel() {
        super("de/augustakom/hurrican/gui/stammdaten/resources/ReferenceAdminPanel.xml");
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        tbMdlReference = new AKTableModelXML(
                "de/augustakom/hurrican/gui/stammdaten/resources/ReferenceTM.xml");
        AKJTable tbReference = new AKJTable(tbMdlReference, AKJTable.AUTO_RESIZE_OFF, ListSelectionModel.SINGLE_SELECTION);
        tbReference.attachSorter();
        tbReference.addTableListener(this);

        tbReference.fitTable(new int[] { 70, 70, 150, 70, 70, 70, 70, 70 });
        AKJScrollPane tableSP = new AKJScrollPane(tbReference, new Dimension(700, 300));

        AKJPanel tablePanel = new AKJPanel(new BorderLayout());
        tablePanel.add(tableSP, BorderLayout.CENTER);

        AKJLabel lblId = getSwingFactory().createLabel("id");
        AKJLabel lblType = getSwingFactory().createLabel("type");
        AKJLabel lblStrVal = getSwingFactory().createLabel("strval");
        AKJLabel lblIntVal = getSwingFactory().createLabel("intval");
        AKJLabel lblFloatVal = getSwingFactory().createLabel("floatval");
        AKJLabel lblUnitId = getSwingFactory().createLabel("unitid");
        AKJLabel lblOrderNo = getSwingFactory().createLabel("orderno");
        AKJLabel lblGuiVis = getSwingFactory().createLabel("gui");

        tfId = getSwingFactory().createFormattedTextField("id");
        tfType = getSwingFactory().createTextField("type");
        tfStrVal = getSwingFactory().createTextField("strval");
        tfIntVal = getSwingFactory().createFormattedTextField("intval");
        tfFloatVal = getSwingFactory().createFormattedTextField("floatval");
        tfUnitId = getSwingFactory().createFormattedTextField("unitid");
        tfOrderNo = getSwingFactory().createFormattedTextField("orderno");
        cbGuiVis = getSwingFactory().createCheckBox("gui");

        AKJPanel dataPanel = new AKJPanel(new GridBagLayout());
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(new AKJPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        dataPanel.add(tfId, GBCFactory.createGBC(50, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblType, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfType, GBCFactory.createGBC(50, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblStrVal, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfStrVal, GBCFactory.createGBC(50, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblIntVal, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfIntVal, GBCFactory.createGBC(50, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblFloatVal, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfFloatVal, GBCFactory.createGBC(50, 0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblUnitId, GBCFactory.createGBC(0, 0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfUnitId, GBCFactory.createGBC(50, 0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblOrderNo, GBCFactory.createGBC(0, 0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(tfOrderNo, GBCFactory.createGBC(50, 0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(lblGuiVis, GBCFactory.createGBC(0, 0, 1, 8, 1, 1, GridBagConstraints.HORIZONTAL));
        dataPanel.add(cbGuiVis, GBCFactory.createGBC(50, 0, 3, 8, 1, 1, GridBagConstraints.HORIZONTAL));
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

            ReferenceService rs = getCCService(ReferenceService.class);
            List<Reference> references = rs.findReferences();
            tbMdlReference.setData(references);
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
        if (details instanceof Reference) {
            this.detail = (Reference) details;
            Reference ref = (Reference) details;

            tfId.setValue(ref.getId());
            tfId.setEnabled(ref.getId() == null);
            tfType.setText(ref.getType());
            tfStrVal.setText(ref.getStrValue());
            tfIntVal.setValue(ref.getIntValue());
            tfFloatVal.setValue(ref.getFloatValue());
            tfUnitId.setValue(ref.getUnitId());
            tfOrderNo.setValue(ref.getOrderNo());
            cbGuiVis.setSelected(ref.getGuiVisible());
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
                detail = new Reference();
            }

            //Setze Daten im Modell anhand des Inhalts der GUI-Felder
            detail.setId(tfId.getValueAsLong(null));
            detail.setType(tfType.getText());
            detail.setStrValue(tfStrVal.getText());
            detail.setIntValue(tfIntVal.getValueAsInt(null));
            detail.setFloatValue(tfFloatVal.getValueAsFloat(null));
            detail.setUnitId(tfUnitId.getValueAsLong(null));
            detail.setOrderNo(tfOrderNo.getValueAsInt(null));
            detail.setGuiVisible(cbGuiVis.isSelectedBoolean());

            // Validierung
            if (detail.getId() == null) {
                throw new Exception("ID muss definiert werden!");
            }
            if (StringUtils.isEmpty(detail.getType())) {
                throw new Exception("Typ muss definiert werden!");
            }

            //Speichern
            ReferenceService service = getCCService(ReferenceService.class);
            service.saveReference(detail);

            // Erneuere Tabelle
            if (isNew) {
                tbMdlReference.addObject(detail);
            }
            tbMdlReference.fireTableDataChanged();
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


