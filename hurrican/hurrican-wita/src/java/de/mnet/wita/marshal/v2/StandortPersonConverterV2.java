/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.PersonType;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Personenname;

public class StandortPersonConverterV2 {

    public static PersonType person(Personenname input) {
        PersonType person = new PersonType();
        person.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), false));
        person.setNachname(input.getNachname());
        person.setVorname(input.getVorname());
        return person;
    }

    public static FirmaType firma(Firmenname input) {
        FirmaType firma = new FirmaType();
        firma.setAnrede(AnredeConverterV2.toWita(input.getAnrede(), true));
        firma.setFirmenname(input.getErsterTeil());
        firma.setFirmennameZweiterTeil(input.getZweiterTeil());
        return firma;
    }


}
