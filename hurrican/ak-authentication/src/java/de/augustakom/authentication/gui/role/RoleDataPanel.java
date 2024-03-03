/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.05.2004 09:35:53
 */
package de.augustakom.authentication.gui.role;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.ApplicationListCellRenderer;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJCheckBox;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJFormattedTextField;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.AKJTextArea;
import de.augustakom.common.gui.swing.AKJTextField;


/**
 * Panel fuer die Bearbeitung der Stammdaten eines AKRole-Objekts.
 *
 *
 */
public class RoleDataPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(RoleDataPanel.class);

    private static final String ROLE_ID = "role.id";
    private static final String ROLE_NAME = "role.name";
    private static final String ROLE_DESCRIPTION = "role.description";
    private static final String ROLE_ADMIN = "role.admin";
    private static final String APPLICATIONS = "applications";

    private AKJFormattedTextField tfId = null;
    private AKJTextField tfName = null;
    private AKJTextArea taDescription = null;
    private AKJComboBox cobApplication = null;
    private AKJCheckBox chbAdmin = null;

    private AKRole model = null;

    /**
     * Konstruktor fuer das Panel mit Angabe eines AKRole-Objekts
     *
     * @param model Darzustellendes AKRole-Objekt.
     */
    public RoleDataPanel(AKRole model) {
        super("de/augustakom/authentication/gui/role/resources/RoleDataPanel.xml");
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblId = getSwingFactory().createLabel(ROLE_ID);
        AKJLabel lblName = getSwingFactory().createLabel(ROLE_NAME);
        AKJLabel lblAdmin = getSwingFactory().createLabel(ROLE_ADMIN);
        AKJLabel lblDescription = getSwingFactory().createLabel(ROLE_DESCRIPTION);
        AKJLabel lblApplication = getSwingFactory().createLabel(APPLICATIONS);

        tfId = getSwingFactory().createFormattedTextField(ROLE_ID);
        tfId.setEditable(false);
        tfName = getSwingFactory().createTextField(ROLE_NAME);
        chbAdmin = getSwingFactory().createCheckBox(ROLE_ADMIN);
        taDescription = getSwingFactory().createTextArea(ROLE_DESCRIPTION);
        taDescription.setWrapStyleWord(true);
        taDescription.setLineWrap(true);
        AKJScrollPane scrDesc = new AKJScrollPane(taDescription);
        cobApplication = getSwingFactory().createComboBox(APPLICATIONS);
        cobApplication.setRenderer(new ApplicationListCellRenderer());

        this.setLayout(new GridBagLayout());
        this.add(new JPanel(), GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        this.add(lblId, GBCFactory.createGBC(0, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(0, 0, 2, 1, 1, 1, GridBagConstraints.NONE));
        this.add(tfId, GBCFactory.createGBC(100, 0, 3, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblName, GBCFactory.createGBC(0, 0, 1, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tfName, GBCFactory.createGBC(100, 0, 3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblAdmin, GBCFactory.createGBC(0, 0, 1, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(chbAdmin, GBCFactory.createGBC(100, 0, 3, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblApplication, GBCFactory.createGBC(0, 0, 1, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(cobApplication, GBCFactory.createGBC(100, 0, 3, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(lblDescription, GBCFactory.createGBC(0, 0, 1, 5, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(scrDesc, GBCFactory.createGBC(100, 50, 3, 5, 1, 3, GridBagConstraints.BOTH));
        this.add(new JPanel(), GBCFactory.createGBC(50, 0, 4, 8, 1, 1, GridBagConstraints.HORIZONTAL));

        read();
    }

    /*
     * Liest die Daten des Modells aus und zeigt sie in
     * den entsprechenden GUI-Komponenten an.
     */
    private void read() {
        readApplications(cobApplication);
        if (model.getId() != null) {
            tfId.setValue(model.getId());
            tfName.setText(model.getName());
            chbAdmin.setSelected(model.isAdmin());
            taDescription.setText(model.getDescription());

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
            model.setAdmin(chbAdmin.isSelected());
            model.setDescription(taDescription.getText());
            model.setApplicationId(((AKApplication) cobApplication.getSelectedItem()).getId());

            validateModel(model);

            try {
                AKRoleService roleService = getAuthenticationService(
                        AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
                roleService.save(model);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new GUIException(GUIException.ROLE_SAVING_ERROR, e);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * Validiert das AKRole-Objekt. <br> Werden ungueltige Daten festgestellt, wird eine GUIException geworfen.
     *
     * @param role zu validierendes AKRole-Objekt
     * @throws GUIException wenn ungueltige Daten festgestellt werden
     */
    private void validateModel(AKRole role) throws GUIException {
        if (StringUtils.isBlank(role.getName())) {
            throw new GUIException(GUIException.ROLE_VALIDATE_NAME);
        }

        if (model.getApplicationId() == null) {
            throw new GUIException(GUIException.ROLE_VALIDATE_APPLICATION);
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable arg0, Object arg1) {
    }
}
