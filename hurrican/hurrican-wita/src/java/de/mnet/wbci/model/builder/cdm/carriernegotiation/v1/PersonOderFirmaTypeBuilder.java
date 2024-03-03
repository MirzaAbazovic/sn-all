/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.09.13
 */
package de.mnet.wbci.model.builder.cdm.carriernegotiation.v1;

import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonOderFirmaType;
import de.mnet.esb.cdm.supplierpartner.carriernegotiationservice.v1.PersonType;

public class PersonOderFirmaTypeBuilder extends V1AbstractBasicBuilder<PersonOderFirmaType> {

    public PersonOderFirmaTypeBuilder() {
        objectType = OBJECT_FACTORY.createPersonOderFirmaType();
    }

    public PersonOderFirmaTypeBuilder withPerson(PersonType person) {
        objectType.setPerson(person);
        return this;
    }

    public PersonOderFirmaTypeBuilder withFirma(FirmaType firma) {
        objectType.setFirma(firma);
        return this;
    }

}
