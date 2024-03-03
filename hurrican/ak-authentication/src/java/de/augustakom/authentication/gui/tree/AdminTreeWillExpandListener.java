/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 14:13:53
 */
package de.augustakom.authentication.gui.tree;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import de.augustakom.common.gui.swing.AKJTree;


/**
 * Listener, um auf Expand-/Collapse-Ereignisse im Tree reagieren zu koennen.
 */
public class AdminTreeWillExpandListener implements TreeWillExpandListener {

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
        AKJTree tree = (AKJTree) event.getSource();
        try {
            if (tree.getParent() != null) {
                tree.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            IAdminTreeModel model = (IAdminTreeModel) tree.getModel();
            model.loadChildren(event.getPath());
        }
        finally {
            if (tree.getParent() != null) {
                tree.getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

    }

}
