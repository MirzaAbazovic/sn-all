/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.12.2008 13:25:35
 */
package de.augustakom.hurrican.gui.base.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import de.augustakom.common.gui.swing.IconHelper;


/**
 * Renderer fuer den Dynamic-Tree. <br>
 *
 *
 */
public class DynamicTreeRenderer extends DefaultTreeCellRenderer {

    private static final IconHelper ICONHELPER = new IconHelper();

    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean,
     * boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DynamicTreeNode) {
            DynamicTreeNode node = (DynamicTreeNode) value;
            label.setText(node.getDisplayName());
            label.setToolTipText(node.getTooltip());

            Icon icon = ICONHELPER.getIcon(node.getIcon());
            if (icon != null) {
                label.setIcon(icon);
            }
        }

        return label;
    }

}
