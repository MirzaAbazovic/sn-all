/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.01.2005 09:55:14
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Action, um die Bauauftrags-Ruecklaeufer fuer Netzplanung anzuzeigen.
 *
 *
 */
public class OpenBAVerlaufNPRL extends OpenVerlaufFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenVerlaufFrameAction#getAbteilung4Verlauf()
     */
    @Override
    protected Long getAbteilung4Verlauf() {
        return Abteilung.NP;
    }

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenVerlaufFrameAction#openFrame4BaRueck()
     */
    @Override
    protected boolean openFrame4BaRueck() {
        return true;
    }
}


