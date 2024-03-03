/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypTAMType;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;

@SuppressWarnings("Duplicates")
@Component
public class TamUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypTAMType> {

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
