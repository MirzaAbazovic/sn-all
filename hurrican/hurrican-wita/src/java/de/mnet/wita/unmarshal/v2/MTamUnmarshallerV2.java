/*
 * Copyright (c) 2016 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.05.2016
 */
package de.mnet.wita.unmarshal.v2;

import org.springframework.stereotype.Component;

import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungsattributeMTAMType;
import de.mnet.esb.cdm.supplierpartner.lineordernotificationservice.v2.MeldungstypMTAMType;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;

@SuppressWarnings("Duplicates")
@Component
public class MTamUnmarshallerV2 extends AbstractUnmarshallerFunctionV2<MeldungstypMTAMType> {

    @Override
    public TerminAnforderungsMeldung unmarshal(MeldungstypMTAMType in) {
        TerminAnforderungsMeldung out = new TerminAnforderungsMeldung();

        mapMeldungAttributes(in, out);

        mapMeldungPositions(in, out);

        return out;
    }

    private void mapMeldungAttributes(MeldungstypMTAMType in, TerminAnforderungsMeldung out) {
        MeldungsattributeMTAMType inMeldungsattribute = in.getMeldungsattribute();

        out.setExterneAuftragsnummer(inMeldungsattribute.getExterneAuftragsnummer());
        out.setKundenNummer(inMeldungsattribute.getKundennummer());
        out.setKundennummerBesteller(inMeldungsattribute.getKundennummerBesteller());
        out.setVertragsNummer(inMeldungsattribute.getVertragsnummer());
        out.setMahnTam(true);
    }

    private void mapMeldungPositions(MeldungstypMTAMType in, TerminAnforderungsMeldung out) {
        out.getMeldungsPositionen().addAll(unmarshalMeldungspositionen(in.getMeldungspositionen().getPosition()));

    }
}
