/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.OnkzErsteMSNRngNrType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class OnkzErsteMSNRngNrTypeBuilder implements LineOrderNotificationTypeBuilder<OnkzErsteMSNRngNrType> {

    private String onkz;
    private String rufnummer;

    @Override
    public OnkzErsteMSNRngNrType build() {
        OnkzErsteMSNRngNrType onkzErsteMSNRngNrType = new OnkzErsteMSNRngNrType();
        onkzErsteMSNRngNrType.setONKZ(onkz);
        onkzErsteMSNRngNrType.setRufnummer(rufnummer);
        return onkzErsteMSNRngNrType;
    }

    public OnkzErsteMSNRngNrTypeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public OnkzErsteMSNRngNrTypeBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

}
