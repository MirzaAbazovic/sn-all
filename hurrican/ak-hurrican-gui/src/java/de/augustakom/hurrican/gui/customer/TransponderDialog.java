/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 10:04:37
 */
package de.augustakom.hurrican.gui.customer;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.gui.base.HurricanGUIException;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.service.base.exceptions.FindException;


/**
 * Dialog zum Erstellen oder Bearbeiten der {@link Transponder} Entitäten.
 */
public class TransponderDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent {

    private static final Logger LOGGER = Logger.getLogger(TransponderDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/customer/resources/TransponderDialog.xml";

    private static final String HOUSINGKEY_TRANSPONDERID = "transponderid";
    private static final String HOUSINGKEY_CUSTOMER_FIRSTNAME = "customerfirstname";
    private static final String HOUSINGKEY_CUSTOMER_LASTNAME = "customerlastname";

    private AKJFormattedTextField tfTransponderId = null;
    private AKJTextField tfCustomerFirstname = null;
    private AKJTextField tfCustomerLastname = null;

    private final TransponderGroup transponderGroup;
    private Transponder toEdit;

    public TransponderDialog(TransponderGroup transponderGroup, Transponder toEdit) {
        super(RESOURCE);
        this.transponderGroup = transponderGroup;
        this.toEdit = toEdit;

        if (toEdit == null) {
            newTransponder();
        }

        createGUI();
        loadData();
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblTransponderId = getSwingFactory().createLabel(HOUSINGKEY_TRANSPONDERID, AKJLabel.LEFT);
        AKJLabel lblCustomerFirstname = getSwingFactory().createLabel(HOUSINGKEY_CUSTOMER_FIRSTNAME, AKJLabel.LEFT);
        AKJLabel lblCustomerLastname = getSwingFactory().createLabel(HOUSINGKEY_CUSTOMER_LASTNAME, AKJLabel.LEFT);

        tfTransponderId = getSwingFactory().createFormattedTextField(HOUSINGKEY_TRANSPONDERID);
        tfCustomerFirstname = getSwingFactory().createTextField(HOUSINGKEY_CUSTOMER_FIRSTNAME);
        tfCustomerLastname = getSwingFactory().createTextField(HOUSINGKEY_CUSTOMER_LASTNAME);

        configureButton(CMD_SAVE, "Speichern", "Speichert den Schlüssel", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Schliesst den Dialog, ohne den bearbeiteten Schlüssel zu speichern", true, true);

        AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("title")));

        // @formatter:off
        editPanel.add(lblTransponderId      , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfTransponderId       , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblCustomerFirstname  , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfCustomerFirstname   , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblCustomerLastname   , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfCustomerLastname    , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel()        , GBCFactory.createGBC(100,100, 2, 3, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(editPanel , GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }


    @Override
    public final void loadData() {
        try {
            if (toEdit != null) {
                tfTransponderId.setValue(toEdit.getTransponderId());
                tfCustomerFirstname.setText(toEdit.getCustomerFirstName());
                tfCustomerLastname.setText(toEdit.getCustomerLastName());
            }
            else {
                throw new FindException("Der selektierte Schlüssel konnte nicht eindeutig ermittelt werden!");
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
            getButton(CMD_SAVE).setEnabled(false);
        }
    }

    private void newTransponder() {
        toEdit = new Transponder();
    }

    @Override
    protected void doSave() {
        try {
            if (toEdit.getId() == null) {
                transponderGroup.getTransponders().add(toEdit);
            }

            toEdit.setTransponderId(tfTransponderId.getValueAsLong(null));
            toEdit.setCustomerFirstName(tfCustomerFirstname.getText(null));
            toEdit.setCustomerLastName(tfCustomerLastname.getText(null));

            if (toEdit.getTransponderId() == null) {
                throw new HurricanGUIException("Es muss zumindest eine Transponder-ID angegeben werden!");
            }

            prepare4Close();
            setValue(Integer.valueOf(OK_OPTION));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    protected void execute(String command) {
    }

}


