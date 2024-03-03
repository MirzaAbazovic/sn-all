/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StrasseType;

public class StrasseTypeBuilder extends V1AbstractBasicBuilder<StrasseType> {

    public StrasseTypeBuilder() {
        objectType = OBJECT_FACTORY.createStrasseType();
    }

    public StrasseTypeBuilder withStrasse(String strasse) {
        objectType.setStrassenname(strasse);
        return this;
    }

    public StrasseTypeBuilder withHausnummer(String hnr) {
        objectType.setHausnummer(hnr);
        return this;
    }

    public StrasseTypeBuilder withHausnummerZusatz(String hnrZusatz) {
        objectType.setHausnummernZusatz(hnrZusatz);
        return this;
    }

}
