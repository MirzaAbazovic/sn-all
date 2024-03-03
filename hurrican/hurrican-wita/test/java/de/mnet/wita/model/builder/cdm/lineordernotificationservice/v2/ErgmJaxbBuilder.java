/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.06.2011 18:08:26
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERGMType;

public class ErgmJaxbBuilder {

    private String externeAuftragsnummer = "0123456789";
    private String kundennummer = "1112223330";
    private String kundennummerBesteller = null;
    private String vertragsnummer = "9876543210";
    private String ergebnislink = "https://result.test";

    public ErgmJaxbBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public ErgmJaxbBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public ErgmJaxbBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public ErgmJaxbBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public ErgmJaxbBuilder withErgebnislink(String ergebnislink) {
        this.ergebnislink = ergebnislink;
        return this;
    }

    public MeldungstypERGMType build() {
        return new MeldungstypERGMTypeBuilder()
                .withMeldungsattribute(
                        new MeldungsattributeERGMTypeBuilder()
                                .withExterneAuftragsnummer(externeAuftragsnummer)
                                .withKundennummer(kundennummer)
                                .withKundennummerBesteller(kundennummerBesteller)
                                .withVertragsnummer(vertragsnummer)
                                .withErgebnislink(ergebnislink)
                                .build()
                )
                .build();
    }

}
