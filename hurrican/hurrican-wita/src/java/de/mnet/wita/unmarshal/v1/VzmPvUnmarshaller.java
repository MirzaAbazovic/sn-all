/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeVZMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypVZMPVType;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;

@Component
public class VzmPvUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypVZMPVType> {

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
