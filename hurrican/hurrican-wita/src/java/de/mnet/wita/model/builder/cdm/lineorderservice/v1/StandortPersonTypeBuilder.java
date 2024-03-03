
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.StandortPersonType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class StandortPersonTypeBuilder implements LineOrderTypeBuilder<StandortPersonType> {

    private FirmaType firma;
    private PersonType person;

    @Override
    public StandortPersonType build() {
        StandortPersonType standortType = new StandortPersonType();
        standortType.setFirma(firma);
        standortType.setPerson(person);
        return standortType;
    }

    public StandortPersonTypeBuilder withFirma(FirmaType firma) {
        this.firma = firma;
        return this;
    }

    public StandortPersonTypeBuilder withPerson(PersonType person) {
        this.person = person;
        return this;
    }

}
