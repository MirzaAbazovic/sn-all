/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.06.2007 15:48:09
 */
package de.augustakom.hurrican.gui.tools.tal.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.tal.UnfinishedCarrierBestellungFrame;


/**
 * Action, um das Frame fuer die noch nicht abgeschlossenen TAL-Bestellungen zu oeffnen.
 *
 *
 */
public class OpenUnfinishedCarrierBestellungAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        UnfinishedCarrierBestellungFrame frame = new UnfinishedCarrierBestellungFrame();
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


