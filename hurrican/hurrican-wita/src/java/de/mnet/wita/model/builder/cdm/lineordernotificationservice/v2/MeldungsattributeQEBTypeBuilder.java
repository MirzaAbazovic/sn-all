/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeQEBType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeQEBTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeQEBType> {

    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;

    @Override
    public MeldungsattributeQEBType build() {
        MeldungsattributeQEBType meldungsattribute = new MeldungsattributeQEBType();
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        return meldungsattribute;
    }

    public MeldungsattributeQEBTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeQEBTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeQEBTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

}
