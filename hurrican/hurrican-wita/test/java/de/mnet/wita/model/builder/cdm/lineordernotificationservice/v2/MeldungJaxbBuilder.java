/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2011 15:59:43
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v2;

import java.util.*;
import com.google.common.collect.ImmutableList;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeType;


public abstract class MeldungJaxbBuilder<BUILDER extends MeldungJaxbBuilder<BUILDER>> {

    String externeAuftragsnummer = "0123452";
    String kundenNummer = "1236300000";
    String kundennummerBesteller = "N123456789";
    String vertragsNummer = "3456665734";
    final List<MeldungspositionType> meldungspositionen = new ArrayList<>();

    public BUILDER withExterneAuftragsnummer(String externeAuftragsnummer) {
        this.externeAuftragsnummer = externeAuftragsnummer;
        return uncheckedThis();
    }

    public BUILDER withKundenNummer(String kundenNummer) {
        this.kundenNummer = kundenNummer;
        return uncheckedThis();
    }

    public BUILDER withKundennummerBesteller(String kundennummerBesteller) {
        this.kundennummerBesteller = kundennummerBesteller;
        return uncheckedThis();
    }

    public BUILDER withVertragsNummer(String vertragsNummer) {
        this.vertragsNummer = vertragsNummer;
        return uncheckedThis();
    }

    public BUILDER addMeldungsposition(String meldungsCode, String meldungsText) {
        return addMeldungsposition(meldungsCode, meldungsText, null);
    }

    BUILDER addMeldungsposition(String meldungsCode, String meldungsText,
            MeldungspositionsattributeType meldungsposattribType) {

        MeldungspositionType meldungspositionType =
                new MeldungspositionTypeBuilder()
                        .withMeldungscode(meldungsCode)
                        .withMeldungstext(meldungsText)
                        .build();
        meldungspositionen.add(meldungspositionType);

        return uncheckedThis();
    }

    @SuppressWarnings("unchecked")
    private BUILDER uncheckedThis() {
        return (BUILDER) this;
    }

    public List<MeldungspositionType> getMeldungspositionen() {
        return ImmutableList.<MeldungspositionType>builder().addAll(meldungspositionen).build();
    }
}
