/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungspositionOhneAttributeType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class MeldungspositionOhneAttributeTypeBuilder implements LineOrderTypeBuilder<MeldungspositionOhneAttributeType> {

    private String meldungscode;
    private String meldungstext;

    @Override
    public MeldungspositionOhneAttributeType build() {
        return enrich(new MeldungspositionOhneAttributeType());
    }

    protected <MP extends MeldungspositionOhneAttributeType> MP enrich(MP meldungsposition) {
        meldungsposition.setMeldungscode(meldungscode);
        meldungsposition.setMeldungstext(meldungstext);
        return meldungsposition;
    }

    public MeldungspositionOhneAttributeTypeBuilder withMeldungscode(String meldungscode) {
        this.meldungscode = meldungscode;
        return this;
    }

    public MeldungspositionOhneAttributeTypeBuilder withMeldungstext(String meldungstext) {
        this.meldungstext = meldungstext;
        return this;
    }

}
