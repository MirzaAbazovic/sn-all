/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypABMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABMPVType> {

    private MeldungstypABMPVType.Meldungsattribute meldungsattribute;
    private List<MeldungstypABMPVType.Meldungspositionen.Position> positionen;

    @Override
    public MeldungstypABMPVType build() {
        MeldungstypABMPVType meldungstyp = new MeldungstypABMPVType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypABMPVTypeBuilder withMeldungsattribute(MeldungstypABMPVType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypABMPVTypeBuilder addPosition(PositionBuilder positionBuilder) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(positionBuilder.build());
        return this;
    }

    public MeldungstypABMPVTypeBuilder withPositionen(List<MeldungstypABMPVType.Meldungspositionen.Position> positionen) {
        this.positionen = positionen;
        return this;
    }

    private MeldungstypABMPVType.Meldungspositionen buildMeldunspositionen(List<MeldungstypABMPVType.Meldungspositionen.Position> positionen) {
        MeldungstypABMPVType.Meldungspositionen meldungspositionen = new MeldungstypABMPVType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

    public static class PositionBuilder extends MeldungspositionTypeBuilder {

        @Override
        public MeldungstypABMPVType.Meldungspositionen.Position build() {
            return enrich(new MeldungstypABMPVType.Meldungspositionen.Position());
        }

    }

}
