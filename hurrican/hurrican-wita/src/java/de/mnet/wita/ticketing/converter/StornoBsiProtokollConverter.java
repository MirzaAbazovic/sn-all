/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 13:16:51
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.Storno;

public class StornoBsiProtokollConverter extends BaseRequestBsiProtokollConverter<Storno> {

    private static final String TEMPLATE = "Storno gesendet " + WITA_REFERENZ_NR_TEMPLATE;

    @Override
    public AddCommunication apply(Storno storno) {
        AddCommunication protokollEintrag = createRequestProtokollEintrag(storno);
        protokollEintrag.setNotes(MessageFormat.format(TEMPLATE,
                getAnbieterwechsel46TKG(storno.getExterneAuftragsnummer()),
                storno.getExterneAuftragsnummer()));
        return protokollEintrag;
    }

}


