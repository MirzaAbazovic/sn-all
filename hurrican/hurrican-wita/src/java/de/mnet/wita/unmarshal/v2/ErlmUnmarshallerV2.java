/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeERLMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypERLMType;
import de.mnet.wita.message.meldung.ErledigtMeldung;

@SuppressWarnings("Duplicates")
@Component
public class ErlmUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypERLMType> {

    @Override
    public ErledigtMeldung unmarshal(MeldungstypERLMType in) {
        ErledigtMeldung out = new ErledigtMeldung();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypERLMType in, ErledigtMeldung out) {
        MeldungstypERLMType.Meldungsattribute inMeldungsattribute = in.getMeldungsattribute();

        out.setExterneAuftragsnummer(inMeldungsattribute.getExterneAuftragsnummer());
        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setKundennummerBesteller(inMeldungsattribute.getKundennummerBesteller());
        out.setErledigungstermin(DateConverterUtils.toLocalDate(inMeldungsattribute.getErledigungstermin()));
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());

        MeldungsattributeERLMType.Produktpositionen inProduktpositionen = inMeldungsattribute.getProduktpositionen();
        if (inProduktpositionen != null) {
            out.getProduktPositionen().addAll(unmarshalProduktpositionen(inProduktpositionen.getPosition()));
        }
    }

    private void mapMeldungPositions(MeldungstypERLMType in, ErledigtMeldung out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }
}
