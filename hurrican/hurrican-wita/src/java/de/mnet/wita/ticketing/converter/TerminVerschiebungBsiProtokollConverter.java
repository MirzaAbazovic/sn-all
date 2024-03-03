/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.02.2012 11:19:26
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.TerminVerschiebung;

public class TerminVerschiebungBsiProtokollConverter extends BaseRequestBsiProtokollConverter<TerminVerschiebung> {

    private static final String TEMPLATE = String.format(
            "Terminverschiebung gesendet %s mit neuem Kundenwunschtermin: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(TerminVerschiebung tv) {
        AddCommunication protokollEintrag = createRequestProtokollEintrag(tv);
        protokollEintrag.setNotes(MessageFormat.format(TEMPLATE,
                getAnbieterwechsel46TKG(tv.getExterneAuftragsnummer()),
                tv.getExterneAuftragsnummer(),
                tv.getTermin().toString()));
        return protokollEintrag;
    }

}


