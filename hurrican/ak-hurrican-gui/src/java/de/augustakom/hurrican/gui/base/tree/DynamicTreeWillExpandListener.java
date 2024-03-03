/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 13:44:45
 */
package de.augustakom.hurrican.gui.base.tree;

import javax.swing.event.*;
import javax.swing.tree.*;


/**
 * Listener, um auf Expand-/Collapse-Ereignisse im Tree reagieren zu koennen.
 *
 *
 */
public class DynamicTreeWillExpandListener implements TreeWillExpandListener {

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
     */
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
        DynamicTreeNode parentNode = (DynamicTreeNode) event.getPath().getLastPathComponent();
        parentNode.loadChildren();
    }

}
