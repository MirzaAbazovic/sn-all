/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypENTMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypENTMType> {

    private MeldungstypENTMType.Meldungsattribute meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypENTMType build() {
        MeldungstypENTMType meldungstyp = new MeldungstypENTMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypENTMTypeBuilder withMeldungsattribute(MeldungstypENTMType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypENTMTypeBuilder withPositionen(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypENTMTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypENTMType.Meldungspositionen buildMeldunspositionen(List<MeldungspositionType> positionen) {
        MeldungstypENTMType.Meldungspositionen meldungspositionen = new MeldungstypENTMType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
