/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.01.2010 18:08:20
 */
package de.augustakom.hurrican.gui.base.tree.hardware;

import javax.swing.*;

import de.augustakom.hurrican.gui.base.tree.AbstractDynamicTreeFrame;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;
import de.augustakom.hurrican.gui.base.tree.hardware.node.HardwareRootNode;


/**
 *
 *
 */
public class HardwareTreeFrame extends AbstractDynamicTreeFrame {

    private static final long serialVersionUID = 4387334842744509939L;

    public HardwareTreeFrame() {
        super(null, 0.3d);
        setTitle("Hardware Tree");
    }

    @Override
    protected Class<? extends DynamicTreeNode> getRootNodeClass() {
        return HardwareRootNode.class;
    }

    @Override
    protected DynamicTreeMouseListener getMouseListener(JComponent panelContainer) {
        return new HardwareTreeMouseListener(this, panelContainer);
    }
}
