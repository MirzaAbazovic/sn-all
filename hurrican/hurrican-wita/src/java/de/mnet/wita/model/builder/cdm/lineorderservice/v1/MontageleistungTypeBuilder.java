/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AnsprechpartnerBaseType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MontageleistungType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class MontageleistungTypeBuilder implements LineOrderTypeBuilder<MontageleistungType> {

    private AnsprechpartnerBaseType ansprechpartner;
    private String montagehinweis;

    @Override
    public MontageleistungType build() {
        return enrich(new MontageleistungType());
    }

    protected <M extends MontageleistungType> M enrich(M montageleistung) {
        montageleistung.setAnsprechpartner(ansprechpartner);
        montageleistung.setMontagehinweis(montagehinweis);
        return montageleistung;
    }

    public MontageleistungTypeBuilder withAnsprechpartner(AnsprechpartnerBaseType ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
        return this;
    }

    public MontageleistungTypeBuilder withMontagehinweis(String montagehinweis) {
        this.montagehinweis = montagehinweis;
        return this;
    }

}
