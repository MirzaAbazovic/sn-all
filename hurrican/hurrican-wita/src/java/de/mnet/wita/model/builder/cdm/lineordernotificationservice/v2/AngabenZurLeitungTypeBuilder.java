/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsbezeichnungType;

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

    public AngabenZurLeitungTypeBuilder withLeitungsbezeichnung(LeitungsbezeichnungType value) {
        return (AngabenZurLeitungTypeBuilder) super.withLeitungsbezeichnung(value);
    }

}
