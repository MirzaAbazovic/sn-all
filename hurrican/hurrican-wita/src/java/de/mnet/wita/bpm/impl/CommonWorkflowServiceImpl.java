/*
# * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2011 10:12:13
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WitaTaskVariables.*;

import java.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.MeldungsType;

@CcTxRequired
public class CommonWorkflowServiceImpl implements CommonWorkflowService {

    private static final Logger LOGGER = Logger.getLogger(CommonWorkflowServiceImpl.class);

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Override
    public boolean deleteProcessInstance(ProcessInstance processInstance) {
        if ((processInstance != null) && (processInstance.getId() != null)) {
            try {
                ProcessInstance processInstanceToDelete = getSuperProcessInstance(processInstance);
                runtimeService.deleteProcessInstance(processInstanceToDelete.getId(), null);
                ProcessInstance stillExistingPi = runtimeService.createProcessInstanceQuery()
                        .processInstanceId(processInstance.getId()).singleResult();
                return (stillExistingPi == null);
            }
            catch (ActivitiException e) {
                throw new WitaBpmException("Error deleting ProcessInstance: " + e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public void handleOutgoingWitaMessage(String businessKey, Map<String, Object> params) {
        String workflowState = getWorkflowState(businessKey);
        if (WorkflowTaskName.WORKFLOW_ERROR.id.equals(workflowState)) {
            throw new WitaBpmException("WITA-Workflow mit dem Business-Key: " + businessKey
                    + " befindet sich im Fehlerzustand. Die Nachricht konnte nicht gesendet werden."
                    + " Der Admistrator wird sich um das Problem kuemmern, bitte versuchen Sie es spaeter nochmals.");
        }
        handleWitaMessage(businessKey, params);
    }

    @Override
    public void handleWitaMessage(String businessKey, Map<String, Object> params) {
        ProcessInstance pi = retrieveProcessInstance(businessKey);
        handleWitaMessage(pi, params);
    }

    @Override
    public void handleWitaMessage(ProcessInstance processInstance, Map<String, Object> params) {
        // When handling a wita message we have to support both the new (no TEQ) and old (wait for TEQ) workflows
        // If the wita message corresponds to a workflow instance created using the old workflow definition, we have to
        // check whether it's waiting for a TEQ (child workflow) and simulate a TEQ when necessary.
        // Afterwards the parent workflow can be signaled as normal
        ProcessInstance parentProcessInstance = getSuperProcessInstance(processInstance);

        signalProcessInstanceIfWaitingForTeq(parentProcessInstance);

        // Signal the active process instance. The active process instance should never be the child(TEQ) process instance!
        ProcessInstance activeProcessInstance = getSubProcessInstance(parentProcessInstance);
        runtimeService.setVariables(activeProcessInstance.getId(), params);
        runtimeService.signal(activeProcessInstance.getId());

        // Check after processing message if workflow instance is in waiting for TEQ state. This can happen after
        // sending out a TV or STORNO request
        signalProcessInstanceIfWaitingForTeq(parentProcessInstance);
    }

    @Override
    public ProcessInstance newProcessInstance(Workflow workflow, String businessKey, Map<String, Object> params) {
        // WORKFLOW_ERROR_RESET_STATE muss gesetzt sein, um Activiti-Fehler zu vermeiden! Fehler taucht auf, wenn das
        // signal im Workflow Error nicht durch resetErrorState, sondern z.B. durch eine weitere Nachricht kommt

        Map<String, Object> defaultParams = new HashMap<>();
        defaultParams.put(WitaTaskVariables.WORKFLOW_ERROR.id, false);
        defaultParams.put(WitaTaskVariables.WORKFLOW_ERROR_RESET_STATE.id, "");

        Map<String, Object> taskParams = Maps.newHashMap();
        taskParams.putAll(defaultParams);
        if (params != null) {
            taskParams.putAll(params);
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(workflow.processDefinitionKey, businessKey, taskParams);
        signalProcessInstanceIfWaitingForTeq(processInstance);
        return processInstance;
    }

    @Override
    public ProcessInstance retrieveProcessInstance(String businessKey) {
        Preconditions.checkNotNull(businessKey, "Es wurde kein Business Key angegeben.");
        ProcessInstance pi = retrieveProcessInstanceIfExists(businessKey);
        if (pi == null) {
            throw new WitaBpmException("Konnte keinen aktiven WITA-Workflow mit dem Business-Key: " + businessKey
                    + " finden. Wahrscheinlich wurde der Workflow bereits abgeschlossen oder abgebrochen.");
        }
        return pi;
    }

    @Override
    public ProcessInstance retrieveProcessInstanceIfExists(String businessKey) {
        return getSubProcessInstance(runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey).singleResult());
    }

    @Override
    public boolean isProcessInstanceFinished(ProcessInstance processInstance) {
        Preconditions.checkNotNull(processInstance);
        ProcessInstance piWithBusinessKey = getSuperProcessInstance(processInstance);
        return isProcessInstanceFinished(piWithBusinessKey.getBusinessKey());
    }

    @Override
    public boolean isProcessInstanceFinished(String businessKey) {
        HistoricProcessInstance hpi = retrieveHistoricProcessInstance(businessKey);
        return hpi.getEndTime() != null;
    }

    @Override
    public String getWorkflowState(String businessKey) {
        ExecutionEntity pi = (ExecutionEntity) retrieveProcessInstanceIfExists(businessKey);
        if (pi != null) {
            return pi.getActivityId();
        }
        HistoricProcessInstanceQuery histQuery = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey);
        HistoricProcessInstance hpi = histQuery.singleResult();
        return hpi.getEndActivityId();
    }

    @Override
    public HistoricProcessInstance retrieveHistoricProcessInstance(String businessKey) {
        HistoricProcessInstance hpi = retrieveHistoricProcessInstanceIfExists(businessKey);
        if (hpi == null) {
            throw new WitaBpmException("Die Workflow Instanz fuer BusinessKey: " + businessKey + " wurde nie gestartet");
        }
        return hpi;
    }

    @Override
    public HistoricProcessInstance retrieveHistoricProcessInstanceIfExists(String businessKey) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey);
        return query.singleResult();
    }

    @Override
    public void deleteHistoricProcessInstance(String businessKey) {
        HistoricProcessInstance hpi = retrieveHistoricProcessInstance(businessKey);
        if (!isProcessInstanceFinished(businessKey)) {
            deleteProcessInstance(retrieveProcessInstance(businessKey));
        }
        historyService.deleteHistoricProcessInstance(hpi.getId());
    }

    /**
     * If a subProcess is active, then return the processInstance of the subProcess, if not, return the given
     * processInstance
     */
    private ProcessInstance getSubProcessInstance(ProcessInstance pi) {
        if (pi == null) {
            return null;
        }
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().superProcessInstanceId(
                pi.getProcessInstanceId());
        return findOrDefault(query, pi);
    }

    /**
     * If pi is a subProcess, then return the processInstance the super process, if not, return the given
     * processInstance
     */
    private ProcessInstance getSuperProcessInstance(ProcessInstance pi) {
        if (pi == null) {
            return null;
        }
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().subProcessInstanceId(
                pi.getProcessInstanceId());
        return findOrDefault(query, pi);
    }

    private ProcessInstance findOrDefault(ProcessInstanceQuery query, ProcessInstance defaultValue) {
        ProcessInstance superProcess = query.singleResult();
        if (superProcess != null) {
            return superProcess;
        }
        return defaultValue;
    }

    @Override
    public boolean isProcessInstanceAlive(String businessKey) {
        ProcessInstance processInstance;
        try {
            processInstance = retrieveProcessInstance(businessKey);
        }
        catch (WitaBpmException e) {
            return false;
        }
        return ((processInstance != null) && !processInstance.isEnded());
    }

    @Override
    public List<ProcessInstance> retrieveAllOpenProcessInstances() {
        return runtimeService.createProcessInstanceQuery().superProcessInstanceId(null).list();
    }

    @Override
    public Collection<ProcessInstance> retrieveFaultedProcessInstances() {
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey(WorkflowTaskName.WORKFLOW_ERROR.id).list();
        List<ProcessInstance> faultedProcessInstances = new ArrayList<>();

        final Map<String, Task> tasksWithError = new HashMap<>();
        for (Task task : tasks) {
            ProcessInstance pi = getSuperProcessInstance(runtimeService.createProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId()).singleResult());
            if (!tasksWithError.containsKey(task.getProcessInstanceId()) && pi != null && !pi.isEnded()) {
                // ProcessInstances koennen mehrere Error-Tasks haben; deshalb ueber Map sichergestellt, dass
                // jede ProcessInstance nur einmal ermittelt wird
                tasksWithError.put(pi.getProcessInstanceId(), task);

                faultedProcessInstances.add(pi);
            }
        }
        return faultedProcessInstances;
    }

    @Override
    public Map<String, Object> getVariablesForExecution(String executionId) {
        return runtimeService.getVariables(executionId);
    }

    @Override
    public Map<String, ProcessDefinition> getProcessDefinitionsById() {
        Map<String, ProcessDefinition> result = Maps.newHashMap();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : processDefinitions) {
            result.put(processDefinition.getId(), processDefinition);
        }
        return result;
    }

    @Override
    public List<HistoricActivityInstance> createActivityHistory(String businessKey) {
        ProcessInstance pi = retrieveProcessInstance(businessKey);
        return createActivityHistory(pi);
    }

    @Override
    public List<HistoricActivityInstance> createActivityHistoryByStartTime(String businessKey) {
        ProcessInstance pi = retrieveProcessInstance(businessKey);
        return createActivityHistoryByStartTime(pi);
    }

    @Override
    public List<HistoricActivityInstance> createActivityHistory(ProcessInstance pi) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(pi.getId()).orderByHistoricActivityInstanceId().asc().list();
    }

    @Override
    public List<HistoricActivityInstance> createActivityHistoryByStartTime(ProcessInstance pi) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(pi.getId()).orderByHistoricActivityInstanceStartTime().asc()
                .orderByHistoricActivityInstanceEndTime().asc().list();
    }

    @Override
    public HistoricActivityInstance retrieveCurrentActivityInstance(ProcessInstance pi) {
        return historyService.createHistoricActivityInstanceQuery().processInstanceId(pi.getProcessInstanceId())
                .unfinished().singleResult();
    }

    // setter for testing
    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void resetErrorState(String businessKey, CommonWorkflowService.WorkflowReentryState newState) {
        String actualState = getWorkflowState(businessKey);
        if (!actualState.equals(WorkflowTaskName.WORKFLOW_ERROR.id)) {
            throw new WitaUserException("Konnte Zustand nicht setzen. Workflow nicht im Fehlerzustand");
        }

        ProcessInstance processInstance = retrieveProcessInstance(businessKey);
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR_RESET_STATE.id,
                newState.toString());
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR.id, false);
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR_MESSAGE.id, null);

        // Pro Durchlauf des Workflow-Error Zustand wird ein UserTask angelegt, diese werden alle abgeschlossen
        completeAllUserTasks(processInstance);

        checkNewStateSet(businessKey, newState);

        if (WorkflowReentryState.CLOSED != newState) {
            // Reset value of Error State after actual state is set
            runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR_RESET_STATE.id, null);
        }
    }

    void completeAllUserTasks(ProcessInstance processInstance) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .list();
        if (tasks.isEmpty()) {
            throw new WitaBpmException("Usertask zu businessKey " + processInstance.getBusinessKey()
                    + " nicht gefunden!");
        }

        Iterator<Task> it = tasks.iterator();

        // Complete nur auf den ersten Task, alle anderen manuell loeschen
        taskService.complete(it.next().getId());

        while (it.hasNext()) {
            taskService.deleteTask(it.next().getId());
        }
    }

    void checkNewStateSet(String businessKey, CommonWorkflowService.WorkflowReentryState newState) {
        if (WorkflowReentryState.CLOSED != newState) {
            String actualState = getWorkflowState(businessKey);
            if (!actualState.equals(newState.newWorkflowState)) {
                throw new WitaUserException("Konnte Zustand nicht setzen. Zielzustand=" + newState.newWorkflowState
                        + ", tatsaechlicher Zustand=" + actualState);
            }
        }
        else {
            if (isProcessInstanceAlive(businessKey)) {
                throw new WitaUserException("Konnte Workflow nicht schliessen.");
            }
        }
    }

    @Override
    public void claimUsertasks(String businessKey, AKUser user) {
        ProcessInstance pi = retrieveProcessInstance(businessKey);

        // Pro Durchlauf des Workflow-Error Zustand wird ein UserTask angelegt, diese werden alle geclaimt!
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
        if (tasks.isEmpty()) {
            throw new WitaBpmException("Usertask zu businessKey " + businessKey + " nicht gefunden!");
        }
        String userId = null;
        // Falls der User null ist, wird der Task wieder freigegeben
        if (user != null) {
            userId = user.getLoginName();
        }
        for (Task task : tasks) {
            taskService.claim(task.getId(), userId);
        }
    }

    @Override
    public void setErrorState(String businessKey, AKUser user, String errorReason) {
        String errorMsg = String.format("Workflow manuell in Error gesetzt; Grund: %s; Bearbeiter: %s; ",
                errorReason, (user != null) ? user.getLoginName() : "unknown");
        setErrorState(businessKey, errorMsg);
    }

    @Override
    public void setErrorState(String businessKey, String errorMsg) {
        String actualState = getWorkflowState(businessKey);
        if (actualState.equals(WorkflowTaskName.WORKFLOW_ERROR.id)) {
            throw new WitaUserException("Konnte Error Zustand nicht setzen. Workflow schon im Fehlerzustand.");
        }

        ProcessInstance processInstance = retrieveProcessInstance(businessKey);
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR_RESET_STATE.id, "");
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR.id, true);
        runtimeService.setVariable(processInstance.getId(), WitaTaskVariables.WORKFLOW_ERROR_MESSAGE.id, errorMsg);
        runtimeService.signal(processInstance.getProcessInstanceId());
    }

    public void signalProcessInstanceIfWaitingForTeq(ProcessInstance processInstance) {
        ProcessInstance activeProcessInstance = getSubProcessInstance(processInstance);

        if(activeProcessInstance == null || runtimeService.createProcessInstanceQuery().processInstanceId(activeProcessInstance.getId()).singleResult() == null) {
            // if the process instance is null or is no longer active (because TEQ has already been simulated), then
            // just return silently
            return;
        }
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(activeProcessInstance.getId());
        LOGGER.trace(String.format("Active tasks for processInstance %s - %s", activeProcessInstance.getId(), activeActivityIds));
        for(String activeActivity: activeActivityIds) {
            if(activeActivity.matches("(?i)(waitForTeq)")) {
                LOGGER.info(String.format("Simulating receipt of TEQ for processInstance %s in state %s",activeProcessInstance.getId(), activeActivityIds));
                Map<String, Object> params = ImmutableMap.<String, Object>of(
                        WITA_MESSAGE_TYPE.id,
                        MeldungsType.TEQ.name(),
                        TEQ_MESSAGE_TYPE.id,
                        true
                );
                runtimeService.setVariables(activeProcessInstance.getId(), params);
                runtimeService.signal(activeProcessInstance.getId());
                return;
            }
        }
    }
}
