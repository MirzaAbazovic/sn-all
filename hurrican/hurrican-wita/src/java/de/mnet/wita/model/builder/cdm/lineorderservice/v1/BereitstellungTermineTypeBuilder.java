/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.BereitstellungTermineType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class BereitstellungTermineTypeBuilder implements LineOrderTypeBuilder<BereitstellungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public BereitstellungTermineType build() {
        BereitstellungTermineType bereitstellungTermineType = new BereitstellungTermineType();
        bereitstellungTermineType.setKundenwunschtermin(kundenwunschtermin);
        return bereitstellungTermineType;
    }

    public BereitstellungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
