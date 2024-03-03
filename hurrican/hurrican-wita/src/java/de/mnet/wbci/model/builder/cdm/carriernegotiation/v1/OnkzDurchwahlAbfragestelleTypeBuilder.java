/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.OnkzDurchwahlAbfragestelleType;

/**
 *
 */
public class OnkzDurchwahlAbfragestelleTypeBuilder extends V1AbstractBasicBuilder<OnkzDurchwahlAbfragestelleType> {

    public OnkzDurchwahlAbfragestelleTypeBuilder() {
        objectType = OBJECT_FACTORY.createOnkzDurchwahlAbfragestelleType();
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withOnkz(String onkz) {
        objectType.setONKZ(onkz);
        return this;
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withDurchwahlnummer(String durchwahlnummer) {
        objectType.setDurchwahlnummer(durchwahlnummer);
        return this;
    }

    public OnkzDurchwahlAbfragestelleTypeBuilder withAbfragestelle(String abfragestelle) {
        objectType.setAbfragestelle(abfragestelle);
        return this;
    }
}
