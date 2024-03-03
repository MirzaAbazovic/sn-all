/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.07.2007 15:15:04
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;

import de.augustakom.hurrican.service.base.exceptions.HurricanServiceCommandException;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B022 TAL-Nutzungsänderung fuer die el.Tal-Schnittstelle DTAG
 * Version 3.0
 *
 */
public class GetDataB023Command extends AbstractTALDataCommand {

    @Override
    public Object execute() throws HurricanServiceCommandException {
        throw new HurricanServiceCommandException("ESAA not longer supported!");
    }

}


