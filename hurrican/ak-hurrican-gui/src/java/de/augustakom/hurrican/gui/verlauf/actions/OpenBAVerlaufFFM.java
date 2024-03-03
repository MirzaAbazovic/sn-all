/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 03.09.14
 */
package de.augustakom.hurrican.gui.verlauf.actions;

import de.augustakom.hurrican.model.cc.Abteilung;

/**
 * Action, um die Bauauftraege fuer die Abteilung bzw. das System FFM anzuzeigen.
 */
public class OpenBAVerlaufFFM extends OpenVerlaufFrameAction {

    @Override
    protected Long getAbteilung4Verlauf() {
        return Abteilung.FFM;
    }

}
