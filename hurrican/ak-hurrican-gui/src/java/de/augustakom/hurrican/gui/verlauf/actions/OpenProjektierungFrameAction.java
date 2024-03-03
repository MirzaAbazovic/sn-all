/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.01.2005 11:44:13
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.verlauf.ProjektierungFrame;


/**
 * Abstrakte Klasse, um ein Frame mit den Projektierungen einer best. Abteilung zu oeffnen. <br> Fuer welche Abteilung
 * die Projektierungen angezeigt werden, wird ueber die Methode <code>getAbteilung4Projektierung</code> entschieden.
 *
 *
 */
public abstract class OpenProjektierungFrameAction extends AKAbstractOpenFrameAction {

    protected String uniqueName = null;

    /**
     * Gibt die ID der Abteilung zurueck, fuer die die Projektierungen geoeffnet werden soll.
     *
     * @return
     */
    protected abstract Long getAbteilung4Projektierung();

    /**
     * Gibt an, ob die Projektierungs-Ruecklaeufer (true) oder die 'normalen' Projektierungen angezeigt werden sollen.
     *
     * @return
     */
    protected boolean openFrame4Rueck() {
        return false;
    }

    @Override
    protected AKJInternalFrame getFrameToOpen() {
        ProjektierungFrame frame = new ProjektierungFrame(getAbteilung4Projektierung(), openFrame4Rueck());
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


