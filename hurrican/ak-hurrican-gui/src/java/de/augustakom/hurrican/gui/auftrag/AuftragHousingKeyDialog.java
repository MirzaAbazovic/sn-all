/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 10:04:37
 */
package de.augustakom.hurrican.gui.auftrag;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.iface.AKDataLoaderComponent;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.gui.swing.AKCustomListCellRenderer;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.hurrican.gui.base.AbstractServiceOptionDialog;
import de.augustakom.hurrican.model.cc.Auftrag;
import de.augustakom.hurrican.model.cc.AuftragHousing;
import de.augustakom.hurrican.model.cc.AuftragHousingKey;
import de.augustakom.hurrican.model.cc.housing.Transponder;
import de.augustakom.hurrican.model.cc.housing.TransponderGroup;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.HousingService;


/**
 * Dialog zum Erstellen oder Bearbeiten der {@link AuftragHousingKey} Entitäten.
 */
public class AuftragHousingKeyDialog extends AbstractServiceOptionDialog implements AKDataLoaderComponent, ItemListener {

    private static final Logger LOGGER = Logger.getLogger(AuftragHousingKeyDialog.class);

    private static final String RESOURCE = "de/augustakom/hurrican/gui/auftrag/resources/AuftragHousingKeyDialog.xml";

    private static final String HOUSINGKEY_TRANSPONDERGROUP = "housingkey.transpondergroup";
    private static final String HOUSINGKEY_TRANSPONDERID = "housingkey.transponderid";
    private static final String HOUSINGKEY_CUSTOMER_FIRSTNAME = "housingkey.customerfirstname";
    private static final String HOUSINGKEY_CUSTOMER_LASTNAME = "housingkey.customerlastname";

    private AKJComboBox cbTransponderGroup = null;
    private AKJFormattedTextField tfTransponderId = null;
    private AKJTextField tfCustomerFirstname = null;
    private AKJTextField tfCustomerLastname = null;

    private AuftragHousingKey currentHousingKey;
    private final AuftragHousing currentHousing;

    private HousingService housingService;
    private CCAuftragService auftragService;

    public AuftragHousingKeyDialog(AuftragHousingKey currentHousingKey, AuftragHousing auftragHousing) {
        super(RESOURCE);
        this.currentHousingKey = currentHousingKey;
        this.currentHousing = auftragHousing;

        if (currentHousingKey == null) {
            newTransponder();
        }
        if (auftragHousing == null) {
            getButton(CMD_SAVE).setEnabled(false);
            MessageHelper.showErrorDialog(getMainFrame(), new Exception("Es wurde kein Auftrag angegeben!"));
        }
        doInit();
        createGUI();
        loadData();
    }

