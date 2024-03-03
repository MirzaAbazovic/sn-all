/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.ProviderwechselTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class ProviderwechselTermineTypeBuilder implements LineOrderTypeBuilder<ProviderwechselTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public ProviderwechselTermineType build() {
        ProviderwechselTermineType termineType = new ProviderwechselTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public ProviderwechselTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
