/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.09.2014
 */
package de.mnet.wita.model.builder.cdm.lineordernotificationservice.v1;

import java.time.*;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionsattributeTALABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StandortKorrekturType;
import de.mnet.wita.model.builder.cdm.lineordernotificationservice.LineOrderNotificationTypeBuilder;

/**
 *
 */
public class MeldungspositionsattributeABBMTypeBuilder implements LineOrderNotificationTypeBuilder<MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute> {

    private LocalDate erledigungsterminOffenerAuftrag;
    private String alternativprodukt;
    private StandortKorrekturType standortAKorrektur;
    private String fehlauftragsnummer;
    private MeldungspositionsattributeTALABBMType tal;

    @Override
    public MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute build() {
        MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute positionsattribute =
                new MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute();
        positionsattribute.setTAL(tal);
        positionsattribute.setAlternativprodukt(alternativprodukt);
        positionsattribute.setErledigungsterminOffenerAuftrag(DateConverterUtils.toXmlGregorianCalendar(erledigungsterminOffenerAuftrag));
        positionsattribute.setFehlauftragsnummer(fehlauftragsnummer);
        positionsattribute.setStandortAKorrektur(standortAKorrektur);
        return positionsattribute;
    }

    public MeldungspositionsattributeABBMTypeBuilder withErledigungsterminOffenerAuftrag(LocalDate erledigungsterminOffenerAuftrag) {
        this.erledigungsterminOffenerAuftrag = erledigungsterminOffenerAuftrag;
        return this;
    }

    public MeldungspositionsattributeABBMTypeBuilder withAlternativprodukt(String alternativprodukt) {
        this.alternativprodukt = alternativprodukt;
        return this;
    }

    public MeldungspositionsattributeABBMTypeBuilder withStandortAKorrektur(StandortKorrekturType standortAKorrektur) {
        this.standortAKorrektur = standortAKorrektur;
        return this;
    }

    public MeldungspositionsattributeABBMTypeBuilder withFehlauftragsnummer(String fehlauftragsnummer) {
        this.fehlauftragsnummer = fehlauftragsnummer;
        return this;
    }

    public MeldungspositionsattributeABBMTypeBuilder withTal(MeldungspositionsattributeTALABBMType tal) {
        this.tal = tal;
        return this;
    }

}
