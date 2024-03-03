/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2004 08:08:32
 */
package de.augustakom.authentication.gui.role;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKRole;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.AKRoleService;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.ResourceReader;


/**
 * Action-Klasse, um ein AKRole-Objekt zu loeschen.
 *
 *
 */
public class DeleteRoleAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteRoleAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        AKJDefaultMutableTreeNode node = (AKJDefaultMutableTreeNode) getValue(SystemConstants.ACTION_PROPERTY_TREENODE);
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);

        if (userObj instanceof AKRole) {
            ResourceReader rr = new ResourceReader("de.augustakom.authentication.gui.role.resources.ActionMessages");
            String title = rr.getValue("delete.role.title");
            String msg = rr.getValue("delete.role.msg", new Object[] { node.getText() });

            int choice = MessageHelper.showConfirmDialog(
                    GUISystemRegistry.instance().getMainFrame(),
                    msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                deleteRole((AKRole) userObj, node);
            }
        }
    }

    /* Loescht die Rolle und entfernt den entsprechenden TreeNode */
    private void deleteRole(AKRole role, AKJDefaultMutableTreeNode node) {
        try {
            AKRoleService service = getAuthenticationService(AKAuthenticationServiceNames.ROLE_SERVICE, AKRoleService.class);
            LOGGER.info("Delete Role wiht ID " + role.getId());
            service.delete(role.getId());

            if (node.getParent() != null) {
                DefaultTreeModel model = (DefaultTreeModel) GUISystemRegistry.instance().getValue(
                        GUISystemRegistry.REGKEY_TREE_MODEL);
                model.removeNodeFromParent(node);
                model.nodeStructureChanged(node.getParent());
            }
        }
        catch (ServiceNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
        }
        catch (AKAuthenticationException ex) {
            LOGGER.error(ex.getMessage(), ex);
            MessageHelper.showErrorDialog(GUISystemRegistry.instance().getMainFrame(), ex);
        }
    }

}
