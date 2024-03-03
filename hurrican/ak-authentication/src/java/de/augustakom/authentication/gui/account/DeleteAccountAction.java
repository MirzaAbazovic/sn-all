/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2004 08:08:32
 */
package de.augustakom.authentication.gui.account;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.SystemConstants;
import de.augustakom.authentication.gui.basics.AbstractAuthenticationServiceAction;
import de.augustakom.authentication.model.AKAccount;
import de.augustakom.authentication.service.AKAccountService;
import de.augustakom.authentication.service.AKAuthenticationServiceNames;
import de.augustakom.authentication.service.exceptions.AKAuthenticationException;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.tools.lang.ResourceReader;


/**
 * Action-Klasse, um ein AKAccount-Objekt zu loeschen.
 *
 *
 */
public class DeleteAccountAction extends AbstractAuthenticationServiceAction {

    private static final Logger LOGGER = Logger.getLogger(DeleteAccountAction.class);

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        AKJDefaultMutableTreeNode node = (AKJDefaultMutableTreeNode) getValue(SystemConstants.ACTION_PROPERTY_TREENODE);
        Object userObj = getValue(SystemConstants.ACTION_PROPERTY_USEROBJECT);

        if (userObj instanceof AKAccount) {
            ResourceReader rr = new ResourceReader("de.augustakom.authentication.gui.account.resources.ActionMessages");
            String title = rr.getValue("delete.account.title");
            String msg = rr.getValue("delete.account.msg", new Object[] { node.getText() });

            int choice = MessageHelper.showConfirmDialog(
                    GUISystemRegistry.instance().getMainFrame(),
                    msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                deleteAccount((AKAccount) userObj, node);
            }
        }
    }

    /* Loescht den Account und entfernt den entsprechenden TreeNode */
    private void deleteAccount(AKAccount account, AKJDefaultMutableTreeNode node) {
        try {
            AKAccountService service = getAuthenticationService(
                    AKAuthenticationServiceNames.ACCOUNT_SERVICE, AKAccountService.class);
            LOGGER.info("Delete Account wiht ID " + account.getId());
            service.delete(account.getId());

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
