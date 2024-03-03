/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.14
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.authentication.model.AKUserBuilder;
import de.augustakom.common.tools.messages.AKWarnings;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.CCAuftragStatusService;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.AutomationTask;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciAutomationTxHelperService;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciMeldungService;

@Test(groups = UNIT)
public class WbciAutomationDonatingServiceImplTest {

    @InjectMocks
    @Spy
    private WbciAutomationDonatingServiceImpl testling;
    
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciElektraService wbciElektraService;
    @Mock
    private WbciMeldungService wbciMeldungService;
    @Mock
    private CCAuftragService auftragService;
    @Mock
    private WbciAutomationTxHelperService wbciAutomationTxHelperService;

    @Mock
    private CCAuftragStatusService auftragStatusService;

    private static WbciCdmVersion wbciCdmVersion = WbciCdmVersion.V1;

    @BeforeMethod
    public void setUp() {
        testling = new WbciAutomationDonatingServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessAutomatableOutgoingRuemVasWithInvalidChangeDate() {
        LocalDate changeDate = LocalDate.now().plusDays(1);
        WbciGeschaeftsfall kueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(Long.valueOf(12345))
                .withWechseltermin(changeDate)
                .buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);
        Collection<WbciGeschaeftsfall> wbciGfs = Arrays.asList(kueMrn);
        when(wbciGeschaeftsfallService.findAutomateableOutgoingRuemVaForKuendigung()).thenReturn(wbciGfs);

        AKUser user = new AKUser();
        testling.processAutomatableOutgoingRuemVas(user, 1L);

        verify(wbciCommonService, never()).findLastForVaId(anyString(), eq(RueckmeldungVorabstimmung.class));
        verify(testling).handleAutomationException(eq(kueMrn), eq(AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN),
                any(RuntimeException.class), eq(user));
    }

    @Test
    public void testProcessAutomatableOutgoingRuemVas() {
        Set<Long> nonBillingRelevant = new HashSet<>();
        nonBillingRelevant.add(Long.valueOf(789456));

        LocalDate changeDate = LocalDate.now().plusDays(10);
        WbciGeschaeftsfall kueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withBillingOrderNoOrig(Long.valueOf(12345))
                .withNonBillingRelevantOrderNos(nonBillingRelevant)
                .withWechseltermin(changeDate)
                .buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);
        Collection<WbciGeschaeftsfall> wbciGfs = Arrays.asList(kueMrn);
        when(wbciGeschaeftsfallService.findAutomateableOutgoingRuemVaForKuendigung()).thenReturn(wbciGfs);

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastForVaId(kueMrn.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        doReturn(new AKWarnings()).when(auftragStatusService).cancelHurricanOrdersAndCreateBA(
                anyLong(), any(Date.class), any(AKUser.class), anyLong());

        AKUser user = new AKUser();
        testling.processAutomatableOutgoingRuemVas(user, 1L);

        verify(wbciElektraService).cancelBillingOrder(kueMrn.getBillingOrderNoOrig(), ruemVa);
        verify(wbciElektraService).cancelBillingOrder(nonBillingRelevant.iterator().next(), ruemVa);
        verify(auftragStatusService).cancelHurricanOrdersAndCreateBA(
                kueMrn.getBillingOrderNoOrig(), Date.from(changeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user, 1L);
        verify(auftragStatusService).cancelHurricanOrdersAndCreateBA(
                nonBillingRelevant.iterator().next(), Date.from(changeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), user, 1L);
    }

    @Test
    public void testProcessAutomatableOutgoingRuemVasNoRuemVaFound() {
        WbciGeschaeftsfall kueMrn = new WbciGeschaeftsfallKueMrnTestBuilder().buildValid(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN);
        Collection<WbciGeschaeftsfall> wbciGfs = Arrays.asList(kueMrn);
        when(wbciGeschaeftsfallService.findAutomateableOutgoingRuemVaForKuendigung()).thenReturn(wbciGfs);

        when(wbciCommonService.findLastForVaId(kueMrn.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(null);

        doNothing().when(testling).handleAutomationException(any(WbciGeschaeftsfall.class), any(AutomationTask.TaskName.class),
                any(RuntimeException.class), any(AKUser.class));

        AKUser user = new AKUserBuilder().withRandomId().setPersist(false).build();
        testling.processAutomatableOutgoingRuemVas(user, 1L);

        verify(testling).handleAutomationException(eq(kueMrn), eq(AutomationTask.TaskName.AUFTRAG_NACH_OUTGOING_RUEMVA_KUENDIGEN),
                any(RuntimeException.class), eq(user));
    }


    @Test
    public void processAutomatableStrAufhErlmsDonatingWithException() {
        ErledigtmeldungStornoAuf erlmStrAufhAuf = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        when(wbciMeldungService.findAutomatableStrAufhErlmsDonatingProcessing())
                .thenReturn(Arrays.asList(erlmStrAufhAuf));

        Long sessionId = 99L;
        AKUser user = new AKUserBuilder().withRandomId().setPersist(false).build();

        doThrow(new WbciServiceException("xxx")).when(wbciAutomationTxHelperService).undoCancellation(
                eq(erlmStrAufhAuf), anyLong(), eq(user), eq(sessionId));

        testling.processAutomatableStrAufhErlmsDonating(user, 99L);

        verify(testling).handleAutomationException(
                eq(erlmStrAufhAuf),
                eq(AutomationTask.TaskName.UNDO_AUFTRAG_KUENDIGUNG),
                any(Exception.class),
                eq(user));
    }


    @Test
    public void processAutomatableStrAufhErlmsDonating() throws StoreException, FindException {
        ErledigtmeldungStornoAuf erlmStrAufhAuf = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        ErledigtmeldungStornoAuf erlmStrAufhAbg = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(wbciCdmVersion, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_ABG);

        when(wbciMeldungService.findAutomatableStrAufhErlmsDonatingProcessing())
                .thenReturn(Arrays.asList(erlmStrAufhAuf, erlmStrAufhAbg));

        Long sessionId = 99L;
        AKUser user = new AKUserBuilder().withRandomId().setPersist(false).build();

        testling.processAutomatableStrAufhErlmsDonating(user, sessionId);
        verify(wbciAutomationTxHelperService).undoCancellation(eq(erlmStrAufhAuf), anyLong(), eq(user), eq(sessionId));
        verify(wbciAutomationTxHelperService).undoCancellation(eq(erlmStrAufhAbg), anyLong(), eq(user), eq(sessionId));
    }


}