    private void doInit() {
        try {
            housingService = getCCService(HousingService.class);
            auftragService = getCCService(CCAuftragService.class);
        }
        catch (ServiceNotFoundException e) {
            getButton(CMD_SAVE).setEnabled(false);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    @Override
    protected final void createGUI() {
        setTitle(getSwingFactory().getText("title"));

        AKJLabel lblTransponderGroup = getSwingFactory().createLabel(HOUSINGKEY_TRANSPONDERGROUP, AKJLabel.LEFT);
        AKJLabel lblTransponderId = getSwingFactory().createLabel(HOUSINGKEY_TRANSPONDERID, AKJLabel.LEFT);
        AKJLabel lblCustomerFirstname = getSwingFactory().createLabel(HOUSINGKEY_CUSTOMER_FIRSTNAME, AKJLabel.LEFT);
        AKJLabel lblCustomerLastname = getSwingFactory().createLabel(HOUSINGKEY_CUSTOMER_LASTNAME, AKJLabel.LEFT);

        cbTransponderGroup = getSwingFactory().createComboBox(HOUSINGKEY_TRANSPONDERGROUP,
                new AKCustomListCellRenderer<>(TransponderGroup.class, TransponderGroup::getTransponderDescription));
        cbTransponderGroup.addItemListener(this);
        tfTransponderId = getSwingFactory().createFormattedTextField(HOUSINGKEY_TRANSPONDERID);
        tfCustomerFirstname = getSwingFactory().createTextField(HOUSINGKEY_CUSTOMER_FIRSTNAME);
        tfCustomerLastname = getSwingFactory().createTextField(HOUSINGKEY_CUSTOMER_LASTNAME);

        configureButton(CMD_SAVE, "Speichern", "Speichert den Schlüssel", true, true);
        configureButton(CMD_CANCEL, "Abbrechen", "Schliesst den Dialog, ohne den bearbeiteten Schlüssel zu speichern", true, true);

        AKJPanel editPanel = new AKJPanel(new GridBagLayout());
        editPanel.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("title.housingkey")));

        // @formatter:off
        editPanel.add(lblTransponderGroup   , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(cbTransponderGroup    , GBCFactory.createGBC(  0,  0, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblTransponderId      , GBCFactory.createGBC(  0,  0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfTransponderId       , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblCustomerFirstname  , GBCFactory.createGBC(  0,  0, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfCustomerFirstname   , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(lblCustomerLastname   , GBCFactory.createGBC(  0,  0, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(tfCustomerLastname    , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        editPanel.add(new AKJPanel()        , GBCFactory.createGBC(100,100, 2, 4, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new GridBagLayout());
        getChildPanel().add(editPanel , GBCFactory.createGBC(100,100, 0, 0, 1, 1, GridBagConstraints.BOTH));
        // @formatter:on
    }


    @Override
    public final void loadData() {
        try {
            if (currentHousingKey != null) {
                if (currentHousingKey.getId() != null) {
                    cbTransponderGroup.setEnabled(false);
                }
                else {
                    // Transponder-Gruppen zum Kunden ermitteln
                    loadTransponderGroups();
                }

                if (currentHousingKey.getTransponder() != null) {
                    Transponder transponder = currentHousingKey.getTransponder();
                    tfTransponderId.setValue(transponder.getTransponderId());
                    tfCustomerFirstname.setText(transponder.getCustomerFirstName());
                    tfCustomerLastname.setText(transponder.getCustomerLastName());
                }
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

    private void loadTransponderGroups() {
        try {
            if (currentHousing != null) {
                Auftrag auftrag = auftragService.findAuftragById(currentHousing.getAuftragId());
                List<TransponderGroup> transponderGroups = housingService.findTransponderGroups(auftrag.getKundeNo());

                cbTransponderGroup.addItems(transponderGroups, true, TransponderGroup.class);
            }
        }
        catch (FindException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(getMainFrame(), e);
        }
    }

    private void newTransponder() {
        currentHousingKey = new AuftragHousingKey();
        currentHousingKey.setAuftragHousingId(currentHousing.getId());
        currentHousingKey.setAuftragId(currentHousing.getAuftragId());
    }

    @Override
    protected void doSave() {
        try {
            TransponderGroup transponderGroup = (TransponderGroup) cbTransponderGroup.getSelectedItem();
            if ((tfTransponderId.getValueAsLong(null) == null) && ((transponderGroup == null) || (transponderGroup.getId() == null))) {
                MessageHelper.showErrorDialog(getMainFrame(), new Exception("Bitte geben Sie eine korrekte Transponder Nr. an oder wählen Sie eine Transponder-Gruppe!"));
                return;
            }

            if (transponderGroup.getId() != null) {
                currentHousingKey.setTransponderGroup(transponderGroup);
            }
            else {
                Transponder transponder = (currentHousingKey.getTransponder() == null) ? new Transponder() : currentHousingKey.getTransponder();
                if (transponder.getId() == null) {
                    currentHousingKey.setTransponder(transponder);
                }
                transponder.setTransponderId(tfTransponderId.getValueAsLong(null));
                transponder.setCustomerFirstName(tfCustomerFirstname.getText(null));
                transponder.setCustomerLastName(tfCustomerLastname.getText(null));
            }

            housingService.saveAuftragHousingKey(currentHousingKey);
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        if ((e.getSource() == cbTransponderGroup) && (e.getStateChange() == ItemEvent.SELECTED)) {
            boolean enableTextFields = ((TransponderGroup) cbTransponderGroup.getSelectedItem()).getId() == null ? true : false;

            tfTransponderId.setEnabled(enableTextFields);
            tfCustomerFirstname.setEnabled(enableTextFields);
            tfCustomerLastname.setEnabled(enableTextFields);

            if (!enableTextFields) {
                tfTransponderId.setText(null);
                tfCustomerFirstname.setText("");
                tfCustomerLastname.setText("");
            }
        }
    }

}


