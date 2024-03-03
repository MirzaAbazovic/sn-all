/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2005 11:59:17
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Action, um die Projektierungen fuer ST Voice anzuzeigen.
 *
 *
 */
public class OpenProjektierungStVoice extends OpenProjektierungFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenProjektierungFrameAction#getAbteilung4Projektierung()
     */
    @Override
    protected Long getAbteilung4Projektierung() {
        return Abteilung.ST_VOICE;
    }


}


