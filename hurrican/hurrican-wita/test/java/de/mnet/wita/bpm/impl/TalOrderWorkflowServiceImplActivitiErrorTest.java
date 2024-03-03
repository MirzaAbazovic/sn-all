/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WorkflowTaskName.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;

@Test(groups = BaseTest.SERVICE)
public class TalOrderWorkflowServiceImplActivitiErrorTest extends AbstractTalOrderWorkflowServiceActivitiImplTest {

    @Autowired
    private TaskService taskService;

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(expectedExceptions = WitaUserException.class)
    public void testWorkflowErrorOnAggregate() throws FindException {
        doThrow(new RuntimeException()).when(lineOrderService).sendToWita(any(WitaMessage.class));

        startProcessInstance();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(expectedExceptions = WitaBpmException.class)
    public void testStornoToWorkflowInErrorState() throws FindException {
        startProcessInstance();
        sendAbm(WORKFLOW_ERROR);
        sendStorno();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(expectedExceptions = WitaBpmException.class)
    public void testTvToWorkflowInErrorState() throws FindException {
        startProcessInstance();
        sendAbm(WORKFLOW_ERROR);
        sendTv();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(expectedExceptions = WitaBpmException.class)
    public void testErlmKToWorkflowInErrorState() throws FindException {
        startProcessInstance();
        sendAbm(WORKFLOW_ERROR);

        sendErlmk();
        verify(lineOrderService, Mockito.atLeast(2)).sendToWita(any(WitaMessage.class));
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testExceptionOnNotSendableStorno() throws FindException {
        startProcessInstance();
        sendQeb();
        sendStorno(WORKFLOW_ERROR);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testExceptionOnNotSendableTv() throws FindException {
        startProcessInstance();
        sendQeb();
        sendTv(WORKFLOW_ERROR);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testResetWorkflowErrorOnAbm() throws FindException {
        startProcessInstance();

        sendAbm(WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.WAIT_FOR_MESSAGE);
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testResetWorkflowErrorOnTv() throws FindException {
        startProcessInstance();
        sendQeb();

        sendTv(WORKFLOW_ERROR);

        reset(mwfEntityDao);
        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.WAIT_FOR_MESSAGE);
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);
        sendTv(WAIT_FOR_MESSAGE);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testResetWorkflowErrorOnStorno() throws FindException {
        startProcessInstance();
        sendQeb();

        sendStorno(WORKFLOW_ERROR);

        reset(mwfEntityDao);
        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.WAIT_FOR_MESSAGE);
        assertProcessInstanceInState(WAIT_FOR_MESSAGE);
        sendStorno(WAIT_FOR_MESSAGE);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testResetWorkflowErrorOnAbbm() throws FindException {
        startProcessInstance();

        when(workflowTaskValidationService.validateMwfInput(any(AbbruchMeldung.class))).thenReturn("error");
        sendAbbm(AenderungsKennzeichen.STANDARD, WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.CLOSED);
        assertProcessInstanceFinished();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testResetWorkflowErrorOnErlm() throws FindException {
        startProcessInstance();
        sendQeb();
        sendAbm();

        when(workflowTaskValidationService.validateMwfInput(any(ErledigtMeldung.class))).thenReturn("error");
        sendErlm(AenderungsKennzeichen.STANDARD, WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.WAIT_FOR_ENTM);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testCreateActivitiHistory() throws FindException {
        startProcessInstance();
        sendQeb();
        sendAbm();

        List<HistoricActivityInstance> activityHistory = commonWorkflowService.createActivityHistory(cbVorgang
                .getBusinessKey());

        List<String> activityHistoryIds = Lists.transform(activityHistory,
                new Function<HistoricActivityInstance, String>() {

                    @Override
                    public String apply(HistoricActivityInstance input) {
                        return input.getActivityId();
                    }
                }
        );

        List<String> expectedActivities = ImmutableList.of("aggregate", "waitForMessage",
                "processMessage", "waitForMessage", "processMessage", "waitForMessage");
        assertThat(activityHistoryIds, equalTo(expectedActivities));

    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testClaimAndCompleteForMultipleWorkflowErrorStates() throws FindException {
        startProcessInstance();

        sendAbm(WORKFLOW_ERROR);
        sendAbm(WORKFLOW_ERROR);

        AKUser user = mock(AKUser.class);
        when(user.getLoginName()).thenReturn("foo");

        assertTasksAssigned(2, false);

        commonWorkflowService.claimUsertasks(cbVorgang.getBusinessKey(), user);

        assertTasksAssigned(2, true);

        commonWorkflowService.resetErrorState(cbVorgang.getCarrierRefNr(), WorkflowReentryState.WAIT_FOR_MESSAGE);

        assertTasksAssigned(0, false);

    }

    private void assertTasksAssigned(int numberOfTasks, boolean assigned) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(cbVorgang.getBusinessKey());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).list();
        Iterator<Task> it = tasks.iterator();
        for (int i = 0; i < numberOfTasks; i++) {
            assertThat(it.next().getAssignee(), assigned ? notNullValue() : nullValue());
        }
        assertFalse(it.hasNext());
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testErrorZustandSetzenWaitForMessage() throws FindException {
        startProcessInstance();
        commonWorkflowService.setErrorState(cbVorgang.getBusinessKey(), "set error manually while testing");
        assertProcessInstanceInState(WORKFLOW_ERROR);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testErrorZustandSetzenWaitForTeq() throws FindException {
        startProcessInstance();
        commonWorkflowService.setErrorState(cbVorgang.getBusinessKey(), "set error manually while testing");
        assertProcessInstanceInState(WORKFLOW_ERROR);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testErrorZustandSetzenWaitForENTM() throws FindException {
        startProcessInstance();
        sendAbm();
        sendErlm();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM);
        commonWorkflowService.setErrorState(cbVorgang.getBusinessKey(), "set error manually while testing");
        assertProcessInstanceInState(WORKFLOW_ERROR);
    }

    @Test(expectedExceptions = WitaBpmException.class)
    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    public void testTvOnClosedWorkflow() throws FindException {
        startProcessInstance();
        sendAbbm();
        sendTv();
    }
}
