/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2009 16:01:39
 */
package de.augustakom.hurrican.gui.base.tree.hardware;

import java.util.*;
import javax.swing.*;

import de.augustakom.common.gui.swing.AKJPopupMenu;
import de.augustakom.hurrican.gui.base.tree.AbstractDynamicTreeFrame;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.actions.RefreshNodeAction;
import de.augustakom.hurrican.gui.base.tree.actions.TreeSearchAction;
import de.augustakom.hurrican.gui.base.tree.hardware.actions.EditEquipmentsAction;
import de.augustakom.hurrican.gui.base.tree.hardware.actions.EditRangierungAction;
import de.augustakom.hurrican.gui.base.tree.hardware.actions.GenerateKombiPortsAction;
import de.augustakom.hurrican.gui.base.tree.hardware.actions.MassenbenachrichtigungAction;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNodeContainer;
import de.augustakom.hurrican.gui.base.tree.hardware.node.HardwareRootNode;
import de.augustakom.hurrican.model.cc.Equipment;


/**
 * HardwareTree-spezifischer MouseListener
 */
public class HardwareTreeMouseListener extends DynamicTreeMouseListener {

    public HardwareTreeMouseListener(AbstractDynamicTreeFrame frame, JComponent panelContainer) {
        super(frame, panelContainer);
    }

    @Override
    protected void addTreeSpecificContextMenuItems(Set<DynamicTreeNode> nodes, AKJPopupMenu popup) {
        boolean isRootSelected = false;
        boolean isOnlyEquipmentNodeContainer = true;
        boolean isOnlyADSLNodes = true;

        for (DynamicTreeNode node : nodes) {
            if (node instanceof HardwareRootNode) {
                isRootSelected = true;
            }
            isOnlyEquipmentNodeContainer = isOnlyEquipmentNodeContainer && (node instanceof EquipmentNodeContainer);
            isOnlyADSLNodes = isOnlyADSLNodes && (node instanceof EquipmentNode) &&
                    (Equipment.HW_SCHNITTSTELLE_ADSL_OUT.equals(((EquipmentNode) node).getEquipment().getHwSchnittstelle()));
        }

        if (!isRootSelected) {
            MassenbenachrichtigungAction massenbenachrichtigungAction = new MassenbenachrichtigungAction(nodes, this);
            addActionsToPopupMenu(popup, Arrays.asList(massenbenachrichtigungAction));
            if (isOnlyEquipmentNodeContainer) {
                List<EquipmentNodeContainer> containers = new ArrayList<EquipmentNodeContainer>();
                for (DynamicTreeNode dynamicTreeNode : nodes) {
                    containers.add((EquipmentNodeContainer) dynamicTreeNode);
                }
                addActionsToPopupMenu(popup, Arrays.asList(new EditEquipmentsAction(containers, this),
                        new EditRangierungAction(containers, this)));
            }
            if (isOnlyADSLNodes) {
                GenerateKombiPortsAction action = new GenerateKombiPortsAction(nodes);
                addActionsToPopupMenu(popup, Arrays.asList(action));
            }
        }
        if (nodes.size() == 1) { // Suchen/Aktualisieren nicht bei Mehrfachauswahl
            addActionsToPopupMenu(popup, Arrays.asList(new RefreshNodeAction(nodes)));
            addActionsToPopupMenu(popup, Arrays.asList(new TreeSearchAction(nodes)));
        }
    }
}
