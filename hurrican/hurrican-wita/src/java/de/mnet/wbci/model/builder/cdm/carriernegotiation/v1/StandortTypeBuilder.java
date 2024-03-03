/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StrasseType;

public class StandortTypeBuilder extends V1AbstractBasicBuilder<StandortType> {

    public StandortTypeBuilder() {
        objectType = OBJECT_FACTORY.createStandortType();
    }

    public StandortTypeBuilder withOrt(String ort) {
        objectType.setOrt(ort);
        return this;
    }

    public StandortTypeBuilder withPostleitzahl(String plz) {
        objectType.setPostleitzahl(plz);
        return this;
    }

    public StandortTypeBuilder withStrasse(StrasseType strasse) {
        objectType.setStrasse(strasse);
        return this;
    }

}
