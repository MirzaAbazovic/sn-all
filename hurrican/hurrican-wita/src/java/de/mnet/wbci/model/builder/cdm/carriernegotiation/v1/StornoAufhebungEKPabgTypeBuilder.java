/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPabgType;

/**
 *
 */
public class StornoAufhebungEKPabgTypeBuilder extends AbstractStornoTypeBuilder<StornoAufhebungEKPabgType> {

    public StornoAufhebungEKPabgTypeBuilder() {
        objectType = OBJECT_FACTORY.createStornoAufhebungEKPabgType();
    }

    public StornoAufhebungEKPabgTypeBuilder withPersonOrderFirma(PersonOderFirmaType personOderFirma) {
        objectType.setName(personOderFirma);
        return this;
    }

    public StornoAufhebungEKPabgTypeBuilder withStandort(StandortType standort) {
        objectType.setStandort(standort);
        return this;
    }

    public StornoAufhebungEKPabgTypeBuilder withStornoGrund(String stornoGrund) {
        objectType.setStornogrund(stornoGrund);
        return this;
    }
}
