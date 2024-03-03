/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 08.10.2010 08:30:00
  */
package de.augustakom.hurrican.gui.base.tree.actions;

import java.awt.event.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;

/**
 * Action-Klasse fuer einen Tree-Node, um diesen zu refreshen.
 */
public class RefreshNodeAction extends AKAbstractAction {

    private final Set<DynamicTreeNode> nodes;

    public RefreshNodeAction(Set<DynamicTreeNode> nodes) {
        this.nodes = nodes;

        setName("Aktualisieren");
        setActionCommand("refresh");
        setTooltip("Lädt die Einträge unterhalb des ausgewählten Elements neu");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (DynamicTreeNode node : nodes) {
            if (node instanceof DynamicTreeNode) {
                DynamicTreeNode treeNode = node;
                treeNode.refreshChildren();
            }
        }
    }
}
