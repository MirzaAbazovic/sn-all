/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PersonType;
import de.mnet.wita.message.common.Firmenname;
import de.mnet.wita.message.common.Personenname;

public class StandortPersonConverter {

    public static PersonType person(Personenname input) {
        PersonType person = new PersonType();
        person.setAnrede(AnredeConverter.toWita(input.getAnrede(), false));
        person.setNachname(input.getNachname());
        person.setVorname(input.getVorname());
        return person;
    }

    public static FirmaType firma(Firmenname input) {
        FirmaType firma = new FirmaType();
        firma.setAnrede(AnredeConverter.toWita(input.getAnrede(), true));
        firma.setFirmenname(input.getErsterTeil());
        firma.setFirmennameZweiterTeil(input.getZweiterTeil());
        return firma;
    }


}
