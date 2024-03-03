/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.08.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.AdresseAbweichendType;

/**
 *
 */
public class AdresseAbweichendTypeBuilder extends V1AbstractBasicBuilder<AdresseAbweichendType> {

    public AdresseAbweichendTypeBuilder() {
        objectType = OBJECT_FACTORY.createAdresseAbweichendType();
    }

    public AdresseAbweichendTypeBuilder withHausnummer(String hausnummer) {
        objectType.setHausnummer(hausnummer);
        return this;
    }

    public AdresseAbweichendTypeBuilder withOrt(String ort) {
        objectType.setOrt(ort);
        return this;
    }

    public AdresseAbweichendTypeBuilder withPostleitzahl(String plz) {
        objectType.setPostleitzahl(plz);
        return this;
    }

    public AdresseAbweichendTypeBuilder withStrassenname(String strassenname) {
        objectType.setStrassenname(strassenname);
        return this;
    }
}
