/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortAType;

/**
 *
 */
public class StandortATypeBuilder extends StandortTypeBuilder {

    private String lageTAEONT;

    @Override
    public StandortAType build() {
        StandortAType standortAType = new StandortAType();
        standortAType.setLageTAEONT(lageTAEONT);
        return enrich(standortAType);
    }

    public StandortATypeBuilder withLageTAEONT(String lageTAEONT) {
        this.lageTAEONT = lageTAEONT;
        return this;
    }

}
