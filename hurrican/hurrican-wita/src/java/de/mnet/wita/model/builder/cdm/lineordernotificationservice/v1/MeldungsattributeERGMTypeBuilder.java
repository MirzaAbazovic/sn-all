/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERGMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeERGMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypERGMType.Meldungsattribute> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;
    private String ergebnislink;

    @Override
    public MeldungstypERGMType.Meldungsattribute build() {
        MeldungstypERGMType.Meldungsattribute meldungsattribute = new MeldungstypERGMType.Meldungsattribute();
        meldungsattribute.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattribute.setVertragsnummer(vertragsnummer);
        meldungsattribute.setKundennummer(kundennummer);
        meldungsattribute.setKundennummerBesteller(kundennummerBesteller);
        meldungsattribute.setErgebnislink(ergebnislink);
        return meldungsattribute;
    }

    public MeldungsattributeERGMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeERGMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeERGMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeERGMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public MeldungsattributeERGMTypeBuilder withErgebnislink(String ergebnislink) {
        this.ergebnislink = ergebnislink;
        return this;
    }

}
