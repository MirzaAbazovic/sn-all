/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.10.2014
 */
package de.mnet.wita.service.impl;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wita.activiti.BusinessKeyUtils;
import de.mnet.wita.bpm.AbgebendPvWorkflowService;
import de.mnet.wita.bpm.KueDtWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.exceptions.WitaBaseException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.IncomingTalOrderMeldung;
import de.mnet.wita.service.WitaReceiveMessageService;

@CcTxRequired
public class WitaReceiveMessageServiceImpl implements WitaReceiveMessageService {
    private static final Logger LOGGER = Logger.getLogger(WitaReceiveMessageServiceImpl.class);

    @Autowired
    private AbgebendPvWorkflowService abgebendPvWorkflowService;
    @Autowired
    private KueDtWorkflowService kueDtWorkflowService;
    @Autowired
    private TalOrderWorkflowService talOrderWorkflowService;

    @Override
    public boolean handleWitaMessage(IncomingMessage witaMessage) {
        boolean success = true;
        if (witaMessage instanceof IncomingTalOrderMeldung) {
            IncomingTalOrderMeldung talOrderMeldung = (IncomingTalOrderMeldung) witaMessage;
            GeschaeftsfallTyp geschaeftsfallTyp = talOrderMeldung.getGeschaeftsfallTyp();
            Preconditions.checkNotNull(geschaeftsfallTyp,
                    "Der GeschaeftsfallTyp ist in einer empfangenen Nachricht nicht gesetzt.");

            if (geschaeftsfallTyp == GeschaeftsfallTyp.KUENDIGUNG_TELEKOM) {
                onKueDtMessage(talOrderMeldung);
            }
            else {
                onTalOrderMeldung(talOrderMeldung);
            }
        }
        else if (witaMessage instanceof IncomingPvMeldung) {
            abgebendPvWorkflowService.handleWitaMessage((IncomingPvMeldung) witaMessage);
        }
        else {
            success = false;
        }
        return success;
    }

    private void onKueDtMessage(IncomingTalOrderMeldung talOrderMeldung) {
        if (talOrderMeldung instanceof ErledigtMeldung || talOrderMeldung instanceof EntgeltMeldung) {
            talOrderMeldung.setExterneAuftragsnummer(BusinessKeyUtils.getKueDtBusinesskey(talOrderMeldung
                    .getExterneAuftragsnummer()));
            if (talOrderMeldung instanceof ErledigtMeldung) {
                kueDtWorkflowService.newProcessInstance((ErledigtMeldung) talOrderMeldung);
            }
            else {
                // Entgeltmeldung: just send a message to the workflow
                kueDtWorkflowService.handleWitaMessage(talOrderMeldung);
            }
        }
        else {
            // bei KUE-DTs können alle Meldungen ausser ERLM/ENTM nicht verarbeitet werden
            throw new WitaBaseException(String.format("Die Meldung '%s' für den Geschäftsfall KUE-DT kann nicht " +
                    "verarbeitet werden. Für den Geschäftsfall KUE-DT werden nur Erledigt- und Entgeltmeldung " +
                    "akzeptiert.", talOrderMeldung));
        }
    }

    private void onTalOrderMeldung(IncomingTalOrderMeldung meldung) {
        LOGGER.debug("Sending Result with extAuftragnr " + meldung.getExterneAuftragsnummer());
        talOrderWorkflowService.handleWitaMessage(meldung);
    }

}
