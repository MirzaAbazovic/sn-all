/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.02.2012 15:20:08
 */
package de.mnet.wita.ticketing.converter;


import static com.google.common.collect.Iterables.*;

import java.text.*;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.esb.cdm.customer.customerservice.v1.AddCommunication;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.KueDtUserTask;
import de.mnet.wita.service.WitaUsertaskService;

/**
 * konvertiert eine ERLM in einen BSI-Protokolleintrag
 */
public class ErlmBsiProtokollConverter extends BaseMeldungBsiProtokollConverter<ErledigtMeldung> {

    private static final String erlmStandard = String.format(
            "Erledigtmeldung empfangen %s - Schaltung erfolgreich - Hinweise: {2}", WITA_REFERENZ_NR_TEMPLATE);
    private static final String erlmStorno = String.format(
            "Erledigtmeldung empfangen %s - Auftrag wurde storniert - Hinweise: {2}", WITA_REFERENZ_NR_TEMPLATE);
    private static final String erlmKueDt = String.format(
            "Leitung wurde von der DTAG gekündigt %s - Gründe: {2}", WITA_REFERENZ_NR_TEMPLATE);

    @Autowired
    WitaUsertaskService witaUsertaskService;

    @Override
    public AddCommunication apply(ErledigtMeldung erlm) {
        AddCommunication protokollEintrag = createMeldungProtokollEintrag(erlm);

        String useTemplate = erlmStandard;

        if (GeschaeftsfallTyp.KUENDIGUNG_TELEKOM.equals(erlm.getGeschaeftsfallTyp())) {
            useTemplate = erlmKueDt;
        }
        else if (AenderungsKennzeichen.STORNO.equals(erlm.getAenderungsKennzeichen())) {
            useTemplate = erlmStorno;
        }

        protokollEintrag.setNotes(MessageFormat.format(useTemplate,
                getAnbieterwechsel46TKG(erlm),
                erlm.getExterneAuftragsnummer(),
                formatMeldungspositionen(erlm.getMeldungsPositionen())));
        return protokollEintrag;
    }

    @Override
    public Long findHurricanAuftragId(ErledigtMeldung erlm) {
        if (GeschaeftsfallTyp.KUENDIGUNG_TELEKOM.equals(erlm.getGeschaeftsfallTyp())) {
            KueDtUserTask kueDtUserTask = witaUsertaskService.findKueDtUserTask(erlm.getExterneAuftragsnummer());
            return getOnlyElement(kueDtUserTask.getAuftragIds());
        }
        return findHurricanAuftragIdViaCbVorgang(erlm.getExterneAuftragsnummer());
    }

    protected String getAnbieterwechsel46TKG(ErledigtMeldung erlm) {
        if (GeschaeftsfallTyp.KUENDIGUNG_TELEKOM.equals(erlm.getGeschaeftsfallTyp())) {
            return "";
        }
        return getAnbieterwechsel46TKG(erlm.getExterneAuftragsnummer());
    }
}


