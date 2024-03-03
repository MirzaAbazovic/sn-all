/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2009 14:11:03
 */
package de.augustakom.hurrican.gui.base.tree;

import java.util.*;
import javax.swing.tree.*;

import de.augustakom.common.gui.swing.AKJTree;


/**
 * AKJTree der ein DefaultTreeModel als TreeModel verlangt.
 *
 *
 */
public class DynamicTree extends AKJTree {

    private static final long serialVersionUID = -8328751971438607875L;

    @Override
    public void setModel(TreeModel model) {
        if (!(model instanceof DefaultTreeModel)) {
            throw new RuntimeException("Dynamic tree only accepts DefaultTreeModel as it's model");
        }
        super.setModel(model);
    }

    @Override
    public DefaultTreeModel getModel() {
        return (DefaultTreeModel) super.getModel();
    }

    /**
     * Expands or collapses the complete tree
     *
     * @param parent TreePath to expand/collapse the tree
     * @param expand flag to define if the tree has to be expanded (true) or collapsed (false)
     */
    public void expandAll(TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (@SuppressWarnings("unchecked") Enumeration<TreeNode> e = node.children(); e.hasMoreElements(); ) {
                TreeNode treeNode = e.nextElement();
                TreePath path = parent.pathByAddingChild(treeNode);
                expandAll(path, expand);
            }
        }
        // Expansion or collapse must be done bottom-up
        if (expand) {
            expandPath(parent);
        }
        else {
            collapsePath(parent);
        }
    }

}
