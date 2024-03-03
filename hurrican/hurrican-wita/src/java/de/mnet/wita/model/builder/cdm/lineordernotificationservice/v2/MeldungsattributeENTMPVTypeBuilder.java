/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeENTMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeENTMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeENTMPVType> {

    private String vertragsnummer;
    private String kundennummer;
    private LocalDate entgeltTermin;

    @Override
    public MeldungsattributeENTMPVType build() {
        return enrich(new MeldungsattributeENTMPVType());
    }

    protected <MA extends MeldungsattributeENTMPVType> MA enrich(MA meldungsattributeENTMPVType) {
        meldungsattributeENTMPVType.setKundennummer(kundennummer);
        meldungsattributeENTMPVType.setVertragsnummer(vertragsnummer);
        meldungsattributeENTMPVType.setEntgelttermin(DateConverterUtils.toXmlGregorianCalendar(entgeltTermin));
        return meldungsattributeENTMPVType;
    }

    public MeldungsattributeENTMPVTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeENTMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeENTMPVTypeBuilder withEntgelttermin(LocalDate entgelttermin) {
        this.entgeltTermin = entgelttermin;
        return this;
    }

}
