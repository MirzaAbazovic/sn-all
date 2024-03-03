/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import java.util.*;
import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABMPVType;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;
import de.mnet.wita.message.meldung.position.MeldungsPosition;

@SuppressWarnings("Duplicates")
@Component
public class AbmPvUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypABMPVType> {

    @Override
    public WitaMessage unmarshal(MeldungstypABMPVType in) {
        AuftragsBestaetigungsMeldungPv out = new AuftragsBestaetigungsMeldungPv();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypABMPVType in, AuftragsBestaetigungsMeldungPv out) {
        MeldungstypABMPVType.Meldungsattribute inMeldungAttributes = in.getMeldungsattribute();
        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
        out.setAufnehmenderProvider(unmarshalAufnehmenderProvider(inMeldungAttributes.getAufnehmenderProvider()));
        out.setLeitung(unmarshallLeitung(inMeldungAttributes.getTAL()));
    }

    private void mapMeldungPositions(MeldungstypABMPVType in, AuftragsBestaetigungsMeldungPv out) {
        List<MeldungstypABMPVType.Meldungspositionen.Position> inPositions = in.getMeldungspositionen().getPosition();

        for (MeldungstypABMPVType.Meldungspositionen.Position inPosition : inPositions) {
            MeldungsPosition outPosition = new MeldungsPosition();
            mapMeldungPosition(inPosition, outPosition);
            out.getMeldungsPositionen().add(outPosition);
        }
    }

    private Leitung unmarshallLeitung(MeldungsattributeTALABMPVType inTal) {
        if (inTal == null || inTal.getLeitung() == null) {
            return null;
        }

        AngabenZurLeitungType inLeitung = inTal.getLeitung();
        Leitung outLeitung = new Leitung();
        outLeitung.setLeitungsBezeichnung(unmarshalLeitungsbezeichnung(inLeitung.getLeitungsbezeichnung()));
        outLeitung.setSchleifenWiderstand(inLeitung.getSchleifenwiderstand());
        return outLeitung;
    }


}
