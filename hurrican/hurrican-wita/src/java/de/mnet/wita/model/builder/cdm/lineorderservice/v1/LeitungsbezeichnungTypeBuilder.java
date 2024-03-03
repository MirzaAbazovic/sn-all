/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.LeitungsbezeichnungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class LeitungsbezeichnungTypeBuilder implements LineOrderTypeBuilder<LeitungsbezeichnungType> {

    private String leitungsschluesselzahl;
    private String onkzA;
    private String onkzB;
    private String ordnungsnummer;

    @Override
    public LeitungsbezeichnungType build() {
        LeitungsbezeichnungType leitungsbezeichnungType = new LeitungsbezeichnungType();
        leitungsbezeichnungType.setLeitungsschluesselzahl(leitungsschluesselzahl);
        leitungsbezeichnungType.setOnkzA(onkzA);
        leitungsbezeichnungType.setOnkzB(onkzB);
        leitungsbezeichnungType.setOrdnungsnummer(ordnungsnummer);
        return leitungsbezeichnungType;
    }

    public LeitungsbezeichnungTypeBuilder withLeitungsschluesselzahl(String leitungsschluesselzahl) {
        this.leitungsschluesselzahl = leitungsschluesselzahl;
        return this;
    }

    public LeitungsbezeichnungTypeBuilder withOnkzA(String onkzA) {
        this.onkzA = onkzA;
        return this;
    }

    public LeitungsbezeichnungTypeBuilder withOnkzB(String onkzB) {
        this.onkzB = onkzB;
        return this;
    }

    public LeitungsbezeichnungTypeBuilder withOrdnungsnummer(String ordnungsnummer) {
        this.ordnungsnummer = ordnungsnummer;
        return this;
    }

}
