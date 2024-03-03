/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnsprechpartnerMitEmailType;

/**
 *
 */
public class AnsprechpartnerMitEmailTypeBuilder extends PersonTypeBuilder {

    private String telefonnummer;
    private String emailadresse;

    @Override
    public AnsprechpartnerMitEmailType build() {
        AnsprechpartnerMitEmailType ansprechpartner = new AnsprechpartnerMitEmailType();
        ansprechpartner.setEmailadresse(emailadresse);
        ansprechpartner.setTelefonnummer(telefonnummer);
        return enrich(ansprechpartner);
    }

    public AnsprechpartnerMitEmailTypeBuilder withTelefonnummer(String value) {
        this.telefonnummer = value;
        return this;
    }

    public AnsprechpartnerMitEmailTypeBuilder withEmailadresse(String value) {
        this.emailadresse = value;
        return this;
    }

}
