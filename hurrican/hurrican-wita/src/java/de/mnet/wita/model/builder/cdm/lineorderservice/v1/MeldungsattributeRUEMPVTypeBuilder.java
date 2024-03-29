/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineorderservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.AbgebenderProviderType;
import de.mnet.esb.cdm.supplierpartner.lineorderservice.v1.MeldungsattributeRUEMPVType;
import de.mnet.wita.model.builder.cdm.lineorderservice.LineOrderTypeBuilder;

/**
 *
 */
public class MeldungsattributeRUEMPVTypeBuilder implements LineOrderTypeBuilder<MeldungsattributeRUEMPVType> {

    private String vertragsnummer;
    private String kundennummer;
    private AbgebenderProviderType abgebenderProviderType;

    @Override
    public MeldungsattributeRUEMPVType build() {
        return enrich(new MeldungsattributeRUEMPVType());
    }

    protected <T extends MeldungsattributeRUEMPVType> T enrich(T meldungsattributeRUEMPVType) {
        meldungsattributeRUEMPVType.setVertragsnummer(vertragsnummer);
        meldungsattributeRUEMPVType.setKundennummer(kundennummer);
        meldungsattributeRUEMPVType.setAbgebenderProvider(abgebenderProviderType);
        return meldungsattributeRUEMPVType;
    }

    public MeldungsattributeRUEMPVTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeRUEMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeRUEMPVTypeBuilder withAbgebenderProviderType(AbgebenderProviderType abgebenderProviderType) {
        this.abgebenderProviderType = abgebenderProviderType;
        return this;
    }

}
