/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.07.2011 14:01:10
 */
package de.augustakom.hurrican.gui.hvt.switchmigration.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.hvt.switchmigration.SwitchMigrationFrame;

/**
 * Action, um das Admin-Frame fuer die Switch Migrationen zu oeffnen.
 */
public class OpenSwitchMigrationFrameAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        SwitchMigrationFrame frame = new SwitchMigrationFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    @Override
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    @Override
    protected String getUniqueName() {
        return uniqueName;
    }

}


