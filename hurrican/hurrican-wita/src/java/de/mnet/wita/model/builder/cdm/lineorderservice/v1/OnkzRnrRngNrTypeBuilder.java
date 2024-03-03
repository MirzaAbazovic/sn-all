/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzRnrRngNrType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class OnkzRnrRngNrTypeBuilder implements LineOrderTypeBuilder<OnkzRnrRngNrType> {

    private String onkz;
    private String rufnummer;

    @Override
    public OnkzRnrRngNrType build() {
        OnkzRnrRngNrType onkzRnrRngNrType = new OnkzRnrRngNrType();
        onkzRnrRngNrType.setONKZ(onkz);
        onkzRnrRngNrType.setRufnummer(rufnummer);
        return onkzRnrRngNrType;
    }

    public OnkzRnrRngNrTypeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public OnkzRnrRngNrTypeBuilder withRufnummer(String rufnummer) {
        this.rufnummer = rufnummer;
        return this;
    }

}
