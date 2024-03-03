/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTAMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeTAMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungsattributeTAMType> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;

    @Override
    public MeldungsattributeTAMType build() {
        MeldungsattributeTAMType meldungsattribute = new MeldungsattributeTAMType();
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        return meldungsattribute;
    }

    public MeldungsattributeTAMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeTAMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeTAMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeTAMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

}
