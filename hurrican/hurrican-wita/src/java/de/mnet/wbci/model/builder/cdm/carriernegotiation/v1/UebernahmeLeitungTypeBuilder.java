/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.UebernahmeLeitungType;

/**
 *
 */
public class UebernahmeLeitungTypeBuilder extends V1AbstractBasicBuilder<UebernahmeLeitungType> {

    public UebernahmeLeitungTypeBuilder() {
        objectType = OBJECT_FACTORY.createUebernahmeLeitungType();
    }

    public UebernahmeLeitungTypeBuilder withLineID(String lineId) {
        objectType.setLineID(lineId);
        return this;
    }

    public UebernahmeLeitungTypeBuilder withVertragsnummer(String vertragsnummer) {
        objectType.setVertragsnummer(vertragsnummer);
        return this;
    }

}
