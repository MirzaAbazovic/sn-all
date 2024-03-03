/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;

/**
 *
 */
public class StornoAufhebungEKPabgTypeTestBuilder extends StornoAufhebungEKPabgTypeBuilder {

    public StornoAufhebungEKPabgType buildValid() {
        AbstractStornoTypeTestBuilder.enrichTestData(objectType);

        withStandort(new StandortTypeTestBuilder().buildValid());
        withPersonOrderFirma(new PersonOderFirmaTypeTestBuilder().buildValid());
        withStornoGrund("Sag ich nicht");

        return build();
    }
}
