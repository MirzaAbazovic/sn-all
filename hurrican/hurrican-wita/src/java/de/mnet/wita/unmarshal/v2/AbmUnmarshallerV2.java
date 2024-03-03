/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.LeitungsabschnittType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungspositionsattributeABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.ProduktpositionType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.SchaltungIsisOpalMeldungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.SchaltungKupferType;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.common.LeitungsBezeichnung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.position.AbmSchaltangaben;
import de.mnet.wita.message.meldung.position.AbmSchaltung;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.LeitungsAbschnitt;
import de.mnet.wita.message.meldung.position.MeldungsPositionWithAnsprechpartner;

@SuppressWarnings({ "Duplicates", "unused" })
@Component
public class AbmUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypABMType> {

    @Override
    public WitaMessage unmarshal(MeldungstypABMType in) {
        AuftragsBestaetigungsMeldung out = new AuftragsBestaetigungsMeldung();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypABMType in, AuftragsBestaetigungsMeldung out) {
        MeldungstypABMType.Meldungsattribute inMeldungAttributes = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setExterneAuftragsnummer(inMeldungAttributes.getExterneAuftragsnummer());
        out.setVerbindlicherLiefertermin(DateConverterUtils.toLocalDate(inMeldungAttributes.getVerbindlicherLiefertermin()));
        out.setKundennummerBesteller(inMeldungAttributes.getKundennummerBesteller());

        mapTAL(inMeldungAttributes.getTAL(), out);

        List<ProduktpositionType> inPositions = inMeldungAttributes.getProduktpositionen().getPosition();
        out.getProduktPositionen().addAll(unmarshalProduktpositionen(inPositions));

    }

    private void mapMeldungPositions(MeldungstypABMType in, AuftragsBestaetigungsMeldung out) {
        List<MeldungstypABMType.Meldungspositionen.Position> inPositions = in.getMeldungspositionen().getPosition();

        for (MeldungstypABMType.Meldungspositionen.Position inPosition : inPositions) {
            MeldungsPositionWithAnsprechpartner outPosition = new MeldungsPositionWithAnsprechpartner();
            mapMeldungPosition(inPosition, outPosition);
            out.getMeldungsPositionen().add(outPosition);
        }
    }

    private void mapTAL(MeldungsattributeTALABMType in, AuftragsBestaetigungsMeldung out) {
        if (in != null) {

            if (StringUtils.isNotBlank(in.getVertragsnummer())) {
                out.setVertragsNummer(in.getVertragsnummer());
            }

            AngabenZurLeitungABMType inLeitung = in.getLeitung();
            if (inLeitung != null) {
                LeitungsBezeichnung outLeitungsBezeichnung = unmarshalLeitungsbezeichnung(inLeitung.getLeitungsbezeichnung());
                Leitung outLeitung = new Leitung(outLeitungsBezeichnung);
                outLeitung.setMaxBruttoBitrate(inLeitung.getMaxBruttoBitrate());
                outLeitung.setSchleifenWiderstand(inLeitung.getSchleifenwiderstand());
                out.setLeitung(outLeitung);
                List<LeitungsabschnittType> leitungsabschnitte = inLeitung.getLeitungsabschnitt();
                for (LeitungsabschnittType leitungsabschnittType : leitungsabschnitte) {
                    LeitungsAbschnitt leitungsAbschnitt = new LeitungsAbschnitt(
                            leitungsabschnittType.getLfdNrLeitungsabschnitt(),
                            leitungsabschnittType.getLeitungslaenge(),
                            leitungsabschnittType.getLeitungsdurchmesser());
                    outLeitung.getLeitungsAbschnitte().add(leitungsAbschnitt);
                }
            }

            SchaltungIsisOpalMeldungType inSchaltangaben = in.getSchaltangaben();
            if (inSchaltangaben != null) {
                AbmSchaltangaben outAbmSChaltangaben = new AbmSchaltangaben();
                outAbmSChaltangaben.setV5Id(inSchaltangaben.getV5ID());

                List<SchaltungKupferType> schaltung = inSchaltangaben.getSchaltung();
                for (SchaltungKupferType schaltungKupferType : schaltung) {
                    AbmSchaltung abmSchaltung = new AbmSchaltung(
                            schaltungKupferType.getUEVT(),
                            schaltungKupferType.getEVS(),
                            schaltungKupferType.getDoppelader());
                    outAbmSChaltangaben.getSchaltungen().add(abmSchaltung);
                }
                for (String zeitschlitz : inSchaltangaben.getZeitschlitz()) {
                    outAbmSChaltangaben.getZeitSchlitz().add(zeitschlitz);
                }
                out.setSchaltangaben(outAbmSChaltangaben);
            }

            out.setRufnummernPortierung(unmarshalRufnummerPortierung(in.getRnrPortierung()));
        }
    }

    private void mapMeldungPosition(MeldungstypABMType.Meldungspositionen.Position in, MeldungsPositionWithAnsprechpartner out) {
        super.mapMeldungPosition(in, out);

        MeldungspositionsattributeABMType inPositionAttribute = in.getPositionsattribute();
        if (inPositionAttribute != null) {
            out.setAnsprechpartnerTelekom(unmarshallAnsprechpartnerTelekom(inPositionAttribute.getAnsprechpartnerTelekom()));
        }
    }
}
