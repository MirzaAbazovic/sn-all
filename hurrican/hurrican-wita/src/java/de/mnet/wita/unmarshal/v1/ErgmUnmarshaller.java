/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeERGMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypERGMType;
import de.mnet.wita.message.meldung.ErgebnisMeldung;

@Component
public class ErgmUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypERGMType> {


    @Override
    public ErgebnisMeldung unmarshal(MeldungstypERGMType in) {
        ErgebnisMeldung out = new ErgebnisMeldung();

        mapMeldungAttributes(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypERGMType in, ErgebnisMeldung out) {
        MeldungsattributeERGMType inMeldungAttributes = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setKundennummerBesteller(inMeldungAttributes.getKundennummerBesteller());
        out.setExterneAuftragsnummer(inMeldungAttributes.getExterneAuftragsnummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
        out.setErgebnislink(inMeldungAttributes.getErgebnislink());
    }
}
