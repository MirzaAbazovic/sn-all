/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeMTAMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeMTAMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeMTAMType> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;

    @Override
    public MeldungsattributeMTAMType build() {
        MeldungsattributeMTAMType meldungsattribute = new MeldungsattributeMTAMType();
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        return meldungsattribute;
    }

    public MeldungsattributeMTAMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeMTAMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeMTAMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeMTAMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

}
