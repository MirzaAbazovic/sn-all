/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import org.activiti.engine.runtime.ProcessInstance;

import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer die Activiti Workflows im Context eines abgebenden Providerwechsels.
 */
public interface AbgebendPvWorkflowService extends WitaService {

    /**
     * Sends a message to the workflow.
     *
     * @param message message the triggering message - the service decides whether to pass it to the workflow, just to
     *                persist it or to decine it
     */
    void handleWitaMessage(IncomingPvMeldung meldung);

    /**
     * Ermittelt eine {@link ProcessInstance} zu einer Vertragsnummer
     *
     * @param vertragsNummer Vertragsnummer, zu der die {@link ProcessInstance} ermittelt werden soll
     * @return die ermittelte / gefundene {@link ProcessInstance}
     */
    ProcessInstance retrieveRunningProcessInstance(String vertragsNummer);

    /**
     * Sendet fuer den angegebenen Workflow (bzw. die Prozess-Instanz), der ueber den BusinessKey definiert ist, eine
     * RUEM-PV.
     *
     * @param businessKey BusinessKey des Workflows, auf dem die RUEM-PV gesendet werden soll.
     * @param antwortCode Antwort-Code fuer die RUEM-PV
     * @param antwortText AntwortText falls fuer AntwortCode erforderlich
     */
    public void sendRuemPv(String businessKey, RuemPvAntwortCode antwortCode, String antwortText);

}
