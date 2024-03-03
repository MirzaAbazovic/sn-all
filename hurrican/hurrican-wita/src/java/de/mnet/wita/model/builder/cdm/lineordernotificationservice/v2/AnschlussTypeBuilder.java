/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class AnschlussTypeBuilder implements LineOrderNotificationTypeBuilder<AnschlussType> {

    private String onkz;
    private String rufnummer;

    @Override
    public AnschlussType build() {
        AnschlussType anschlussType = new AnschlussType();
        anschlussType.setONKZ(onkz);
        anschlussType.setRufnummer(rufnummer);
        return anschlussType;
    }

    public AnschlussTypeBuilder withONKZ(String value) {
        this.onkz = value;
        return this;
    }

    public AnschlussTypeBuilder withRufnummer(String value) {
        this.rufnummer = value;
        return this;
    }

}
