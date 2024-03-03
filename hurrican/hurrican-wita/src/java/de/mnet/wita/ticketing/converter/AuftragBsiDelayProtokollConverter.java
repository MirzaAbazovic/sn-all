/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.01.2013 09:00:06
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.Auftrag;

/**
 * Konverter-Implementierung fuer einen BSI Delay Eintrag.
 */
public class AuftragBsiDelayProtokollConverter extends BaseRequestBsiDelayProtokollConverter<Auftrag> {

    private static final String TEMPLATE = "WITA Geschäftsfall {2} wird vorgehalten. "
            + WITA_REFERENZ_NR_TEMPLATE;

    @Override
    public AddCommunication apply(Auftrag auftrag) {
        AddCommunication protokollEintrag = createRequestProtokollEintrag(auftrag);

        protokollEintrag.setNotes(MessageFormat.format(TEMPLATE,
                getAnbieterwechsel46TKG(auftrag.getExterneAuftragsnummer()),
                auftrag.getExterneAuftragsnummer(),
                auftrag.getGeschaeftsfall().getGeschaeftsfallTyp().getDisplayName()));

        return protokollEintrag;
    }

    @Override
    public Long findHurricanAuftragId(Auftrag auftrag) {
        return findHurricanAuftragIdViaCbVorgang(auftrag.getExterneAuftragsnummer());
    }
}


