/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import org.activiti.engine.runtime.ProcessInstance;

import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Quittung;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.meldung.TalOrderMeldung;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer die Activiti Workflows im Context einer TAL-Bestellung.
 */
public interface TalOrderWorkflowService extends WitaService {

    /**
     * Erstellt eine neue Prozess-Instanz fuer den angegebenen CB-Vorgang.
     *
     * @return die erstellte {@link ProcessInstance}
     */
    ProcessInstance newProcessInstance(WitaCBVorgang cbVorgang);

    /**
     * Uebergibt dem Workflow die eingehende Meldung von der DTAG.
     *
     * @param message das {@link TalOrderMeldung}sobjekt (z.B. {@link Quittung} fuer TEQ)
     */
    <T extends TalOrderMeldung> void handleWitaMessage(T message);

    /**
     * Sends the given {@link TerminVerschiebung} or {@link Storno} to the corresponding Workflow using its {@code
     * extAuftragsnummer}.
     */
    void sendTvOrStornoRequest(MnetWitaRequest request);

    /**
     * Schickt einer ERLMK an den Workflow, der diese an die DTAG schickt
     */
    WitaCBVorgang sendErlmk(WitaCBVorgang witaCbVorgang);

    /** ---------------------------------------- Nur fuer Tests ------------------------------------------------- */

    /**
     * Restarts an already completed process instance in state "WaitForTEQ". ONLY FOR TEST PURPOSES!
     */
    void restartProcessInstance(WitaCBVorgang cbVorgang);

}
