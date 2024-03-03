/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALABBMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypABBMPVType;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;

@SuppressWarnings("Duplicates")
@Component
public class AbbmPvUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypABBMPVType> {

    @Override
    public AbbruchMeldungPv unmarshal(MeldungstypABBMPVType in) {
        AbbruchMeldungPv out = new AbbruchMeldungPv();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypABBMPVType in, AbbruchMeldungPv out) {
        MeldungstypABBMPVType.Meldungsattribute inMeldungAttributes = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
        out.setLeitung(unmarshallLeitung(inMeldungAttributes.getTAL()));
    }

    private void mapMeldungPositions(MeldungstypABBMPVType in, AbbruchMeldungPv out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

    private Leitung unmarshallLeitung(MeldungsattributeTALABBMPVType inTal) {
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
