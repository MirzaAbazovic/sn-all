/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 15:01:38
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.Auftrag;

public class AuftragBsiProtokollConverter extends BaseRequestBsiProtokollConverter<Auftrag> {

    private static final String TEMPLATE = "WITA Bestellung mit Geschäftsfall {2} ausgelöst "
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
}
