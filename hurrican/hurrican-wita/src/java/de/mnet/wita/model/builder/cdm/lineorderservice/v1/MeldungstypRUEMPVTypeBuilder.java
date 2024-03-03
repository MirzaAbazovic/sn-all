/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import java.util.*;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungsattributeTALRUEMPVType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungstypRUEMPVType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class MeldungstypRUEMPVTypeBuilder implements LineOrderTypeBuilder<MeldungstypRUEMPVType> {

    private MeldungstypRUEMPVType.Meldungsattribute meldungsattribute;
    private List<MeldungspositionType> positionen;

    @Override
    public MeldungstypRUEMPVType build() {
        MeldungstypRUEMPVType meldungstypRUEMPVType = new MeldungstypRUEMPVType();
        MeldungstypRUEMPVType.Meldungspositionen meldungspositionen = new MeldungstypRUEMPVType.Meldungspositionen();
        if (positionen != null) {
            meldungspositionen.getPosition().addAll(positionen);
        }
        meldungstypRUEMPVType.setMeldungspositionen(meldungspositionen);
        return meldungstypRUEMPVType;
    }

    public MeldungstypRUEMPVTypeBuilder withMeldungsattribute(MeldungstypRUEMPVType.Meldungsattribute meldungsattribute) {
        this.meldungsattribute = meldungsattribute;
        return this;
    }

    public MeldungstypRUEMPVTypeBuilder withPosition(List<MeldungspositionType> positionen) {
        this.positionen = positionen;
        return this;
    }

    public MeldungstypRUEMPVTypeBuilder addPosition(MeldungspositionType position) {
        if (this.positionen == null) {
            this.positionen = new ArrayList<>();
        }
        this.positionen.add(position);
        return this;
    }

    public static class MeldungsattributeBuilder extends MeldungsattributeRUEMPVTypeBuilder {

        private MeldungsattributeTALRUEMPVType tal;

        @Override
        public MeldungstypRUEMPVType.Meldungsattribute build() {
            MeldungstypRUEMPVType.Meldungsattribute meldungsattribute = new MeldungstypRUEMPVType.Meldungsattribute();
            meldungsattribute.setTAL(tal);
            return enrich(meldungsattribute);
        }

        public MeldungsattributeBuilder withTal(MeldungsattributeTALRUEMPVType tal) {
            this.tal = tal;
            return this;
        }

    }

}
