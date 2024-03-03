/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.*;
import javax.validation.*;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang.StringUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.CarrierKennung;
import de.augustakom.hurrican.model.cc.Carrierbestellung;
import de.augustakom.hurrican.model.cc.CarrierbestellungBuilder;
import de.augustakom.hurrican.service.cc.CarrierService;
import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.RuemPvAntwortCode;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.activiti.CanOpenActivitiWorkflow;
import de.mnet.wita.bpm.CommonWorkflowService;
import de.mnet.wita.bpm.CommonWorkflowService.WorkflowReentryState;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.exceptions.WitaUserException;
import de.mnet.wita.integration.LineOrderService;
import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AnkuendigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungPvBuilder;
import de.mnet.wita.message.builder.meldung.VerzoegerungsMeldungPvBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldungPv;
import de.mnet.wita.message.meldung.AnkuendigungsMeldungPv;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldungPv;
import de.mnet.wita.message.meldung.EntgeltMeldungPv;
import de.mnet.wita.message.meldung.ErledigtMeldungPv;
import de.mnet.wita.message.meldung.IncomingPvMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.RueckMeldungPv;
import de.mnet.wita.message.meldung.VerzoegerungsMeldungPv;
import de.mnet.wita.message.meldung.position.MeldungsPosition;
import de.mnet.wita.model.AkmPvUserTask;
import de.mnet.wita.service.WitaConfigService;
import de.mnet.wita.service.WitaUsertaskService;

@Test(groups = BaseTest.SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
})
public class AbgebendPvWorkflowServiceImplActivitiTest extends AbstractActivitiBaseTest {

    private static final String AKM_PV_EXTAUFTRAGSNUMMER = "123";
    private static final Long AKM_PV_ID = 456L;
    private static final String AKM_PV_VERTRAGSNUMMER = "1234567890";
    private static final Long MELDUNG_ID = 1L;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Mocked by spring application context
     */
    @Autowired
    private MwfEntityDao mwfEntityDao;
    @Autowired
    private CBVorgangDAO cbVorgangDAO;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private WitaUsertaskService witaUsertaskService;
    @Autowired
    private WorkflowTaskValidationService workflowTaskValidationService;
    @Autowired
    private WitaConfigService witaConfigService;
    @Autowired
    private LineOrderService lineOrderService;

    private AbgebendPvWorkflowServiceImpl underTest;
    private AnkuendigungsMeldungPv akmPv;
    private CarrierKennung carrierKennung;

