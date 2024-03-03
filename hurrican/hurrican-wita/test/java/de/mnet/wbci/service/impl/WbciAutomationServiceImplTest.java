package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.elektra.ElektraResponseDto.*;
import static de.mnet.wbci.model.AutomationTask.*;
import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;
import static de.mnet.wbci.model.AutomationTask.TaskName.*;
import static de.mnet.wbci.service.impl.WbciAutomationServiceImpl.*;
import static de.mnet.wbci.service.impl.WbciAutomationServiceImpl.AkmTrAutomationTask.*;
import static de.mnet.wita.model.TamUserTask.TamBearbeitungsStatus.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.hurrican.service.elektra.ElektraResponseDto;
import de.augustakom.hurrican.service.elektra.builder.ElektraResponseDtoBuilder;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.wbci.exception.WbciAutomationValidationException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciElektraService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciKuendigungsService;
import de.mnet.wbci.service.WbciMeldungService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wita.message.meldung.position.AenderungsKennzeichen;
import de.mnet.wita.model.TamUserTask;
import de.mnet.wita.model.WitaCBVorgang;
import de.mnet.wita.model.WitaCBVorgangBuilder;

@Test(groups = UNIT)
public class WbciAutomationServiceImplTest {

    @InjectMocks
    @Spy
    private WbciAutomationServiceImpl testling;

    @Mock
    private WbciElektraService wbciElektraServiceMock;

    @Mock
    private WbciMeldungService wbciMeldungService;

    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;

    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;

    @Mock
    private WbciCommonService wbciCommonService;

    @Mock
    private WbciKuendigungsService wbciKuendigungsService;

    private static final WbciCdmVersion WBCI_CDM_VERSION = WbciCdmVersion.V1;

