/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypABBMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABBMType> {

    private MeldungstypABBMType.Meldungsattribute meldungsattribute;
    private List<MeldungstypABBMType.Meldungspositionen.Position> positionen;

    @Override
    public MeldungstypABBMType build() {
        MeldungstypABBMType meldungstyp = new MeldungstypABBMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypABBMTypeBuilder withMeldungsattribute(MeldungstypABBMType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypABBMTypeBuilder addPosition(MeldungstypABBMType.Meldungspositionen.Position position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypABBMType.Meldungspositionen buildMeldunspositionen(List<MeldungstypABBMType.Meldungspositionen.Position> positionen) {
        MeldungstypABBMType.Meldungspositionen meldungspositionen = new MeldungstypABBMType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
