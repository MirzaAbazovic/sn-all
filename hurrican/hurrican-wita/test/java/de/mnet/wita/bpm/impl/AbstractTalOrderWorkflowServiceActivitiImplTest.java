/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.05.2011 13:07:16
 */
package de.mnet.wita.bpm.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wita.bpm.WorkflowTaskName.*;
import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import javax.annotation.*;
import com.google.common.collect.ImmutableList;
import org.activiti.engine.runtime.ProcessInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.tools.lang.Pair;
import de.augustakom.hurrican.dao.cc.CBVorgangDAO;
import de.augustakom.hurrican.model.cc.Reference;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CarrierElTALService;
import de.augustakom.hurrican.service.cc.ReferenceService;
import de.mnet.wita.OutgoingWitaMessage;
import de.mnet.wita.WitaCdmVersion;
import de.mnet.wita.WitaMessage;
import de.mnet.wita.aggregator.GeschaeftsfallAggregator;
import de.mnet.wita.aggregator.KundeAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerAggregator;
import de.mnet.wita.aggregator.ProduktBezeichnerKueKdAggregator;
import de.mnet.wita.bpm.WorkflowTaskName;
import de.mnet.wita.bpm.WorkflowTaskValidationService;
import de.mnet.wita.bpm.tasks.WitaDataAggregationTask;
import de.mnet.wita.bpm.test.AbstractActivitiBaseTest;
import de.mnet.wita.dao.MwfEntityDao;
import de.mnet.wita.exceptions.WitaBpmException;
import de.mnet.wita.integration.LineOrderService;
import de.mnet.wita.message.Auftrag;
import de.mnet.wita.message.MnetWitaRequest;
import de.mnet.wita.message.Storno;
import de.mnet.wita.message.TerminVerschiebung;
import de.mnet.wita.message.auftrag.Kunde;
import de.mnet.wita.message.builder.auftrag.geschaeftsfall.GeschaeftsfallBuilder;
import de.mnet.wita.message.builder.meldung.AbbruchMeldungBuilder;
import de.mnet.wita.message.builder.meldung.AuftragsBestaetigungsMeldungBuilder;
import de.mnet.wita.message.builder.meldung.EntgeltMeldungBuilder;
import de.mnet.wita.message.builder.meldung.ErledigtMeldungBuilder;
import de.mnet.wita.message.builder.meldung.QualifizierteEingangsBestaetigungBuilder;
import de.mnet.wita.message.meldung.AbbruchMeldung;
import de.mnet.wita.message.meldung.AuftragsBestaetigungsMeldung;
import de.mnet.wita.message.meldung.EntgeltMeldung;
import de.mnet.wita.message.meldung.ErledigtMeldung;
import de.mnet.wita.message.meldung.Meldung;
import de.mnet.wita.message.meldung.QualifizierteEingangsBestaetigung;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;
import de.mnet.wita.service.MwfEntityService;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = SERVICE)
@ContextConfiguration(locations = {
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml",
        "classpath:de/mnet/wita/v1/wita-activiti-context.xml",
        "classpath:de/mnet/wita/route/camel-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-test-context.xml",
        "classpath:de/mnet/wita/bpm/activiti-mockito-context.xml", })
public class AbstractTalOrderWorkflowServiceActivitiImplTest extends AbstractActivitiBaseTest {

    /**
     * Mocked by spring application context
     */
    @Autowired
    MwfEntityService mwfEntityService;
    @Autowired
    protected MwfEntityDao mwfEntityDao;
    @Autowired
    CBVorgangDAO cbVorgangDao;
    @Autowired
    private CarrierElTALService carrierElTALService;
    @Autowired
    protected WorkflowTaskValidationService workflowTaskValidationService;
    @Autowired
    private WitaConfigService witaConfigService;

    @Resource(name = "de.augustakom.hurrican.service.cc.ReferenceService")
    private ReferenceService referenceService;

    @Autowired
    private KundeAggregator kundeAggregator;
    @Autowired
    private GeschaeftsfallAggregator geschaeftsfallAggregator;
    @Autowired
    private WitaDataAggregationTask witaDataAggregationTask;
    @Autowired
    protected LineOrderService lineOrderService;

    TalOrderWorkflowServiceImpl underTest;

    protected WitaCBVorgang cbVorgang;
    private Auftrag auftrag;

