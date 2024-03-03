/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.05.2004 15:54:21
 */
package de.augustakom.authentication.gui.tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import de.augustakom.common.gui.swing.AKJDefaultMutableTreeNode;
import de.augustakom.common.gui.swing.IconHelper;


/**
 * Renderer fuer den Admin-Tree.
 */
public class AdminTreeRenderer extends DefaultTreeCellRenderer {

    private static final IconHelper ICONHELPER = new IconHelper();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
        JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof AKJDefaultMutableTreeNode) {
            AKJDefaultMutableTreeNode node = (AKJDefaultMutableTreeNode) value;
            label.setText(node.getText());
            label.setToolTipText(node.getTooltip());

            Icon icon = ICONHELPER.getIcon(node.getIconName());
            if (icon != null) {
                label.setIcon(icon);
            }
        }

        return label;
    }
}
