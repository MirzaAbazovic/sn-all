/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.KundeType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class KundeTypeBuilder implements LineOrderNotificationTypeBuilder<KundeType> {

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
