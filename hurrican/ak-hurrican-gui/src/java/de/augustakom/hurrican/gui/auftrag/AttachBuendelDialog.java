/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2010 13:18:00
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.tools.collections.CollectionTools;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;

/**
 * Dialog, um eine Buendelnummer zuzuordnen.
 *
 *
 */
public class AttachBuendelDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(AttachBuendelDialog.class);

    // GUI-Elemete
    private AKJComboBox cbNumbers;
    private AKJFormattedTextField tfNumber;

    // Modelle
    private final List<Integer> buendelNrn;

    public AttachBuendelDialog(List<Integer> buendelNrn) {
        super("de/augustakom/hurrican/gui/auftrag/resources/AttachBuendelDialog.xml");
        this.buendelNrn = buendelNrn;
        createGUI();
        loadData();
        showValues();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));
        AKJLabel lblVorhanden = getSwingFactory().createLabel("existingNumbers");
        AKJLabel lblManuell = getSwingFactory().createLabel("manualNumber");

        AKCustomListCellRenderer<Integer> renderer = new AKCustomListCellRenderer<>(Integer.class, Object::toString);
        cbNumbers = getSwingFactory().createComboBox("number.list");
        cbNumbers.setRenderer(renderer);
        cbNumbers.addItemListener(this);
        tfNumber = getSwingFactory().createFormattedTextField("number.text");
        tfNumber.setHorizontalAlignment(JTextField.LEFT);

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        getChildPanel().add(lblVorhanden, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(cbNumbers, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(lblManuell, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(tfNumber, GBCFactory.createGBC(100, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 1, 5, 1, 1, GridBagConstraints.BOTH));
        getChildPanel().add(new AKJPanel(), GBCFactory.createGBC(100, 100, 2, 5, 1, 1, GridBagConstraints.BOTH));
    }

    /**
     * @see de.augustakom.common.gui.iface.AKDataLoaderComponent#loadData()
     */
    @Override
    public final void loadData() {
        // intentionally left blank
    }


    private void showValues() {
        if (CollectionTools.isEmpty(buendelNrn)) {
            cbNumbers.setEnabled(false);
        }
        else {
            cbNumbers.setEnabled(true);
            cbNumbers.addItems(buendelNrn, true, String.class);
        }
    }


    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void doSave() {
        try {
            prepare4Close();
            if (cbNumbers.getSelectedItem() instanceof Integer) {
                setValue(cbNumbers.getSelectedItem());
            }
            else if (StringUtils.isNotEmpty(tfNumber.getText())) {
                parseAndSaveBuendelNummer();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void parseAndSaveBuendelNummer() throws HurricanGUIException {
        try {
            Integer value = Integer.parseInt(tfNumber.getText());
            setValue(value);
        }
        catch (NumberFormatException e) {
            throw new HurricanGUIException("Ungueltiger Wert fuer 'Buendelnummer'!");
        }
    }

    /**
     * @see de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog#doSave()
     */
    @Override
    protected void cancel() {
        setValue(0);
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        // intentionally left blank
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1) {
        // intentionally left blank
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == cbNumbers) {
            if (cbNumbers.getSelectedItem() instanceof Integer) {
                tfNumber.setEnabled(false);
                tfNumber.setText(null);
            }
            else {
                tfNumber.setEnabled(true);
            }
        }
    }

}


