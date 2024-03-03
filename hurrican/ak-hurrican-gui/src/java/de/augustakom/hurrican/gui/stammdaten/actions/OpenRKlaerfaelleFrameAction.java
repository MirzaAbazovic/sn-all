/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2004 10:46:23
 */
package de.augustakom.hurrican.gui.stammdaten.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.stammdaten.RangierungsKlaerfaelleFrame;


/**
 * Action, um das Frame fuer die Rangierungsklaerfaelle zu oeffnen.
 *
 *
 */
public class OpenRKlaerfaelleFrameAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        RangierungsKlaerfaelleFrame frame = new RangierungsKlaerfaelleFrame();
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

    @Override
    protected boolean maximizeFrame() {
        return true;
    }

}


