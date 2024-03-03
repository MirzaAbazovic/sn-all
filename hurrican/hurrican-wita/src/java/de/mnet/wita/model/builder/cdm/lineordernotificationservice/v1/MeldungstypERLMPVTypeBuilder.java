/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypERLMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERLMPVType> {

    private MeldungstypERLMPVType.Meldungsattribute meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypERLMPVType build() {
        MeldungstypERLMPVType meldungstyp = new MeldungstypERLMPVType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypERLMPVTypeBuilder withMeldungsattribute(MeldungstypERLMPVType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypERLMPVTypeBuilder withPositionen(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypERLMPVTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypERLMPVType.Meldungspositionen buildMeldunspositionen(List<MeldungspositionType> positionen) {
        MeldungstypERLMPVType.Meldungspositionen meldungspositionen = new MeldungstypERLMPVType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
