/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.OnkzAnschlussType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class OnkzAnschlussTypeBuilder implements LineOrderTypeBuilder<OnkzAnschlussType> {

    private String onkz;
    private String anschlussbereich;

    @Override
    public OnkzAnschlussType build() {
        OnkzAnschlussType onkzAnschlussType = new OnkzAnschlussType();
        onkzAnschlussType.setONKZ(onkz);
        onkzAnschlussType.setAnschlussbereich(anschlussbereich);
        return onkzAnschlussType;
    }

    public OnkzAnschlussTypeBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public OnkzAnschlussTypeBuilder withAnschlussbereich(String anschlussbereich) {
        this.anschlussbereich = anschlussbereich;
        return this;
    }

}
