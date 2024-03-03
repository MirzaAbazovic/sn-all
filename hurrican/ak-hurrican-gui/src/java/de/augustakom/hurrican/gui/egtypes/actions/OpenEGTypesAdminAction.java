/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.06.2011 12:32:00
 */
package de.augustakom.hurrican.gui.egtypes.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.egtypes.EGTypesAdminFrame;

/**
 * Action, um das Admin-Frame fuer die Verwaltung der Endgeraetetypen zu oeffnen.
 */
public class OpenEGTypesAdminAction extends AKAbstractOpenFrameAction {


    private String uniqueName = null;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        EGTypesAdminFrame frame = new EGTypesAdminFrame();
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


