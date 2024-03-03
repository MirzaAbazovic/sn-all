/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionOhneAttributeType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypABMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABMType> {

    private MeldungstypABMType.Meldungsattribute meldungsattribute;
    private List<MeldungstypABMType.Meldungspositionen.Position> positionen;

    @Override
    public MeldungstypABMType build() {
        MeldungstypABMType meldungstyp = new MeldungstypABMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypABMTypeBuilder withMeldungsattribute(MeldungstypABMType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypABMTypeBuilder addPosition(PositionBuilder positionBuilder) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(positionBuilder.build());
        return this;
    }

    public MeldungstypABMTypeBuilder withPositionen(List<MeldungstypABMType.Meldungspositionen.Position> positionen) {
        this.positionen = positionen;
        return this;
    }

    private MeldungstypABMType.Meldungspositionen buildMeldunspositionen(List<MeldungstypABMType.Meldungspositionen.Position> positionen) {
        MeldungstypABMType.Meldungspositionen meldungspositionen = new MeldungstypABMType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

    public static class PositionBuilder extends MeldungspositionTypeBuilder
            implements LineOrderNotificationTypeBuilder<MeldungspositionOhneAttributeType> {

        private MeldungspositionsattributeABMType positionsattribute;

        @Override
        public MeldungstypABMType.Meldungspositionen.Position build() {
            MeldungstypABMType.Meldungspositionen.Position position = new MeldungstypABMType.Meldungspositionen.Position();
            position.setPositionsattribute(positionsattribute);
            return position;
        }

        public PositionBuilder withPositionsattribute(MeldungspositionsattributeABMType positionsattribute) {
            this.positionsattribute = positionsattribute;
            return this;
        }

        @Override
        public PositionBuilder withMeldungscode(String meldungscode) {
            return (PositionBuilder) super.withMeldungscode(meldungscode);
        }

        @Override
        public PositionBuilder withMeldungstext(String meldungstext) {
            return (PositionBuilder) super.withMeldungstext(meldungstext);
        }

    }

}
