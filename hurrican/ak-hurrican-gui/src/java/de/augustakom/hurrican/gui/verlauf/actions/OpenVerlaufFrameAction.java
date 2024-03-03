/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 09:44:13
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.common.gui.swing.AKAbstractOpenFrameAction;
import de.augustakom.common.gui.swing.AKJInternalFrame;
import de.augustakom.common.gui.swing.AbstractMDIMainFrame;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.verlauf.BauauftragFrame;


/**
 * Abstrakte Klasse, um ein Frame mit der Verlaufs-Uebersicht einer best. Abteilung zu oeffnen. <br> Fuer welche
 * Abteilung der Verlauf geoeffnet wird, wird ueber die Methode <code>getAbteilung4Verlauf</code> entschieden.
 *
 *
 */
public abstract class OpenVerlaufFrameAction extends AKAbstractOpenFrameAction {

    String uniqueName = null;

    /**
     * Gibt die ID der Abteilung zurueck, fuer die der Verlauf geoeffnet werden soll.
     *
     * @return
     */
    protected abstract Long getAbteilung4Verlauf();

    /**
     * Gibt an, ob die Bauauftrags-Ruecklaeufer (true) oder die 'normalen' Bauauftraege angezeigt werden sollen.
     *
     * @return
     */
    protected boolean openFrame4BaRueck() {
        return false;
    }

    /**
     * @see de.augustakom.common.gui.swing.AKAbstractOpenFrameAction#getFrameToOpen()
     */
    @Override
    protected AKJInternalFrame getFrameToOpen() {
        BauauftragFrame frame = new BauauftragFrame(getAbteilung4Verlauf(), openFrame4BaRueck());
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


