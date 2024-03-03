/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonType;

public class PersonOderFirmaTypeTestBuilder extends PersonOderFirmaTypeBuilder {

    public PersonOderFirmaType buildValid() {
        PersonType person = new PersonType();
        person.setAnrede("1");
        person.setVorname("Max");
        person.setNachname("Mustermann");
        withPerson(person);
        return super.build();
    }
}
