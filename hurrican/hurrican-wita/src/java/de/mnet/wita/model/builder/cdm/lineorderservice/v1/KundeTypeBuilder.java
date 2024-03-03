/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundeType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class KundeTypeBuilder implements LineOrderTypeBuilder<KundeType> {

    private String kundennummer;
    private String leistungsnummer;

    @Override
    public KundeType build() {
        KundeType kundeType = new KundeType();
        kundeType.setKundennummer(kundennummer);
        kundeType.setLeistungsnummer(leistungsnummer);
        return kundeType;
    }

    public KundeTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public KundeTypeBuilder withLeistungsnummer(String leistungsnummer) {
        this.leistungsnummer = leistungsnummer;
        return this;
    }

}