    @BeforeMethod
    public void setUp() {
        testling = new WbciAutomationServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void processAutomatableRuemVas() {
        AKUser user = new AKUser();
        String preagreementId_1 = "DEU.MNET.V00000001";
        String preagreementId_2 = "DEU.MNET.V00000002";
        String preagreementId_3 = "DEU.MNET.V00000003";
        String preagreementId_4 = "DEU.MNET.V00000004";
        
        RueckmeldungVorabstimmung ruemVa1 = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ruemVa1.getWbciGeschaeftsfall().setVorabstimmungsId(preagreementId_1);
        RueckmeldungVorabstimmung ruemVa2 = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ruemVa2.getWbciGeschaeftsfall().setVorabstimmungsId(preagreementId_2);
        RueckmeldungVorabstimmung ruemVa3 = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ruemVa3.getWbciGeschaeftsfall().setVorabstimmungsId(preagreementId_3);
        RueckmeldungVorabstimmung ruemVa4 = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ruemVa4.getWbciGeschaeftsfall().setVorabstimmungsId(preagreementId_4);

        when(wbciGeschaeftsfallService.findPreagreementsWithAutomatableRuemVa()).thenReturn(Arrays.asList(preagreementId_1,
                preagreementId_2, preagreementId_3, preagreementId_4));
        when(wbciCommonService.findLastForVaId(eq(preagreementId_1), eq(RueckmeldungVorabstimmung.class))).thenReturn(ruemVa1);
        when(wbciCommonService.findLastForVaId(eq(preagreementId_2), eq(RueckmeldungVorabstimmung.class))).thenReturn(ruemVa2);
        when(wbciCommonService.findLastForVaId(eq(preagreementId_3), eq(RueckmeldungVorabstimmung.class))).thenReturn(ruemVa3);
        when(wbciCommonService.findLastForVaId(eq(preagreementId_4), eq(RueckmeldungVorabstimmung.class))).thenReturn(ruemVa4);

        when(wbciElektraServiceMock.processRuemVaNewTx(preagreementId_1, user)).thenThrow(Exception.class);
        when(wbciElektraServiceMock.processRuemVaNewTx(preagreementId_2, user))
                .thenReturn(new ElektraResponseDtoBuilder().withStatus(ResponseStatus.OK).build());
        when(wbciElektraServiceMock.processRuemVaNewTx(preagreementId_3, user))
                .thenReturn(new ElektraResponseDtoBuilder().withStatus(ResponseStatus.OK).build());
        when(wbciElektraServiceMock.processRuemVaNewTx(preagreementId_4, user))
                .thenReturn(new ElektraResponseDtoBuilder().withStatus(ResponseStatus.ERROR).build());

        doThrow(Exception.class).when(wbciMeldungService).sendAutomatedAkmTr(preagreementId_3, user);

        testling.processAutomatableRuemVas(user);

        verify(wbciElektraServiceMock).processRuemVaNewTx(preagreementId_1, user);
        verify(wbciMeldungService, never()).sendAutomatedAkmTr(preagreementId_1, user);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTaskNewTx(eq(preagreementId_1), eq(ruemVa1),
                eq(TAIFUN_NACH_RUEMVA_AKTUALISIEREN), eq(ERROR), anyString(), eq(user));
        verify(wbciGeschaeftsfallService, never()).createOrUpdateAutomationTaskNewTx(eq(preagreementId_1), eq(ruemVa1),
                eq(WBCI_SEND_AKMTR), any(AutomationStatus.class), anyString(), eq(user));

        verify(wbciElektraServiceMock).processRuemVaNewTx(preagreementId_2, user);
        verify(wbciMeldungService).sendAutomatedAkmTr(preagreementId_2, user);
        verify(wbciGeschaeftsfallService, never()).createOrUpdateAutomationTaskNewTx(eq(preagreementId_2), eq(ruemVa2),
                any(TaskName.class), any(AutomationStatus.class), anyString(), eq(user));

        verify(wbciElektraServiceMock).processRuemVaNewTx(preagreementId_3, user);
        verify(wbciMeldungService).sendAutomatedAkmTr(preagreementId_3, user);
        verify(wbciGeschaeftsfallService, never()).createOrUpdateAutomationTaskNewTx(eq(preagreementId_3), eq(ruemVa3),
                eq(TAIFUN_NACH_RUEMVA_AKTUALISIEREN), any(AutomationStatus.class), anyString(), eq(user));
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTaskNewTx(eq(preagreementId_3), eq(ruemVa3),
                eq(WBCI_SEND_AKMTR), eq(ERROR), anyString(), eq(user));

        verify(wbciElektraServiceMock).processRuemVaNewTx(preagreementId_4, user);
        verify(wbciMeldungService, never()).sendAutomatedAkmTr(preagreementId_4, user);
        verify(wbciGeschaeftsfallService, never()).createOrUpdateAutomationTaskNewTx(eq(preagreementId_4), eq(ruemVa4),
                eq(TAIFUN_NACH_RUEMVA_AKTUALISIEREN), any(AutomationStatus.class), anyString(), eq(user));
        verify(wbciGeschaeftsfallService, never()).createOrUpdateAutomationTaskNewTx(eq(preagreementId_4), eq(ruemVa4),
                eq(WBCI_SEND_AKMTR), any(AutomationStatus.class), anyString(), eq(user));
    }

    @Test
    public void testProcessAutomatableAkmTrs() throws Exception {
        String vertNrANBW = "100";
        String vertNrNEU = "200";
        UebernahmeRessourceMeldung akmTrANBW = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .withLeitungen(Sets.newHashSet(new LeitungBuilder().withVertragsnummer("V0002").build()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        UebernahmeRessourceMeldung akmTrNEU = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTrNEU.setLeitungen(null);
        AKUser user = new AKUser();

        when(wbciMeldungService.findAutomatableAkmTRsForWitaProcesing()).thenReturn(Arrays.asList(akmTrANBW, akmTrNEU));
        doNothing().when(testling).checkOnlyOneHurricanOrderAssigned(akmTrNEU);
        doNothing().when(testling).checkOnylOneWitaVertNrInAkmTr(akmTrANBW);

        when(wbciWitaServiceFacade.createWitaVorgang(NEUBESTELLUNG, akmTrNEU.getVorabstimmungsId(), user))
                .thenReturn(new WitaCBVorgangBuilder().withCarrierRefNr(vertNrNEU).build());
        when(wbciWitaServiceFacade.createWitaVorgang(ANBIETERWECHSEL, akmTrANBW.getVorabstimmungsId(), user))
                .thenReturn(new WitaCBVorgangBuilder().withCarrierRefNr(vertNrANBW).build());

        Collection<String> result = testling.processAutomatableAkmTrs(user);
        verify(testling, never()).handleAutomationException(any(UebernahmeRessourceMeldung.class), any(TaskName.class), 
                any(Exception.class), any(AKUser.class));
        verify(wbciWitaServiceFacade, times(2)).createWitaVorgang(any(AkmTrAutomationTask.class), anyString(), eq(user));
        verify(testling).checkOnlyOneHurricanOrderAssigned(akmTrNEU);
        verify(testling).checkOnylOneWitaVertNrInAkmTr(akmTrANBW);
        assertEquals(result.size(), 2);
        assertTrue(result.contains(vertNrANBW));
        assertTrue(result.contains(vertNrNEU));
}

    @Test
    public void testProcessAutomatableAkmTrsException() throws Exception {
        String vertNrNEU = "200";
        UebernahmeRessourceMeldung akmTrANBW = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .withLeitungen(Sets.newHashSet(new LeitungBuilder().withVertragsnummer("V0001").build()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        UebernahmeRessourceMeldung akmTrNEU = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTrNEU.setLeitungen(null);
        AKUser user = new AKUser();

        when(wbciMeldungService.findAutomatableAkmTRsForWitaProcesing()).thenReturn(Arrays.asList(akmTrANBW, akmTrNEU));
        doNothing().when(testling).checkOnlyOneHurricanOrderAssigned(akmTrNEU);
        WbciValidationException exp = new WbciValidationException("TEST");
        doThrow(exp).when(testling).checkOnylOneWitaVertNrInAkmTr(akmTrANBW);
        when(wbciWitaServiceFacade.createWitaVorgang(NEUBESTELLUNG, akmTrNEU.getVorabstimmungsId(), user))
                .thenReturn(new WitaCBVorgangBuilder().withCarrierRefNr(vertNrNEU).build());

        Collection<String> result = testling.processAutomatableAkmTrs(user);
        verify(testling).handleAutomationException(akmTrANBW, WITA_SEND_ANBIETERWECHSEL, exp, user);
        verify(wbciWitaServiceFacade).createWitaVorgang(NEUBESTELLUNG, akmTrNEU.getVorabstimmungsId(), user);
        verify(wbciWitaServiceFacade, never()).createWitaVorgang(ANBIETERWECHSEL, akmTrANBW.getVorabstimmungsId(), user);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(vertNrNEU));
    }


    @Test
    public void testProcessAutomatableIncomingAkmTrsNoWitaOrdersToCancel() {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WBCI_CDM_VERSION, GeschaeftsfallTyp.VA_KUE_MRN);
        WbciRequest va = new VorabstimmungsAnfrageTestBuilder<>()
                .buildValid(WBCI_CDM_VERSION, GeschaeftsfallTyp.VA_KUE_MRN);
        AKUser user = new AKUser();

        when(wbciMeldungService.findAutomatableIncomingAkmTRsProcesing()).thenReturn(Collections.singletonList(akmTr));
        when(wbciKuendigungsService.getCancellableWitaVertragsnummern(anyString())).thenReturn(null);
        when(wbciElektraServiceMock.updatePortKennungTnbTx(anyString(), any(AKUser.class))).thenReturn(getOkElektraResponse());
        when(wbciCommonService.findLastWbciRequest(anyString())).thenReturn(va);

        Collection<String> result = testling.processAutomatableIncomingAkmTrs(user);
        assertEquals(result.size(), 0);
        verify(wbciElektraServiceMock).updatePortKennungTnbTx(eq(akmTr.getVorabstimmungsId()), eq(user));
        verify(testling, never()).handleAutomationException(any(Meldung.class), eq(WITA_SEND_KUENDIGUNG), any(Exception.class), eq(user));
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTaskNewTx(
                eq(akmTr.getVorabstimmungsId()),
                eq(akmTr),
                eq(WITA_SEND_KUENDIGUNG),
                eq(AutomationStatus.COMPLETED),
                eq(WbciAutomationServiceImpl.KEINE_WITA_KUENDIGUNGEN_NOTWENDIG),
                eq(user));
        verify(wbciCommonService).findLastWbciRequest(akmTr.getWbciGeschaeftsfall().getVorabstimmungsId());
        verify(wbciCommonService).closeProcessing(va);
    }

    @Test
    public void testProcessAutomatableIncomingAkmTrsException() {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WBCI_CDM_VERSION, GeschaeftsfallTyp.VA_KUE_MRN);
        AKUser user = new AKUser();

        when(wbciElektraServiceMock.updatePortKennungTnbTx(anyString(), any(AKUser.class))).thenReturn(getOkElektraResponse());
        when(wbciMeldungService.findAutomatableIncomingAkmTRsProcesing()).thenReturn(Collections.singletonList(akmTr));
        WbciServiceException exp = new WbciServiceException("TEST");
        when(wbciKuendigungsService.getCancellableWitaVertragsnummern(anyString())).thenThrow(exp);

        testling.processAutomatableIncomingAkmTrs(user);
        verify(testling).handleAutomationException(akmTr, WITA_SEND_KUENDIGUNG, exp, user);
        verify(wbciCommonService, never()).closeProcessing(any(WbciRequest.class));
    }

    @DataProvider
    public Object[][] processAutomatableIncomingAkmTrsDP() {
        return new  Object[][] {
                {GeschaeftsfallTyp.VA_KUE_MRN, true},
                {GeschaeftsfallTyp.VA_KUE_ORN, false},
                {GeschaeftsfallTyp.VA_RRNP, false}
        };
    }

    @Test(dataProvider = "processAutomatableIncomingAkmTrsDP")
    public void testProcessAutomatableIncomingAkmTrs(GeschaeftsfallTyp gfTyp, boolean updatePortKennungExpected) {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WBCI_CDM_VERSION, gfTyp);
        AKUser user = new AKUser();

        SortedSet<String> witaVtrNrs = new TreeSet<>();
        witaVtrNrs.add("123456789");

        WitaCBVorgang witaCBVorgang = new WitaCBVorgangBuilder().withCarrierRefNr("5555555").setPersist(false).build();

        when(wbciMeldungService.findAutomatableIncomingAkmTRsProcesing()).thenReturn(Collections.singletonList(akmTr));
        when(wbciKuendigungsService.getCancellableWitaVertragsnummern(akmTr.getVorabstimmungsId())).thenReturn(witaVtrNrs);
        when(wbciElektraServiceMock.updatePortKennungTnbTx(akmTr.getVorabstimmungsId(), user)).thenReturn(getOkElektraResponse());
        when(wbciWitaServiceFacade.createWitaCancellations(akmTr, witaVtrNrs, user))
                .thenReturn(Collections.singletonList(witaCBVorgang));

        Collection<String> result = testling.processAutomatableIncomingAkmTrs(user);
        assertEquals(result.size(), 1);
        assertTrue(result.contains(witaCBVorgang.getCarrierRefNr()));

        verify(wbciMeldungService).findAutomatableIncomingAkmTRsProcesing();
        verify(wbciKuendigungsService).getCancellableWitaVertragsnummern(akmTr.getVorabstimmungsId());
        verify(wbciElektraServiceMock, updatePortKennungExpected ? times(1) : never()).updatePortKennungTnbTx(akmTr.getVorabstimmungsId(), user);
        verify(wbciWitaServiceFacade).createWitaCancellations(akmTr, witaVtrNrs, user);
    }

    @Test
    public void testProcessAutomatableIncomingAkmTrsWithErrorInPkiAuf() {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(false)
                .buildValid(WBCI_CDM_VERSION, GeschaeftsfallTyp.VA_KUE_MRN);
        AKUser user = new AKUser();

        SortedSet<String> witaVtrNrs = new TreeSet<>();
        witaVtrNrs.add("123456789");

        when(wbciMeldungService.findAutomatableIncomingAkmTRsProcesing()).thenReturn(Collections.singletonList(akmTr));
        when(wbciKuendigungsService.getCancellableWitaVertragsnummern(anyString())).thenReturn(witaVtrNrs);
        when(wbciElektraServiceMock.updatePortKennungTnbTx(akmTr.getVorabstimmungsId(), user)).thenReturn(getErrorElektraResponse());

        Collection<String> result = testling.processAutomatableIncomingAkmTrs(user);
        assertEquals(result.size(), 0);

        verify(wbciElektraServiceMock, times(1)).updatePortKennungTnbTx(akmTr.getVorabstimmungsId(), user);
        verify(wbciWitaServiceFacade, never()).createWitaCancellations(akmTr, witaVtrNrs, user);
    }

    private ElektraResponseDto getOkElektraResponse() {
        return new ElektraResponseDtoBuilder()
                .withStatus(ResponseStatus.OK)
                .build();
    }

    private ElektraResponseDto getErrorElektraResponse() {
        return new ElektraResponseDtoBuilder()
                .withStatus(ResponseStatus.ERROR)
                .withModifications("Some error")
                .build();
    }

    @Test
    public void testHandleAutomationException() throws Exception {
        String vaId = "DEU.MNET.VH001";
        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        ruemVa.getWbciGeschaeftsfall().setVorabstimmungsId(vaId);
        
        Exception exp = new WbciAutomationValidationException("VAL");
        TaskName task = WITA_SEND_NEUBESTELLUNG;
        testling.handleAutomationException(ruemVa, task, exp, null);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTaskNewTx(vaId, ruemVa, task, ERROR, "VAL", null);

        exp = new WbciServiceException("EXP");
        testling.handleAutomationException(ruemVa, task, exp, null);
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTaskNewTx(vaId, ruemVa, task, ERROR, Throwables.getStackTraceAsString(exp), null);
    }

    @Test
    public void testCheckOnylOneWitaVertNrInAkmTr() throws Exception {
        String vertNr = "V000001ANBW";
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .withLeitungen(Sets.newHashSet(new LeitungBuilder().withVertragsnummer(vertNr).build()))
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        testling.checkOnylOneWitaVertNrInAkmTr(akmTr);
        verify(wbciCommonService, never()).findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);

        akmTr.setLeitungen(null);
        RueckmeldungVorabstimmung ruemVA = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(new TechnischeRessourceBuilder().withVertragsnummer(vertNr).build())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVA);
        testling.checkOnylOneWitaVertNrInAkmTr(akmTr);
        verify(wbciCommonService).findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class, expectedExceptionsMessageRegExp =
            "F.r die Vorabstimmung 'DEU.MNET.VH.*' ist eine automatische Erzeugung des WITA-Vorgangs ist nicht m.glich, " +
                    "da die Anzahl der zu .bernehmenden Leitung in der RUEM-VA ungleich 1 ist - WITA-Vertragsnummern: V000001ANBW, V000002ANBW")
    public void testCheckOnylOneWitaVertNrInAkmTrExcpetionRuemVA() throws Exception {
        String vertNr = "V000001ANBW";
        String vertNr2 = "V000002ANBW";
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTr.setLeitungen(null);
        RueckmeldungVorabstimmung ruemVA = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(new TechnischeRessourceBuilder().withVertragsnummer(vertNr).build())
                .addTechnischeRessource(new TechnischeRessourceBuilder().withVertragsnummer(vertNr2).build())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVA);
        testling.checkOnylOneWitaVertNrInAkmTr(akmTr);
        verify(wbciCommonService).findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class, expectedExceptionsMessageRegExp =
            "F.r die Vorabstimmung 'DEU.MNET.VH.*' ist eine automatische Erzeugung des WITA-Vorgangs ist nicht m.glich, " +
                    "da die Anzahl der zu .bernehmenden Leitung in der AKM-TR ungleich 1 ist - WITA-Vertragsnummern: V000001ANBW, V000002ANBW")
    public void testCheckOnylOneWitaVertNrInAkmTrExcpetionAkmTr() throws Exception {
        String vertNr = "V000001ANBW";
        String vertNr2 = "V000002ANBW";
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .addLeitung(new LeitungBuilder().withVertragsnummer(vertNr).build())
                .addLeitung(new LeitungBuilder().withVertragsnummer(vertNr2).build())
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        testling.checkOnylOneWitaVertNrInAkmTr(akmTr);
        verify(wbciCommonService, never()).findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
    }

    @Test
    public void testCheckOnlyOneHurricanOrderAssigned() throws Exception {
        Long billingOrderNo = 111L;
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setBillingOrderNoOrig(billingOrderNo);
        when(wbciCommonService.getWbciRelevantHurricanOrderNos(billingOrderNo, null)).thenReturn(Sets.newHashSet(billingOrderNo));

        testling.checkOnlyOneHurricanOrderAssigned(akmTr);
        verify(wbciCommonService).getWbciRelevantHurricanOrderNos(billingOrderNo, null);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class,
            expectedExceptionsMessageRegExp = "Der Vorabstimmung '.*' wurde keinen Taifun-Auftrag zugeordnet")
    public void testCheckOnlyOneHurricanOrderAssignedNoTaifnOrder() throws Exception {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setBillingOrderNoOrig(null);

        testling.checkOnlyOneHurricanOrderAssigned(akmTr);
    }

    @Test(expectedExceptions = WbciAutomationValidationException.class,
            expectedExceptionsMessageRegExp = "Für die Vorabstimmung 'DEU.MNET.VH.*' ist eine automatische Erzeugung des " +
                    "WITA-Vorgangs ist nicht möglich, da die Anzahl der zugewiesenen Hurrican-Aufträge ungleich 1 ist" +
                    " - Hurrican-Auftrag-IDs: ..., ...")
    public void testCheckOnlyOneHurricanOrderAssignedNoSingleHurricanOrder() throws Exception {
        Long billingOrderNo = 111L;
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(true)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        akmTr.getWbciGeschaeftsfall().setBillingOrderNoOrig(billingOrderNo);
        when(wbciCommonService.getWbciRelevantHurricanOrderNos(billingOrderNo, null)).thenReturn(Sets.newHashSet(billingOrderNo, 112L));
        testling.checkOnlyOneHurricanOrderAssigned(akmTr);
    }

    @Test
    public void testProcessAutomatableErlmTvsNoActiveWitaCbv() {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciMeldungService.findAutomatableTvErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlmTv));
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlmTv.getVorabstimmungsId())).thenReturn(null);
        
        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableErlmTvs(user);
        Assert.assertTrue(result.isEmpty());
        verify(testling).handleAutomationException(eq(erlmTv), eq(TaskName.WITA_SEND_TV), any(WbciAutomationValidationException.class), eq(user));
        verify(wbciWitaServiceFacade, never()).doWitaTerminverschiebung(anyString(), anyLong(), any(AKUser.class), any(TamUserTask.TamBearbeitungsStatus.class));
    }


    @Test
    public void testProcessAutomatableErlmTvsWbciStornoExist() {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciMeldungService.findAutomatableTvErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlmTv));
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                .withRandomId()
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                .setPersist(false).build();
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlmTv.getVorabstimmungsId())).thenReturn(cbVorgang);

        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableErlmTvs(user);
        Assert.assertTrue(result.isEmpty());
        verify(testling).handleAutomationException(eq(erlmTv), eq(TaskName.WITA_SEND_TV), any(WbciAutomationValidationException.class), eq(user));
        verify(wbciWitaServiceFacade, never()).doWitaTerminverschiebung(anyString(), anyLong(), any(AKUser.class), any(TamUserTask.TamBearbeitungsStatus.class));
    }

    @Test
    public void testProcessAutomatableErlmTvsSuccess() {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .withWechseltermin(LocalDate.now().plusDays(10))
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciMeldungService.findAutomatableTvErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlmTv));
        
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withRandomId().setPersist(false).build();
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlmTv.getVorabstimmungsId())).thenReturn(cbVorgang);

        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableErlmTvs(user);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.iterator().next(), erlmTv.getWbciGeschaeftsfall().getVorabstimmungsId());

        verify(wbciWitaServiceFacade).doWitaTerminverschiebung(erlmTv.getVorabstimmungsId(), cbVorgang.getId(), user, TV_60_TAGE);
        verify(testling, never()).handleAutomationException(any(Meldung.class), any(TaskName.class), 
                any(WbciAutomationValidationException.class), eq(user));
    }

    @Test
    public void testProcessAutomatableErlmTvsFailureOnWitaTv() {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withRandomId().setPersist(false).build();
        
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .withWechseltermin(LocalDate.now().plusDays(10))
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciMeldungService.findAutomatableTvErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlmTv));

        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlmTv.getVorabstimmungsId())).thenReturn(cbVorgang);

        when(wbciWitaServiceFacade.doWitaTerminverschiebung(erlmTv.getVorabstimmungsId(), cbVorgang.getId(), null, TV_60_TAGE))
                .thenThrow(new WbciServiceException("WITA TV Failure!"));

        Collection<String> result = testling.processAutomatableErlmTvs(null);
        Assert.assertTrue(result.isEmpty());

        verify(wbciWitaServiceFacade).doWitaTerminverschiebung(erlmTv.getVorabstimmungsId(), cbVorgang.getId(), null, TV_60_TAGE);
        verify(testling).handleAutomationException(eq(erlmTv), eq(TaskName.WITA_SEND_TV),
                any(WbciAutomationValidationException.class), (AKUser) isNull());
    }


    @Test
    public void testProcessAutomatableErlmNoTvBecauseSameDate() {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withRandomId().setPersist(false).build();
    
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTestBuilder()
                .withWechseltermin(DateConverterUtils.asLocalDate(cbVorgang.getVorgabeMnet()))
                .buildValidForTv(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciMeldungService.findAutomatableTvErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlmTv));

        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlmTv.getVorabstimmungsId())).thenReturn(cbVorgang);

        Collection<String> result = testling.processAutomatableErlmTvs(null);
        Assert.assertTrue(result.isEmpty());
        verify(testling, never()).handleAutomationException(any(Meldung.class), any(TaskName.class), 
                any(WbciAutomationValidationException.class), (AKUser) isNull());
        verify(wbciWitaServiceFacade, never()).doWitaTerminverschiebung(anyString(), anyLong(), any(AKUser.class), any(TamUserTask.TamBearbeitungsStatus.class));
    }

    @Test
    public void testProcessAutomatableStrAufhErlmsNoActiveWitaCbv() {
        ErledigtmeldungStornoAuf erlm = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        when(wbciMeldungService.findAutomatableStrAufhErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlm));
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlm.getVorabstimmungsId())).thenReturn(null);

        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableStrAufhErlms(user);
        Assert.assertTrue(result.isEmpty());
        verify(testling).handleAutomationException(eq(erlm), eq(TaskName.WITA_SEND_STORNO), any(WbciAutomationValidationException.class), eq(user));
        verify(wbciWitaServiceFacade, never()).doWitaStorno(any(ErledigtmeldungStornoAuf.class), anyLong(), any(AKUser.class));
    }

    @Test
    public void testProcessAutomatableStrAufhErlmsWbciStornoExist() {
        ErledigtmeldungStornoAuf erlm = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        when(wbciMeldungService.findAutomatableStrAufhErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlm));
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder()
                .withRandomId()
                .withAenderungsKennzeichen(AenderungsKennzeichen.STORNO)
                .setPersist(false).build();
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlm.getVorabstimmungsId())).thenReturn(cbVorgang);

        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableErlmTvs(user);
        Assert.assertTrue(result.isEmpty());
        verify(testling, never()).handleAutomationException(eq(erlm), eq(TaskName.WITA_SEND_STORNO), any(WbciAutomationValidationException.class), eq(user));
        verify(wbciWitaServiceFacade, never()).doWitaStorno(any(ErledigtmeldungStornoAuf.class), anyLong(), any(AKUser.class));
    }

    @Test
    public void testProcessAutomatableStrAufhErlmsSuccess() {
        ErledigtmeldungStornoAuf erlm = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .withWechseltermin(LocalDate.now().plusDays(10))
                .buildValidForStorno(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        when(wbciMeldungService.findAutomatableStrAufhErlmsForWitaProcessing()).thenReturn(Collections.singletonList(erlm));

        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withRandomId().setPersist(false).build();
        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(erlm.getVorabstimmungsId())).thenReturn(cbVorgang);

        AKUser user = new AKUser();
        Collection<String> result = testling.processAutomatableStrAufhErlms(user);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.iterator().next(), erlm.getWbciGeschaeftsfall().getVorabstimmungsId());

        verify(wbciWitaServiceFacade).doWitaStorno(erlm, cbVorgang.getId(), user);
        verify(testling, never()).handleAutomationException(any(Meldung.class), any(TaskName.class),
                any(WbciAutomationValidationException.class), eq(user));
    }

    @Test
    public void testProcessAutomatableStrAufhErlmsFailureOnWitaStorno() {
        WitaCBVorgang cbVorgang = new WitaCBVorgangBuilder().withRandomId().setPersist(false).build();

        ErledigtmeldungStornoAuf strErlm = (ErledigtmeldungStornoAuf) new ErledigtmeldungTestBuilder()
                .buildValidForStorno(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN, RequestTyp.STR_AUFH_AUF);
        when(wbciMeldungService.findAutomatableStrAufhErlmsForWitaProcessing()).thenReturn(Collections.singletonList(strErlm));

        when(wbciWitaServiceFacade.findSingleActiveWitaCbVorgang(strErlm.getVorabstimmungsId())).thenReturn(cbVorgang);

        when(wbciWitaServiceFacade.doWitaStorno(strErlm, cbVorgang.getId(), null))
                .thenThrow(new WbciServiceException("WITA Storno Failure!"));

        Collection<String> result = testling.processAutomatableStrAufhErlms(null);
        Assert.assertTrue(result.isEmpty());

        verify(wbciWitaServiceFacade).doWitaStorno(strErlm, cbVorgang.getId(), null);
        verify(testling).handleAutomationException(eq(strErlm), eq(TaskName.WITA_SEND_STORNO),
                any(WbciAutomationValidationException.class), (AKUser) isNull());
    }

    @Test
    public void canWitaOrderBeProcessedAutomaticallyNotAutomatable() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test
    public void canWitaOrderBeProcessedAutomaticallyKlaerfall() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAutomatable(true)
                        .withKlaerfall(true)
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "keine RessourcenUebernahme und keine Zuordnung zu einem Taifun-Auftrag -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_1() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .withBillingOrderNoOrig(null)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(false)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
        verify(wbciCommonService, never()).getWbciRelevantHurricanOrderNos(anyLong(), any());
    }

    @Test(description = "keine RessourcenUebernahme und Zuordnung von mehreren Taifun-Auftraege -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_2() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnBuilder()
                .withAufnehmenderEKP(CarrierCode.MNET)
                .withAutomatable(true)
                .withKlaerfall(false)
                .withBillingOrderNoOrig(1L)
                .withNonBillingRelevantOrderNos(new HashSet<>(Collections.singletonList(2L)))
                .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(false)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);
        when(wbciCommonService.getWbciRelevantHurricanOrderNos(wbciGeschaeftsfall.getBillingOrderNoOrig(),
                wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs())).thenReturn(new HashSet<>(Arrays.asList(1L, 2L)));

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "keine RessourcenUebernahme und Zuordnung von einem Taifun-Auftrag, aber die M-net-Technologie ist nicht WITA kompatibel -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_3() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .withBillingOrderNoOrig(1L)
                        .withMnetTechnologie(Technologie.ADSL_SA)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(false)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);
        when(wbciCommonService.getWbciRelevantHurricanOrderNos(wbciGeschaeftsfall.getBillingOrderNoOrig(),
                wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs())).thenReturn(new HashSet<>(Collections.singletonList(1L)));

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "keine RessourcenUebernahme und Zuordnung von einem Taifun-Auftrag und die M-net-Technologie ist WITA kompatibel -> Automatisierung moeglich")
    public void canWitaOrderBeProcessedAutomatically_4() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .withBillingOrderNoOrig(1L)
                        .withMnetTechnologie(Technologie.TAL_DSL)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(false)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);
        when(wbciCommonService.getWbciRelevantHurricanOrderNos(wbciGeschaeftsfall.getBillingOrderNoOrig(),
                wbciGeschaeftsfall.getNonBillingRelevantOrderNoOrigs())).thenReturn(new HashSet<>(Collections.singletonList(1L)));

        assertTrue(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "RessourcenUebernahme und mehrere WitaVertragsnummer in der AkmTr -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_5() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(true)
                        .addLeitung(new LeitungBuilder().withVertragsnummer("123").build())
                        .addLeitung(new LeitungBuilder().withVertragsnummer("124").build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "RessourcenUebernahme und genau eine WitaVertragsnummer in der AkmTr -> Automatisierung moeglich")
    public void canWitaOrderBeProcessedAutomatically_6() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(true)
                        .addLeitung(new LeitungBuilder().withVertragsnummer("123").build())
                        .addLeitung(new LeitungBuilder().withLineId("123").build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        assertTrue(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "RessourcenUebernahme, keine WitaVertragsnummer in der AkmTr und keine WitaVertragsnummer in der RuemVa -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_7() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(true)
                        .addLeitung(new LeitungBuilder().withLineId("123").build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        RueckmeldungVorabstimmung ruemVa =
                new RueckmeldungVorabstimmungBuilder()
                        .withTechnischeRessourcen(new HashSet<>())
                        .build();
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "RessourcenUebernahme, keine WitaVertragsnummer in der AkmTr und mehrere WitaVertragsnummer in der RuemVa -> Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_8() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(true)
                        .addLeitung(new LeitungBuilder().withLineId("123").build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        RueckmeldungVorabstimmung ruemVa =
                new RueckmeldungVorabstimmungBuilder()
                        .addTechnischeRessource(
                                new TechnischeRessourceBuilder()
                                        .withVertragsnummer("123")
                                        .build())
                        .addTechnischeRessource(
                                new TechnischeRessourceBuilder()
                                        .withVertragsnummer("124")
                                        .build())
                        .build();
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

    @Test(description = "RessourcenUebernahme, keine WitaVertragsnummer in der AkmTr und genau eine WitaVertragsnummer in der RuemVa -> Automatisierung moeglich")
    public void canWitaOrderBeProcessedAutomatically_9() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(true)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        UebernahmeRessourceMeldung akmTr =
                new UebernahmeRessourceMeldungTestBuilder()
                        .withUebernahme(true)
                        .addLeitung(new LeitungBuilder().withLineId("123").build())
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.getLastUebernahmeRessourceMeldung(vorabstimmungsId)).thenReturn(akmTr);

        RueckmeldungVorabstimmung ruemVa =
                new RueckmeldungVorabstimmungBuilder()
                        .addTechnischeRessource(
                                new TechnischeRessourceBuilder()
                                        .withVertragsnummer("123")
                                        .build())
                        .build();
        when(wbciCommonService.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);

        assertTrue(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }
    
    @Test(description = "AufnehmenderEKP <> M-Net", expectedExceptions = WbciServiceException.class)
    public void canWitaOrderBeProcessedAutomatically_10() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAutomatable(true)
                        .withVorabstimmungsId("123")
                        .withKlaerfall(true)
                        .withAufnehmenderEKP(CarrierCode.DTAG)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId);
    }

    @Test(description = "WBCI Vorgang nicht automatisierbar geflaggt - Automatisierung nicht moeglich")
    public void canWitaOrderBeProcessedAutomatically_11() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfallKueMrn wbciGeschaeftsfall =
                new WbciGeschaeftsfallKueMrnBuilder()
                        .withAufnehmenderEKP(CarrierCode.MNET)
                        .withAutomatable(false)
                        .withKlaerfall(false)
                        .build();
        when(wbciCommonService.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        assertFalse(testling.canWitaOrderBeProcessedAutomatically(vorabstimmungsId));
    }

}
