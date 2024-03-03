/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 16:43:09
 */
package de.augustakom.authentication.gui.tree;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.*;
import org.apache.log4j.Logger;

import de.augustakom.authentication.gui.GUISystemRegistry;
import de.augustakom.authentication.gui.exceptions.TreeException;
import de.augustakom.authentication.gui.system.MDIMainFrame;
import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.common.gui.swing.MessageHelper;
import de.augustakom.common.service.exceptions.ServiceNotFoundException;
import de.augustakom.common.service.locator.ServiceLocator;


/**
 * MouseListener fuer den Admin-Tree. <br>
 */
public class AdminTreeMouseListener extends MouseAdapter {

    private static final Logger LOGGER = Logger.getLogger(AdminTreeMouseListener.class);

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showContextMenu(e);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showContextMenu(e);
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
            showDefaultFrame(e);
        }
    }

    /*
     * Oeffnet das Standardfenster zu dem User-Object des
     * selektierten TreeNodes.
     * @param e
     */
    private void showDefaultFrame(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        try {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            TreePath selPath = tree.getSelectionModel().getSelectionPath();

            AKJDefaultMutableTreeNode node = null;
            if (selPath != null) {
                node = (AKJDefaultMutableTreeNode) selPath.getLastPathComponent();
            }

            if ((node == null) || (node.getUserObject() == null) || (node.getUserObject() instanceof String)) {
                LOGGER.warn("Could not open frame for the node.");
                return;
            }

            @SuppressWarnings("unchecked")
            AbstractTreeService<Object, Object> ts = (AbstractTreeService<Object, Object>)
                    getTreeService(node.getUserObject().getClass().getName());
            if (ts != null) {
                try {
                    AKJInternalFrame f = ts.getFrame4Object(node.getUserObject(), node);
                    if (f != null) {
                        MDIMainFrame mf = (MDIMainFrame) GUISystemRegistry.instance()
                                .getValue(GUISystemRegistry.REGKEY_MAINFRAME);
                        mf.registerFrame(f, false);
                    }
                }
                catch (TreeException ex) {
                    LOGGER.warn(ex.getMessage(), ex);
                }
            }
        }
        finally {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Stellt ein PopupMenu mit den verfuegbaren Kontexten des TreeNode User-Objects dar.
     */
    private void showContextMenu(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

        if ((selPath != null) && (selPath.getPathCount() != 1)) { // not root
            tree.setSelectionPath(selPath);
            AKJDefaultMutableTreeNode node = (AKJDefaultMutableTreeNode) selPath.getLastPathComponent();
            if ((node == null) || (node.getUserObject() == null)) {
                LOGGER.warn("Could not open context menu for the node.");
                return;
            }

            AbstractTreeService<?, ?> ts = getTreeService(node.getUserObject().getClass().getName());
            if (ts != null) {
                try {
                    List<?> contextList = ts.getContextList4Object(node.getUserObject(), node);
                    if ((contextList != null) && (!contextList.isEmpty())) {
                        AKJPopupMenu popup = new AKJPopupMenu();

                        for (int i = 0; i < contextList.size(); i++) {
                            Object context = contextList.get(i);
                            if (context instanceof Action) {
                                popup.add((Action) context);
                            }
                            else if (context instanceof JSeparator) {
                                popup.add((JSeparator) context);
                            }
                        }

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
                catch (TreeException ex) {
                    LOGGER.warn(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Sucht nach einem TreeService und gibt diesen zurueck.
     */
    private AbstractTreeService<?, ?> getTreeService(String serviceName) {
        try {
            return ServiceLocator.instance().getService(serviceName, AbstractTreeService.class);
        }
        catch (ServiceNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            MessageHelper.showErrorDialog(null, e);
        }
        return null;
    }

}
