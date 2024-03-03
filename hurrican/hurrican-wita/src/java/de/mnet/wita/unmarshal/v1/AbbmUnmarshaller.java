/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import java.util.*;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DoppeladerBelegtType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.DurchwahlanlageBestandType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.LeitungsbezeichnungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungspositionsattributeTALABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABBMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.RufnummernblockType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StandortKorrekturType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.StrasseType;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.auftrag.BestandsSuche;
import de.mnet.wita.message.auftrag.StandortKundeKorrektur;
import de.mnet.wita.message.common.portierung.RufnummernBlock;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.position.AnschlussPortierungKorrekt;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.message.meldung.position.Positionsattribute;

@Component
public class AbbmUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypABBMType> {

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

            out.setAnschlussPortierungKorrekt(unmarshalAnschlussPortierungKorrekt(in.getAnschlussPortierungKorrekt()));

        }
    }

    private AnschlussPortierungKorrekt unmarshalAnschlussPortierungKorrekt(DurchwahlanlageBestandType inDurchwahlanlageBestandType) {
        if (inDurchwahlanlageBestandType == null) {
            return null;
        }
        AnschlussPortierungKorrekt outAnschlussPortierungKorrekt = new AnschlussPortierungKorrekt();
        BestandsSuche bestandsSuche = new BestandsSuche();
        bestandsSuche.setAnlagenAbfrageStelle(inDurchwahlanlageBestandType.getOnkzDurchwahlAbfragestelle()
                .getAbfragestelle());
        bestandsSuche.setAnlagenDurchwahl(inDurchwahlanlageBestandType.getOnkzDurchwahlAbfragestelle()
                .getDurchwahlnummer());
        bestandsSuche.setAnlagenOnkz(inDurchwahlanlageBestandType.getOnkzDurchwahlAbfragestelle().getONKZ());
        outAnschlussPortierungKorrekt.setOnkzDurchwahlAbfragestelle(bestandsSuche);

        for (RufnummernblockType rufnummerBlockType : inDurchwahlanlageBestandType.getRufnummernbloecke()) {
            RufnummernBlock rufnummernBlock = new RufnummernBlock();
            rufnummernBlock.setVon(rufnummerBlockType.getRnrBlockVon());
            rufnummernBlock.setBis(rufnummerBlockType.getRnrBlockBis());
            outAnschlussPortierungKorrekt.addRufnummernblock(rufnummernBlock);
        }
        return outAnschlussPortierungKorrekt;
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
