/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.02.2012 13:12:38
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.RueckMeldungPv;

/**
 * Konvertiert eine RUEM-PV in einen BSI-Protokolleintrag
 */
public class RuemPvBsiProtokollConverter extends AbstractMeldungPvBsiProtokollConverter<RueckMeldungPv> {

    private static final String RUEM_PV_POSITIV = String.format(
            "Positive Rückmeldung von M-net zum Providerwechsel %s - Gründe: {2}",
            WITA_REFERENZ_NR_TEMPLATE);
    private static final String RUEM_PV_NEGATIV = String.format(
            "Negative Rückmeldung von M-net zum Providerwechsel %s - Gründe: {2}",
            WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(RueckMeldungPv ruemPv) {
        Preconditions.checkNotNull(ruemPv);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(ruemPv);
        if (ruemPv.getAbgebenderProvider().isZustimmungProviderWechsel()) {
            protokollEintrag.setNotes(MessageFormat.format(RUEM_PV_POSITIV,
                    getAnbieterwechsel46TKG(ruemPv.getExterneAuftragsnummer()),
                    ruemPv.getExterneAuftragsnummer(),
                    formatMeldungspositionen(ruemPv.getMeldungsPositionen())));
        }
        else {
            protokollEintrag.setNotes(MessageFormat.format(RUEM_PV_NEGATIV,
                    getAnbieterwechsel46TKG(ruemPv.getExterneAuftragsnummer()),
                    ruemPv.getExterneAuftragsnummer(),
                    formatMeldungspositionen(ruemPv.getMeldungsPositionen())));
        }
        return protokollEintrag;

    }
}
