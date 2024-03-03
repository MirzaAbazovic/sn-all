/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 08:56:15
 */
package de.mnet.wita.service.impl;

import java.time.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.annotation.CcTxRequired;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.impl.Workflow;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.IncomingMessage;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.OutgoingMeldung;
import de.mnet.wita.model.ResendableMessage;
import de.mnet.wita.model.WorkflowInstanceDetailsDto;
import de.mnet.wita.model.WorkflowInstanceDto;
import de.mnet.wita.service.WitaAdminService;
import de.mnet.wita.service.WitaReceiveMessageService;
import de.mnet.wita.service.WitaSendMessageService;

/**
 * Service fuer Wita-AdminTools
 */
@CcTxRequired
public class WitaAdminServiceImpl implements WitaAdminService {

    @Autowired
    private CommonWorkflowService commonWorkflowService;
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private TalOrderWorkflowService talOrderWorkflowService;
    @Autowired
    private WitaSendMessageService witaSendMessageService;
    @Autowired
    private WitaReceiveMessageService witaReceiveMessageService;

    @Override
    public List<WorkflowInstanceDto> findOpenWorkflowInstances(boolean onlyWorkflowErrors) {
        Collection<ProcessInstance> processInstances;
        if (onlyWorkflowErrors) {
            processInstances = commonWorkflowService.retrieveFaultedProcessInstances();
        }
        else {
            processInstances = commonWorkflowService.retrieveAllOpenProcessInstances();
        }

        return getWorkflowInstances(processInstances, null);
    }

    @Override
    public WorkflowInstanceDto getWorkflowInstance(String businessKey) {
        ProcessInstance processInstance = commonWorkflowService.retrieveProcessInstanceIfExists(businessKey);
        if (processInstance == null) {
            return null;
        }

        List<WorkflowInstanceDto> errorTasksForBusinessKey = getWorkflowInstances(ImmutableList.of(processInstance),
                businessKey);
        return Iterables.getOnlyElement(errorTasksForBusinessKey);
    }

    @Override
    public WorkflowInstanceDetailsDto getWorkflowInstanceDetails(String businessKey) {
        ProcessInstance processInstance = commonWorkflowService.retrieveProcessInstance(businessKey);
        if (processInstance == null) {
            return null;
        }

        List<HistoricActivityInstance> taskHistory = commonWorkflowService
                .createActivityHistoryByStartTime(businessKey);

        WorkflowInstanceDetailsDto details = new WorkflowInstanceDetailsDto(businessKey, taskHistory);
        details.setWorkflowVariables(commonWorkflowService.getVariablesForExecution(processInstance
                .getProcessInstanceId()));
        return details;
    }

    private List<WorkflowInstanceDto> getWorkflowInstances(Collection<ProcessInstance> processInstances,
            String searchedBusinessKey) {
        List<WorkflowInstanceDto> workflowInstances = new ArrayList<>();

        Map<String, ProcessDefinition> processDefinitions = commonWorkflowService.getProcessDefinitionsById();

        for (ProcessInstance processInstance : processInstances) {
            String businessKey = processInstance.getBusinessKey();
            if ((businessKey == null) && (searchedBusinessKey != null)) {
                businessKey = searchedBusinessKey;
            }
            else if ((businessKey == null)) {
                continue;
            }

            WorkflowInstanceDto workflowInstance = new WorkflowInstanceDto(businessKey);
            workflowInstance.setWorkflow(Workflow.of(processInstance, processDefinitions));

            HistoricActivityInstance currentTask = commonWorkflowService
                    .retrieveCurrentActivityInstance(processInstance);
            if (currentTask != null) {
                workflowInstance.setTaskBearbeiter(currentTask.getAssignee());
                if (WorkflowTaskName.WORKFLOW_ERROR.id.equals(currentTask.getActivityId())) {
                    workflowInstance.setLastErrorTaskStart(currentTask.getStartTime() != null
                            ? DateConverterUtils.asLocalDateTime(currentTask.getStartTime())
                            : LocalDateTime.now());
                }
            }

            HistoricProcessInstance historicProcessInstance = commonWorkflowService
                    .retrieveHistoricProcessInstance(businessKey);
            if (historicProcessInstance != null) {
                workflowInstance.setStart(historicProcessInstance.getStartTime() != null
                        ? DateConverterUtils.asLocalDateTime(historicProcessInstance.getStartTime())
                        : LocalDateTime.now());
            }

            workflowInstances.add(workflowInstance);
        }

        Ordering<WorkflowInstanceDto> errorTaskStart = Ordering.natural().nullsLast()
                .onResultOf(WorkflowInstanceDto.GET_LAST_ERROR_TASK_START);
        Ordering<WorkflowInstanceDto> workflowStart = Ordering.natural().nullsLast()
                .onResultOf(WorkflowInstanceDto.GET_START);
        Collections.sort(workflowInstances, errorTaskStart.compound(workflowStart));

        return workflowInstances;
    }

