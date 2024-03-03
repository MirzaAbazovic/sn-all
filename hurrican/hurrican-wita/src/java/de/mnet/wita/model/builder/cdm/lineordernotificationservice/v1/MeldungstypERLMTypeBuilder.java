/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypERLMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERLMType> {

    private MeldungstypERLMType.Meldungsattribute meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypERLMType build() {
        MeldungstypERLMType meldungstyp = new MeldungstypERLMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypERLMTypeBuilder withMeldungsattribute(MeldungstypERLMType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypERLMTypeBuilder withPositionen(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypERLMTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypERLMType.Meldungspositionen buildMeldunspositionen(List<MeldungspositionType> positionen) {
        MeldungstypERLMType.Meldungspositionen meldungspositionen = new MeldungstypERLMType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
