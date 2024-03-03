/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeERLMKType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMKType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypERLMKTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERLMKType> {

    private MeldungsattributeERLMKType meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypERLMKType build() {
        MeldungstypERLMKType meldungstypERLMKType = new MeldungstypERLMKType();
        meldungstypERLMKType.setMeldungsattribute(meldungsattribute);
        MeldungstypERLMKType.Meldungspositionen meldungspositionen = new MeldungstypERLMKType.Meldungspositionen();
        if (positionen != null) {
            meldungspositionen.getPosition().addAll(positionen);
        }
        meldungstypERLMKType.setMeldungspositionen(meldungspositionen);
        return meldungstypERLMKType;
    }

    public MeldungstypERLMKTypeBuilder withMeldungsattribute(MeldungsattributeERLMKType meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypERLMKTypeBuilder withPosition(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypERLMKTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

}
