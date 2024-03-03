/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProduktgruppenwechselTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProduktgruppenwechselTermineTypeBuilder implements LineOrderTypeBuilder<ProduktgruppenwechselTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public ProduktgruppenwechselTermineType build() {
        ProduktgruppenwechselTermineType termineType = new ProduktgruppenwechselTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public ProduktgruppenwechselTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
