/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.11.2008 08:41:56
 */
package de.augustakom.hurrican.gui.tools.rs.monitor.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.rs.monitor.RsMonitorConfigFrame;


/**
 * Action, um das Frame fuer die Ressourcen-Monitor Konfiguration zu oeffnen.
 *
 *
 */
public class OpenRsMConfigAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        RsMonitorConfigFrame frame = new RsMonitorConfigFrame();
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

}


