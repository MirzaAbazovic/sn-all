/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004
 */
package de.augustakom.common.gui.swing;

import javax.swing.event.*;

import de.augustakom.common.gui.iface.AKMenuOwner;


/**
 * Implementierung eines InternalFrame-Listeners. <br> Dieser Listener traegt das InternalFrame aus einem
 * AbstractMDIMainFrame aus.
 *
 *
 */
public class MDIInternalFrameListener extends InternalFrameAdapter {

    /**
     * Meldet das InternalFrame vom MainFrame ab (sofern ein MainFrame gefunden wurde). <br>
     *
     * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosed(InternalFrameEvent e) {
        if (e.getInternalFrame() instanceof AKJInternalFrame &&
                ((AKJInternalFrame) e.getInternalFrame()).getMainFrame() != null) {
            AbstractMDIMainFrame mdiFrame = ((AKJInternalFrame) e.getInternalFrame()).getMainFrame();
            mdiFrame.unregisterFrame((AKJInternalFrame) e.getInternalFrame());
        }
    }

    /**
     * Informiert das InternalFrame darueber, dass es geschlossen werden soll. <br>
     *
     * @see javax.swing.event.InternalFrameListener#internalFrameClosing(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameClosing(InternalFrameEvent e) {
        if (e.getInternalFrame() instanceof AKJInternalFrame) {
            ((AKJInternalFrame) e.getInternalFrame()).frameWillClose();
        }
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameActivated(InternalFrameEvent e) {
        if (e.getSource() instanceof AKMenuOwner) {
            AKJMenu menu = ((AKMenuOwner) e.getSource()).getMenuOfOwner();
            if (menu != null) {
                AbstractMDIMainFrame mdiFrame = ((AKJInternalFrame) e.getInternalFrame()).getMainFrame();
                if (mdiFrame != null) {
                    mdiFrame.addAdditionalMenu(menu);
                }
            }
        }
    }

    /**
     * @see javax.swing.event.InternalFrameListener#internalFrameDeactivated(javax.swing.event.InternalFrameEvent)
     */
    public void internalFrameDeactivated(InternalFrameEvent e) {
        if (e.getSource() instanceof AKMenuOwner) {
            AbstractMDIMainFrame mdiFrame = ((AKJInternalFrame) e.getInternalFrame()).getMainFrame();
            if (mdiFrame != null) {
                mdiFrame.removeAdditionalMenu();
            }
        }
    }
}
