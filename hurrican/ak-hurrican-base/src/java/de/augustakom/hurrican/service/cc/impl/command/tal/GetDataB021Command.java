/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2007 13:37:11
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B021 fuer die el.TAL-Schnittstelle DTAG
 * Version 3.0
 *
 */
public class GetDataB021Command extends AbstractTALDataCommand {

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        // Wird zur Zeit in MUC nicht benutzt
        return null;
    }

}


