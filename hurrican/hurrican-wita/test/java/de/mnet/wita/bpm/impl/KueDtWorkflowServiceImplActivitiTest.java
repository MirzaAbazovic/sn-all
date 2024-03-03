/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WorkflowTaskName.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.google.common.collect.ImmutableList;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = BaseTest.SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml", })
public class KueDtWorkflowServiceImplActivitiTest extends AbstractActivitiBaseTest {

    private static final long ERLM_ID = 123L;

    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private WitaUsertaskService witaUsertaskService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WorkflowTaskValidationService workflowTaskValidationService;

    private KueDtWorkflowServiceImpl underTest;
    private ErledigtMeldung erlm;
    private EntgeltMeldung entm;

    @BeforeMethod
    public void setUp() {
        Mockito.reset(mwfEntityDao, witaUsertaskService, carrierService, workflowTaskValidationService);
        underTest = new KueDtWorkflowServiceImpl();
        underTest.commonWorkflowService = commonWorkflowService;
        underTest.mwfEntityDao = mwfEntityDao;

        erlm = new ErledigtMeldungBuilder().withGeschaeftsfallTyp(KUENDIGUNG_TELEKOM).build();
        entm = new EntgeltMeldungBuilder().withGeschaeftsfallTyp(KUENDIGUNG_TELEKOM).build();

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ErledigtMeldung storedErlm = (ErledigtMeldung) invocation.getArguments()[0];
                storedErlm.setId(ERLM_ID);
                return null;
            }
        }).when(mwfEntityDao).store(erlm);

        when(mwfEntityDao.findById(ERLM_ID, ErledigtMeldung.class)).thenReturn(erlm);
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    public void testCompleteWorkflow() throws Exception {
        Carrierbestellung cb = mock(Carrierbestellung.class);
        when(carrierService.findCBsByNotExactVertragsnummer(erlm.getVertragsNummer())).thenReturn(ImmutableList.of(cb));

        sendErlm();
        sendEntm();
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    public void testWithoutCb() throws Exception {
        when(carrierService.findCBsByNotExactVertragsnummer(erlm.getVertragsNummer())).thenReturn(
                ImmutableList.<Carrierbestellung>of());
        underTest.newProcessInstance(erlm);
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    public void testResetWorkflowErrorOnErlm() {
        when(workflowTaskValidationService.validateMwfInput(erlm)).thenReturn("anyerror");
        sendErlm(WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(erlm.getBusinessKey(),
                CommonWorkflowService.WorkflowReentryState.WAIT_FOR_ENTM);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM);
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    public void testHandleSecondErlmOnOpenWorkflow() throws Exception {
        sendErlm();
        verifyNoMoreInteractions(witaUsertaskService);
        reset(mwfEntityDao);

        sendErlm(WORKFLOW_ERROR);
        verifyNoMoreInteractions(witaUsertaskService);
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    @Test(expectedExceptions = WitaBpmException.class)
    public void testHandleSecondErlmOnClosedWorkflow() throws Exception {
        sendErlm();
        sendEntm();
        verifyNoMoreInteractions(witaUsertaskService);

        sendErlm();
    }

    @Deployment(resources = "de/mnet/wita/v1/bpm/KueDT.bpmn20.xml")
    @Test(expectedExceptions = WitaUserException.class)
    public void testInvalidResetWorkflowErrorOnErlm() {
        when(workflowTaskValidationService.validateMwfInput(erlm)).thenReturn("anyerror");
        sendErlm(WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(erlm.getBusinessKey(),
                CommonWorkflowService.WorkflowReentryState.WAIT_FOR_RUEMPV);
    }

    private void sendErlm(WorkflowTaskName expected) {
        ProcessInstance processInstance = underTest.newProcessInstance(erlm);
        assertThat(processInstance, notNullValue());
        assertProcessInstanceInState(expected);
        verify(mwfEntityDao).store(erlm);

        if (!WORKFLOW_ERROR.equals(expected)) {
            verify(witaUsertaskService).createKueDtUserTask(erlm);
        }
    }

    private void sendErlm() {
        sendErlm(WAIT_FOR_ENTM);
    }

    private void sendEntm() {
        underTest.handleWitaMessage(entm);
        // ENTM should close the workflow
        assertTrue(commonWorkflowService.isProcessInstanceFinished(erlm.getBusinessKey()));
        verify(mwfEntityDao).store(erlm);
    }

    private void assertProcessInstanceInState(WorkflowTaskName state) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(erlm.getBusinessKey());
        assertProcessInstanceInState(pi, state, erlm.getBusinessKey());
    }

}
