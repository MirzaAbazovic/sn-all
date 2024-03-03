/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 10:14:58
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WitaTaskVariables.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.MeldungsType;

@Test(groups = BaseTest.UNIT)
public class CommonWorkflowServiceImplTest extends BaseTest {

    @InjectMocks
    private CommonWorkflowServiceImpl cut;
    @Mock
    private RuntimeService runtimeServiceMock;
    @Mock
    private HistoryService historyServiceMock;
    @Mock
    private ProcessInstanceQuery query;
    @Mock
    private HistoricProcessInstanceQuery histQuery;
    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    @BeforeMethod
    public void setUp() {
        cut = new CommonWorkflowServiceImpl();
        MockitoAnnotations.initMocks(this);

        when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(query);
        when(historyServiceMock.createHistoricProcessInstanceQuery()).thenReturn(histQuery);
    }

    public void deleteProcessInstanceWithNull() {
        boolean result = cut.deleteProcessInstance(null);
        assertFalse(result);
    }

    public void deleteProcessInstanceWithNullId() {
        boolean result = cut.deleteProcessInstance(new ExecutionEntity());
        assertFalse(result);
    }

    public void deleteProcessInstance() {
        ExecutionEntity pi = new ExecutionEntity();
        pi.setId("123");

        when(query.processInstanceId(pi.getId())).thenReturn(query);
        when(query.subProcessInstanceId(pi.getProcessInstanceId())).thenReturn(query);

        cut.deleteProcessInstance(pi);
        verify(runtimeServiceMock, times(1)).deleteProcessInstance(pi.getId(), null);
    }

    public void testCheckErrorStateClosedSuccess() {
        when(query.processInstanceBusinessKey(anyString())).thenReturn(query);
        cut.checkNewStateSet("123", WorkflowReentryState.CLOSED);
    }

    @Test(expectedExceptions = WitaUserException.class)
    public void testCheckErrorStateClosedFailed() {
        when(query.processInstanceBusinessKey(anyString())).thenReturn(query);
        when(query.superProcessInstanceId(anyString())).thenReturn(query);

        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(query.singleResult()).thenReturn(processInstance);

        cut.checkNewStateSet("123", WorkflowReentryState.CLOSED);
    }

    @DataProvider
    public Object[][] dataProviderErrorStateNotClosedSuccess() {
        return new Object[][] {
                { WorkflowReentryState.WAIT_FOR_ENTM, WorkflowReentryState.WAIT_FOR_ENTM.newWorkflowState, true },
                { WorkflowReentryState.WAIT_FOR_ENTM, "endActiviti", false },
                { WorkflowReentryState.WAIT_FOR_MESSAGE, WorkflowReentryState.WAIT_FOR_MESSAGE.newWorkflowState, true },
                { WorkflowReentryState.WAIT_FOR_MESSAGE, "endActiviti", false },
                { WorkflowReentryState.WAIT_FOR_RUEMPV, WorkflowReentryState.WAIT_FOR_RUEMPV.newWorkflowState, true },
                { WorkflowReentryState.WAIT_FOR_RUEMPV, "endActiviti", false },
                { WorkflowReentryState.WAIT_FOR_ENTMPV, WorkflowReentryState.WAIT_FOR_ENTMPV.newWorkflowState, true },
                { WorkflowReentryState.WAIT_FOR_ENTMPV, "endActiviti", false },
        };
    }

    @Test(dataProvider = "dataProviderErrorStateNotClosedSuccess")
    public void testCheckErrorStateNotClosedSuccess(WorkflowReentryState workflowReentryState, String actualState,
            boolean expectSuccess) {
        when(query.processInstanceBusinessKey(anyString())).thenReturn(query);

        when(histQuery.processInstanceBusinessKey(anyString())).thenReturn(histQuery);
        HistoricProcessInstance historicProcessInstance = mock(HistoricProcessInstance.class);
        when(historicProcessInstance.getEndActivityId()).thenReturn(actualState);
        when(histQuery.singleResult()).thenReturn(historicProcessInstance);

        try {
            cut.checkNewStateSet("123", workflowReentryState);
            if (!expectSuccess) {
                fail(String.format("Expected exception for %s, %s", workflowReentryState.toString(), actualState));
            }
        }
        catch (WitaUserException e) {
            if (expectSuccess) {
                fail(String.format("Unexpected exception for %s, %s", workflowReentryState.toString(), actualState), e);
            }
        }
    }

    @Test
    public void shouldSignalProcessInstanceWaitingForTeq() {
        String pid = "123";
        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(processInstance.getId()).thenReturn(pid);

        when(query.processInstanceId(pid)).thenReturn(query);
        when(query.superProcessInstanceId(anyString())).thenReturn(query);
        when(query.singleResult()).thenReturn(processInstance);
        when(runtimeServiceMock.getActiveActivityIds(pid)).thenReturn(Arrays.asList("waitforteq"));

        cut.signalProcessInstanceIfWaitingForTeq(processInstance);

        verify(runtimeServiceMock).setVariables(eq(pid), captor.capture());
        assertEquals(captor.getValue().size(), 2);
        assertEquals(captor.getValue().get(WITA_MESSAGE_TYPE.id), MeldungsType.TEQ.name());
        assertEquals(captor.getValue().get(TEQ_MESSAGE_TYPE.id), true);
        verify(runtimeServiceMock).signal(pid);
    }

    @Test
    public void shouldNotSignalProcessInstanceAsTeqNotExpected() {
        String pid = "123";
        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(processInstance.getId()).thenReturn(pid);

        when(query.processInstanceId(pid)).thenReturn(query);
        when(query.superProcessInstanceId(anyString())).thenReturn(query);
        when(query.singleResult()).thenReturn(processInstance);
        when(runtimeServiceMock.getActiveActivityIds(pid)).thenReturn(Arrays.asList("something other task"));

        cut.signalProcessInstanceIfWaitingForTeq(processInstance);

        verify(runtimeServiceMock, never()).setVariables(eq(pid), captor.capture());
        verify(runtimeServiceMock, never()).signal(pid);
    }

    @Test
    public void shouldNotSignalProcessAsItsNoLongerActive() {
        String pid = "123";
        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(processInstance.getId()).thenReturn(pid);

        when(query.processInstanceId(pid)).thenReturn(query);
        when(query.superProcessInstanceId(anyString())).thenReturn(query);
        when(query.singleResult()).thenReturn(null);

        cut.signalProcessInstanceIfWaitingForTeq(processInstance);

        verify(runtimeServiceMock,never()).getActiveActivityIds(anyString());
    }
}
