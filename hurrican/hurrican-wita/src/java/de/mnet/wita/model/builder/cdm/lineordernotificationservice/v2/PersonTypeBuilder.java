/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.PersonType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class PersonTypeBuilder implements LineOrderNotificationTypeBuilder<PersonType> {

    private String anrede;
    private String vorname;
    private String nachname;

    @Override
    public PersonType build() {
        return enrich(new PersonType());
    }

    protected <PT extends PersonType> PT enrich(PT personType) {
        personType.setAnrede(anrede);
        personType.setVorname(vorname);
        personType.setNachname(nachname);
        return personType;
    }

    public PersonTypeBuilder withAnrede(String value) {
        this.anrede = value;
        return this;
    }

    public PersonTypeBuilder withVorname(String value) {
        this.vorname = value;
        return this;
    }

    public PersonTypeBuilder withNachname(String value) {
        this.nachname = value;
        return this;
    }

}
