/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.03.2012 09:31:17
 */
package de.augustakom.hurrican.gui.tools.tal.wita.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.tal.wita.WitaClearingToolsFrame;

public class WitaClearingToolsAction extends AKAbstractOpenFrameAction {

    private static final long serialVersionUID = 4832151311531099192L;

    private String uniqueName = null;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        WitaClearingToolsFrame frame = new WitaClearingToolsFrame();
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
