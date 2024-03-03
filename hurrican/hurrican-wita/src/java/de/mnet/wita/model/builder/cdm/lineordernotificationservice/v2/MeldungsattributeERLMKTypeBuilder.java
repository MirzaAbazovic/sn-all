/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeERLMKType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeERLMKTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeERLMKType> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;

    @Override
    public MeldungsattributeERLMKType build() {
        MeldungsattributeERLMKType meldungsattributeERLMKType = new MeldungsattributeERLMKType();
        meldungsattributeERLMKType.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattributeERLMKType.setVertragsnummer(vertragsnummer);
        meldungsattributeERLMKType.setKundennummer(kundennummer);
        meldungsattributeERLMKType.setKundennummerBesteller(kundennummerBesteller);
        return meldungsattributeERLMKType;
    }

    public MeldungsattributeERLMKTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeERLMKTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeERLMKTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeERLMKTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

}
