/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.gui.innenauftrag.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.innenauftrag.IaLevelAdminFrame;

/**
 * Action, um das Admin-Frame fuer die IA-Levels zu oeffnen.
 */
public class OpenIaLevelAdminFrameAction extends AKAbstractOpenFrameAction {

    private static final long serialVersionUID = -4617538470702345573L;

    private String uniqueName;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        IaLevelAdminFrame frame = new IaLevelAdminFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    @Override
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    @Override
    protected boolean maximizeFrame() {
        return true;
    }

    @Override
    protected String getUniqueName() {
        return uniqueName;
    }
}
