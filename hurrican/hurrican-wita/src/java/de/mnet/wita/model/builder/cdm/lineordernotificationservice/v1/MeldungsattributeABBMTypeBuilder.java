/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AbgebenderProviderMitAbgabedatumType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungsattributeABBMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABBMType.Meldungsattribute> {

    private String vertragsnummer;
    private String externeAuftragsnummer;
    private String kundennummer;
    private String kundennummerBesteller;
    private LocalDate wiedervorlagetermin;
    private AbgebenderProviderMitAbgabedatumType abgebenderProvider;

    @Override
    public MeldungstypABBMType.Meldungsattribute build() {
        return enrich(new MeldungstypABBMType.Meldungsattribute());
    }

    protected <MA extends MeldungsattributeABBMType> MA enrich(MA meldungsattributeABBMType) {
        meldungsattributeABBMType.setAbgebenderProvider(abgebenderProvider);
        meldungsattributeABBMType.setExterneAuftragsnummer(externeAuftragsnummer);
        meldungsattributeABBMType.setKundennummer(kundennummer);
        meldungsattributeABBMType.setKundennummerBesteller(kundennummerBesteller);
        meldungsattributeABBMType.setVertragsnummer(vertragsnummer);
        meldungsattributeABBMType.setWiedervorlagetermin(DateConverterUtils.toXmlGregorianCalendar(wiedervorlagetermin));
        return meldungsattributeABBMType;
    }

    public MeldungsattributeABBMTypeBuilder withVertragsnummer(String vertragsnummer) {
        this.vertragsnummer = vertragsnummer;
        return this;
    }

    public MeldungsattributeABBMTypeBuilder withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return this;
    }

    public MeldungsattributeABBMTypeBuilder withKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
        return this;
    }

    public MeldungsattributeABBMTypeBuilder withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return this;
    }

    public MeldungsattributeABBMTypeBuilder withWiedervorlagetermin(LocalDate wiedervorlagetermin) {
        this.wiedervorlagetermin = wiedervorlagetermin;
        return this;
    }

    public MeldungsattributeABBMTypeBuilder withAbgebenderProvider(AbgebenderProviderMitAbgabedatumType abgebenderProvider) {
        this.abgebenderProvider = abgebenderProvider;
        return this;
    }

}
