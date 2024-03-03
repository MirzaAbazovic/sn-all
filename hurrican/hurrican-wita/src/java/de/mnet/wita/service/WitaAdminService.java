/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 08:57:52
 */
package de.mnet.wita.service;

import java.util.*;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.model.ResendableMessage;
import de.mnet.wita.model.WorkflowInstanceDetailsDto;
import de.mnet.wita.model.WorkflowInstanceDto;

/**
 * Service fuer Wita-AdminTools
 */
public interface WitaAdminService extends WitaService {

    /**
     * Such nach offenen Workflow-Instanzen.
     *
     * @param onlyWorkflowErrors Flag definiert, ob nach allen (false) Workflow-Instanzen oder nur nach
     *                           Workflow-Instanzen mit Error-Task (true) gesucht werden soll.
     */
    List<WorkflowInstanceDto> findOpenWorkflowInstances(boolean onlyWorkflowErrors);

    /**
     * Sucht die Workflow-Instanz zum BusinessKey
     */
    WorkflowInstanceDto getWorkflowInstance(String businessKey);

    /**
     * Laedt die details zu einer Workflow Instanz.
     */
    WorkflowInstanceDetailsDto getWorkflowInstanceDetails(String businessKey);

    void resetErrorState(String businessKey, CommonWorkflowService.WorkflowReentryState newState);

    /**
     * Der Usertask, der zur Processinstance mit dem BusinessKey businessKey gehoert, wird dem Hurrican user
     * zugewiesen.
     *
     * @param businessKey fachlicher Schluessel der Prozess-Instanz der geclaimt werden soll
     * @param user        Hurrican-Login-Name falls ein Benutzer den Task uebernimmt oder {@code null} falls der Task
     *                    freigegeben werden soll
     */
    void claimErrorTask(String businessKey, AKUser user);

    List<ResendableMessage> findAllResenableMessages(String businessKey);

    void resendMessage(ResendableMessage message);

    /**
     * Setzt die Workflow-Instanz zum BusinessKey im Error Zustand (inkl. Angabe eines Grundes).
     */
    WorkflowInstanceDto setErrorState(String businessKey, AKUser user, String errorReason);
}
