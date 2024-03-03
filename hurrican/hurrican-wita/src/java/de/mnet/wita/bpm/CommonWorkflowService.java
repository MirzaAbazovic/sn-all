/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;

import de.augustakom.authentication.model.AKUser;
import de.mnet.wita.bpm.impl.Workflow;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.service.WitaService;

/**
 * Abstraction-Layer fuer die alle Funktionalitaeten von Activiti, die unabhaengig vom konkreten Workflow ausgefuehrt
 * werden koennen.
 */
public interface CommonWorkflowService extends WitaService {

    public enum WorkflowReentryState {
        CLOSED(null),
        WAIT_FOR_RUEMPV("waitForRuemPv"),
        WAIT_FOR_MESSAGE("waitForMessage"),
        WAIT_FOR_ENTM("waitForENTMMessage"),
        WAIT_FOR_ENTMPV("waitForEntmPvMessage");

        public final String newWorkflowState;

        private WorkflowReentryState(String newWorkflowState) {
            this.newWorkflowState = newWorkflowState;
        }

        public static List<WorkflowReentryState> getFor(Workflow workflow) {
            switch (workflow) {
                case TAL_ORDER:
                    return ImmutableList.of(CLOSED, WAIT_FOR_MESSAGE, WAIT_FOR_ENTM);
                case KUE_DT:
                    return ImmutableList.of(CLOSED, WAIT_FOR_ENTM);
                case ABGEBEND_PV:
                    return ImmutableList.of(CLOSED, WAIT_FOR_RUEMPV, WAIT_FOR_MESSAGE, WAIT_FOR_ENTMPV);
                default:
                    throw new IllegalArgumentException("WorkflowTyp muss gesetzt sein!");
            }
        }
    }

    /**
     * Schickt eine Nachricht an den Workflow.
     *
     * @param businessKey businessKey der Prozess-Instanz
     * @param params      Variablen fuer Workflow-Ausfuehrung
     */
    void handleWitaMessage(String businessKey, Map<String, Object> params);

    /**
     * Schickt eine ausgehende Nachricht an den Workflow. Falls sich der Workflow im Fehlerzustand befindet, wird eine
     * Exception geworfen.
     *
     * @param businessKey businessKey der Prozess-Instanz
     * @param params      Variablen fuer Workflow-Ausfuehrung
     */
    void handleOutgoingWitaMessage(String businessKey, Map<String, Object> params);

    /**
     * Schickt eine Nachricht an den Workflow.
     *
     * @param processInstance Prozess-Instanz des Workflows
     * @param params          Variablen fuer Workflow-Ausfuehrung
     */
    void handleWitaMessage(ProcessInstance processInstance, Map<String, Object> params);

    /**
     * Loescht die angegebene {@link ProcessInstance}
     *
     * @return {@code true} wenn die {@link ProcessInstance} geloescht werden konnte, sonst {@code false}
     */
    boolean deleteProcessInstance(ProcessInstance processInstance);

    /**
     * Loescht die {@link ProcessInstance} (wenn vorhanden) und die {@link HistoricProcessInstance} zum angegebenen
     * businessKey
     */
    void deleteHistoricProcessInstance(String businessKey);

    /**
     * Creates a new process instance for a given business key and a map of key-value params to be passed to the process
     * instance. The business key must be unique and is used to retrieve a running process instance.
     */
    ProcessInstance newProcessInstance(Workflow workflow, String businessKey, Map<String, Object> params);

    /**
     * Ermittelt eine einzelne {@link ProcessInstance} ueber den angegebenen Business-Key.
     *
     * @param businessKey businessKey des Workflows
     * @throws WitaBpmException falls keine Instanz gefunden werden konnte
     */
    ProcessInstance retrieveProcessInstance(String businessKey);

    /**
     * Ermittelt eine einzelne {@link ProcessInstance} ueber den angegebenen Business-Key.
     *
     * @param businessKey businessKey des Workflows
     * @return {@code null} falls es den Prozess nicht gibt
     */
    ProcessInstance retrieveProcessInstanceIfExists(String businessKey);

    /**
     * Sucht alle offenen Workflow Instanzen
     */
    Collection<ProcessInstance> retrieveAllOpenProcessInstances();

    /**
     * Sucht nach allen offenen Workflows, fuer die ein Error-UserTask existiert.
     */
    Collection<ProcessInstance> retrieveFaultedProcessInstances();

