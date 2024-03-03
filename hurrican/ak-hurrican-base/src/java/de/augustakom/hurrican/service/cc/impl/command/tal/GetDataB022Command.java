/*
 * Copyright (c) 2007 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2007 13:39:32
 */
package de.augustakom.hurrican.service.cc.impl.command.tal;


@Deprecated
/**
 * Klasse ist zu entfernen, da nur fuer ESAA-Schnittstelle relevant, die mittlerweile deaktiviert ist!
 *
 * Rstellt das Segment B022 HVT-Karussel fuer die el.Tal-Schnittstelle DTAG
 * VErsion 3.0
 *
 */
public class GetDataB022Command extends AbstractTALDataCommand {

    /**
     * @see de.augustakom.hurrican.service.base.impl.AbstractHurricanServiceCommand#execute()
     */
    @Override
    public Object execute() throws Exception {
        // wird nicht benötigt. In MUC prinzipiell mit "N" belegt, ist aber kein "Muss"-segment
        return null;
    }

}


