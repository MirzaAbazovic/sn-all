/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 15:42:05
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.ApplicationListCellRenderer;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJComboBox;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJTabbedPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Panel zur Darstellung von Daten, die dem Benutzer zugeordnet sind bzw. zugeordnet werden koennen.
 */
public class UserAssignmentsPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(UserAssignmentsPanel.class);

    private static final String APPLICATIONS = "applications";

    private AKJComboBox cobApplication = null;
    private UserRolePanel userRolePanel = null;
    private UserAccountPanel userAccountPanel = null;

    private AKUser model = null;

    /**
     * Konstruktor mit Angabe des User-Models.
     */
    public UserAssignmentsPanel(AKUser model) {
        super("de/augustakom/authentication/gui/user/resources/UserAssignmentsPanel.xml");
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblApplications = getSwingFactory().createLabel(APPLICATIONS);
        cobApplication = getSwingFactory().createComboBox(APPLICATIONS);
        cobApplication.addActionListener(getActionListener());
        cobApplication.setRenderer(new ApplicationListCellRenderer());

        userRolePanel = new UserRolePanel(model);
        userAccountPanel = new UserAccountPanel(model);

        AKJTabbedPane tabbedPane = new AKJTabbedPane();
        tabbedPane.addTab(getSwingFactory().getText("tab.roles"), userRolePanel);
        tabbedPane.addTab(getSwingFactory().getText("tab.db.accounts"), userAccountPanel);

        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createTitledBorder(getSwingFactory().getText("assignments")));
        this.add(lblApplications, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        this.add(cobApplication, GBCFactory.createGBC(50, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(new JPanel(), GBCFactory.createGBC(100, 0, 3, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        this.add(tabbedPane, GBCFactory.createGBC(100, 100, 0, 1, 4, 1, GridBagConstraints.BOTH));

        read();
    }

    /* Liest alle Applikationen aus und stellt sie in der ComboBox dar. */
    private void read() {
        try {
            readApplications(cobApplication);
        }
        catch (Exception e) {
            LOGGER.error(e, e);
            MessageHelper.showErrorDialog(this, e);
        }
        finally {
            execute(APPLICATIONS);
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (APPLICATIONS.equals(command)) {
            Object selection = cobApplication.getSelectedItem();
            if (selection instanceof AKApplication) {
                userRolePanel.setApplication((AKApplication) selection);
                userAccountPanel.setApplication((AKApplication) selection);
            }
        }
    }

    /**
     * @see de.augustakom.authentication.gui.basics.SavePanel#doSave()
     */
    public void doSave() throws GUIException {
        userRolePanel.doSave();
        userAccountPanel.doSave();
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
    }
}
