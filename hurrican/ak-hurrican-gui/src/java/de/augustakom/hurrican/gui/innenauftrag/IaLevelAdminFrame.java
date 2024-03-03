/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag;

import javax.swing.*;

import de.augustakom.hurrican.gui.base.tree.AbstractDynamicTreeFrame;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeMouseListener;
import de.augustakom.hurrican.gui.base.tree.DynamicTreeNode;

/**
 * Frame zur Administration der IA-Levels.
 */
public class IaLevelAdminFrame extends AbstractDynamicTreeFrame {

    private static final long serialVersionUID = -7413526552399982246L;

    private static final String RESOURCE = "de/augustakom/hurrican/gui/innenauftrag/resources/IaLevelAdminFrame.xml";

    public IaLevelAdminFrame() {
        super(RESOURCE, 1d);
        createGUI();
    }

    @Override
    protected Class<? extends DynamicTreeNode> getRootNodeClass() {
        return IaLevelRootNode.class;
    }

    @Override
    protected DynamicTreeMouseListener getMouseListener(JComponent panelContainer) {
        return new IaLevelTreeMouseListener(this, panelContainer);
    }
}