    /**
     * Ermittelt die historische Prozess-Instanz zu einem beendeten Prozess
     *
     * @param businessKey businessKey des Workflows
     * @throws WitaBpmException falls es den Prozess nicht gegeben hat
     */
    HistoricProcessInstance retrieveHistoricProcessInstance(String businessKey);

    /**
     * Ermittelt die historische Prozess-Instanz zu einem beendeten Prozess
     *
     * @param businessKey businessKey des Workflows
     * @return {@code null} falls es den Prozess nicht gibt
     */
    HistoricProcessInstance retrieveHistoricProcessInstanceIfExists(String businessKey);

    /**
     * Ermittelt den Zustand eines aktiven oder beendeten Workflows.
     *
     * @param businessKey businessKey der Prozess-Instanz
     */
    String getWorkflowState(String businessKey);

    /**
     * Ueberprueft, ob ein Workflow bereits beendet wurde
     *
     * @param processInstance Prozess-Instanze des zu ueberpruefenden Workflows
     */
    boolean isProcessInstanceFinished(ProcessInstance processInstance);

    /**
     * Ueberprueft, ob ein Workflow bereits beendet wurde
     *
     * @param businessKey businessKey der Prozess-Instanz
     */
    boolean isProcessInstanceFinished(String businessKey);

    /**
     * Ueberprueft, ob der Prozess zu dem angegebenen Objekt noch aktiv ist.
     *
     * @return {@code true}, wenn der zugehoerige Prozess noch aktiv ist, sonst {@code false}
     */
    boolean isProcessInstanceAlive(String businessKey);

    /**
     * Ermittelt Activity-historie (ausgefuehrte Activities) sortiert nach Start-Zeit
     */
    List<HistoricActivityInstance> createActivityHistory(String businessKey);

    /**
     * Ermittelt Activity-historie (ausgefuehrte Activities) sortiert nach Start-Zeit
     */
    List<HistoricActivityInstance> createActivityHistory(ProcessInstance processInstance);

    /**
     * Ermittelt Activity-historie (ausgefuehrte Activities) sortiert nach Start-Zeit
     */
    List<HistoricActivityInstance> createActivityHistoryByStartTime(String businessKey);

    /**
     * Ermittelt Activity-historie (ausgefuehrte Activities) sortiert nach Start-Zeit
     */
    List<HistoricActivityInstance> createActivityHistoryByStartTime(ProcessInstance pi);

    /**
     * Ermittelt Activity-historie (ausgefuehrte Activities) sortiert nach Start-Zeit
     */
    HistoricActivityInstance retrieveCurrentActivityInstance(ProcessInstance processInstance);

    /**
     * Setzt eine Instanz aus dem Error-Zustand zurueck
     *
     * @param businessKey
     * @param newState
     * @throws WitaUserException, wenn der Zustand nicht gesetzt werden konnte (z.B. weil die Instanz nicht im
     *                            Error-Zustand war oder der neue Zustand nicht zulaessig ist)
     * @throws WitaBpmException,  wenn die Instanz nicht gefunden werden konnte
     */
    void resetErrorState(String businessKey, CommonWorkflowService.WorkflowReentryState newState);

    /**
     * Liefert alle processDefinitionen von Activity zurueck
     */
    Map<String, ProcessDefinition> getProcessDefinitionsById();

    /**
     * Holt sich die Workflow Variablen einer Execution
     *
     * @param executionId ExecutionId (processInstance.getId())
     */
    Map<String, Object> getVariablesForExecution(String executionId);

    /**
     * Alle Usertasks, die zur Processinstance mit dem BusinessKey businessKey gehoeren, werden dem Hurrican user
     * zugewiesen.
     *
     * @param businessKey fachlicher Schluessel der Prozess-Instanz der geclaimt werden soll
     * @param user        Hurrican-Login-Name falls ein Benutzer den Task uebernimmt oder {@code null} falls der Task
     *                    freigegeben werden soll
     */
    void claimUsertasks(String businessKey, AKUser user);

    /**
     * Setzt eine Instanz manuell in den Error Zustand (inkl. Angabe eines Grundes)
     */
    void setErrorState(String businessKey, AKUser user, String errorReason);

    /**
     * Setzt eine Instanz manuell in den Error Zustand
     */
    void setErrorState(String businessKey, String errorMsg);
}
