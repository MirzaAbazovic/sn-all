/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.tree;

import java.time.*;
import java.util.*;
import javax.swing.tree.*;

import de.augustakom.hurrican.gui.base.tree.DynamicTree;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;

/**
 * Tree fuer die Darstellung von Vorabstimmungs-Messages.
 */
public class VaTree extends DynamicTree {
    private static final long serialVersionUID = -5596887572988482080L;

    private TreeNode treeNodeWithLatestWbciMessage = null;

    /**
     * Ermittelt die neueste WBCI-Nachricht in dem Tree und selektiert diese.
     */
    public void selectLatestWbciMessageInTree() {
        treeNodeWithLatestWbciMessage = null;

        walk(getModel(), getModel().getRoot());
        if (treeNodeWithLatestWbciMessage != null) {
            TreePath path = new TreePath(treeNodeWithLatestWbciMessage);
            setSelectionPath(new TreePath(getModel().getPathToRoot(treeNodeWithLatestWbciMessage)));
            scrollPathToVisible(path);
        }
    }

    private void walk(DefaultTreeModel model, Object node) {
        int cc = model.getChildCount(node);
        for (int i = 0; i < cc; i++) {
            Object child = model.getChild(node, i);

            if (treeNodeWithLatestWbciMessage == null || isChildNewer(treeNodeWithLatestWbciMessage, child)) {
                treeNodeWithLatestWbciMessage = (TreeNode) child;
            }

            if (!model.isLeaf(child)) {
                walk(model, child);
            }
        }
    }

    /* Prueft, ob die Message des Child-Nodes neuer ist als die von 'nodeWithLatestWbciMessage' */
    private boolean isChildNewer(Object nodeWithLatestWbciMessage, Object child) {
        LocalDateTime latestProcessedAt = getProcessedAt((DynamicTreeNode) nodeWithLatestWbciMessage);
        LocalDateTime childProcessedAt = getProcessedAt((DynamicTreeNode) child);

        if (childProcessedAt != null) {
            return childProcessedAt.isAfter(latestProcessedAt);
        }
        else {
            return true;
        }
    }

    /* Ermittelt aus dem angegebenen TreeNode die WbciMessage und davon das 'processedAt' Datum */
    private LocalDateTime getProcessedAt(DynamicTreeNode node) {
        if (node.getUserObject() instanceof WbciRequest) {
            final Date processedAt = ((WbciRequest) node.getUserObject()).getProcessedAt();
            return toLocalDateTime(processedAt);
        }
        else if (node.getUserObject() instanceof WbciMessage) {
            final Date processedAt = ((WbciMessage) node.getUserObject()).getProcessedAt();
            return toLocalDateTime(processedAt);
        }
        return null;
    }

    private LocalDateTime toLocalDateTime(Date processedAt) {
        return DateConverterUtils.asLocalDateTime(processedAt);
    }

}
