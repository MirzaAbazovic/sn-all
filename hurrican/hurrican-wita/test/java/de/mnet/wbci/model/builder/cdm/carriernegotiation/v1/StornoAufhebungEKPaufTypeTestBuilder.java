/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAufhebungEKPaufType;

/**
 *
 */
public class StornoAufhebungEKPaufTypeTestBuilder extends StornoAufhebungEKPaufTypeBuilder {

    public StornoAufhebungEKPaufType buildValid() {
        AbstractStornoTypeTestBuilder.enrichTestData(objectType);
        return build();
    }
}
