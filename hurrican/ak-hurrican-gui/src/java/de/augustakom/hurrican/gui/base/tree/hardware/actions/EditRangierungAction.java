/**
  * Copyright (c) 2010 - M-net Telekommunikations GmbH
  * All rights reserved.
  * -------------------------------------------------------
  * File created: 01.07.2010 12:07:20
  */
package de.augustakom.hurrican.gui.base.tree.hardware.actions;

import java.awt.event.*;
import java.util.*;

import de.augustakom.common.gui.swing.AKAbstractAction;
import de.augustakom.common.gui.swing.AKJPanel;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.hardware.EditRangierungPanel;
import de.augustakom.hurrican.gui.base.tree.hardware.node.EquipmentNodeContainer;

public class EditRangierungAction extends AKAbstractAction {

    private final List<EquipmentNodeContainer> nodes;
    private final DynamicTreeMouseListener dynamicTreeMouseListener;

    public EditRangierungAction(List<EquipmentNodeContainer> nodes, DynamicTreeMouseListener dynamicTreeMouseListener) {
        this.nodes = nodes;
        this.dynamicTreeMouseListener = dynamicTreeMouseListener;

        setName("Rangierungen bearbeiten...");
        setActionCommand("edit.rangierung");
        setTooltip("Öffnet eine Tabelle, um alle Rangierungen dieser Baugruppe zu bearbeiten");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AKJPanel panel = new EditRangierungPanel(nodes);
        dynamicTreeMouseListener.showPanel(panel, panel.getName());
    }
}
