/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 16:12:00
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.TerminAnforderungsMeldung;

/**
 * konvertiert eine TAM Meldung in einen BSI-Protokolleintrag
 */
public class TamBsiProtokollConverter extends BaseMeldungBsiProtokollConverter<TerminAnforderungsMeldung> {

    private static final String template = String.format(
            "Terminanforderungsmeldung empfangen %s - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(TerminAnforderungsMeldung tam) {
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(tam);

        protokollEintrag.setNotes(MessageFormat.format(template,
                getAnbieterwechsel46TKG(tam.getExterneAuftragsnummer()),
                tam.getExterneAuftragsnummer(),
                formatMeldungspositionen(tam.getMeldungsPositionen())));
        return protokollEintrag;
    }
}
