/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.07.2004 15:01:05
 */
package de.augustakom.common.gui.swing;

import java.awt.event.*;


/**
 * Abstrakte Action, um ein Internal-Frame zu oeffnen und in einem Main-Frame darzustellen. <br>
 * <p/>
 * Liefert die Methode getUniqueName() einen Wert zurueck, ueberprueft die Action zuerst, ob bereits ein Frame mit
 * diesem Namen dargestellt wird. Ist dies der Fall, dann wird das Frame lediglich aktiviert. <br> Liefert die Methode
 * getUniqueName() <code>null</code> zurueck, wird eine neue Instanz des Frames angefordert und dann dargestellt.
 *
 *
 */
public abstract class AKAbstractOpenFrameAction extends AKAbstractAction {

    /**
     * Gibt das Frame zurueck, das geoeffnet und im MainFrame dargestellt werden soll.
     *
     * @return
     */
    protected abstract AKJInternalFrame getFrameToOpen();

    /**
     * Gibt das MainFrame zurueck, in dem das zu oeffnende InternalFrame dargestellt werden soll.
     *
     * @return
     */
    protected abstract AbstractMDIMainFrame getMainFrame();

    /**
     * Gibt einen eindeutigen Namen fuer das Frame zurueck, das dargestellt werden soll.
     *
     * @return
     */
    protected abstract String getUniqueName();

    /**
     * Gibt an, ob das InternalFrame maximiert werden soll. Default = false
     *
     * @return
     */
    protected boolean maximizeFrame() {
        return false;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        try {
            getMainFrame().setWaitCursor();

            if ((getUniqueName() != null) && getMainFrame().isFrameDisplayed(getUniqueName())) {
                getMainFrame().activateInternalFrame(getUniqueName());
                return;
            }

            getMainFrame().registerFrame(getFrameToOpen(), maximizeFrame());
        }
        finally {
            getMainFrame().setDefaultCursor();
        }
    }

}


