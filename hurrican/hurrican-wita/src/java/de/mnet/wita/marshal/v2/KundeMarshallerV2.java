/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.marshal.v2;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v2.KundeType;
import de.mnet.wita.message.auftrag.Kunde;

public class KundeMarshallerV2 extends AbstractBaseMarshallerV2 {

    public KundeType generate(Kunde input) {
        KundeType kunde = OBJECT_FACTORY.createKundeType();
        kunde.setKundennummer(input.getKundennummer());
        kunde.setLeistungsnummer(input.getLeistungsnummer());
        return kunde;
    }

}
