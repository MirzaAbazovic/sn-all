/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2005 16:00:56
 */
package de.augustakom.authentication.gui.user;

import java.awt.*;
import java.util.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationOptionDialog;
import de.augustakom.authentication.gui.exceptions.GUIException;
import de.augustakom.authentication.gui.role.RoleRightsDialog;
import de.augustakom.authentication.gui.tree.AdminTreeNode;
import de.augustakom.authentication.gui.tree.IAdminTreeModel;
import de.augustakom.authentication.model.AKDepartment;
import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKUserService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.awt.GBCFactory;
import de.augustakom.common.gui.swing.AKJButton;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;

public class UserRoleCopyDialog extends AbstractAuthenticationOptionDialog {

    private static final Logger LOGGER = Logger.getLogger(UserRoleCopyDialog.class);

    private static final String CMD_CANCEL = "cancel";
    private static final String CMD_SAVE = "save";

    private UserDataPanel userDataPanel = null;

    private AKUser newUser = null;
    private AKUser origUser = null;

    /**
     * @param user
     */
    public UserRoleCopyDialog(AKUser user1, AKUser user2) {
        super("de/augustakom/authentication/gui/user/resources/UserRoleCopyDialog.xml", true);
        newUser = user1;
        origUser = user2;
        createGUI();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKJAbstractOptionDialog#createGUI()
     */
    @Override
    protected final void createGUI() {
        setTitle("User kopieren");

        AKJButton btnCancel = getSwingFactory().createButton(CMD_CANCEL, getActionListener());
        AKJButton btnSave = getSwingFactory().createButton(CMD_SAVE, getActionListener());

        AKJPanel btnPanel = new AKJPanel(new GridBagLayout());
        btnPanel.add(btnCancel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(btnSave, GBCFactory.createGBC(0, 0, 1, 0, 1, 1, GridBagConstraints.NONE));
        btnPanel.add(new AKJPanel(), GBCFactory.createGBC(100, 0, 2, 0, 1, 1, GridBagConstraints.HORIZONTAL));

        AKJPanel fill = new AKJPanel();
        fill.setPreferredSize(new Dimension(2, 2));

        userDataPanel = new UserDataPanel(newUser);
        AKJPanel child = new AKJPanel(new GridBagLayout());
        child.add(userDataPanel, GBCFactory.createGBC(0, 0, 0, 0, 1, 1, GridBagConstraints.NONE));
        child.add(btnPanel, GBCFactory.createGBC(0, 0, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        child.add(fill, GBCFactory.createGBC(100, 100, 1, 2, 1, 1, GridBagConstraints.BOTH));

        getChildPanel().setLayout(new BorderLayout());
        getChildPanel().add(child, BorderLayout.CENTER);
    }

    @Override
    protected void execute(String command) {
        if (CMD_SAVE.equals(command)) {
            try {
                save();
            }
            catch (AKAuthenticationException e) {
                LOGGER.error(e.getMessage(), e);
                MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
            }
        }
        else if (CMD_CANCEL.equals(command)) {
            cancel();
        }
    }


    /* Schliesst den Dialog. */
    private void cancel() {
        prepare4Close();
        setValue(Integer.valueOf(RoleRightsDialog.CANCEL_OPTION));
    }

    /* Kopiert den Benutzer. */
    private void save() throws AKAuthenticationException {
        try {
            addUser2Tree(newUser);
            userDataPanel.doSave();

            AKUserService service = getAuthenticationService(
                    AKAuthenticationServiceNames.USER_SERVICE, AKUserService.class);
            service.copyUserRoles(origUser.getId(), newUser.getId());
            service.copyUserAccounts(origUser.getId(), newUser.getId());

            prepare4Close();
            setValue(OK_OPTION);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
        catch (GUIException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
    }

    /* Fuegt den neuen Benutzer in den Tree ein. */
    private void addUser2Tree(AKUser user) {
        try {
            AdminTreeNode treeNode = new AdminTreeNode();
            treeNode.setUserObject(user);

            IAdminTreeModel treeModel = ((IAdminTreeModel) GUISystemRegistry.instance().getValue(
                    GUISystemRegistry.REGKEY_TREE_MODEL));
            // Node mit User-Object bei Neuanlage einfuegen oder bei Aenderung der Department-ID verschieben
            AKJDefaultMutableTreeNode newParent = treeModel.findNode(AKDepartment.class, user.getDepartmentId(), "getId");
            treeModel.moveNode(newParent, treeNode);
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), e);
        }
    }

    public void update(Observable o, Object arg) {
    }
}
