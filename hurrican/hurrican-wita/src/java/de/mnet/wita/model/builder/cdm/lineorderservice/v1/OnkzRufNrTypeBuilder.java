/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRufNrType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class OnkzRufNrTypeBuilder implements LineOrderTypeBuilder<OnkzRufNrType> {

    private String onkz;
    private String rufnummer;

    @Override
    public OnkzRufNrType build() {
        OnkzRufNrType onkzRufNrType = new OnkzRufNrType();
        onkzRufNrType.setONKZ(onkz);
        onkzRufNrType.setRufnummer(rufnummer);
        return onkzRufNrType;
    }

    public OnkzRufNrTypeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public OnkzRufNrTypeBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

}
