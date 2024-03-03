/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeERGMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERGMType;
import de.mnet.wita.message.meldung.ErgebnisMeldung;

@SuppressWarnings("Duplicates")
@Component
public class ErgmUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypERGMType> {


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
