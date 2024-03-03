/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2005 11:17:03
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;

/**
 * Action, um die Projektierungen fuer ST Online anzuzeigen.
 *
 *
 */
public class OpenProjektierungStOnline extends OpenProjektierungFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenProjektierungFrameAction#getAbteilung4Projektierung()
     */
    @Override
    protected Long getAbteilung4Projektierung() {
        return Abteilung.ST_ONLINE;
    }


}


