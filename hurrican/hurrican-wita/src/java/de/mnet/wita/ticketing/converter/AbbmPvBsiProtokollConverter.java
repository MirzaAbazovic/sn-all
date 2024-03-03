/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 16:31:20
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import java.util.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;

/**
 * konvertiert eine ABM-PV in einen BSI-Protokolleintrag
 */
public class AbbmPvBsiProtokollConverter extends AbstractMeldungPvBsiProtokollConverter<AbbruchMeldungPv> {

    private static final String ABBM_PV_EMPFANGEN = String.format(
            "Abbruchmeldung zum Providerwechsel %s - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(AbbruchMeldungPv abbmPv) throws IllegalArgumentException, NoSuchElementException {
        Preconditions.checkNotNull(abbmPv);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(abbmPv);

        protokollEintrag.setNotes(MessageFormat.format(ABBM_PV_EMPFANGEN,
                getAnbieterwechsel46TKG(abbmPv.getExterneAuftragsnummer()),
                abbmPv.getExterneAuftragsnummer(),
                formatMeldungspositionen(abbmPv.getMeldungsPositionen())));
        return protokollEintrag;
    }
}
