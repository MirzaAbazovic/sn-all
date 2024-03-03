/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.09.2014
 */
package de.mnet.wita.unmarshal.v1;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungsattributeTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v1.MeldungstypTAMType;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;

@Component
public class TamUnmarshaller extends AbstractUnmarshallerFunction<MeldungstypTAMType> {

    @Override
    public TerminAnforderungsMeldung unmarshal(MeldungstypTAMType in) {
        TerminAnforderungsMeldung out = new TerminAnforderungsMeldung();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypTAMType in, TerminAnforderungsMeldung out) {
        MeldungsattributeTAMType inMeldungsattribute = in.getMeldungsattribute();

        out.setExterneAuftragsnummer(inMeldungsattribute.getExterneAuftragsnummer());
        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setKundennummerBesteller(inMeldungsattribute.getKundennummerBesteller());
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
    }

    private void mapMeldungPositions(MeldungstypTAMType in, TerminAnforderungsMeldung out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));
    }

}
