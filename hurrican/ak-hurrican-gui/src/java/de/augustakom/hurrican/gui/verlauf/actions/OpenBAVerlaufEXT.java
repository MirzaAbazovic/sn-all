/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.01.2005 15:00:58
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;


/**
 * Action, um die Bauauftraege fuer die Abteilung Extern anzuzeigen.
 *
 *
 */
public class OpenBAVerlaufEXT extends OpenVerlaufFrameAction {

    /**
     * @see de.augustakom.hurrican.gui.verlauf.actions.OpenVerlaufFrameAction#getAbteilung4Verlauf()
     */
    @Override
    protected Long getAbteilung4Verlauf() {
        return Abteilung.EXTERN;
    }

}


