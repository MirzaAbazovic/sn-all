/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTALABMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypABMPVType;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;

@Component
public class WitaAbmPvUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypABMPVType> {

    @Override
    public AuftragsBestaetigungsMeldungPv unmarshal(MeldungstypABMPVType in) {
        AuftragsBestaetigungsMeldungPv out = new AuftragsBestaetigungsMeldungPv();

        mapMeldungAttributes(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypABMPVType in, AuftragsBestaetigungsMeldungPv out) {
        MeldungstypABMPVType.Meldungsattribute inMeldungsattribute = in.getMeldungsattribute();

        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
        out.setLeitung(unmarshallLeitung(inMeldungsattribute.getTAL()));
        out.setAufnehmenderProvider(unmarshalAufnehmenderProvider(in.getMeldungsattribute().getAufnehmenderProvider()));

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
