/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.VerbundleistungTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class VerbundleistungTermineTypeBuilder implements LineOrderTypeBuilder<VerbundleistungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public VerbundleistungTermineType build() {
        VerbundleistungTermineType termineType = new VerbundleistungTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public VerbundleistungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
