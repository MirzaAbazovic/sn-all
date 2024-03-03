/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2005 08:31:32
 */
package de.augustakom.hurrican.gui.auftrag.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.auftrag.SperreFieldServiceFrame;

/**
 * Action oeffnet ein Frame mit allen aktuellen Sperren der Abteilung FieldService. <br> Darueber koennen die Sperren
 * bearbeitet werden.
 *
 *
 */
public class SperreFieldServiceAction extends AKAbstractOpenFrameAction {

    private String uniqueName = null;

    // FIXME (HUR-15 / HUR-31) delete me - wenn manuell auszufuehrende Sperren zukuenftig ueber Bauauftraege laufen

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    protected AKJInternalFrame getFrameToOpen() {
        SperreFieldServiceFrame frame = new SperreFieldServiceFrame();
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


}


