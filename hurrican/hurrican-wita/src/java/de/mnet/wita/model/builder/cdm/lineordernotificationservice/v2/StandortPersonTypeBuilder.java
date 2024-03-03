
/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.FirmaType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PersonType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortPersonType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class StandortPersonTypeBuilder implements LineOrderNotificationTypeBuilder<StandortPersonType> {

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
