/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypAKMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungstypAKMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypAKMPVType> {

    private MeldungstypAKMPVType.Meldungsattribute meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypAKMPVType build() {
        MeldungstypAKMPVType meldungstyp = new MeldungstypAKMPVType();
        meldungstyp.setMeldungsattribute(meldungsattribute);
        if (positionen != null) {
            meldungstyp.setMeldungspositionen(buildMeldunspositionen(positionen));
        }
        return meldungstyp;
    }

    public MeldungstypAKMPVTypeBuilder withMeldungsattribute(MeldungstypAKMPVType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypAKMPVTypeBuilder withPositionen(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypAKMPVTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    private MeldungstypAKMPVType.Meldungspositionen buildMeldunspositionen(List<MeldungspositionType> positionen) {
        MeldungstypAKMPVType.Meldungspositionen meldungspositionen = new MeldungstypAKMPVType.Meldungspositionen();
        meldungspositionen.getPosition().addAll(positionen);
        return meldungspositionen;
    }

}
