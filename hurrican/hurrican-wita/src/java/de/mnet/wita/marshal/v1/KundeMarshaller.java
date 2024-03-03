/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundeType;
import de.mnet.wita.message.auftrag.Kunde;

public class KundeMarshaller extends AbstractBaseMarshaller {

    public KundeType generate(Kunde input) {
        KundeType kunde = OBJECT_FACTORY.createKundeType();
        kunde.setKundennummer(input.getKundennummer());
        kunde.setLeistungsnummer(input.getLeistungsnummer());
        return kunde;
    }

}