    @BeforeMethod
    public void setUp() throws Exception {
        reset(cbVorgangDAO, mwfEntityDao, lineOrderService, witaUsertaskService, workflowTaskValidationService);

        underTest = new AbgebendPvWorkflowServiceImpl();
        underTest.cbVorgangDAO = cbVorgangDAO;
        underTest.commonWorkflowService = commonWorkflowService;
        underTest.mwfEntityDao = mwfEntityDao;

        akmPv = new AnkuendigungsMeldungPvBuilder().withWitaVersion(WitaCdmVersion.V1).withExterneAuftragsnummer(null)
                .withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();

        Carrierbestellung carrierbestellung = new CarrierbestellungBuilder().withVtrNr(AKM_PV_VERTRAGSNUMMER).build();
        List<Carrierbestellung> cbList = Lists.newArrayList();
        cbList.add(carrierbestellung);

        when(carrierService.findCBsByNotExactVertragsnummer(AKM_PV_VERTRAGSNUMMER)).thenReturn(cbList);

        when(cbVorgangDAO.getNextCarrierRefNr()).thenReturn(AKM_PV_EXTAUFTRAGSNUMMER);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AnkuendigungsMeldungPv akmPv = (AnkuendigungsMeldungPv) invocation.getArguments()[0];
                akmPv.setId(AKM_PV_ID);
                return null;
            }
        }).when(mwfEntityDao).store(akmPv);

        when(mwfEntityDao.findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD, AKM_PV_VERTRAGSNUMMER))
                .thenReturn(getMeldungsList());
        when(mwfEntityDao.findById(eq(AKM_PV_ID), eq(AnkuendigungsMeldungPv.class))).thenReturn(akmPv);
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.getDefault());

        carrierKennung = new CarrierKennung();
        carrierKennung.setBezeichnung("Mnet München");
        carrierKennung.setKundenNr(akmPv.getKundenNummer());
        when(carrierService.findCarrierKennungen()).thenReturn(Collections.singletonList(carrierKennung));
    }

    @DataProvider
    public Object[][] sendRuemPvFunctionProvider() {
        // @formatter:off
        return new Object[][] {
                { new SendPositiveRuemPvFunction()},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.TERMIN_UNGUELTIG, "Termin ungueltig", 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.ANSCHLUSSINHABER_FALSCH, null, 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.AUFTRAG_NICHT_LESBAR, null, 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.MSN_FALSCH, null, 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.RUFNUMMER_NICHT_GESCHALTET, null, 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.SONSTIGES, "Keine Lust", 1)},
                { new SendNegativeRuemPvFunction(RuemPvAntwortCode.UNTERSCHRIFT_FEHLT, null, 1)}
        };
        // @formatter:on
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testAbbmBeforeRuemPv() {
        sendAkmPv();
        sendAbbmPv();
        assertProcessInstanceIsFinished();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testAbmBeforeRuemPv() {
        sendAkmPv();

        sendAbmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendErlmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM_PV);

        sendEntmPv();
        assertProcessInstanceIsFinished();
    }

    @Test
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void sendRuemPvAfterAbmNotPossible() {
        sendAkmPv();
        sendAbmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);
        sendRuemPv(new SendNegativeRuemPvFunction(RuemPvAntwortCode.TERMIN_UNGUELTIG, "Termin passt nicht!", 0));
        assertProcessInstanceInState(WorkflowTaskName.WORKFLOW_ERROR);
    }

    @Test(expectedExceptions = WitaBpmException.class)
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void sendRuemPvAfterAbbmNotPossible() {
        sendAkmPv();
        sendAbbmPv();
        sendRuemPv(new SendNegativeRuemPvFunction(RuemPvAntwortCode.TERMIN_UNGUELTIG, "Termin passt nicht!", 1));
    }

    @Test
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void secondAkmPvResultsInWaitForRuemPv() {
        sendAkmPv();

        long meldungId = 11223344L;
        AnkuendigungsMeldungPv akmPv2 = new AnkuendigungsMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        doReturn(akmPv2).when(mwfEntityDao).findById(meldungId, AnkuendigungsMeldungPv.class);

        send(akmPv2, meldungId);

        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_RUEMPV);
    }

    @Test(dataProvider = "sendRuemPvFunctionProvider")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testAbm(Function<Void, Void> sendRuemPvFunction) throws Exception {
        sendAkmPv();

        sendRuemPv(sendRuemPvFunction);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendAbmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendErlmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM_PV);

        sendEntmPv();
        assertProcessInstanceIsFinished();
    }

    @Test(dataProvider = "sendRuemPvFunctionProvider")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testAbbm(Function<Void, Void> sendRuemPvFunction) throws Exception {
        sendAkmPv();

        sendRuemPv(sendRuemPvFunction);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendAbbmPv();
        assertProcessInstanceIsFinished();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testAbbmOnAkmPv() throws Exception {
        sendAkmPv();

        sendAbbmPv();
        assertProcessInstanceIsFinished();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testInstanceStillRunningAndNotInErrorState() throws Exception {
        AnkuendigungsMeldungPv existingAkmPv = new AnkuendigungsMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AnkuendigungsMeldungPv akmPv = (AnkuendigungsMeldungPv) invocation.getArguments()[0];
                akmPv.setId(AKM_PV_ID);
                return null;
            }
        }).when(mwfEntityDao).store(existingAkmPv);

        underTest.handleWitaMessage(existingAkmPv);
        assertNotNull(existingAkmPv.getBusinessKey());

        ProcessInstance pi1 = commonWorkflowService.retrieveProcessInstance(existingAkmPv.getBusinessKey());
        assertProcessInstanceInState(pi1, WorkflowTaskName.WAIT_FOR_RUEMPV, existingAkmPv.getBusinessKey());

        when(mwfEntityDao.findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD, AKM_PV_VERTRAGSNUMMER))
                .thenReturn(getMeldungsList(existingAkmPv));

        underTest.handleWitaMessage(akmPv);
        ProcessInstance pi2 = commonWorkflowService.retrieveProcessInstance(akmPv.getBusinessKey());
        assertProcessInstanceInState(pi2, WorkflowTaskName.WAIT_FOR_RUEMPV, akmPv.getBusinessKey());
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    @Test(expectedExceptions = WitaBpmException.class)
    public void testSendRuemPvForFinishedInstance() throws Exception {
        underTest.handleWitaMessage(akmPv);
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(akmPv.getBusinessKey());
        commonWorkflowService.deleteProcessInstance(pi);
        sendRuemPv();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testResetWorkflowErrorOnAkmPv() {
        when(workflowTaskValidationService.validateMwfInput(any(Meldung.class))).thenReturn("error");
        sendAkmPv(WorkflowTaskName.WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(akmPv.getBusinessKey(),
                CommonWorkflowService.WorkflowReentryState.WAIT_FOR_RUEMPV);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_RUEMPV);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    @Test(expectedExceptions = WitaUserException.class)
    public void testWrongResetWorkflowErrorOnAkmPv() {
        when(workflowTaskValidationService.validateMwfInput(any(Meldung.class))).thenReturn("error");
        sendAkmPv(WorkflowTaskName.WORKFLOW_ERROR);
        commonWorkflowService.resetErrorState(akmPv.getBusinessKey(),
                CommonWorkflowService.WorkflowReentryState.WAIT_FOR_ENTM);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml" })
    @Test(expectedExceptions = WitaBpmException.class)
    public void testRuemPvToWorkflowInErrorState() {
        when(workflowTaskValidationService.validateMwfInput(any(Meldung.class))).thenReturn("error");
        sendAkmPv(WorkflowTaskName.WORKFLOW_ERROR);

        sendRuemPv();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testResetWorkflowErrorOnAbmPv() {
        sendAkmPv();
        sendRuemPv();

        // Send abm-pv with exception
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().withVertragsnummer(
                AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(any(Long.class), eq(AuftragsBestaetigungsMeldungPv.class))).thenThrow(
                new RuntimeException());
        send(abmPv);
        assertProcessInstanceInState(WorkflowTaskName.WORKFLOW_ERROR);

        commonWorkflowService.resetErrorState(akmPv.getBusinessKey(),
                CommonWorkflowService.WorkflowReentryState.WAIT_FOR_MESSAGE);
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testResetWorkflowErrorOnAbbmPv() {
        sendAkmPv();
        sendRuemPv();

        // Send abbm-pv with exception
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(any(Long.class), eq(AbbruchMeldungPv.class))).thenThrow(new RuntimeException());
        send(abbmPv);
        assertProcessInstanceInState(WorkflowTaskName.WORKFLOW_ERROR);

        commonWorkflowService
                .resetErrorState(akmPv.getBusinessKey(), CommonWorkflowService.WorkflowReentryState.CLOSED);
        assertProcessInstanceIsFinished();
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml" })
    public void testVzmPv() throws Exception {
        sendAkmPv();

        sendAbmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendVzmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);

        sendErlmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_ENTM_PV);

        sendEntmPv();
        assertProcessInstanceIsFinished();
    }

    @DataProvider
    public Object[][] dataProviderOpenWorkflowForAllMessagesAndSetCheckState() {
        String vtrnr = "1231231230";

        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(vtrnr).build();
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder()
                .withExterneAuftragsnummer(null).withVertragsnummer(vtrnr).build();
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(vtrnr).build();
        ErledigtMeldungPv erlmPv = new ErledigtMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(vtrnr).build();
        EntgeltMeldungPv entmPv = new EntgeltMeldungPvBuilder().withExterneAuftragsnummer(null)
                .withVertragsnummer(vtrnr).build();

        // @formatter:off
        return new Object[][] {
                { akmPv, WorkflowTaskName.WAIT_FOR_RUEMPV },
                { abmPv, WorkflowTaskName.WORKFLOW_ERROR },
                { abbmPv, WorkflowTaskName.WORKFLOW_ERROR },
                { erlmPv, WorkflowTaskName.WORKFLOW_ERROR },
                { entmPv, WorkflowTaskName.WORKFLOW_ERROR },
        };
        // @formatter:o
    }

    @Test(dataProvider = "dataProviderOpenWorkflowForAllMessagesAndSetCheckState")
    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml"})
    public <T extends MwfEntity & IncomingPvMeldung & CanOpenActivitiWorkflow>
    void testOpenWorkflowForAllMessagesAndSetCheckState(T meldung, WorkflowTaskName expectedState) {
        long meldungId = 123123123L;
        doReturn(meldung).when(mwfEntityDao).findById(meldungId, meldung.getClass());

        send(meldung, meldungId);

        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(meldung.getBusinessKey());
        assertProcessInstanceInState(pi, expectedState, meldung.getBusinessKey());
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml"})
    public void testOpenWorkflowForAllMessagesAndSetCheckStateAndFindWorkflowInstance() {
        long meldungId = 1231231230L;
        String vtrnr =  "abc234";
        assertNull(underTest.retrieveRunningProcessInstance(vtrnr));

        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder()
                .withExterneAuftragsnummer(null).withVertragsnummer(vtrnr).build();
        doReturn(abmPv).when(mwfEntityDao).findById(meldungId, AuftragsBestaetigungsMeldungPv.class);
        doReturn(Lists.newArrayList(abmPv)).when(mwfEntityDao).findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD,
                vtrnr);

        send(abmPv, meldungId);
        ProcessInstance pi = underTest.retrieveRunningProcessInstance(vtrnr);

        ErledigtMeldungPv erlmPv = new ErledigtMeldungPvBuilder()
            .withExterneAuftragsnummer(null).withVertragsnummer(vtrnr).build();
        send(erlmPv, meldungId);
        ProcessInstance pi2 = underTest.retrieveRunningProcessInstance(vtrnr);

        assertEquals(pi.getId(), pi2.getId());
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml"})
    public void testOpenWorkflowForAllMessagesAndProcessFollowingMessagesWithSameWorkflow() {
        long abmPvId = 123123123L;
        long erlmPvId = 123123124L;
        long entmPvId = 123123125L;
        String vtrnr = "1231231230";

        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().withId(abmPvId)
                .withExterneAuftragsnummer(null).withVertragsnummer(vtrnr).build();
        ErledigtMeldungPv erlmPv = new ErledigtMeldungPvBuilder().withExterneAuftragsnummer(null).withId(erlmPvId)
                .withVertragsnummer(vtrnr).build();
        EntgeltMeldungPv entmPv = new EntgeltMeldungPvBuilder().withExterneAuftragsnummer(null).withId(entmPvId)
                .withVertragsnummer(vtrnr).build();
        doReturn(abmPv).when(mwfEntityDao).findById(abmPv, AuftragsBestaetigungsMeldungPv.class);
        doReturn(abmPv).when(mwfEntityDao).findById(erlmPvId, ErledigtMeldungPv.class);
        doReturn(abmPv).when(mwfEntityDao).findById(entmPvId, EntgeltMeldungPv.class);
        @SuppressWarnings("unchecked")
        List<? extends Meldung<MeldungsPosition>> meldungen = Lists.newArrayList(abmPv, erlmPv, entmPv);
        doReturn(meldungen).when(mwfEntityDao).findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD,
                vtrnr);

        send(abmPv, abmPvId);

        verify(cbVorgangDAO).getNextCarrierRefNr();
        ProcessInstance pi = underTest.retrieveRunningProcessInstance(vtrnr);
        assertProcessInstanceInState(pi, WorkflowTaskName.WORKFLOW_ERROR, AKM_PV_EXTAUFTRAGSNUMMER);
        verifyNoMoreInteractions(cbVorgangDAO);

        commonWorkflowService.resetErrorState(AKM_PV_EXTAUFTRAGSNUMMER, WorkflowReentryState.WAIT_FOR_MESSAGE);
        pi = underTest.retrieveRunningProcessInstance(vtrnr);
        assertProcessInstanceInState(pi, WorkflowTaskName.WAIT_FOR_MESSAGE, AKM_PV_EXTAUFTRAGSNUMMER);

        send(erlmPv, erlmPvId);
        ProcessInstance pi2 = underTest.retrieveRunningProcessInstance(vtrnr);
        assertProcessInstanceInState(pi2, WorkflowTaskName.WAIT_FOR_ENTM_PV, AKM_PV_EXTAUFTRAGSNUMMER);
        verifyNoMoreInteractions(cbVorgangDAO);

        send(entmPv, entmPvId);
        ProcessInstance pi3 = underTest.retrieveRunningProcessInstance(vtrnr);
        assertProcessInstanceIsFinished(pi3, true, AKM_PV_EXTAUFTRAGSNUMMER);
        verifyNoMoreInteractions(cbVorgangDAO);
    }

    public void testDoNotFindTwoProcessInstancesForSameVertragsnummerIfKueKdAndAbgebendPvExists() {
        String vtrnr = "1A";

        String talOrderExtOrderNo = "123";
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder()
                .withExterneAuftragsnummer(talOrderExtOrderNo).withVertragsnummer(vtrnr).build();
        String abgebendPvExtOrderNo = "234";
        AnkuendigungsMeldungPv akmPv = new AnkuendigungsMeldungPvBuilder()
                .withExterneAuftragsnummer(abgebendPvExtOrderNo).withVertragsnummer(vtrnr).build();

        doReturn(Lists.<Meldung<?>>newArrayList(abm, akmPv)).when(mwfEntityDao).findByProperty(Meldung.class,
                Meldung.VERTRAGS_NUMMER_FIELD, vtrnr);

        ProcessInstance talOrderPi = mock(ProcessInstance.class);
        when(talOrderPi.getId()).thenReturn("talOrderPiId");
        ProcessInstance abgebendPvPi = mock(ProcessInstance.class);
        when(abgebendPvPi.getId()).thenReturn("abgebendPvPiId");

        underTest.commonWorkflowService = mock(CommonWorkflowService.class);
        doReturn(talOrderPi).when(underTest.commonWorkflowService).retrieveProcessInstanceIfExists(talOrderExtOrderNo);
        doReturn(false).when(underTest.commonWorkflowService).isProcessInstanceFinished(talOrderPi);
        doReturn(abgebendPvPi).when(underTest.commonWorkflowService).retrieveProcessInstanceIfExists(abgebendPvExtOrderNo);
        doReturn(false).when(underTest.commonWorkflowService).isProcessInstanceFinished(abgebendPvPi);

        ProcessInstance result = underTest.retrieveRunningProcessInstance(vtrnr);
        assertSame(result, abgebendPvPi);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/AbgebendPV.bpmn20.xml"})
    @Test(enabled = false)
    public void testAutomaticAnswerForAkmPv() {
        sendAkmPv(WorkflowTaskName.WAIT_FOR_MESSAGE);
    }

    private void assertProcessInstanceInState(WorkflowTaskName state) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(akmPv.getBusinessKey());
        assertProcessInstanceInState(pi, state, akmPv.getBusinessKey());
    }

    private void assertProcessInstanceIsFinished() {
        assertProcessInstanceIsFinished(akmPv);
    }

    private void sendAkmPv() {
        sendAkmPv(WorkflowTaskName.WAIT_FOR_RUEMPV);
    }

    private void sendAkmPv(WorkflowTaskName expectedState) {
        underTest.handleWitaMessage(akmPv);
        ProcessInstance processInstance = commonWorkflowService.retrieveProcessInstance(akmPv.getBusinessKey());
        assertThat(processInstance, notNullValue());
        assertProcessInstanceInState(expectedState);

        verify(mwfEntityDao).store(akmPv);

        when(
                mwfEntityDao.findByProperty(Meldung.class, Meldung.VERTRAGS_NUMMER_FIELD,
                        AKM_PV_VERTRAGSNUMMER)).thenReturn(getMeldungsList(akmPv));
        when(witaUsertaskService.findAkmPvUserTask(akmPv.getExterneAuftragsnummer())).thenReturn(new AkmPvUserTask());
    }

    private class SendPositiveRuemPvFunction implements Function<Void, Void> {

        @Override
        public Void apply(Void input) {
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object creator = invocation.getArguments()[0];
                    assertTrue(creator instanceof OutgoingWitaMessage);
                    RueckMeldungPv ruemPvSent = assertValidRuemPvSent((OutgoingWitaMessage) creator);
                    assertThat(ruemPvSent.getAbgebenderProvider().isZustimmungProviderWechsel(), equalTo(true));
                    assertThat(ruemPvSent.getAbgebenderProvider().getAntwortCode(), nullValue());

                    return null;
                }
            }).when(lineOrderService).sendToWita(any(WitaMessage.class));

            underTest.sendRuemPv(akmPv.getBusinessKey(), RuemPvAntwortCode.OK, null);
            verify(lineOrderService).sendToWita(any(WitaMessage.class));

            return null;
        }
    }

    private class SendNegativeRuemPvFunction implements Function<Void, Void> {

        private final RuemPvAntwortCode antwortCode;
        private final String antwortText;
        private final int sendCount;

        public SendNegativeRuemPvFunction(RuemPvAntwortCode antwortCode, String antwortText, final int sendCount) {
            this.antwortCode = antwortCode;
            this.antwortText = antwortText;
            this.sendCount = sendCount;
        }

        @Override
        public Void apply(Void input) {
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object creator = invocation.getArguments()[0];
                    assertTrue(creator instanceof OutgoingWitaMessage);
                    RueckMeldungPv ruemPvSent = assertValidRuemPvSent((OutgoingWitaMessage) creator);
                    assertThat(ruemPvSent.getAbgebenderProvider().isZustimmungProviderWechsel(), equalTo(false));
                    assertThat(StringUtils.isNotBlank(ruemPvSent.getAbgebenderProvider().getAntwortText()),
                            equalTo(antwortCode.antwortTextRequired));
                    return null;
                }
            }).when(lineOrderService).sendToWita(any(WitaMessage.class));

            underTest.sendRuemPv(akmPv.getBusinessKey(), antwortCode, antwortText);
            if (sendCount > 0) {
                verify(lineOrderService, times(sendCount)).sendToWita(any(WitaMessage.class));
                assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_MESSAGE);
            }
            return null;
        }
    }

    private RueckMeldungPv assertValidRuemPvSent(OutgoingWitaMessage message) {
        RueckMeldungPv ruemPv = (RueckMeldungPv) message;
        assertNotNull(ruemPv);
        Set<ConstraintViolation<RueckMeldungPv>> violations = validator.validate(ruemPv);
        assertThat("Unexpected violations: " + violations, violations.isEmpty(), equalTo(true));
        assertThat(ruemPv.getKundenNummer(), equalTo(carrierKennung.getKundenNr()));
        assertThat(ruemPv.getAbgebenderProvider().getProviderName(), equalTo(carrierKennung.getBezeichnung()));

        return ruemPv;
    }

    private void sendRuemPv() {
        sendRuemPv(new SendPositiveRuemPvFunction());
    }

    private void sendRuemPv(Function<Void, Void> function) {
        function.apply(null);
    }

    private void sendAbmPv() {
        AuftragsBestaetigungsMeldungPv abmPv = new AuftragsBestaetigungsMeldungPvBuilder().withVertragsnummer(
                AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(MELDUNG_ID, AuftragsBestaetigungsMeldungPv.class)).thenReturn(abmPv);
        send(abmPv);
    }

    private void sendAbbmPv() {
        AbbruchMeldungPv abbmPv = new AbbruchMeldungPvBuilder().withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(MELDUNG_ID, AbbruchMeldungPv.class)).thenReturn(abbmPv);
        send(abbmPv);
    }

    private void sendVzmPv() {
        VerzoegerungsMeldungPv vzmPv = new VerzoegerungsMeldungPvBuilder().withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(MELDUNG_ID, VerzoegerungsMeldungPv.class)).thenReturn(vzmPv);
        send(vzmPv);
    }

    private void sendErlmPv() {
        ErledigtMeldungPv erlmPv = new ErledigtMeldungPvBuilder().withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(MELDUNG_ID, ErledigtMeldungPv.class)).thenReturn(erlmPv);
        send(erlmPv);
    }

    private void sendEntmPv() {
        EntgeltMeldungPv entmPv = new EntgeltMeldungPvBuilder().withVertragsnummer(AKM_PV_VERTRAGSNUMMER).build();
        when(mwfEntityDao.findById(MELDUNG_ID, EntgeltMeldungPv.class)).thenReturn(entmPv);
        send(entmPv);
    }

    private <T extends MwfEntity & IncomingPvMeldung & CanOpenActivitiWorkflow> void send(T meldung) {
        send(meldung, MELDUNG_ID);
    }

    private <T extends MwfEntity & IncomingPvMeldung & CanOpenActivitiWorkflow> void send(final T meldung, final long id) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Meldung<?> meldung = (Meldung<?>) invocation.getArguments()[0];
                meldung.setId(id);
                return null;
            }
        }).when(mwfEntityDao).store(any(Meldung.class));

        underTest.handleWitaMessage(meldung);

        verify(mwfEntityDao).store(meldung);
    }

    @Deployment(resources = { "de/mnet/wita/v1/bpm/TalOrder.bpmn20.xml"})
    public void testErrorZustandSetzenWaitForRuemPv() {
        sendAkmPv();
        assertProcessInstanceInState(WorkflowTaskName.WAIT_FOR_RUEMPV);
        commonWorkflowService.setErrorState(akmPv.getBusinessKey(), "set error manually while testing");
        assertProcessInstanceInState(WorkflowTaskName.WORKFLOW_ERROR);
    }

    @SuppressWarnings("rawtypes")
    private List<Meldung> getMeldungsList(Meldung...meldungen) {
        return Lists.newArrayList(meldungen);
    }

}
