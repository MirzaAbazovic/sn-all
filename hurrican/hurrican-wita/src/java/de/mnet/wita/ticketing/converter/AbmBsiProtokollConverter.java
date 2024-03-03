/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 11:26:31
 */
package de.mnet.wita.ticketing.converter;

import java.text.*;
import java.time.format.*;
import com.google.common.base.Preconditions;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;

/**
 * konvertiert eine ABM in einen BSI-Protokolleintrag
 */
public class AbmBsiProtokollConverter extends BaseMeldungBsiProtokollConverter<AuftragsBestaetigungsMeldung> {

    private static final String TEMPLATE = String.format(
            "Auftragsbestätigung empfangen %s - Lieferung erfolgt am: {2} - Hinweise: {3}", WITA_REFERENZ_NR_TEMPLATE);

    @Override
    public AddCommunication apply(AuftragsBestaetigungsMeldung abm) {
        Preconditions.checkNotNull(abm);
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(abm);

        protokollEintrag.setNotes(MessageFormat.format(TEMPLATE,
                getAnbieterwechsel46TKG(abm.getExterneAuftragsnummer()),
                abm.getExterneAuftragsnummer(),
                abm.getVerbindlicherLiefertermin().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")),
                formatMeldungspositionen(abm.getMeldungsPositionen())));
        return protokollEintrag;
    }

}


