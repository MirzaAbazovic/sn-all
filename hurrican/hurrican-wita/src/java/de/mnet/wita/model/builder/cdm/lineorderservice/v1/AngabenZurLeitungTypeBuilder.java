/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AngabenZurLeitungType;

/**
 *
 */
public class AngabenZurLeitungTypeBuilder extends BasisAngabenZurLeitungTypeBuilder {

    private String schleifenwiderstand;

    public AngabenZurLeitungType build() {
        return enrich(new AngabenZurLeitungType());
    }

    protected <AZLT extends AngabenZurLeitungType> AZLT enrich(AZLT angabenZurLeitungType) {
        angabenZurLeitungType.setSchleifenwiderstand(schleifenwiderstand);
        return super.enrich(angabenZurLeitungType);
    }

    public AngabenZurLeitungTypeBuilder withSchleifenwiderstand(String value) {
        this.schleifenwiderstand = value;
        return this;
    }

}
