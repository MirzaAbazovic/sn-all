/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.2010 16:29:52
 */
package de.augustakom.hurrican.gui.hvt.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.hvt.PortStatisticsFrame;

/**
 * Action, um das Frame für den Portverbrauch anzuzeigen.
 */
public class OpenPortStatisticsFrameAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    protected AKJInternalFrame getFrameToOpen() {
        PortStatisticsFrame frame = new PortStatisticsFrame();
        uniqueName = frame.getUniqueName();
        return frame;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getMainFrame()
     */
    @Override
    protected AbstractMDIMainFrame getMainFrame() {
        return HurricanSystemRegistry.instance().getMainFrame();
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getUniqueName()
     */
    @Override
    protected String getUniqueName() {
        return uniqueName;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#maximizeFrame()
     */
    @Override
    protected boolean maximizeFrame() {
        return true;
    }
}
