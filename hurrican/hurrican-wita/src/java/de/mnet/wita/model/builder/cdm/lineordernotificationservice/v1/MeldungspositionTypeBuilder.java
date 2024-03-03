/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionType;

/**
 *
 */
public class MeldungspositionTypeBuilder extends MeldungspositionOhneAttributeTypeBuilder {

    @Override
    public MeldungspositionType build() {
        return enrich(new MeldungspositionType());
    }

    @Override
    public MeldungspositionTypeBuilder withMeldungscode(String meldungscode) {
        return (MeldungspositionTypeBuilder) super.withMeldungscode(meldungscode);
    }

    @Override
    public MeldungspositionTypeBuilder withMeldungstext(String meldungstext) {
        return (MeldungspositionTypeBuilder) super.withMeldungstext(meldungstext);
    }

}
