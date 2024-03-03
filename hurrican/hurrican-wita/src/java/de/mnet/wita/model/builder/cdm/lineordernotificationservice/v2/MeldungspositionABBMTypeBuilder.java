/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMType;

/**
 *
 */
public class MeldungspositionABBMTypeBuilder extends MeldungspositionTypeBuilder {

    private MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute positionsattribute;

    @Override
    public MeldungstypABBMType.Meldungspositionen.Position build() {
        MeldungstypABBMType.Meldungspositionen.Position position = new MeldungstypABBMType.Meldungspositionen.Position();
        position.setPositionsattribute(positionsattribute);
        return enrich(position);
    }

    public MeldungspositionABBMTypeBuilder withPositionsattribute(MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute positionsattribute) {
        this.positionsattribute = positionsattribute;
        return this;
    }

    @Override
    public MeldungspositionABBMTypeBuilder withMeldungscode(String meldungscode) {
        return (MeldungspositionABBMTypeBuilder) super.withMeldungscode(meldungscode);
    }

    @Override
    public MeldungspositionABBMTypeBuilder withMeldungstext(String meldungstext) {
        return (MeldungspositionABBMTypeBuilder) super.withMeldungstext(meldungstext);
    }

}
