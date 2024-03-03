/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import java.util.*;
import org.springframework.stereotype.Component;

import de.mnet.common.tools.DateConverterUtils;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.AnlageMitTypType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypENTMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.ProduktpositionType;
import de.mnet.wita.message.meldung.EntgeltMeldung;

@Component
public class EntmUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypENTMType> {

    @Override
    public EntgeltMeldung unmarshal(MeldungstypENTMType in) {
        EntgeltMeldung out = new EntgeltMeldung();

        mapMeldungAttributes(in, out);
        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypENTMType in, EntgeltMeldung out) {
        MeldungsattributeENTMType inMeldungAttributes = in.getMeldungsattribute();

        out.setKundenNummer(inMeldungAttributes.getKundennummer());
        out.setKundennummerBesteller(inMeldungAttributes.getKundennummerBesteller());
        out.setExterneAuftragsnummer(inMeldungAttributes.getExterneAuftragsnummer());
        out.setVertragsNummer(inMeldungAttributes.getVertragsnummer());
        out.setEntgelttermin(DateConverterUtils.toLocalDate(inMeldungAttributes.getEntgelttermin()));

        List<ProduktpositionType> inPositionen = inMeldungAttributes.getProduktpositionen().getPosition();
        out.getProduktPositionen().addAll(unmarshalProduktpositionen(inPositionen));
        out.getAnlagen().addAll(unmarshalAnlagen(getAnlagen(inMeldungAttributes.getAnlagen())));
    }

    private List<AnlageMitTypType> getAnlagen(MeldungsattributeENTMType.Anlagen anlagen) {
        if (anlagen != null) {
            return anlagen.getAnlage();
        }
        return null;
    }

    private void mapMeldungPositions(MeldungstypENTMType in, EntgeltMeldung out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

}
