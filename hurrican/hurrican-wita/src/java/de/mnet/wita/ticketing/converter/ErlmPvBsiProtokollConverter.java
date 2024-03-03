/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 17:24:53
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;

/**
 * konvertiert eine ERLM-PV in einen BSI-Protokolleintrag
 */
public class ErlmPvBsiProtokollConverter extends AbstractMeldungPvBsiProtokollConverter<ErledigtMeldungPv> {

    private static final String ERLM_PV_EMPFANGEN = String.format(
            "Erledigungsmeldungg zum Providerwechsel %s - Hinweise: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(ErledigtMeldungPv erlmPv) {
        Preconditions.checkNotNull(erlmPv);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(erlmPv);

        protokollEintrag.setNotes(MessageFormat.format(ERLM_PV_EMPFANGEN,
                getAnbieterwechsel46TKG(erlmPv.getExterneAuftragsnummer()),
                erlmPv.getExterneAuftragsnummer(),
                formatMeldungspositionen(erlmPv.getMeldungsPositionen())));
        return protokollEintrag;
    }
}
