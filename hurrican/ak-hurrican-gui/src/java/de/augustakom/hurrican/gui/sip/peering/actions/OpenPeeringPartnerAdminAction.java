/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.02.2015
 */
package de.augustakom.hurrican.gui.sip.peering.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.sip.peering.PeeringPartnerAdminFrame;

public class OpenPeeringPartnerAdminAction extends AKAbstractOpenFrameAction {

    private static final long serialVersionUID = -2253149952570194161L;
    private String uniqueName;

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        final PeeringPartnerAdminFrame peeringPartnerAdminFrame = new PeeringPartnerAdminFrame();
        uniqueName = peeringPartnerAdminFrame.getUniqueName();
        return peeringPartnerAdminFrame;
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
