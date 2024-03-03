/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2011 10:14:58
 */
package de.mnet.wita.bpm.impl;

import static de.mnet.wita.bpm.WitaTaskVariables.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wita.bpm.WitaTaskVariables;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.builder.AuftragBuilder;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.TerminVerschiebungBuilder;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.service.WitaConfigService;

/**
 * TestNG Klasse fuer {@link TalOrderWorkflowServiceImpl}
 */
@Test(groups = BaseTest.UNIT)
public class TalOrderWorkflowServiceImplTest extends BaseTest {

    @InjectMocks
    private TalOrderWorkflowServiceImpl cut;
    @Mock
    private RuntimeService runtimeServiceMock;
    @Mock
    private HistoryService historyServiceMock;
    @Mock
    private ProcessInstanceQuery query;
    @Mock
    private HistoricProcessInstanceQuery histQuery;
    @Mock
    private CommonWorkflowServiceImpl commonWorkflowService;
    @Mock
    private MwfEntityDao mwfEntityDao;
    @Mock
    private WitaConfigService witaConfigService;

    @Captor
    private
    ArgumentCaptor<Map<String, Object>> paramCaptor;

    @BeforeMethod
    public void setUp() {
        cut = new TalOrderWorkflowServiceImpl();
        MockitoAnnotations.initMocks(this);

        when(runtimeServiceMock.createProcessInstanceQuery()).thenReturn(query);
        when(historyServiceMock.createHistoricProcessInstanceQuery()).thenReturn(histQuery);
    }

    public void getWorkflowState() {
        WitaCBVorgang cbVorgang = new WitaCBVorgang();

        when(commonWorkflowService.getWorkflowState(cbVorgang.getCarrierRefNr())).thenReturn("Warten");

        String workflowState = commonWorkflowService.getWorkflowState(cbVorgang.getBusinessKey());

        assertEquals(workflowState, "Warten");
    }

    @SuppressWarnings("unchecked")
    public void newProcessInstance() {
        String businessKey = "123";

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(1L);
        cbVorgang.setCarrierRefNr(businessKey);

        when(commonWorkflowService.newProcessInstance(eq(Workflow.TAL_ORDER), eq(businessKey), anyMap()))
                .thenReturn(new ExecutionEntity());

        ProcessInstance processInstance = cut.newProcessInstance(cbVorgang);

        verify(commonWorkflowService).newProcessInstance(eq(Workflow.TAL_ORDER), eq(businessKey), anyMap());

        assertNotNull(processInstance);
    }

    @SuppressWarnings("unchecked")
    public void restartProcessInstance() {
        String businessKey = "123";

        WitaCBVorgang cbVorgang = new WitaCBVorgang();
        cbVorgang.setId(1L);
        cbVorgang.setCarrierRefNr(businessKey);

        when(commonWorkflowService.newProcessInstance(eq(Workflow.TAL_ORDER), eq(businessKey), anyMap())).thenReturn(new ExecutionEntity());

        cut.restartProcessInstance(cbVorgang);

        verify(commonWorkflowService).deleteHistoricProcessInstance(anyString());

        verify(commonWorkflowService, times(1)).newProcessInstance(eq(Workflow.TAL_ORDER), eq(businessKey), paramCaptor.capture());

        Map<String, Object> achievedVariables = paramCaptor.getValue();
        assertThat(achievedVariables, Matchers.<String, Object>hasEntry(WitaTaskVariables.RESTART.id, true));
    }

    @DataProvider
    public Object[][] dataProviderSendRequest() {
        Auftrag auftrag = (new AuftragBuilder(BEREITSTELLUNG)).withId(1L).buildValid();
        TerminVerschiebung tv = (new TerminVerschiebungBuilder(LEISTUNGS_AENDERUNG)).withId(2L).buildValid();
        Storno storno = (new StornoBuilder(RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG)).withId(3L).buildValid();
        return new Object[][] {
                { tv,       true  },
                { storno,   true  },
                { auftrag, false },
        };
    }

    @Test(dataProvider = "dataProviderSendRequest")
    public void testSendRequest(MnetWitaRequest request, boolean isAllowed) {
        request.setSentAt(null);
        try {
            when(witaConfigService.isSendAllowed(request)).thenReturn(isAllowed);
            cut.sendTvOrStornoRequest(request);
        }
        catch (Exception e) {
            if (!isAllowed) {
                assertEquals(e.getMessage(), "request have to be a TV or Storno request.");
            }
            else {
                throw e;
            }
        }

        VerificationMode expected = (isAllowed) ? times(1) : never();
        verify(commonWorkflowService, expected).handleOutgoingWitaMessage(any(String.class), paramCaptor.capture());
        if (isAllowed) {
            Map<String, Object> variables = paramCaptor.getValue();
            assertThat(variables,
                    Matchers.<String, Object>hasEntry(WITA_MESSAGE_TYPE.id, request.getMeldungsTyp().name()));
            assertThat(variables, Matchers.<String, Object>hasEntry(WITA_OUT_MWF_ID.id, request.getId()));
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("NP")
    public void testSendRequestWithoutRequest() throws Throwable {
        cut.sendTvOrStornoRequest(null);
    }
}
