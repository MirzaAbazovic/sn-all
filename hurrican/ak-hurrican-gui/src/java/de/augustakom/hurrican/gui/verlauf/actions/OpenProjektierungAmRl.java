/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 14:25:18
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Action, um die Projektierungs-Ruecklaeufer fuer AM anzuzeigen.
 *
 *
 */
public class OpenProjektierungAmRl extends OpenProjektierungFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenProjektierungFrameAction#getAbteilung4Projektierung()
     */
    @Override
    protected Long getAbteilung4Projektierung() {
        return Abteilung.AM;
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenProjektierungFrameAction#openFrame4Rueck()
     */
    @Override
    protected boolean openFrame4Rueck() {
        return true;
    }
}


