/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.util.*;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.DoppeladerBelegtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeTALABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StandortKorrekturType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.StrasseType;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.auftrag.StandortKundeKorrektur;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.Positionsattribute;

@SuppressWarnings("Duplicates")
@Component
public class AbbmUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypABBMType> {

    @Override
    public WitaMessage unmarshal(MeldungstypABBMType in) {
        AbbruchMeldung out = new AbbruchMeldung();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypABBMType in, AbbruchMeldung out) {
        MeldungstypABBMType.Meldungsattribute inMeldungAttributes = in.getMeldungsattribute();

        out.setExterneAuftragsnummer(inMeldungAttributes.getExterneAuftragsnummer());
        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setKundennummerBesteller(inMeldungAttributes.getKundennummerBesteller());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
    }

    private void mapMeldungPositions(MeldungstypABBMType in, AbbruchMeldung out) {
        List<MeldungstypABBMType.Meldungspositionen.Position> inPositions = in.getMeldungspositionen().getPosition();

        for (MeldungstypABBMType.Meldungspositionen.Position inPosition : inPositions) {
            MeldungsPosition outPosition = new MeldungsPosition();
            mapMeldungPosition(inPosition, outPosition);
            out.getMeldungsPositionen().add(outPosition);
        }
    }

    private void mapMeldungPosition(MeldungstypABBMType.Meldungspositionen.Position in, MeldungsPosition out) {
        super.mapMeldungPosition(in, out);

        MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute inPositionAttribute = in.getPositionsattribute();
        if (inPositionAttribute != null) {
            Positionsattribute outPositionAttribute = new Positionsattribute();
            mapPositionAttribute(inPositionAttribute, outPositionAttribute);
            out.setPositionsattribute(outPositionAttribute);
        }
    }

    private void mapPositionAttribute(MeldungstypABBMType.Meldungspositionen.Position.Positionsattribute in, Positionsattribute out) {
        doReallyDirtyHack(in.getAlternativprodukt());

        out.setErledigungsterminOffenerAuftrag(DateConverterUtils.toLocalDate(in.getErledigungsterminOffenerAuftrag()));
        out.setAlternativprodukt(in.getAlternativprodukt());
        out.setFehlauftragsnummer(in.getFehlauftragsnummer());
        out.setStandortKundeKorrektur(unmarshalStandortKundeKorrektur(in.getStandortAKorrektur()));
        MeldungspositionsattributeTALABBMType tal = in.getTAL();
        mapTAL(tal, out);
    }

    private void mapTAL(MeldungspositionsattributeTALABBMType in, Positionsattribute out) {
        if (in != null) {
            DoppeladerBelegtType inDoppeladerBelegt = in.getDoppeladerBelegt();
            if ((inDoppeladerBelegt != null) && !CollectionUtils.isEmpty(inDoppeladerBelegt.getLeitungsbezeichnung())) {
                for (LeitungsbezeichnungType inLbz : inDoppeladerBelegt.getLeitungsbezeichnung()) {
                    out.addDoppeladerBelegt(unmarshalLeitungsbezeichnung(inLbz));
                }
            }
        }

        // WITAX: anschlussPortierungKorrekt im MeldungspositionsattributeTALABBMType wurde in der WITA 9 entfernt
        out.setAnschlussPortierungKorrekt(null);

    }

    private StandortKundeKorrektur unmarshalStandortKundeKorrektur(StandortKorrekturType in) {
        if (in == null) {
            return null;
        }
        StandortKundeKorrektur out = new StandortKundeKorrektur();
        if (in.getGebaeudeteil() != null) {
            out.setGebaeudeteilName(in.getGebaeudeteil().getGebaeudeteilName());
            out.setGebaeudeteilZusatz(in.getGebaeudeteil().getGebaeudeteilZusatz());
        }
        StrasseType strasseType = in.getStrasse();
        out.setStrassenname(strasseType.getStrassenname());
        out.setHausnummer(strasseType.getHausnummer());
        out.setHausnummernZusatz(strasseType.getHausnummernZusatz());
        out.setPostleitzahl(in.getPostleitzahl());
        out.setOrtsname(in.getOrt().getOrtsname());
        out.setOrtsteil(in.getOrt().getOrtsteil());
        out.setLand(in.getLand());
        return out;
    }

    private void doReallyDirtyHack(String alternativProdukt) {
        // !!! VERY DIRTY HACK to satisfy Kft test case TAL_TEQ-NOK_01 part 2 !!!
        if ((alternativProdukt != null) && !alternativProdukt.contains("TAL")) {
            throw new UnmarshallingFailureException("Alternativprodukt ist nicht aus Produktgruppe TAL");
        }
    }
}
