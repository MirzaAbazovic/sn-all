/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.02.2012 16:09:25
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;

/**
 * Konvertiert eine AKM-PV in einen BSI-Protokolleintrag
 */
public class AkmPvBsiProtokollConverter extends AbstractMeldungPvBsiProtokollConverter<AnkuendigungsMeldungPv> {

    private static final String AKM_PV_EMPFANGEN = String.format(
            "Ankündigungsmeldung, dass Kunde zum Provider: {2} wechselt %s - Der angefragte Termin: {3} - Gründe: {4}",
            WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(AnkuendigungsMeldungPv akmPv) {
        Preconditions.checkNotNull(akmPv);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(akmPv);

        protokollEintrag.setNotes(MessageFormat.format(AKM_PV_EMPFANGEN,
                getAnbieterwechsel46TKG(akmPv.getExterneAuftragsnummer()),
                akmPv.getExterneAuftragsnummer(),
                akmPv.getAufnehmenderProvider().getProvidernameAufnehmend(),
                akmPv.getAufnehmenderProvider().getUebernahmeDatumGeplant(),
                formatMeldungspositionen(akmPv.getMeldungsPositionen())));
        return protokollEintrag;
    }
}
