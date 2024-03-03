/**
 * Copyright (c) 2010 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.06.2010
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;

/**
 * Action, um die Bauauftraege fuer die M-Queue anzuzeigen.
 */
public class OpenBAVerlaufMQueue extends OpenVerlaufFrameAction {
    @Override
    protected Long getAbteilung4Verlauf() {
        return Abteilung.MQUEUE;
    }
}
