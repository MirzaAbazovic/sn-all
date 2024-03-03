/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzRufNrType;

/**
 *
 */
public class OnkzRufNrTypeBuilder<ORNT extends OnkzRufNrType> extends V1AbstractBasicBuilder<ORNT> {

    public OnkzRufNrTypeBuilder() {
        objectType = (ORNT) OBJECT_FACTORY.createOnkzRufNrType();
    }

    public OnkzRufNrTypeBuilder<ORNT> withONKZ(String onkz) {
        objectType.setONKZ(onkz);
        return this;
    }

    public OnkzRufNrTypeBuilder<ORNT> withRufnummer(String rufnummer) {
        objectType.setRufnummer(rufnummer);
        return this;
    }

}
