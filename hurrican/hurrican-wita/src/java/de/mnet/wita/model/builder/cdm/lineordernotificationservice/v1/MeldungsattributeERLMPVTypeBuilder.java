/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALERLMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERLMPVType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeERLMPVTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERLMPVType.Meldungsattribute> {

    private String vertragsnummer;
    private String kundennummer;
    private LocalDate erledigungstermin;
    private MeldungsattributeTALERLMPVType tal;

    @Override
    public MeldungstypERLMPVType.Meldungsattribute build() {
        MeldungstypERLMPVType.Meldungsattribute meldungsattribute = new MeldungstypERLMPVType.Meldungsattribute();
        meldungsattribute.setTAL(tal);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setErledigungstermin(DateConverterUtils.toXmlGregorianCalendar(erledigungstermin));
        return meldungsattribute;
    }

    public MeldungsattributeERLMPVTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeERLMPVTypeBuilder withErledigungstermin(LocalDate erledigungstermin) {
        this.erledigungstermin = erledigungstermin;
        return this;
    }

    public MeldungsattributeERLMPVTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeERLMPVTypeBuilder withTal(MeldungsattributeTALERLMPVType tal) {
        this.tal = tal;
        return this;
    }

}