    @BeforeMethod
    public void setUp() throws Exception {
        reset(lineOrderService, carrierElTALService, workflowTaskValidationService, mwfEntityDao, witaConfigService);

        underTest = new TalOrderWorkflowServiceImpl();
        underTest.commonWorkflowService = commonWorkflowService;
        underTest.mwfEntityDao = mwfEntityDao;
        underTest.cbVorgangDao = cbVorgangDao;
        underTest.witaConfigService = witaConfigService;

        witaDataAggregationTask.produktBezeichnerAggregator = mock(ProduktBezeichnerAggregator.class);
        witaDataAggregationTask.produktBezeichnerKueKdAggregator = mock(ProduktBezeichnerKueKdAggregator.class);

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                Object arg = invocation.getArguments()[0];
                if (arg instanceof Meldung) {
                    Meldung<?> meldung = (Meldung<?>) arg;
                    meldung.setId(1L);
                }
                return null;
            }
        }).when(mwfEntityDao).store(any(WitaMessage.class));

        when(witaConfigService.isSendAllowed(any(MnetWitaRequest.class))).thenReturn(true);
        when(witaConfigService.getDefaultWitaVersion()).thenReturn(WitaCdmVersion.getDefault());

        List<Reference> referenceList = Lists.newArrayList();
        Reference pufferReference = new Reference();
        pufferReference.setIntValue(2);
        referenceList.add(pufferReference);
        when(referenceService.findReferencesByType(Reference.REF_TYPE_WITA_VLT_PUFFERZEIT, Boolean.FALSE)).thenReturn(
                referenceList);
    }

    protected void startProcessInstance() throws FindException {
        cbVorgang = new WitaCBVorgangBuilder().withId(123L).withCarrierRefNr("TEST" + EntityBuilder.getLongId())
                .build();
        when(carrierElTALService.findCBVorgang(cbVorgang.getId())).thenReturn(cbVorgang);

        Pair<Kunde, Kunde> kundeAggregatorResult = new Pair<>(mock(Kunde.class), mock(Kunde.class));
        when(kundeAggregator.aggregate(cbVorgang)).thenReturn(kundeAggregatorResult);

        when(geschaeftsfallAggregator.aggregate(cbVorgang)).thenReturn(
                new GeschaeftsfallBuilder(BEREITSTELLUNG).withKundenwunschtermin(LocalDate.now().plusDays(14))
                        .buildValid()
        );

        underTest.newProcessInstance(cbVorgang);

        ArgumentCaptor<Auftrag> auftragCaptor = ArgumentCaptor.forClass(Auftrag.class);
        verify(mwfEntityDao).store(auftragCaptor.capture());
        auftrag = auftragCaptor.getValue();
        assertNotNull(auftrag);
        assertEquals(auftrag.getExterneAuftragsnummer(), cbVorgang.getCarrierRefNr());

        verify(workflowTaskValidationService).validateMwfOutput(auftragCaptor.capture());
        when(mwfEntityDao.getAuftragOfCbVorgang(cbVorgang.getId())).thenReturn(auftrag);

        assertProcessInstanceInState(WAIT_FOR_MESSAGE);
        verify(lineOrderService).sendToWita(any(Auftrag.class));
        reset(lineOrderService);
    }

    protected void assertProcessInstanceInState(WorkflowTaskName state) {
        ProcessInstance pi = commonWorkflowService.retrieveProcessInstance(cbVorgang.getBusinessKey());
        assertProcessInstanceInState(pi, state, cbVorgang.getBusinessKey());
    }

    protected void assertProcessInstanceFinished() {
        assertProcessInstanceIsFinished(true);
    }

    private void assertProcessInstanceIsFinished(boolean expectedToBeFinished) {
        ProcessInstance pi;
        try {
            pi = commonWorkflowService.retrieveProcessInstance(cbVorgang.getBusinessKey());
        }
        catch (WitaBpmException e) {
            pi = null;
        }
        assertProcessInstanceIsFinished(pi, expectedToBeFinished, cbVorgang.getBusinessKey());
    }

    protected void sendStorno() {
        sendStorno(WAIT_FOR_MESSAGE);
    }

    protected void sendStorno(WorkflowTaskName expected) {
        final Storno storno = new Storno(auftrag);
        storno.setId(1248L);
        when(mwfEntityDao.findById(storno.getId(), Storno.class)).thenReturn(storno);

        if (expected == WorkflowTaskName.WORKFLOW_ERROR) {
            doThrow(new RuntimeException()).when(lineOrderService).sendToWita(any(WitaMessage.class));
        }
        else {
            doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object message = invocation.getArguments()[0];
                    assertTrue(message instanceof OutgoingWitaMessage);
                    assertEquals(message, storno);
                    return null;
                }
            }).when(lineOrderService).sendToWita(any(WitaMessage.class));
        }

        underTest.sendTvOrStornoRequest(storno);
        assertProcessInstanceInState(expected);
    }

    protected void sendTv() {
        sendTv(WAIT_FOR_MESSAGE);
    }

    protected void sendTv(WorkflowTaskName expected) {
        final TemporalField fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek();
        final TerminVerschiebung tv = new TerminVerschiebung(auftrag, LocalDate.now().plusDays(21).with(fieldISO, 4));  // 4=thursday
        tv.setId(1248L);
        when(mwfEntityDao.findById(tv.getId(), TerminVerschiebung.class)).thenReturn(tv);

        if (expected == WorkflowTaskName.WORKFLOW_ERROR) {
            doThrow(new RuntimeException()).when(lineOrderService).sendToWita(any(WitaMessage.class));
        }
        else {
            doAnswer(new Answer<Void>() {

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object message = invocation.getArguments()[0];
                    assertTrue(message instanceof OutgoingWitaMessage);
                    assertEquals(message, tv);
                    return null;
                }
            }).when(lineOrderService).sendToWita(any(WitaMessage.class));
        }

        underTest.sendTvOrStornoRequest(tv);
        assertProcessInstanceInState(expected);
    }

    protected void sendQeb() {
        QualifizierteEingangsBestaetigung qeb = new QualifizierteEingangsBestaetigungBuilder()
                .withExterneAuftragsnummer(cbVorgang.getCarrierRefNr()).build();
        when(mwfEntityDao.findById(any(Long.class), eq(QualifizierteEingangsBestaetigung.class))).thenReturn(qeb);
        when(
                mwfEntityDao.queryByExample(any(QualifizierteEingangsBestaetigung.class),
                        eq(QualifizierteEingangsBestaetigung.class))
        ).thenReturn(ImmutableList.of(qeb));

        underTest.handleWitaMessage(qeb);

        verify(mwfEntityDao).store(qeb);

        assertProcessInstanceInState(WAIT_FOR_MESSAGE);
    }

    protected void sendAbm() {
        sendAbm(WAIT_FOR_MESSAGE);
    }

    protected void sendAbm(WorkflowTaskName expectedState) {
        String extAuftragsnr = cbVorgang.getCarrierRefNr();
        final TemporalField fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek();
        AuftragsBestaetigungsMeldung abm = new AuftragsBestaetigungsMeldungBuilder()
                .withExterneAuftragsnummer(extAuftragsnr)
                .withVerbindlicherLiefertermin((LocalDate.now()).plusDays(14).with(fieldISO, 4)).build();  // 4=thursday

        when(mwfEntityDao.findById(any(Long.class), eq(AuftragsBestaetigungsMeldung.class))).thenReturn(abm);
        when(
                mwfEntityDao.queryByExample(any(AuftragsBestaetigungsMeldung.class),
                        eq(AuftragsBestaetigungsMeldung.class))
        ).thenReturn(ImmutableList.of(abm));
        when(mwfEntityService.checkMeldungReceived(extAuftragsnr, AuftragsBestaetigungsMeldung.class)).thenReturn(true);

        if (expectedState.equals(WorkflowTaskName.WORKFLOW_ERROR)) {
            when(workflowTaskValidationService.validateMwfInput(abm)).thenReturn("error");
        }
        underTest.handleWitaMessage(abm);
        assertProcessInstanceInState(expectedState);
    }

    protected void sendAbbm() {
        sendAbbm(AenderungsKennzeichen.STANDARD);
        assertProcessInstanceFinished();
    }

    protected void sendAbbm(AenderungsKennzeichen aenderungsKennzeichen, WorkflowTaskName expectedState) {
        sendAbbm(aenderungsKennzeichen);
        assertProcessInstanceInState(expectedState);
    }

    private void sendAbbm(AenderungsKennzeichen aenderungsKennzeichen) {
        AbbruchMeldung abbm = (new AbbruchMeldungBuilder()).withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withAenderungsKennzeichen(aenderungsKennzeichen).build();

        when(mwfEntityDao.findById(any(Long.class), eq(AbbruchMeldung.class))).thenReturn(abbm);
        when(mwfEntityDao.queryByExample(any(AbbruchMeldung.class), eq(AbbruchMeldung.class))).thenReturn(
                ImmutableList.of(abbm));

        underTest.handleWitaMessage(abbm);
    }

    protected void sendErlmk() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object message = invocation.getArguments()[0];
                assertTrue(message instanceof OutgoingWitaMessage);
                assertNotNull(message);

                return null;
            }
        }).when(lineOrderService).sendToWita(any(WitaMessage.class));

        underTest.sendErlmk(cbVorgang);

        assertProcessInstanceInState(WAIT_FOR_ENTM);
    }

    protected void sendErlm() {
        sendErlm(AenderungsKennzeichen.STANDARD, WAIT_FOR_ENTM);
    }

    void sendErlm(AenderungsKennzeichen aenderungsKennzeichen) {
        sendErlm(aenderungsKennzeichen, WAIT_FOR_ENTM);
    }

    protected void sendErlm(AenderungsKennzeichen aenderungsKennzeichen, WorkflowTaskName expectedState) {
        ErledigtMeldung erlm = new ErledigtMeldungBuilder().withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withAenderungsKennzeichen(aenderungsKennzeichen).build();
        when(mwfEntityDao.findById(any(Long.class), eq(ErledigtMeldung.class))).thenReturn(erlm);
        underTest.handleWitaMessage(erlm);
        assertProcessInstanceInState(expectedState);
    }

    void sendEntm() {
        sendEntm(AenderungsKennzeichen.STANDARD);
    }

    void sendEntm(AenderungsKennzeichen aenderungsKennzeichen) {
        EntgeltMeldung entm = new EntgeltMeldungBuilder().withExterneAuftragsnummer(cbVorgang.getCarrierRefNr())
                .withAenderungsKennzeichen(aenderungsKennzeichen).build();
        when(mwfEntityDao.findById(any(Long.class), eq(EntgeltMeldung.class))).thenReturn(entm);
        underTest.handleWitaMessage(entm);
        assertProcessInstanceFinished();
    }
}
