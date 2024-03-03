/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 09:42:06
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;

/**
 * konvertiert eine ERLM-K in einen BSI-Protokolleintrag
 */
public class ErlmkBsiProtokollConverter extends BaseMeldungBsiProtokollConverter<ErledigtMeldungKunde> {

    private static final String ERLMK_EMPFANGEN = String.format(
            "Fertigmeldung gesendet %s - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(ErledigtMeldungKunde erlmk) {
        AddCommunication bsiProtokollEintrag = createMeldungProtokollEintrag(erlmk);

        bsiProtokollEintrag.setNotes(MessageFormat.format(ERLMK_EMPFANGEN,
                getAnbieterwechsel46TKG(erlmk.getExterneAuftragsnummer()),
                erlmk.getExterneAuftragsnummer(),
                formatMeldungspositionen(erlmk.getMeldungsPositionen())));
        return bsiProtokollEintrag;
    }
}
