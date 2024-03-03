/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.10.2011 14:23:08
 */
package de.mnet.wita.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.TalOrderWorkflowService;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.MeldungsType;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.builder.StornoBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungKundeBuilder;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldungKunde;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.model.ResendableMessage;
import de.mnet.wita.model.WorkflowInstanceDto;
import de.mnet.wita.service.WitaAdminService;
import de.mnet.wita.service.WitaReceiveMessageService;
import de.mnet.wita.service.WitaSendMessageService;

@Test(groups = BaseTest.UNIT)
public class WitaAdminServiceImplTest extends BaseTest {

    @InjectMocks
    private WitaAdminService witaAdminService;
    @Mock
    private CommonWorkflowService commonWorkflowService;
    @Mock
    private TalOrderWorkflowService talOrderWorkflowService;
    @Mock
    private MwfEntityDao mwfEntityDao;
    @Mock
    private WitaSendMessageService witaSendMessageService;
    @Mock
    private WitaReceiveMessageService witaReceiveMessageService;

    @BeforeMethod
    public void setup() {
        witaAdminService = new WitaAdminServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    public void testFindAllOpenWorkflowInstances() {
        HistoricActivityInstance historicActivityInstance = mock(HistoricActivityInstance.class);
        when(commonWorkflowService.createActivityHistoryByStartTime(getBusinessKey())).thenReturn(
                ImmutableList.of(historicActivityInstance));

        ProcessInstance processInstance = mock(ProcessInstance.class);
        when(processInstance.getBusinessKey()).thenReturn(getBusinessKey());
        when(commonWorkflowService.retrieveAllOpenProcessInstances()).thenReturn(ImmutableList.of(processInstance));

        HistoricProcessInstance historicProcessInstance = mock(HistoricProcessInstance.class);
        when(historicProcessInstance.getStartTime()).thenReturn(new Date());
        when(commonWorkflowService.retrieveHistoricProcessInstance(getBusinessKey())).thenReturn(
                historicProcessInstance);

        List<WorkflowInstanceDto> workflowInstanceDtos = witaAdminService.findOpenWorkflowInstances(false);
        WorkflowInstanceDto workflowInstanceDto = Iterables.getOnlyElement(workflowInstanceDtos);

        assertThat(workflowInstanceDto.getBusinessKey(), equalTo(getBusinessKey()));
        assertThat(workflowInstanceDto.getLastErrorTaskStart(), nullValue());
        assertThat(workflowInstanceDto.getStart(), equalTo(DateConverterUtils.asLocalDateTime(historicProcessInstance.getStartTime())));
        assertThat(workflowInstanceDto.getTaskBearbeiter(), nullValue());
    }

    @Test(expectedExceptions = WitaUserException.class)
    public void testResendMessageForNotFoundMessage() {
        ResendableMessage message = new ResendableMessage();
        message.setMwfEntityId(1133L);

        when(mwfEntityDao.findById(1133L, Meldung.class)).thenReturn(null);

        witaAdminService.resendMessage(message);
    }

    @Test(expectedExceptions = WitaUserException.class)
    public void testResendMessageForIncomingMessageException() {
        ResendableMessage message = new ResendableMessage();
        message.setMessageType(MeldungsType.ABM);
        message.setMwfEntityId(1144L);

        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();
        when(mwfEntityDao.findById(1144L, Meldung.class)).thenReturn(abm);
        when(witaReceiveMessageService.handleWitaMessage(abm)).thenReturn(false);

        witaAdminService.resendMessage(message);
    }

    public void testResendMessageForIncomingMessage() {
        ResendableMessage message = new ResendableMessage();
        message.setMessageType(MeldungsType.ABM);
        message.setMwfEntityId(1144L);

        AuftragsBestaetigungsMeldung abm = (new AuftragsBestaetigungsMeldungBuilder()).build();
        when(mwfEntityDao.findById(1144L, Meldung.class)).thenReturn(abm);
        when(witaReceiveMessageService.handleWitaMessage(abm)).thenReturn(true);

        witaAdminService.resendMessage(message);

        verify(mwfEntityDao).findById(1144L, Meldung.class);
        verify(witaReceiveMessageService).handleWitaMessage(abm);
        verifyNoMoreInteractions(talOrderWorkflowService, mwfEntityDao, witaReceiveMessageService, witaSendMessageService);
    }

    public void testResendMessageForOutgoingMeldung() {
        ResendableMessage message = new ResendableMessage();
        message.setMessageType(MeldungsType.ERLM_K);
        message.setMwfEntityId(1155L);

        ErledigtMeldungKunde erlmk = (new ErledigtMeldungKundeBuilder()).build();
        when(mwfEntityDao.findById(1155L, Meldung.class)).thenReturn(erlmk);

        witaAdminService.resendMessage(message);

        verify(mwfEntityDao).findById(1155L, Meldung.class);
        verify(witaSendMessageService).sendAndProcessMessage(erlmk);
        verifyNoMoreInteractions(talOrderWorkflowService, mwfEntityDao, witaReceiveMessageService, witaSendMessageService);
    }

    public void testResendMessageForMnetWitaRequest() {
        ResendableMessage message = new ResendableMessage();
        message.setMessageType(MeldungsType.STORNO);
        message.setMwfEntityId(1166L);

        Storno storno = (new StornoBuilder(GeschaeftsfallTyp.BEREITSTELLUNG)).buildValid();
        when(mwfEntityDao.findById(1166L, MnetWitaRequest.class)).thenReturn(storno);

        witaAdminService.resendMessage(message);

        verify(mwfEntityDao).findById(1166L, MnetWitaRequest.class);
        verify(talOrderWorkflowService).sendTvOrStornoRequest(storno);
        verifyNoMoreInteractions(talOrderWorkflowService, mwfEntityDao, witaReceiveMessageService, witaSendMessageService);
    }

    private String getBusinessKey() {
        return "key1";
    }

}
