/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2007 13:29:44
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Erstellt das Segment B016 Auftragsklammer fuer dei el. Talschnittstelle
 * Dtag Version 3.00
 *
 */
public class GetDataB016Command extends AbstractTALDataCommand {

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        // GetDataB016Command.execute- wird momentan von MUC nicht benutzt
        return null;
    }

}


