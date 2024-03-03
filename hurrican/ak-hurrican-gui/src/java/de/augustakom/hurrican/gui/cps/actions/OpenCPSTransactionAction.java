/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 *
 */
package de.augustakom.hurrican.gui.cps.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.cps.CPSTxFrame;

/**
 * Action, um die CPS-Transaktion anzuzeigen.
 *
 *
 */
public class OpenCPSTransactionAction extends AKAbstractOpenFrameAction {

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    protected AKJInternalFrame getFrameToOpen() {
        CPSTxFrame cpsTransactionFrame = new CPSTxFrame();
        return cpsTransactionFrame;
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
        return getClass().getName();
    }
}
