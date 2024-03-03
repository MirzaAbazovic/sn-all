/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypQEBType;

/**
 *
 */
public class MeldungspositionQEBTypeBuilder extends MeldungspositionTypeBuilder {

    private MeldungspositionsattributeQEBType positionsattribute;

    @Override
    public MeldungstypQEBType.Meldungspositionen.Position build() {
        MeldungstypQEBType.Meldungspositionen.Position position = new MeldungstypQEBType.Meldungspositionen.Position();
        position.setPositionsattribute(positionsattribute);
        return enrich(position);
    }

    public MeldungspositionQEBTypeBuilder withPositionsattribute(MeldungspositionsattributeQEBType positionsattribute) {
        this.positionsattribute = positionsattribute;
        return this;
    }

    @Override
    public MeldungspositionQEBTypeBuilder withMeldungscode(String meldungscode) {
        return (MeldungspositionQEBTypeBuilder) super.withMeldungscode(meldungscode);
    }

    @Override
    public MeldungspositionQEBTypeBuilder withMeldungstext(String meldungstext) {
        return (MeldungspositionQEBTypeBuilder) super.withMeldungstext(meldungstext);
    }

}
