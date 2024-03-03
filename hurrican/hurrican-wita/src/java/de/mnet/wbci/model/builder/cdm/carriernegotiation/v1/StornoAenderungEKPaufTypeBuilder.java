/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StornoAenderungEKPaufType;

/**
 *
 */
public class StornoAenderungEKPaufTypeBuilder extends AbstractStornoAenderungTypeBuilder<StornoAenderungEKPaufType> {

    public StornoAenderungEKPaufTypeBuilder() {
        objectType = OBJECT_FACTORY.createStornoAenderungEKPaufType();
    }

}
