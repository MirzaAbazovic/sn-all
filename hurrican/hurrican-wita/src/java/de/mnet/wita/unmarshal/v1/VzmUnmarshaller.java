/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeVZMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypVZMType;
import de.mnet.wita.message.meldung.VerzoegerungsMeldung;

@Component
public class VzmUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypVZMType> {

    @Override
    public VerzoegerungsMeldung unmarshal(MeldungstypVZMType in) {
        VerzoegerungsMeldung out = new VerzoegerungsMeldung();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypVZMType in, VerzoegerungsMeldung out) {
        MeldungsattributeVZMType inMeldungsattribute = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setKundennummerBesteller(inMeldungsattribute.getKundennummerBesteller());
        out.setExterneAuftragsnummer(inMeldungsattribute.getExterneAuftragsnummer());
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
        out.setVerzoegerungstermin(DateConverterUtils.toLocalDate(inMeldungsattribute.getVerzoegerungstermin()));
    }

    private void mapMeldungPositions(MeldungstypVZMType in, VerzoegerungsMeldung out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

}
