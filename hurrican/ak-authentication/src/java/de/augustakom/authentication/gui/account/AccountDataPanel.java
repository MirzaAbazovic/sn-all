/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 13:38:16
 */
package de.augustakom.authentication.gui.account;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.ApplicationListCellRenderer;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.service.AKAccountService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJPasswordField;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;
import de.augustakom.common.tools.lang.DesEncrypter;


/**
 * Panel um die Stammdaten eines AKAccount-Objekts darstellen und aendern zu koennen.
 *
 *
 */
public class AccountDataPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(AccountDataPanel.class);

    private static final String ACCOUNT_ID = "account.id";
    private static final String ACCOUNT_NAME = "account.name";
    private static final String ACCOUNT_USER = "account.user";
    private static final String ACCOUNT_PASSWORD = "account.password";
    private static final String ACCOUNT_MAX_ACTIVE = "account.max.active";
    private static final String ACCOUNT_MAX_IDLE = "account.max.idle";
    private static final String ACCOUNT_DESCRIPTION = "account.description";
    private static final String APPLICATIONS = "applications";

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextField tfUser = null;
    private AKJPasswordField tfPassword = null;
    private AKJFormattedTextField tfMaxActive = null;
    private AKJFormattedTextField tfMaxIdle = null;
    private AKJTextArea taDescription = null;
    public AKJComboBox cobApplication = null;

    private AKAccount model = null;

    /**
     * Konstruktor mit Angabe des Modells.
     *
     * @param model AKAccount-Objekt, das dargestellt/bearbeitet werden soll
     */
    public AccountDataPanel(AKAccount model) {
        super("de/augustakom/authentication/gui/account/resources/AccountDataPanel.xml");
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
        AKJLabel lblUser = getSwingFactory().createLabel(ACCOUNT_USER);
        AKJLabel lblPassword = getSwingFactory().createLabel(ACCOUNT_PASSWORD);
        AKJLabel lblMaxActive = getSwingFactory().createLabel(ACCOUNT_MAX_ACTIVE);
        AKJLabel lblMaxIdle = getSwingFactory().createLabel(ACCOUNT_MAX_IDLE);
        AKJLabel lblDescription = getSwingFactory().createLabel(ACCOUNT_DESCRIPTION);
        AKJLabel lblApplication = getSwingFactory().createLabel(APPLICATIONS);

        tfId = getSwingFactory().createFormattedTextField(ACCOUNT_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(ACCOUNT_NAME);
        tfUser = getSwingFactory().createTextField(ACCOUNT_USER);
        tfPassword = getSwingFactory().createPasswordField(ACCOUNT_PASSWORD);
        tfMaxActive = getSwingFactory().createFormattedTextField(ACCOUNT_MAX_ACTIVE);
        tfMaxIdle = getSwingFactory().createFormattedTextField(ACCOUNT_MAX_IDLE);
        taDescription = getSwingFactory().createTextArea(ACCOUNT_DESCRIPTION);
        taDescription.setWrapStyleWord(true);
        taDescription.setLineWrap(true);
        AKJScrollPane scrDesc = new AKJScrollPane(taDescription);
        cobApplication = getSwingFactory().createComboBox(APPLICATIONS);
        cobApplication.setRenderer(new ApplicationListCellRenderer());

        this.setLayout(new GridBagLayout());
        // @formatter:off
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblId              , GBCFactory.createGBC(  0,  0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 2, 1, 1, 1, GridBagConstraints.NONE));
        this.add(tfId               , GBCFactory.createGBC(100,  0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblName            , GBCFactory.createGBC(  0,  0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfName             , GBCFactory.createGBC(100,  0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblApplication     , GBCFactory.createGBC(  0,  0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cobApplication     , GBCFactory.createGBC(100,  0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblUser            , GBCFactory.createGBC(  0,  0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfUser             , GBCFactory.createGBC(100,  0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblPassword        , GBCFactory.createGBC(  0,  0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfPassword         , GBCFactory.createGBC(100,  0, 3, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblMaxActive       , GBCFactory.createGBC(  0,  0, 1, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfMaxActive        , GBCFactory.createGBC(100,  0, 3, 6, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblMaxIdle         , GBCFactory.createGBC(  0,  0, 1, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfMaxIdle          , GBCFactory.createGBC(100,  0, 3, 7, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel()       , GBCFactory.createGBC(  0,  0, 1, 8, 1, 1, GridBagConstraints.NONE));
        this.add(lblDescription     , GBCFactory.createGBC(  0,  0, 1, 9, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(scrDesc            , GBCFactory.createGBC(100, 50, 3, 9, 1, 3, GridBagConstraints.BOTH));
        this.add(new JPanel()       , GBCFactory.createGBC( 50,  0, 4,13, 1, 1, GridBagConstraints.HORIZONTAL));
        // @formatter:on

        read();
    }

    /* Liest die Daten des Modells aus. */
    private void read() {
        readApplications(cobApplication);
        if (model.getId() != null) {
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
            tfUser.setText(model.getAccountUser());
            tfMaxActive.setValue(model.getMaxActive());
            tfMaxIdle.setValue(model.getMaxIdle());
            taDescription.setText(model.getDescription());
            String password = "";
            try {
                password = DesEncrypter.getInstance().decrypt(model.getAccountPassword());
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            tfPassword.setText(password);

            selectApplication(model.getApplicationId(), cobApplication);
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
            model.setAccountUser(tfUser.getText());
            model.setAccountPassword(tfPassword.getPasswordAsString());
            model.setMaxActive(tfMaxActive.getValueAsInt(null));
            model.setMaxIdle(tfMaxIdle.getValueAsInt(null));
            model.setApplicationId(((AKApplication) cobApplication.getSelectedItem()).getId());
            model.setDescription(taDescription.getText());

            validateModel(model);

            try {
                AKAccountService accService = getAuthenticationService(
                        AKAuthenticationServiceNames.ACCOUNT_SERVICE, AKAccountService.class);
                accService.save(model);
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
    private void validateModel(AKAccount account) throws GUIException {
        if (StringUtils.isBlank(account.getName())) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_NAME);
        }

        if (StringUtils.isBlank(account.getAccountUser())) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_USER);
        }

        if (account.getApplicationId() == null) {
            throw new GUIException(GUIException.ACCOUNT_VALIDATE_APPLICATION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }

}
