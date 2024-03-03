/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import org.activiti.engine.runtime.ProcessInstance;

import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.IncomingTalOrderMeldung;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer die Activiti Workflows im Kontext einer DTAG Kuendigung.
 */
public interface KueDtWorkflowService extends WitaService {

    /**
     * Creates a new Kuendigung DTAG workflow process instance.
     *
     * @param erledigtMeldung the triggering erlm object - must have Geschaeftsfall KueDt
     * @return the created process instance
     */
    ProcessInstance newProcessInstance(ErledigtMeldung erledigtMeldung);

    /**
     * Sends a message to the workflow.
     *
     * @param the message to be send to the workflow - the service decides whether to pass it to the workflow, just to
     *            persist it or to decine it
     */
    <T extends IncomingTalOrderMeldung> void handleWitaMessage(T message);

}
