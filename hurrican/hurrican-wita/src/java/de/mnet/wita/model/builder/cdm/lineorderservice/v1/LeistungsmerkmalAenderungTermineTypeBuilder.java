/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.KundenwunschterminType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeistungsmerkmalAenderungTermineType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeistungsmerkmalAenderungTermineTypeBuilder implements LineOrderTypeBuilder<LeistungsmerkmalAenderungTermineType> {

    private KundenwunschterminType kundenwunschtermin;

    @Override
    public LeistungsmerkmalAenderungTermineType build() {
        LeistungsmerkmalAenderungTermineType termineType = new LeistungsmerkmalAenderungTermineType();
        termineType.setKundenwunschtermin(kundenwunschtermin);
        return termineType;
    }

    public LeistungsmerkmalAenderungTermineTypeBuilder withKundenwunschtermin(KundenwunschterminType kundenwunschtermin) {
        this.kundenwunschtermin = kundenwunschtermin;
        return this;
    }

}
