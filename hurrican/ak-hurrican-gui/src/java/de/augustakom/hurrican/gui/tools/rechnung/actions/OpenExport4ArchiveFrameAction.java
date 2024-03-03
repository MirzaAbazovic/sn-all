/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 13:31:56
 */
package de.augustakom.hurrican.gui.tools.rechnung.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.rechnung.Export4ArchiveFrame;


/**
 * Action, um die Rechnungen und EVNs zu archivieren.
 *
 *
 */
public class OpenExport4ArchiveFrameAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    protected AKJInternalFrame getFrameToOpen() {
        Export4ArchiveFrame frame = new Export4ArchiveFrame();
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


