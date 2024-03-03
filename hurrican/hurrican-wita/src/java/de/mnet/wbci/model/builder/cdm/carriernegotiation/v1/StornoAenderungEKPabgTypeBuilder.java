/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPabgType;

/**
 *
 */
public class StornoAenderungEKPabgTypeBuilder extends AbstractStornoAenderungTypeBuilder<StornoAenderungEKPabgType> {

    public StornoAenderungEKPabgTypeBuilder() {
        objectType = OBJECT_FACTORY.createStornoAenderungEKPabgType();
    }

    public StornoAenderungEKPabgTypeBuilder withStornoGrund(String stornoGrund) {
        objectType.setStornogrund(stornoGrund);
        return this;
    }
}