    @Override
    public void resetErrorState(String businessKey, CommonWorkflowService.WorkflowReentryState newState) {
        commonWorkflowService.resetErrorState(businessKey, newState);
    }

    @Override
    public void claimErrorTask(String businessKey, AKUser user) {
        commonWorkflowService.claimUsertasks(businessKey, user);
    }

    @Override
    public List<ResendableMessage> findAllResenableMessages(String businessKey) {
        List<ResendableMessage> result = Lists.newArrayList();
        List<Meldung<?>> meldungen = mwfEntityDao.findAllMeldungen(businessKey);
        for (Meldung<?> meldung : meldungen) {
            ResendableMessage workflowErrorMeldung = new ResendableMessage();
            workflowErrorMeldung.setBusinessKey(meldung.getExterneAuftragsnummer());
            workflowErrorMeldung.setVersandZeitstempel(DateConverterUtils.asLocalDateTime(meldung.getVersandZeitstempel()));
            workflowErrorMeldung.setMessageType(meldung.getMeldungsTyp());
            workflowErrorMeldung.setMwfEntityId(meldung.getId());

            result.add(workflowErrorMeldung);
        }
        List<MnetWitaRequest> requests = mwfEntityDao.findAllRequests(businessKey);
        for (MnetWitaRequest request : requests) {
            if (request.isAuftrag()) {
                continue;
            }
            ResendableMessage workflowErrorMeldung = new ResendableMessage();
            workflowErrorMeldung.setBusinessKey(request.getExterneAuftragsnummer());
            workflowErrorMeldung.setVersandZeitstempel(DateConverterUtils.asLocalDateTime(request.getSentAt()));
            workflowErrorMeldung.setMessageType(request.getMeldungsTyp());
            workflowErrorMeldung.setMwfEntityId(request.getId());

            result.add(workflowErrorMeldung);
        }
        return result;
    }

    @Override
    public void resendMessage(ResendableMessage message) {
        if (message.getMessageType() == MeldungsType.STORNO || message.getMessageType() == MeldungsType.TV) {
            MnetWitaRequest request = mwfEntityDao.findById(message.getMwfEntityId(), MnetWitaRequest.class);
            talOrderWorkflowService.sendTvOrStornoRequest(request);
        }
        else {
            Meldung<?> meldung = mwfEntityDao.findById(message.getMwfEntityId(), Meldung.class);
            if (meldung instanceof IncomingMessage) {
                boolean success = witaReceiveMessageService.handleWitaMessage((IncomingMessage) meldung);
                if (!success) {
                    throw new WitaUserException("Message konnte nicht erneut an Workflow gesendet werden: " + message);
                }
            }
            else if (meldung instanceof OutgoingMeldung) {
                witaSendMessageService.sendAndProcessMessage(meldung);
            }
            else {
                throw new WitaUserException("Keine gültig Meldung, um diese erneut an Workflow zu senden: " + meldung);
            }
        }
    }

    @Override
    public WorkflowInstanceDto setErrorState(String businessKey, AKUser user, String errorReason) {
        commonWorkflowService.setErrorState(businessKey, user, errorReason);
        WorkflowInstanceDto workflowInstance = getWorkflowInstance(businessKey);
        if (workflowInstance == null) {
            throw new WitaUserException("Workflow konnte nicht in Fehlerzustand gesetzt werden");
        }
        return workflowInstance;

    }

}
