/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 09:50:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import de.augustakom.hurrican.model.cc.Endstelle;


/**
 * Command-Klasse, um HVT-Daten der Endstelle B eines best. Auftrags zu sammeln und diese in einer HashMap zu
 * speichern.
 *
 *
 */
public class GetHvtEsBDatenCommand extends AbstractHvtDatenCommand {

    @Override
    protected String getEndstelleTyp() {
        return Endstelle.ENDSTELLEN_TYP_B;
    }
}
