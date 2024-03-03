/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AngabenZurLeitungType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.AnschlussType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTALERLMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERLMPVType;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.position.Leitung;

@SuppressWarnings("Duplicates")
@Component
public class ErlmPvUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypERLMPVType> {

    @Override
    public ErledigtMeldungPv unmarshal(MeldungstypERLMPVType in) {
        ErledigtMeldungPv out = new ErledigtMeldungPv();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypERLMPVType in, ErledigtMeldungPv out) {
        MeldungstypERLMPVType.Meldungsattribute inMeldungsattribute = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
        out.setErledigungstermin(DateConverterUtils.toLocalDate(inMeldungsattribute.getErledigungstermin()));
        out.setLeitung(unmarshallLeitung(inMeldungsattribute.getTAL()));

        mapAnschluss(inMeldungsattribute.getTAL(), out);
    }

    private void mapMeldungPositions(MeldungstypERLMPVType in, ErledigtMeldungPv out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

    private Leitung unmarshallLeitung(MeldungsattributeTALERLMPVType inTal) {
        if (inTal == null || inTal.getLeitung() == null) {
            return null;
        }

        AngabenZurLeitungType inLeitung = inTal.getLeitung();
        Leitung outLeitung = new Leitung();
        outLeitung.setLeitungsBezeichnung(unmarshalLeitungsbezeichnung(inLeitung.getLeitungsbezeichnung()));
        outLeitung.setSchleifenWiderstand(inLeitung.getSchleifenwiderstand());
        return outLeitung;
    }

    private void mapAnschluss(MeldungsattributeTALERLMPVType inTal, ErledigtMeldungPv out) {
        if (inTal == null || inTal.getAnschluss() == null) {
            return;
        }

        AnschlussType inAnschluss = inTal.getAnschluss();
        out.setAnschlussOnkz(inAnschluss.getONKZ());
        out.setAnschlussRufnummer(inAnschluss.getRufnummer());
    }

}
