/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 15:51:47
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;

/**
 * konvertiert eine ABM-PV in einen BSI-Protokolleintrag
 */
public class AbmPvBsiProtokollConverter extends AbstractMeldungPvBsiProtokollConverter<AuftragsBestaetigungsMeldungPv> {

    private static final String ABM_PV_EMPFANGEN = String.format(
            "Auftragsbestätigungsmeldung an Provider {2} zum Providerwechsel %s - Termin: {3} - Hinweise: {4}",
            WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(AuftragsBestaetigungsMeldungPv abmPv) {
        Preconditions.checkNotNull(abmPv);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(abmPv);

        protokollEintrag.setNotes(MessageFormat.format(ABM_PV_EMPFANGEN,
                getAnbieterwechsel46TKG(abmPv.getExterneAuftragsnummer()),
                abmPv.getExterneAuftragsnummer(),
                abmPv.getAufnehmenderProvider().getProvidernameAufnehmend(),
                abmPv.getAufnehmenderProvider().getUebernahmeDatumVerbindlich(),
                formatMeldungspositionen(abmPv.getMeldungsPositionen())));
        return protokollEintrag;
    }
}
