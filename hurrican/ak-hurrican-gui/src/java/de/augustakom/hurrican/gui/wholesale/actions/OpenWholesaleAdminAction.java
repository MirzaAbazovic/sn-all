/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2011 09:34:22
 */

package de.augustakom.hurrican.gui.wholesale.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.wholesale.WholesaleAdminFrame;


/**
 * Action, um das Admin-Frame fuer die Verwaltung der Wholesale EKPs und A10NSPs.
 */
public class OpenWholesaleAdminAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        AKJInternalFrame frame = new WholesaleAdminFrame();
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
