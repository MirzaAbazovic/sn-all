/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeQEBType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypQEBType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypQEBTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypQEBType> {

    private MeldungsattributeQEBType meldungsattribute;
    private List<MeldungstypQEBType.Meldungspositionen.Position> positionen;

    @Override
    public MeldungstypQEBType build() {
        MeldungstypQEBType meldungstyp = new MeldungstypQEBType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypQEBTypeBuilder withMeldungsattribute(MeldungsattributeQEBType meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypQEBTypeBuilder addPosition(MeldungstypQEBType.Meldungspositionen.Position position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypQEBType.Meldungspositionen buildMeldunspositionen(List<MeldungstypQEBType.Meldungspositionen.Position> positionen) {
        MeldungstypQEBType.Meldungspositionen meldungspositionen = new MeldungstypQEBType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
