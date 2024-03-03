/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2005 13:20:07
 */
package de.augustakom.hurrican.gui.stammdaten.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.stammdaten.BillingLeistungKonfigFrame;


/**
 * Action, um die Konfigurations-GUI fuer die techn. Leistungen zu oeffnen.
 *
 *
 */
public class OpenTechLeistungKonfFrameAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        BillingLeistungKonfigFrame frame = new BillingLeistungKonfigFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getMainFrame()
     */
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getUniqueName()
     */
    protected String getUniqueName() {
        return uniqueName;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#maximizeFrame()
     */
    protected boolean maximizeFrame() {
        return true;
    }

}


