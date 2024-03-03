/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypTAMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypTAMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypTAMType> {

    private MeldungsattributeTAMType meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypTAMType build() {
        MeldungstypTAMType meldungstyp = new MeldungstypTAMType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypTAMTypeBuilder withMeldungsattribute(MeldungsattributeTAMType meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypTAMTypeBuilder withPositionen(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypTAMTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypTAMType.Meldungspositionen buildMeldunspositionen(List<MeldungspositionType> positionen) {
        MeldungstypTAMType.Meldungspositionen meldungspositionen = new MeldungstypTAMType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
