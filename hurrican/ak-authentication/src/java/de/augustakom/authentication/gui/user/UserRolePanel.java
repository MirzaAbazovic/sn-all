/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2004 10:29:31
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationPanel;
import de.augustakom.authentication.gui.basics.SavePanel;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.model.AKApplication;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJLabel;
import de.augustakom.common.gui.swing.AKJList;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.AKJScrollPane;
import de.augustakom.common.gui.swing.MessageHelper;


/**
 * Panel fuer die User-Rollen-Zuordnung. <br>
 *
 *
 */
public class UserRolePanel extends AbstractAuthenticationPanel implements SavePanel {

    private static final Logger LOGGER = Logger.getLogger(UserRolePanel.class);

    private static final String AVAILABLE_ROLES = "available.roles";
    private static final String ASSIGNED_ROLES = "assigned.roles";
    private static final String CMD_ASSIGN = "assign";
    private static final String CMD_REMOVE = "remove";

    private AKJList lsAssigned = null;
    private AKJList lsAvailable = null;

    private AKUser model = null;
    private List<AKRole> availableRoles = null;
    private List<AKRole> userRoles = null;
    private AKApplication application = null;

    /**
     * Konstruktor mit Angabe des User-Models.
     *
     * @param model
     */
    public UserRolePanel(AKUser model) {
        super("de/augustakom/authentication/gui/user/resources/UserRolePanel.xml");
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

        RoleListCellRenderer roleRenderer = new RoleListCellRenderer();
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
     * @see de.augustakom.common.gui.swing.AKJAbstractPanel#execute(java.lang.String)
     */
    @Override
    protected void execute(String command) {
        if (CMD_ASSIGN.equals(command)) {
            assignRoles();
        }
        else if (CMD_REMOVE.equals(command)) {
            removeRoles();
        }
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
            readAssignedRoles();
            readAvailableRoles();
        }
        finally {
            setDefaultCursor();
        }
    }

    /*
     * Liest alle verfuegbaren Rollen fuer die z.Z. ausgewaehlte Applikation aus
     * und ordnet sie der entsprechenden Liste zu.
     */
    private void readAvailableRoles() {
        if (availableRoles == null) {
            try {
                AKRoleService roleService = getAuthenticationService(
                        AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
                availableRoles = roleService.findAll();
            }
            catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
        }
        Collections.sort(availableRoles); //NEW
        AKApplication application = getApplication();
        if ((availableRoles != null) && (application != null)) {
            DefaultListModel lsModel = new DefaultListModel();
            try {
                // roles sorted by name case insensitive
                availableRoles.stream()
                        .filter(role -> role.getApplicationId().equals(application.getId()) && !isRoleAssigned(role))
                        .sorted((r1, r2) -> StringUtils.trimToEmpty(r1.getName()).toLowerCase()
                                .compareTo(StringUtils.trimToEmpty(r2.getName()).toLowerCase()))
                        .forEach(role -> lsModel.addElement(role));
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
     * Ueberprueft, ob die Rolle dem Benutzer bereits
     * zugeordnet ist.
     * @param role Rolle, die ueberprueft werden soll.
     * @return true wenn die Rolle dem Benutzer bereits
     * zugeordnet ist.
     */
    private boolean isRoleAssigned(AKRole role) {
        if (userRoles != null) {
            for (int i = 0; i < userRoles.size(); i++) {
                if ((userRoles.get(i)).getId().equals(role.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Liest alle Rollen aus, die dem Benutzer fuer die aktuell ausgewaehlte
     * Applikation zugeordnet sind und ordnet sie der enstprechenden Liste zu.
     */
    private void readAssignedRoles() {
        if (userRoles == null) {
            if (model.getId() != null) {
                try {
                    AKUserService userService = getAuthenticationService(
                            AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                    userRoles = userService.getRoles(model.getId());
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
                }
            }
            else {
                userRoles = new ArrayList<AKRole>();
            }
        }

        AKApplication application = getApplication();
        if ((userRoles != null) && (application != null)) {
            DefaultListModel lsModel = new DefaultListModel();
            try {
                // roles sorted by name case insensitive
                userRoles.stream()
                        .filter(role -> role.getApplicationId().equals(application.getId()))
                        .sorted((r1, r2) -> StringUtils.trimToEmpty(r1.getName()).toLowerCase()
                                .compareTo(StringUtils.trimToEmpty(r2.getName()).toLowerCase()))
                        .forEach(role -> lsModel.addElement(role));
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

    /* Ordnet die ausgewaehlten Rollen dem Benutzer zu. */
    private void assignRoles() {
        Object[] selection = lsAvailable.getSelectedValues();
        if ((selection != null) && (userRoles != null)) {
            for (int i = 0; i < selection.length; i++) {
                if (selection[i] instanceof AKRole) {
                    userRoles.add((AKRole) selection[i]);
                }
            }

            read();
        }
    }

    /* Entfernt die ausgewaehlten Rollen aus der Benutzer-Rollen-Zuordnung. */
    private void removeRoles() {
        Object[] selection = lsAssigned.getSelectedValues();
        if ((selection != null) && (userRoles != null)) {
            List<AKRole> toRemove = new ArrayList<AKRole>();
            for (int i = 0; i < selection.length; i++) {
                if (selection[i] instanceof AKRole) {
                    AKRole role = (AKRole) selection[i];

                    for (int k = 0; k < userRoles.size(); k++) {
                        if ((userRoles.get(k)).getId().equals(role.getId())) {
                            toRemove.add(userRoles.get(k));
                        }
                    }
                }
            }

            for (int i = 0; i < toRemove.size(); i++) {
                userRoles.remove(toRemove.get(i));
            }

            read();
        }
    }

    /**
     * @see de.augustakom.authentication.gui.basics.SavePanel#doSave()
     */
    public void doSave() throws GUIException {
        List<Long> applications = new ArrayList<Long>();
        for (int i = 0; i < userRoles.size(); i++) {
            AKRole role = userRoles.get(i);
            applications.add(role.getApplicationId());
        }

        try {
            setWaitCursor();

            try {
                AKUserService userService = getAuthenticationService(
                        AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
                userService.setRoles(model.getId(), userRoles);
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
    public void update(Observable o, Object arg) {
    }

    /**
     * Renderer fuer die Lists mit AKRole-Objekten
     */
    static class RoleListCellRenderer extends DefaultListCellRenderer {
        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if ((comp instanceof JLabel) && (value instanceof AKRole)) {
                ((JLabel) comp).setText(((AKRole) value).getName());
            }

            return comp;
        }
    }
}
