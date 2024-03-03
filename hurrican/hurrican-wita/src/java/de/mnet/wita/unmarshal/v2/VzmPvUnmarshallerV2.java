/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeVZMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypVZMPVType;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

@SuppressWarnings("Duplicates")
@Component
public class VzmPvUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypVZMPVType> {

    @Override
    public VerzoegerungsMeldungPv unmarshal(MeldungstypVZMPVType in) {
        VerzoegerungsMeldungPv out = new VerzoegerungsMeldungPv();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypVZMPVType in, VerzoegerungsMeldungPv out) {
        MeldungsattributeVZMPVType inMeldungsattribute = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
        out.setVerzoegerungstermin(DateConverterUtils.toLocalDate(inMeldungsattribute.getVerzoegerungstermin()));
    }

    private void mapMeldungPositions(MeldungstypVZMPVType in, VerzoegerungsMeldungPv out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

}
