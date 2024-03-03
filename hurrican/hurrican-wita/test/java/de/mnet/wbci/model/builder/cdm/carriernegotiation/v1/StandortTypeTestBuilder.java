/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.StandortType;

public class StandortTypeTestBuilder extends StandortTypeBuilder {

    public StandortType buildValid() {
        withOrt("München");
        withPostleitzahl("80992");
        withStrasse(new StrasseTypeBuilder()
                .withStrasse("Strasse")
                .withHausnummer("99")
                .withHausnummerZusatz("abc")
                .build());
        return super.build();
    }

}
