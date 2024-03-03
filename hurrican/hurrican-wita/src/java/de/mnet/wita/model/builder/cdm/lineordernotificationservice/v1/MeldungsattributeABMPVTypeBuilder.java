/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AufnehmenderProviderABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALABMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeABMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABMPVType.Meldungsattribute> {

    private String vertragsnummer;
    private String kundennummer;
    private AufnehmenderProviderABMType aufnehmenderProvider;
    private MeldungsattributeTALABMPVType tal;

    @Override
    public MeldungstypABMPVType.Meldungsattribute build() {
        MeldungstypABMPVType.Meldungsattribute meldungsattribute = new MeldungstypABMPVType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setAufnehmenderProvider(aufnehmenderProvider);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        return meldungsattribute;
    }

    public MeldungsattributeABMPVTypeBuilder withVertragsnummer(String externeAuftragsnummer) {
        this.vertragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeABMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeABMPVTypeBuilder withAufnehmderProvider(AufnehmenderProviderABMType aufnehmenderProvider) {
        this.aufnehmenderProvider = aufnehmenderProvider;
        return this;
    }

    public MeldungsattributeABMPVTypeBuilder withTal(MeldungsattributeTALABMPVType tal) {
        this.tal = tal;
        return this;
    }

}
