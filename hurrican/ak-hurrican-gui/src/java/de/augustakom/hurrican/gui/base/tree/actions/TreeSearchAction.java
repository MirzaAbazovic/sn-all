/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 07.10.2010 10:00:00
  */
package de.augustakom.hurrican.gui.base.tree.actions;

import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.DialogHelper;
import de.augustakom.common.gui.swing.table.AKTreeSearchDialog;
import de.augustakom.common.tools.lang.WildcardTools;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;

/**
 * Action-Klasse, um in einem Tree einen Such-Dialog zu oeffnen.
 */
public class TreeSearchAction extends AKAbstractAction {

    private final Set<DynamicTreeNode> nodes;

    public TreeSearchAction(Set<DynamicTreeNode> nodes) {
        this.nodes = nodes;

        setName("Suchen...");
        setActionCommand("find.hardware");
        setTooltip("Öffnet einen Dialog für die Suche nach Elementen des Hardwaretrees");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AKTreeSearchDialog searchDialog = new AKTreeSearchDialog(nodes.iterator().next()) {
            @Override
            protected DefaultMutableTreeNode searchNodeChildren(DefaultMutableTreeNode node, String pattern, boolean ignoreFirstNode) {
                if (isExpandAll() && (node instanceof DynamicTreeNode) && !isCanceled()) {
                    DynamicTreeNode treeNode = (DynamicTreeNode) node;
                    if (treeNode.getChildrenFound() == null) {
                        treeNode.loadChildren();
                    }
                }
                return super.searchNodeChildren(node, pattern, ignoreFirstNode);
            }

            @Override
            protected boolean matches(TreeNode node, String pattern) {
                if (node instanceof DynamicTreeNode) {
                    DynamicTreeNode treeNode = (DynamicTreeNode) node;
                    boolean match = WildcardTools.matchIgnoreCase(getNodeText(node), pattern);
                    if (treeNode.getTooltip() != null) {
                        match |= WildcardTools.matchIgnoreCase(treeNode.getTooltip(), pattern);
                    }
                    if (match) {
                        TreePath path = new TreePath(treeNode.getPath());
                        treeNode.getTree().setSelectionPath(path);
                        treeNode.getTree().scrollPathToVisible(path);
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected String getNodeText(TreeNode node) {
                String text = node.toString();
                if (node instanceof DynamicTreeNode) {
                    DynamicTreeNode treeNode = (DynamicTreeNode) node;
                    text = treeNode.getDisplayName();
                }
                return text;
            }
        };
        DialogHelper.showDialog(HurricanSystemRegistry.instance().getMainFrame(), searchDialog, true, true);
    }
}
