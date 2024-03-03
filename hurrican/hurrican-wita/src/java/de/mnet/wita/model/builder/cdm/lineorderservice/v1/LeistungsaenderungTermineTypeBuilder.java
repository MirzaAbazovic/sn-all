/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsaenderungTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeistungsaenderungTermineTypeBuilder implements LineOrderTypeBuilder<LeistungsaenderungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public LeistungsaenderungTermineType build() {
        LeistungsaenderungTermineType leistungsaenderungTermineType = new LeistungsaenderungTermineType();
        leistungsaenderungTermineType.setKundenwunschtermin(kundenwunschtermin);
        return leistungsaenderungTermineType;
    }

    public LeistungsaenderungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
