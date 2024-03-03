/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.09.2005 10:59:59
 */
package de.augustakom.hurrican.gui.tools.dn.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.dn.DnLeistungKonfFrame;

/**
 *
 */
public class DnLeistungKonfAction extends AKAbstractOpenFrameAction {
    private String uniqueName = null;


    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        DnLeistungKonfFrame frame = new DnLeistungKonfFrame();
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
