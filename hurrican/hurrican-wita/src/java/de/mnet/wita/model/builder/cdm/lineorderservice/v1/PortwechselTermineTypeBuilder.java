/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PortwechselTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class PortwechselTermineTypeBuilder implements LineOrderTypeBuilder<PortwechselTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public PortwechselTermineType build() {
        PortwechselTermineType termineType = new PortwechselTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public PortwechselTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
