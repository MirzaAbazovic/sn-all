/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 16:07:13
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAccountService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Panel fuer die User-Account-Zuordnung. <br>
 */
public class UserAccountPanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(UserAccountPanel.class);

    private static final String AVAILABLE_ROLES = "available.accounts";
    private static final String ASSIGNED_ROLES = "assigned.accounts";
    private static final String CMD_ASSIGN = "assign";
    private static final String CMD_REMOVE = "remove";

    private AKJList lsAssigned = null;
    private AKJList lsAvailable = null;

    private AKUser model = null;
    private AKApplication application = null;
    private List<AKAccount> availableAccounts = null;
    private List<AKAccount> userAccounts = null;

    /**
     * Konstruktor mit Angabe des Models.
     *
     * @param model
     */
    public UserAccountPanel(AKUser model) {
        super("de/augustakom/authentication/gui/user/resources/UserAccountPanel.xml");
        this.model = model;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#createGUI()
     */
    @Override
    protected final void createGUI() {
        AKJLabel lblAssigned = getSwingFactory().createLabel(ASSIGNED_ROLES);
        AKJLabel lblAvailable = getSwingFactory().createLabel(AVAILABLE_ROLES);

        AccountListCellRenderer roleRenderer = new AccountListCellRenderer();
        lsAssigned = getSwingFactory().createList(ASSIGNED_ROLES);
        lsAssigned.setCellRenderer(roleRenderer);
        lsAvailable = getSwingFactory().createList(AVAILABLE_ROLES);
        lsAvailable.setCellRenderer(roleRenderer);

        AKJButton btnAssign = getSwingFactory().createButton(CMD_ASSIGN, getActionListener());
        AKJButton btnRemove = getSwingFactory().createButton(CMD_REMOVE, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnAssign, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnRemove, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new JPanel(), GBCFactory.createGBC(0, 100, 0, 2, 1, 1, GridBagConstraints.VERTICAL));

        Dimension lsSize = new Dimension(150, 100);
        AKJScrollPane scrAssigned = new AKJScrollPane(lsAssigned);
        scrAssigned.setPreferredSize(lsSize);
        AKJScrollPane scrAvailable = new AKJScrollPane(lsAvailable);
        scrAvailable.setPreferredSize(lsSize);

        AKJPanel listPanel = new AKJPanel(new GridBagLayout());
        listPanel.add(lblAssigned, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        listPanel.add(lblAvailable, GBCFactory.createGBC(0, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        listPanel.add(scrAssigned, GBCFactory.createGBC(50, 50, 0, 1, 1, 5, GridBagConstraints.BOTH));
        listPanel.add(btnPanel, GBCFactory.createGBC(0, 0, 1, 1, 1, 5, GridBagConstraints.VERTICAL));
        listPanel.add(scrAvailable, GBCFactory.createGBC(50, 50, 2, 1, 1, 5, GridBagConstraints.BOTH));
        listPanel.add(new JPanel(), GBCFactory.createGBC(50, 0, 3, 1, 1, 5, GridBagConstraints.HORIZONTAL));

        this.setLayout(new GridBagLayout());
        this.add(listPanel, GBCFactory.createGBC(100, 50, 0, 0, 1, 1, GridBagConstraints.BOTH));
        this.add(new JPanel(), GBCFactory.createGBC(100, 0, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL));

        read();
    }

    /**
     * Setzt die Applikation, fuer die die Benutzer-Rollen-Zuordnungen angezeigt werden sollen.
     *
     * @param application
     */
    protected void setApplication(AKApplication application) {
        this.application = application;
        read();
    }

    /**
     * Gibt die aktuell ausgewaehlte Applikation zurueck.
     *
     * @return
     */
    protected AKApplication getApplication() {
        return application;
    }

    /* Liest alle benoetigten Daten aus und ordnet sie den GUI-Komponenten zu. */
    private void read() {
        try {
            setWaitCursor();
            readAssignedAccounts();
            readAvailableAccounts();
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Liest alle verfuegbaren Accounts fuer die z.Z. ausgewaehlte Applikation aus
     * und ordnet sie der entsprechenden Liste zu.
     */
    private void readAvailableAccounts() {
        if (availableAccounts == null) {
            try {
                AKAccountService accService = getAuthenticationService(
                        AKAuthenticationServiceNames.ACCOUNT_SERVICE, AKAccountService.class);
                availableAccounts = accService.findAll();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
        }

        AKApplication app = getApplication();
        if ((availableAccounts != null) && (app != null)) {
            DefaultListModel lsModel = new DefaultListModel();
            try {
                for (int i = 0; i < availableAccounts.size(); i++) {
                    AKAccount acc = availableAccounts.get(i);
                    if (acc.getApplicationId().equals(app.getId()) && (!isAccountAssigned(acc))) {
                        lsModel.addElement(acc);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
            finally {
                lsAvailable.setModel(lsModel);
            }
        }
    }

    /*
     * Ueberprueft, ob der Account dem Benutzer bereits
     * zugeordnet ist.
     * @param acc Account, der ueberprueft werden soll.
     * @return true wenn der Account dem Benutzer bereits
     * zugeordnet ist.
     */
    private boolean isAccountAssigned(AKAccount acc) {
        if (userAccounts != null) {
            for (int i = 0; i < userAccounts.size(); i++) {
                if ((userAccounts.get(i)).getId().equals(acc.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Liest alle Accounts aus, die dem Benutzer fuer die aktuell ausgewaehlte
     * Applikation zugeordnet sind und ordnet sie der enstprechenden Liste zu.
     */
    private void readAssignedAccounts() {
        if (userAccounts == null) {
            if (model.getId() != null) {
                try {
                    AKUserService userService = getAuthenticationService(
                            AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                    userAccounts = userService.getDBAccounts(model.getId());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
                }
            }
            else {
                userAccounts = new ArrayList<AKAccount>();
            }
        }

        AKApplication app = getApplication();
        if ((userAccounts != null) && (application != null)) {
            DefaultListModel lsModel = new DefaultListModel();
            try {
                for (int i = 0; i < userAccounts.size(); i++) {
                    if ((userAccounts.get(i)).getApplicationId().equals(app.getId())) {
                        lsModel.addElement(userAccounts.get(i));
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
            finally {
                lsAssigned.setModel(lsModel);
            }
        }
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CMD_ASSIGN.equals(command)) {
            assignAccounts();
        }
        else if (CMD_REMOVE.equals(command)) {
            removeAccounts();
        }
    }

    /* Ordnet die ausgewaehlten Accounts dem Benutzer zu. */
    private void assignAccounts() {
        Object[] selection = lsAvailable.getSelectedValues();
        if ((selection != null) && (userAccounts != null)) {
            for (int i = 0; i < selection.length; i++) {
                if (selection[i] instanceof AKAccount) {
                    userAccounts.add((AKAccount) selection[i]);
                }
            }

            read();
        }
    }

    /* Entfernt die ausgewaehlten Accounts aus der Benutzer-Account-Zuordnung. */
    private void removeAccounts() {
        Object[] selection = lsAssigned.getSelectedValues();
        if ((selection != null) && (userAccounts != null)) {
            List<AKAccount> toRemove = new ArrayList<>();
            for (int i = 0; i < selection.length; i++) {
                if (selection[i] instanceof AKAccount) {
                    AKAccount role = (AKAccount) selection[i];

                    for (int k = 0; k < userAccounts.size(); k++) {
                        if ((userAccounts.get(k)).getId().equals(role.getId())) {
                            toRemove.add(userAccounts.get(k));
                        }
                    }
                }
            }

            for (int i = 0; i < toRemove.size(); i++) {
                userAccounts.remove(toRemove.get(i));
            }

            read();
        }
    }

    /**
     * @see de.augustakom.authentication.gui.basics.SavePanel#doSave()
     */
    @Override
    public void doSave() throws GUIException {
        try {
            setWaitCursor();

            try {
                AKUserService userService = getAuthenticationService(
                        AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                userService.setDBAccounts(model.getId(), userAccounts);
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new GUIException(GUIException.USER_SAVING_ERROR, e);
            }
        }
        finally {
            setDefaultCursor();
        }
    }

    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable o, Object arg) {
    }

    /**
     * Renderer fuer die Lists mit AKAccount-Objekten
     */
    static class AccountListCellRenderer extends DefaultListCellRenderer {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof AKAccount)) {
                ((JLabel) comp).setText(((AKAccount) value).getName());
            }

            return comp;
        }
    }
}
