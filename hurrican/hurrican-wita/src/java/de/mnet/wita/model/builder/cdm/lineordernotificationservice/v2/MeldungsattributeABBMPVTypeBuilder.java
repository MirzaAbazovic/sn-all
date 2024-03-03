/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABBMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeABBMPVTypeBuilder
        implements LineOrderNotificationTypeBuilder<MeldungstypABBMPVType.Meldungsattribute> {

    private String vertragsnummer;
    private String kundennummer;
    private MeldungsattributeTALABBMPVType tal;

    @Override
    public MeldungstypABBMPVType.Meldungsattribute build() {
        MeldungstypABBMPVType.Meldungsattribute meldungsattribute = new MeldungstypABBMPVType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        return meldungsattribute;
    }

    public MeldungsattributeABBMPVTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeABBMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeABBMPVTypeBuilder withTal(MeldungsattributeTALABBMPVType tal) {
        this.tal = tal;
        return this;
    }

}
