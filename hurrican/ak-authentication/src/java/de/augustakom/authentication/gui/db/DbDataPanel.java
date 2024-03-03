/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.02.2002 13:05:16
 */
package de.augustakom.authentication.gui.db;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKDb;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKDbService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;


/**
 * Panel um die Stammdaten eines AKDb-Objekts darstellen und aendern zu koennen.
 */
public class DbDataPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(DbDataPanel.class);

    private static final String ACCOUNT_ID = "db.id";
    private static final String ACCOUNT_NAME = "db.name";
    private static final String ACCOUNT_DRIVER = "db.driver";
    private static final String ACCOUNT_URL = "db.url";
    private static final String ACCOUNT_SCHEMA = "db.schema";
    private static final String ACCOUNT_DESCRIPTION = "db.description";

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextField tfDriver = null;
    private AKJTextField tfUrl = null;
    private AKJTextField tfSchema = null;
    private AKJTextArea taDescription = null;

    private AKDb model = null;

    /**
     * Konstruktor mit Angabe des Modells.
     *
     * @param model AKDb-Objekt, das dargestellt/bearbeitet werden soll
     */
    public DbDataPanel(AKDb model) {
        super("de/augustakom/authentication/gui/db/resources/DbDataPanel.xml");
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel(ACCOUNT_ID);
        AKJLabel lblName = getSwingFactory().createLabel(ACCOUNT_NAME);
        AKJLabel lblDriver = getSwingFactory().createLabel(ACCOUNT_DRIVER);
        AKJLabel lblUrl = getSwingFactory().createLabel(ACCOUNT_URL);
        AKJLabel lblSchema = getSwingFactory().createLabel(ACCOUNT_SCHEMA);
        AKJLabel lblDescription = getSwingFactory().createLabel(ACCOUNT_DESCRIPTION);

        tfId = getSwingFactory().createFormattedTextField(ACCOUNT_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(ACCOUNT_NAME);
        tfDriver = getSwingFactory().createTextField(ACCOUNT_DRIVER);
        tfUrl = getSwingFactory().createTextField(ACCOUNT_URL);
        tfSchema = getSwingFactory().createTextField(ACCOUNT_SCHEMA);
        taDescription = getSwingFactory().createTextArea(ACCOUNT_DESCRIPTION);
        taDescription.setWrapStyleWord(true);
        taDescription.setLineWrap(true);
        AKJScrollPane scrDesc = new AKJScrollPane(taDescription);

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblId              , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        this.add(tfId               , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblName            , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfName             , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblDriver          , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfDriver           , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblUrl             , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfUrl              , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblSchema          , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfSchema           , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.NONE));
        this.add(lblDescription     , GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(scrDesc            , GBCFactory.createGBC(100, 50, 3, 7, 1, 3, GridBagConstraints.BOTH));
        this.add(new JPanel()       , GBCFactory.createGBC( 50,  0, 4,11, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        read();
    }

    /* Liest die Daten des Modells aus. */
    private void read() {
        if (model.getId() != null) {
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
            tfDriver.setText(model.getDriver());
            tfUrl.setText(model.getUrl());
            tfSchema.setText(model.getSchema());
            taDescription.setText(model.getDescription());
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
    }

    /**
     * @see de.augustakom.authentication.gui.basics.SavePanel#doSave()
     */
    public void doSave() throws GUIException {
        try {
            setWaitCursor();

            model.setName(tfName.getText());
            model.setDriver(tfDriver.getText(null));
            model.setUrl(tfUrl.getText(null));
            model.setSchema(tfSchema.getText(null));
            model.setDescription(taDescription.getText());

            validateModel(model);

            try {
                AKDbService dbService = getAuthenticationService(
                        AKAuthenticationServiceNames.DB_SERVICE, AKDbService.class);
                dbService.save(model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new GUIException(GUIException.ACCOUNT_SAVING_ERROR, e);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /* Validiert das Modell und wird eine Exception, wenn ungueltige Daten erkannt werden. */
    private void validateModel(AKDb db) throws GUIException {
        if (StringUtils.isBlank(db.getName())) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_NAME);
        }

        if (StringUtils.isBlank(db.getDriver())) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_USER);
        }

        if (db.getUrl() == null) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_APPLICATION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}
