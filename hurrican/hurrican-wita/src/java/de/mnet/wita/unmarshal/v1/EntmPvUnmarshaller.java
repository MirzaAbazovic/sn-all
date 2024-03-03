/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeENTMPVType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMPVType;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;

@Component
public class EntmPvUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypENTMPVType> {


    @Override
    public EntgeltMeldungPv unmarshal(MeldungstypENTMPVType in) {
        EntgeltMeldungPv out = new EntgeltMeldungPv();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypENTMPVType in, EntgeltMeldungPv out) {
        MeldungsattributeENTMPVType inAttribute = in.getMeldungsattribute();

        out.setKundenNummer(inAttribute.getKundennummer());
        out.setVertragsNummer(inAttribute.getVertragsnummer());
        out.setEntgelttermin(DateConverterUtils.toLocalDate(inAttribute.getEntgelttermin()));
    }

    private void mapMeldungPositions(MeldungstypENTMPVType in, EntgeltMeldungPv out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }
}
