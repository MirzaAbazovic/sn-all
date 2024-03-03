/*
 * Copyright (c) 2007 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2008 15:14:24
 */
package de.augustakom.hurrican.service.reporting.impl.command;

import de.augustakom.hurrican.model.cc.Endstelle;


/**
 * Command-Klasse, um HVT-Daten der Endstelle A eines best. Auftrags zu sammeln und diese in einer HashMap zu
 * speichern.
 *
 *
 */
public class GetHvtEsADatenCommand extends AbstractHvtDatenCommand {

    @Override
    protected String getEndstelleTyp() {
        return Endstelle.ENDSTELLEN_TYP_A;
    }
}
