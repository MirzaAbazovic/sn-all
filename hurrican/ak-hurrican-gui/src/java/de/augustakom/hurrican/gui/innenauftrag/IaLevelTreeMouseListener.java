/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.hurrican.gui.base.tree.AbstractDynamicTreeFrame;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.innenauftrag.actions.AddIaLevelAction;
import de.augustakom.hurrican.gui.innenauftrag.actions.DeleteIaLevelAction;
import de.augustakom.hurrican.gui.innenauftrag.actions.EditIaLevelAction;

/**
 * MouseListener fuer die IaLevel Tree.
 */
public class IaLevelTreeMouseListener extends DynamicTreeMouseListener {

    /**
     * Konstruktor fuer den MouseListener.
     *
     * @param frame
     * @param panelContainer Angabe des Containers, in dem die Panels fuer die TreeNodes dargestellt werden sollen.
     */
    public IaLevelTreeMouseListener(AbstractDynamicTreeFrame frame, JComponent panelContainer) {
        super(frame, panelContainer);
    }

    @Override
    protected void addTreeSpecificContextMenuItems(Set<DynamicTreeNode> nodes, AKJPopupMenu popup) {
        if (nodes.size() == 1) {
            DynamicTreeNode selectedNode = nodes.iterator().next();
            if (!(selectedNode instanceof IaLevel5TreeNode)) {
                addActionsToPopupMenu(popup, Arrays.asList(new AddIaLevelAction(selectedNode)));
            }

            if (!(selectedNode instanceof IaLevelRootNode)) {
                addActionsToPopupMenu(popup, Arrays.asList(new EditIaLevelAction(selectedNode)));
                addActionsToPopupMenu(popup, Arrays.asList(new DeleteIaLevelAction(selectedNode)));
            }
        }
    }
}
