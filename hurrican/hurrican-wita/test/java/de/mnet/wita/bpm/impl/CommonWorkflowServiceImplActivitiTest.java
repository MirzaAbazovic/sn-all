/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.08.2011 11:26:44
 */
package de.mnet.wita.bpm.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableMap;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.TeqMeldungsCode;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.message.Quittung;

/**
 * Service Test für den CommonWorkflowService
 */
@Test(groups = BaseTest.SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml", })
public class CommonWorkflowServiceImplActivitiTest extends AbstractActivitiBaseTest {

    private ProcessInstance pi, subPi;

    public class IsRunningProcessMatcher extends BaseMatcher<ProcessInstance> {

        @Override
        public boolean matches(Object item) {
            ProcessInstance pi = (ProcessInstance) item;
            return activitiRuntimeService.createProcessInstanceQuery().processInstanceId(pi.getProcessInstanceId())
                    .singleResult() != null;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("was ").appendValue("running");
        }

    }

    public IsRunningProcessMatcher running() {
        return new IsRunningProcessMatcher();
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void processInstanceShouldBeStarted() throws Exception {
        pi = activitiRuntimeService.startProcessInstanceByKey("Caller", "12345");
        assertNotNull(pi);
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void subProcessInstanceShouldBeRetrieved() throws Exception {
        pi = activitiRuntimeService.startProcessInstanceByKey("Caller", "12345");

        subPi = commonWorkflowService.retrieveProcessInstance("12345");

        assertNotNull(subPi);
        assertThat(pi, not(subPi));
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void deleteSubProcessInstanceShouldDeleteProcess() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        commonWorkflowService.deleteProcessInstance(subPi);

        assertThat(pi, not(running()));

    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void deleteProcessInstanceShouldDeleteSubProcess() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        commonWorkflowService.deleteProcessInstance(pi);

        assertThat(subPi, not(running()));
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void shouldSignalSubProcess() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        signal();

        assertThat(pi, running());
        assertThat(subPi, not(running()));
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void shouldSignalProcess() throws Exception {
        shouldSignalSubProcess();

        signal();

        assertThat(pi, not(running()));
        assertThat(subPi, not(running()));
    }

    private void signal() {
        commonWorkflowService.handleWitaMessage(pi, ImmutableMap.<String, Object>of("parameter", "OK"));
    }

    private void signalFailure() {
        commonWorkflowService.handleWitaMessage(pi, ImmutableMap.<String, Object>of("parameter", "BAD"));
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void shouldRetrieveSubProcessByBusinessKey() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        ProcessInstance foundSubProcess = commonWorkflowService.retrieveProcessInstance(pi.getBusinessKey());

        assertEquals(subPi.getProcessInstanceId(), foundSubProcess.getProcessInstanceId());
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void shouldRetrieveSubProcessIfExists() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        ProcessInstance foundSubProcess = commonWorkflowService.retrieveProcessInstanceIfExists(pi.getBusinessKey());

        assertEquals(subPi.getProcessInstanceId(), foundSubProcess.getProcessInstanceId());
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void shouldRetrieveSubProcessIfNotExists() throws Exception {
        ProcessInstance foundSubProcess = commonWorkflowService.retrieveProcessInstanceIfExists("1232446");

        assertNull(foundSubProcess);
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void getWorkflowStateShouldUseSubProcess() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        String workflowState = commonWorkflowService.getWorkflowState(pi.getBusinessKey());

        assertEquals(workflowState, "wait");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void getWorkflowStateShouldUseProcess() throws Exception {
        shouldSignalSubProcess();

        String workflowState = commonWorkflowService.getWorkflowState(pi.getBusinessKey());

        assertEquals(workflowState, "waitForSignal");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void getWorkflowStateShouldFindHistoricProcesses() throws Exception {
        shouldSignalProcess();

        String workflowState = commonWorkflowService.getWorkflowState(pi.getBusinessKey());

        assertEquals(workflowState, "end");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void isProcessInstanceFinishedShouldWorkForSubProcess() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        assertFalse(commonWorkflowService.isProcessInstanceFinished(pi.getBusinessKey()));
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void errorStateShouldWork() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        signalFailure();

        assertEquals(commonWorkflowService.getWorkflowState(pi.getBusinessKey()), "workflowError");
    }

    @Deployment(resources = { "de/mnet/wita/bpm/CallActivityTest.bpmn20.xml" })
    public void deleteHistoricProcessInstancesShouldDeleteRunningInstance() throws Exception {
        subProcessInstanceShouldBeRetrieved();

        commonWorkflowService.deleteHistoricProcessInstance(pi.getBusinessKey());

        assertThat(pi, not(running()));
        assertThat(subPi, not(running()));
        try {
            commonWorkflowService.retrieveHistoricProcessInstance(pi.getBusinessKey());
        }
        catch (WitaBpmException e) {
            return;
        }
        fail();
    }

    @DataProvider
    public Object[][] dataProviderWaitAndProcessTeq() {
        // @formatter:off
        Map<String, Object> paramsWithWorkflowErrorFalse = ImmutableMap.<String, Object>of(
                WitaTaskVariables.CB_VORGANG_ID.id, 12123435L,
                WitaTaskVariables.WORKFLOW_ERROR.id, false);
        Map<String, Object> paramsWithWorkflowErrorTrue = ImmutableMap.<String, Object>of(
                WitaTaskVariables.CB_VORGANG_ID.id, 12123434L,
                WitaTaskVariables.WORKFLOW_ERROR.id, true);
        Map<String, Object> paramsWithoutWorkflowError = ImmutableMap.<String, Object>of(
                WitaTaskVariables.CB_VORGANG_ID.id, 12123436L);
        // @formatter:on

        Quittung positiveTeq = new Quittung(TeqMeldungsCode.OK);
        Quittung negativeTeq = new Quittung(TeqMeldungsCode.UNEXPECTED_ERROR);

        // @formatter:off
        return new Object[][] {
                { "ProcessTeq01", positiveTeq, paramsWithWorkflowErrorFalse, "endTeq" },
                { "ProcessTeq02", positiveTeq, paramsWithWorkflowErrorTrue,  "error" },
                { "ProcessTeq03", positiveTeq, paramsWithoutWorkflowError,   "endTeq" },

                { "ProcessTeq11", negativeTeq, paramsWithWorkflowErrorFalse, "error" },
                { "ProcessTeq12", negativeTeq, paramsWithWorkflowErrorTrue,  "error" },
                { "ProcessTeq13", negativeTeq, paramsWithoutWorkflowError,   "error" },
        };
        // @formatter:on
    }
}
