/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerBaseType;

/**
 *
 */
public class AnsprechpartnerBaseTypeBuilder extends PersonTypeBuilder {

    private String telefonnummer;
    private String emailadresse;

    @Override
    public AnsprechpartnerBaseType build() {
        return enrich(new AnsprechpartnerBaseType());
    }

    protected <ABT extends AnsprechpartnerBaseType> ABT enrich(ABT ansprechpartner) {
        ansprechpartner.setEmailadresse(emailadresse);
        ansprechpartner.setTelefonnummer(telefonnummer);
        return super.enrich(ansprechpartner);
    }

    public AnsprechpartnerBaseTypeBuilder withTelefonnummer(String value) {
        this.telefonnummer = value;
        return this;
    }

    public AnsprechpartnerBaseTypeBuilder withEmailadresse(String value) {
        this.emailadresse = value;
        return this;
    }

}
