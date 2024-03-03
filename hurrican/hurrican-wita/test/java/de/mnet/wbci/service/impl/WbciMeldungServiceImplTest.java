/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 28.08.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.AutomationTask.AutomationStatus.*;
import static de.mnet.wbci.model.AutomationTask.TaskName.*;
import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.WbciCdmVersion.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isNull;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import javax.validation.*;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragDatenBuilder;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.base.exceptions.StoreException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.mnet.common.exceptions.MessageProcessingException;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.InvalidRequestStatusChangeException;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.Abbruchmeldung;
import de.mnet.wbci.model.AbbruchmeldungStornoAen;
import de.mnet.wbci.model.AbbruchmeldungStornoAuf;
import de.mnet.wbci.model.AbbruchmeldungTechnRessource;
import de.mnet.wbci.model.AbbruchmeldungTerminverschiebung;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.Erledigtmeldung;
import de.mnet.wbci.model.ErledigtmeldungStornoAen;
import de.mnet.wbci.model.ErledigtmeldungStornoAuf;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.IOType;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.MessageProcessingMetadata;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.TechnischeRessource;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.UebernahmeRessourceMeldung;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciGeschaeftsfallRrnp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.AbbruchmeldungTestBuilder;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.LeitungBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceBuilder;
import de.mnet.wbci.model.builder.MeldungPositionAbbruchmeldungTechnRessourceTestBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaBuilder;
import de.mnet.wbci.model.builder.MeldungPositionRueckmeldungVaTestBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungBuilder;
import de.mnet.wbci.model.builder.RueckmeldungVorabstimmungTestBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceBuilder;
import de.mnet.wbci.model.builder.TechnischeRessourceTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.UebernahmeRessourceMeldungTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallRrnpTestBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciGeschaeftsfallService;
import de.mnet.wbci.service.WbciGeschaeftsfallStatusUpdateService;
import de.mnet.wbci.service.WbciRequestStatusUpdateService;
import de.mnet.wbci.service.WbciSendMessageService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.service.WbciWitaServiceFacade;
import de.mnet.wbci.validation.helper.ConstraintViolationHelper;
import de.mnet.wita.service.WitaConfigService;

/**
 *
 */
@Test(groups = UNIT)
public class WbciMeldungServiceImplTest {

    @Mock
    protected WbciGeschaeftsfallStatusUpdateService gfStatusUpdateServiceMock;
    @Mock
    private WbciSendMessageService wbciSendMessageServiceMock;
    @Mock
    private WbciCommonService wbciCommonServiceMock;
    @Mock
    private WbciDao wbciDaoMock;
    @Mock
    private WbciRequestStatusUpdateService requestStatusUpdateService;
    @Mock
    private WbciValidationService validationServiceMock;
    @Mock
    private ConstraintViolationHelper constraintViolationHelperMock;
    @Mock
    private WbciGeschaeftsfallService wbciGeschaeftsfallService;
    @Mock
    private WitaConfigService witaConfigService;
    @Mock
    private WbciWitaServiceFacade wbciWitaServiceFacade;
    @Mock
    private CCAuftragService ccAuftragService;
    @InjectMocks
    @Spy
    private WbciMeldungServiceImpl testling = new WbciMeldungServiceImpl();
    private String meldungCodes = "123";
    private LocalDateTime meldungProcessedAt = LocalDateTime.now();

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateAbbruchmeldung() {
        final Abbruchmeldung meldung = new AbbruchmeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        verfiyMeldungCreatedAndSend(meldung, wbciGeschaeftsfallKueMrn);
        verify(wbciGeschaeftsfallService).closeGeschaeftsfall(wbciGeschaeftsfallKueMrn.getId());
    }

    @Test
    public void testCreateAbbruchmeldungTechnRessourceWithRessourcenuebernahme() {
        final AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessourceTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        when(wbciCommonServiceMock.isResourceUebernahmeRequested(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()))
                .thenReturn(true);
        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        verfiyMeldungCreatedAndSend(meldung, wbciGeschaeftsfallKueMrn);
        verify(wbciCommonServiceMock).isResourceUebernahmeRequested(wbciGeschaeftsfallKueMrn.getVorabstimmungsId());
        verify(wbciGeschaeftsfallService).issueClarified(eq(wbciGeschaeftsfallKueMrn.getId()), (AKUser) isNull(),
                eq(WbciMeldungServiceImpl.REASON_ABBM_TR_SEND));
    }

    @Test
    public void testCreateAbbruchmeldungTechnRessourceWithoutRessourcenuebernahme() {
        final AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessourceTestBuilder()
                .withMeldungsPositionen(new HashSet<>(Arrays.asList(
                        new MeldungPositionAbbruchmeldungTechnRessourceTestBuilder()
                                .withMeldungsCode(MeldungsCode.LID_OVAID)
                                .buildValid(V1, VA_KUE_MRN)
                )))
                .buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        when(wbciCommonServiceMock.isResourceUebernahmeRequested(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()))
                .thenReturn(false);
        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        verfiyMeldungCreatedAndSend(meldung, wbciGeschaeftsfallKueMrn);
        verify(wbciCommonServiceMock).isResourceUebernahmeRequested(wbciGeschaeftsfallKueMrn.getVorabstimmungsId());
        verify(wbciGeschaeftsfallService).issueClarified(eq(wbciGeschaeftsfallKueMrn.getId()), (AKUser) isNull(),
                eq(WbciMeldungServiceImpl.REASON_ABBM_TR_SEND));
    }

    @DataProvider
    public Object[][] createAbbruchmeldungTechnRessourceNotAllowedDataProvider() {
        return new Object[][] {
                { MeldungsCode.UETN_NM },
                { MeldungsCode.UETN_BB },
        };
    }

    @Test(expectedExceptions = WbciServiceException.class, dataProvider = "createAbbruchmeldungTechnRessourceNotAllowedDataProvider")
    public void testCreateAbbruchmeldungTechnRessourceNotAllowed(MeldungsCode meldungsCode) {
        final AbbruchmeldungTechnRessource meldung = new AbbruchmeldungTechnRessourceTestBuilder()
                .withMeldungsPositionen(new HashSet<>(Arrays.asList(
                        new MeldungPositionAbbruchmeldungTechnRessourceTestBuilder()
                                .withMeldungsCode(meldungsCode)
                                .buildValid(V1, VA_KUE_MRN)
                )))
                .buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        when(wbciCommonServiceMock.isResourceUebernahmeRequested(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()))
                .thenReturn(false);
        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());
    }

    @Test
    public void testCreateWbciMeldung() {
        final RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        verfiyMeldungCreatedAndSend(meldung, wbciGeschaeftsfallKueMrn);
        verify(wbciGeschaeftsfallService, never()).closeGeschaeftsfall(anyLong());
    }

    @DataProvider
    public Object[][] testCreateUebernahmeRessourceMeldungDataProvider() {
        return new Object[][] {
                // ada code present, but resourceuebernahme=false -> NO exception
                { true, false, false },
                // ada code present, resourceuebernahme=true -> exception (ada with resourceuebernahme not allowed)
                { true, true, true },
                // no ada code present, resourceuebernahme=true -> NO exception
                { false, true, false },
                // no ada code present, resourceuebernahme=false -> NO exception
                { false, false, false }
        };
    }

    @Test(dataProvider = "testCreateUebernahmeRessourceMeldungDataProvider")
    public void testCreateUebernahmeRessourceMeldung(boolean ruemVaWithAdaCode, boolean uebernahme, boolean expectServiceException) {
        final UebernahmeRessourceMeldung akmTrMeldung = new UebernahmeRessourceMeldungTestBuilder()
                .withUebernahme(uebernahme)
                .buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        RueckmeldungVorabstimmung ruemVaMock = Mockito.mock(RueckmeldungVorabstimmung.class);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);

        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfallKueMrn.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVaMock);
        when(ruemVaMock.hasADAMeldungsCode()).thenReturn(ruemVaWithAdaCode);

        try {
            testling.createAndSendWbciMeldung(akmTrMeldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());
            Assert.assertFalse(expectServiceException);
            verfiyMeldungCreatedAndSend(akmTrMeldung, wbciGeschaeftsfallKueMrn);
            if (uebernahme) {
                verify(ruemVaMock).hasADAMeldungsCode();
            }
        }
        catch (WbciServiceException e) {
            Assert.assertTrue(expectServiceException, String.format("Exception thrown, but no exception was expected: '%s'", e.getMessage()));
        }
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testCreateWithConstraintViolation() throws Exception {
        final RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder().buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        Set<ConstraintViolation<RueckmeldungVorabstimmung>> errorsMock = Mockito.mock(Set.class);

        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfallKueMrn);
        when(validationServiceMock.checkWbciMessageForErrors(any(CarrierCode.class), any(RueckmeldungVorabstimmung.class)))
                .thenReturn(errorsMock);
        when(errorsMock.size()).thenReturn(2);
        when(constraintViolationHelperMock.generateErrorMsg(errorsMock)).thenReturn("ERRROR MSG");

        testling.createAndSendWbciMeldung(meldung, wbciGeschaeftsfallKueMrn.getVorabstimmungsId());

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfallKueMrn.getVorabstimmungsId()));
        verify(wbciSendMessageServiceMock, never()).sendAndProcessMessage(eq(meldung));
        verify(validationServiceMock).checkWbciMessageForErrors(meldung.getEKPPartner(), meldung);
    }

    @Test
    public void testVerifyErlmTvTermin() throws Exception {
        final TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder().withTvTermin(
                DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate()).buildValid(V1, VA_KUE_MRN);
        final ErledigtmeldungTerminverschiebung erlm = new ErledigtmeldungTestBuilder()
                .withAenderungsIdRef(tv.getAenderungsId())
                .withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate())
                .buildForTv(MeldungsCode.TV_OK);

        prepareMocksForCreateAndSendMeldung(tv.getWbciGeschaeftsfall());
        when(wbciDaoMock.findWbciRequestByChangeId(tv.getVorabstimmungsId(), tv.getAenderungsId(), TerminverschiebungsAnfrage.class))
                .thenReturn(tv);
        testling.createAndSendWbciMeldung(erlm, tv.getVorabstimmungsId());
        verfiyMeldungCreatedAndSend(erlm, tv.getWbciGeschaeftsfall());
        verify(validationServiceMock).assertErlmTvTermin(erlm);
    }


    @Test
    public void testPostProcessAkmTrWithoutUebernahme() {
        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        akmTr.setUebernahme(false);

        WbciCommonService commonServiceMock = Mockito.mock(WbciCommonService.class);

        testling.postProcessIncomingAkmTr(akmTr);
        verify(commonServiceMock, times(0)).findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class);
        verify(wbciWitaServiceFacade, times(0)).updateWitaVorabstimmungAbgebend(akmTr);
    }

    @DataProvider(name = "postProcessAkmTrWithoutUebernahmeNotPossibleDataProvider")
    public Object[][] postProcessAkmTrWithoutUebernahmeNotPossibleDataProvider() {
        return new Object[][] {
                { Technologie.TAL_ISDN, 0 },
                { Technologie.TAL_DSL, 0 },
                { Technologie.FTTB, 0 },
                { Technologie.FTTH, 0 },
                { Technologie.KUPFER, 1 },
        };
    }

    @Test(dataProvider = "postProcessAkmTrWithoutUebernahmeNotPossibleDataProvider")
    public void testPostProcessAkmTrWithoutUebernahmeNotPossible(Technologie technologieForRuemVa, int countOfSendMessage) {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        UebernahmeRessourceMeldung akmTr = new UebernahmeRessourceMeldungTestBuilder()
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        akmTr.setUebernahme(true);
        akmTr.setLeitungen(null);
        akmTr.setAbsender(CarrierCode.DTAG);

        RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        ruemVa.setTechnologie(technologieForRuemVa);

        when(wbciCommonServiceMock.findLastForVaId(akmTr.getVorabstimmungsId(), RueckmeldungVorabstimmung.class))
                .thenReturn(ruemVa);
        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfall);

        when(wbciCommonServiceMock.isResourceUebernahmeRequested(akmTr.getVorabstimmungsId())).thenReturn(
                akmTr.isUebernahme());
        testling.postProcessIncomingAkmTr(akmTr);
        verify(wbciSendMessageServiceMock, times(countOfSendMessage)).sendAndProcessMessage(any(Meldung.class));
        verify(wbciWitaServiceFacade, times(countOfSendMessage > 0 ? 0 : 1)).updateWitaVorabstimmungAbgebend(akmTr);
    }


    @Test
    public void testPostProcessValidVertragsNrIncomingAkmTr() throws Exception {
        final String vertragsNr = "V00001";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final TechnischeRessource technischeRessource = new TechnischeRessourceTestBuilder()
                .withVertragsnummer(vertragsNr)
                .build();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(technischeRessource)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .addLeitung(new LeitungBuilder()
                        .withVertragsnummer(vertragsNr)
                        .build())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfall);

        testling.postProcessIncomingAkmTr(akmtr);
        verify(wbciDaoMock, never()).store(any(Meldung.class));
        verify(wbciSendMessageServiceMock, never()).sendAndProcessMessage(any(Meldung.class));
        verify(wbciWitaServiceFacade).updateWitaVorabstimmungAbgebend(akmtr);
    }

    @Test
    public void testPostProcessInvalidVertragsNrIncomingAkmTr() throws Exception {
        final String vertragsNr = "V00001";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final TechnischeRessource technischeRessource = new TechnischeRessourceTestBuilder()
                .withVertragsnummer(vertragsNr)
                .build();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(technischeRessource)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .addLeitung(new LeitungBuilder()
                        .withVertragsnummer("V00005")
                        .build())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfall);

        testling.postProcessIncomingAkmTr(akmtr);
        verify(wbciDaoMock).store(any(AbbruchmeldungTechnRessource.class));
        verify(wbciSendMessageServiceMock).sendAndProcessMessage(any(AbbruchmeldungTechnRessource.class));
        verify(wbciWitaServiceFacade, times(0)).updateWitaVorabstimmungAbgebend(akmtr);
    }

    @Test
    public void testPostProcessValidLineIdIncomingAkmTr() throws Exception {
        final String lineID = "DTAG.00001";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final TechnischeRessource technischeRessource = new TechnischeRessourceTestBuilder()
                .withIdentifizierer(lineID)
                .build();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(technischeRessource)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .addLeitung(new LeitungBuilder()
                        .withLineId(null)
                        .build())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfall);

        testling.postProcessIncomingAkmTr(akmtr);
        verify(wbciDaoMock, never()).store(any(Meldung.class));
        verify(wbciSendMessageServiceMock, never()).sendAndProcessMessage(any(Meldung.class));
        verify(wbciWitaServiceFacade).updateWitaVorabstimmungAbgebend(akmtr);
    }

    @Test
    public void testPostProcessInvalidLineIdIncomingAkmTr() throws Exception {
        final String lineID = "DTAG.00001";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final TechnischeRessource technischeRessource = new TechnischeRessourceTestBuilder()
                .withIdentifizierer(lineID)
                .build();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(technischeRessource)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);
        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .addLeitung(new LeitungBuilder()
                        .withLineId("DTAG.0002")
                        .build())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .buildValid(V1, VA_KUE_MRN);

        when(wbciCommonServiceMock.findLastForVaId(wbciGeschaeftsfall.getVorabstimmungsId(), RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        prepareMocksForCreateAndSendMeldung(wbciGeschaeftsfall);

        testling.postProcessIncomingAkmTr(akmtr);
        verify(wbciDaoMock).store(any(AbbruchmeldungTechnRessource.class));
        verify(wbciSendMessageServiceMock).sendAndProcessMessage(any(AbbruchmeldungTechnRessource.class));
        verify(wbciWitaServiceFacade, times(0)).updateWitaVorabstimmungAbgebend(akmtr);
    }

    @Test
    public void testProcessIncomingMeldung() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .buildValid(V1, VA_KUE_MRN);
        meldung.setWbciGeschaeftsfall(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(meldung.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, meldung);

        verify(wbciDaoMock).findWbciGeschaeftsfall(meldung.getVorabstimmungsId());
        Assert.assertNotNull(meldung.getWbciGeschaeftsfall());
        verify(wbciDaoMock).store(meldung);
    }

    @Test(expectedExceptions = MessageProcessingException.class)
    public void testProcessIncomingMeldungWithUnknownVorabstimmungsId() {
        final RueckmeldungVorabstimmung meldung = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findById(anyString(), eq(WbciGeschaeftsfall.class))).thenReturn(null);

        testling.processIncomingMeldung(metadata, meldung);
    }

    @Test
    public void testProcessIncomingMeldungWithInvalidRequestStatus_AKM_TR_EMPFANGEN() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .buildValid(V1, VA_KUE_MRN);
        akmtr.setWbciGeschaeftsfall(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        prepareThrowInvalidRequestStatusChangeException(AKM_TR_EMPFANGEN, AKM_TR_EMPFANGEN);

        testling.processIncomingMeldung(metadata, akmtr);

        verify(wbciDaoMock, times(1)).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(akmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);

        ArgumentCaptor<MessageProcessingMetadata> metadataArgument = ArgumentCaptor.forClass(MessageProcessingMetadata.class);
        verify(wbciSendMessageServiceMock).sendAndProcessMessage(metadataArgument.capture(), any(AbbruchmeldungTechnRessource.class));
        Assert.assertFalse(metadataArgument.getValue().isPostProcessMessage());
        Assert.assertFalse(metadataArgument.getValue().isResponseToDuplicateVaRequest());
    }

    @Test
    public void testProcessIncomingMeldungWithInvalidRequestStatus_RUEM_VA_EMPFANGEN() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .buildValid(V1, VA_KUE_MRN);
        akmtr.setWbciGeschaeftsfall(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        doThrow(new InvalidRequestStatusChangeException(1L, RUEM_VA_EMPFANGEN, RUEM_VA_EMPFANGEN)).when(testling)
                .updateCorrelatingRequestForMeldung(akmtr);

        testling.processIncomingMeldung(metadata, akmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(akmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService).markGfForClarification(akmtr.getWbciGeschaeftsfall().getId(), "Doppelte RUEM-VA", null);
    }

    @Test
    public void shouldMarkGfAsKlaerfallAsIncomingMeldungHasValidationErrors() {
        simulateValidationErrorOrWarning(false);
    }

    @Test
    public void shouldMarkGfAsKlaerfallAsIncomingMeldungHasValidationWarnings() {
        simulateValidationErrorOrWarning(true);
    }

    private void simulateValidationErrorOrWarning(boolean simulateWarning) {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .buildValid(V1, VA_KUE_MRN);
        akmtr.setWbciGeschaeftsfall(null);
        akmtr.setPortierungskennungPKIauf(null);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();
        String errorOrWarningMessage = "Some validation warning or error";

        Set<ConstraintViolation<UebernahmeRessourceMeldung>> errorsOrWarnings = (Set<ConstraintViolation<UebernahmeRessourceMeldung>>) Mockito.mock(Set.class);
        when(errorsOrWarnings.size()).thenReturn(1);

        when(wbciDaoMock.findWbciGeschaeftsfall(akmtr.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        if (simulateWarning) {
            when(validationServiceMock.checkWbciMessageForWarnings(akmtr.getAbsender(), akmtr)).thenReturn(errorsOrWarnings);
            when(constraintViolationHelperMock.generateWarningForInboundMsg(errorsOrWarnings)).thenReturn(errorOrWarningMessage);
        }
        else {
            when(validationServiceMock.checkWbciMessageForErrors(akmtr.getAbsender(), akmtr)).thenReturn(errorsOrWarnings);
            when(constraintViolationHelperMock.generateErrorMsgForInboundMsg(errorsOrWarnings)).thenReturn(errorOrWarningMessage);
        }

        testling.processIncomingMeldung(metadata, akmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId());
        Assert.assertEquals(akmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        if (simulateWarning) {
            verify(wbciGeschaeftsfallService, never()).markGfForClarification(anyLong(), anyString(), (AKUser) isNull());
            verify(wbciCommonServiceMock).addComment(wbciGeschaeftsfall.getVorabstimmungsId(), errorOrWarningMessage, null);
        }
        else {
            verify(wbciGeschaeftsfallService).markGfForClarification(anyLong(), anyString(), (AKUser) isNull());
        }
        verify(wbciDaoMock).store(akmtr);
    }

    @Test
    public void shouldRemoveADAMeldungscodeFromRuemVa() {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallRrnp wbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .addMeldungPosition(new MeldungPositionRueckmeldungVaTestBuilder()
                        .withMeldungsCode(MeldungsCode.ADAHSNR)
                        .build())
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);

        testling.cleanseIncomingMeldung(ruemVa);

        Assert.assertTrue(CollectionUtils.isEmpty(ruemVa.getMeldungsPositionen()));
    }

    @Test
    public void shouldRemovePKIAufFromAkmTrMeldungForGeschaeftsfallKueOrn() {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallKueOrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueOrnTestBuilder()
                .buildValid(V1, GeschaeftsfallTyp.VA_KUE_ORN);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withPortierungskennungPKIauf("PKIAuf")
                .buildValid(V1, GeschaeftsfallTyp.VA_KUE_ORN);

        testling.cleanseIncomingMeldung(akmtr);

        Assert.assertNull(akmtr.getPortierungskennungPKIauf());
    }

    @Test
    public void shouldRemoveZWAMeldungscodeFromRuemVa() {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallRrnp wbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .addMeldungPosition(new MeldungPositionRueckmeldungVaTestBuilder()
                        .withMeldungsCode(MeldungsCode.ZWA)
                        .build())
                .addMeldungPosition(new MeldungPositionRueckmeldungVaTestBuilder()
                        .withMeldungsCode(MeldungsCode.NAT)
                        .build())
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);

        testling.cleanseIncomingMeldung(ruemVa);

        Assert.assertEquals(ruemVa.getMeldungsPositionen().size(), 1);
        Assert.assertEquals(ruemVa.getMeldungsPositionen().iterator().next().getMeldungsCode(), MeldungsCode.NAT);
    }

    @Test
    public void shouldRemoveTechnischeResourceFromRuemVa() {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallRrnp wbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .addTechnischeRessource(new TechnischeRessourceTestBuilder().buildValid(V1, GeschaeftsfallTyp.VA_RRNP))
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);

        testling.cleanseIncomingMeldung(ruemVa);

        Assert.assertNull(ruemVa.getTechnischeRessourcen());
    }

    @Test
    public void shouldRemoveTechnologyFromRuemVa() {
        final long wbciGeschaeftsfallId = 1L;
        final WbciGeschaeftsfallRrnp wbciGeschaeftsfall = new WbciGeschaeftsfallRrnpTestBuilder()
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);
        wbciGeschaeftsfall.setId(wbciGeschaeftsfallId);

        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId())
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .withTechnologie(Technologie.ADSL_SA)
                .buildValid(V1, GeschaeftsfallTyp.VA_RRNP);

        testling.cleanseIncomingMeldung(ruemVa);

        Assert.assertNull(ruemVa.getTechnologie());
    }

    @Test
    public void testProcessIncomingMeldungAbbmTrAsKlaerfall_1() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final AbbruchmeldungTechnRessource abbmtr = new AbbruchmeldungTechnRessourceTestBuilder()
                .addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(MeldungsCode.UETN_NM)
                        .withMeldungsText(MeldungsCode.UETN_NM.getStandardText())
                        .build())
                .buildValid(V1, VA_KUE_MRN);
        abbmtr.setWbciGeschaeftsfall(null);
        abbmtr.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, abbmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(abbmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService).markGfForClarification(abbmtr.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
    }

    @Test
    public void testProcessIncomingMeldungAbbmTrAsKlaerfall_2() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final AbbruchmeldungTechnRessource abbmtr = new AbbruchmeldungTechnRessourceTestBuilder()
                .addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(MeldungsCode.UETN_NM)
                        .withMeldungsText(MeldungsCode.UETN_NM.getStandardText())
                        .build())
                .addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(MeldungsCode.UETN_BB)
                        .withMeldungsText(MeldungsCode.UETN_BB.getStandardText())
                        .build())
                .buildValid(V1, VA_KUE_MRN);
        abbmtr.setWbciGeschaeftsfall(null);
        abbmtr.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, abbmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(abbmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService).markGfForClarification(abbmtr.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
    }

    @Test
    public void testProcessIncomingMeldungAbbmTrNotAsKlaerfall() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final AbbruchmeldungTechnRessource abbmtr = new AbbruchmeldungTechnRessourceTestBuilder()
                .addMeldungPosition(new MeldungPositionAbbruchmeldungTechnRessourceBuilder()
                        .withMeldungsCode(MeldungsCode.UETN_BB)
                        .withMeldungsText(MeldungsCode.UETN_BB.getStandardText())
                        .build())
                .buildValid(V1, VA_KUE_MRN);
        abbmtr.setWbciGeschaeftsfall(null);
        abbmtr.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, abbmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(abbmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService, never()).markGfForClarification(abbmtr.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
    }

    @Test
    public void testProcessIncomingMeldungRuemVaWithoutStrAen() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final RueckmeldungVorabstimmung ruemva = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        ruemva.setWbciGeschaeftsfall(null);
        ruemva.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, ruemva);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(ruemva.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService, never()).markGfForClarification(ruemva.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
        verify(witaConfigService, never()).getWbciRuemVaPortingDateDifference();
    }


    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Der Geschäftsfall mit der " +
            "VorabstimmungsId '.*' befindet sich in einem inkonsistenten Zustand. Der Wechseltermin für die vorangegangene " +
            "Vorabstimmung ist nicht gesetzt obwohl die Vorabstimmung mit Storno-Aenderung erfolgreich storniert wurde.")
    public void testProcessIncomingMeldungRuemVaWithoutExistingRuemVa() {
        String strAenVorabstimmungsId = "DEU.MNET.V1234556789";
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStrAenVorabstimmungsId(strAenVorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);

        final RueckmeldungVorabstimmung ruemva = new RueckmeldungVorabstimmungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        ruemva.setWbciGeschaeftsfall(null);
        ruemva.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());

        final WbciGeschaeftsfallKueMrn cancelledWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withVorabstimmungsId(strAenVorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciDaoMock.findWbciGeschaeftsfall(strAenVorabstimmungsId)).thenReturn(cancelledWbciGeschaeftsfall);

        testling.processIncomingMeldung(metadata, ruemva);
    }

    @DataProvider
    public Object[][] processIncomingMeldungRuemVaProvider() {
        // [Meldung, Typ, Direction, Expected Status]
        return new Object[][] {
                { LocalDate.of(2014, 1, 9), LocalDate.of(2014, 1, 9), 5, false },
                { LocalDate.of(2014, 1, 9), LocalDate.of(2014, 1, 16), 5, false },
                { LocalDate.of(2014, 1, 9), LocalDate.of(2014, 1, 17), 5, true },
                { LocalDate.of(2014, 1, 9), LocalDate.of(2014, 1, 9), 0, false },
                { LocalDate.of(2014, 1, 9), LocalDate.of(2014, 1, 10), 0, true },
                { LocalDate.of(2014, 1, 9), LocalDate.of(2013, 12, 17), 0, false },
        };
    }

    @Test(dataProvider = "processIncomingMeldungRuemVaProvider")
    public void testProcessIncomingMeldungRuemVa(LocalDate originalPortingDate, LocalDate newPortingDate,
            int portingDateDifference, boolean expectedIsKlaerfall) {
        String strAenVorabstimmungsId = "DEU.MNET.V1234567890";
        Long geschaeftsfallId = 1L;
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStrAenVorabstimmungsId(strAenVorabstimmungsId)
                .buildValid(V1, VA_KUE_MRN);
        wbciGeschaeftsfall.setId(geschaeftsfallId);

        final WbciGeschaeftsfallKueMrn cancelledWbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withStrAenVorabstimmungsId(strAenVorabstimmungsId)
                .withWechseltermin(originalPortingDate)
                .buildValid(V1, VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        when(wbciDaoMock.findWbciGeschaeftsfall(strAenVorabstimmungsId)).thenReturn(cancelledWbciGeschaeftsfall);
        when(witaConfigService.getWbciRuemVaPortingDateDifference()).thenReturn(portingDateDifference);

        final RueckmeldungVorabstimmung newRuemva = new RueckmeldungVorabstimmungTestBuilder()
                .withWechseltermin(newPortingDate)
                .buildValid(V1, VA_KUE_MRN);
        newRuemva.setWbciGeschaeftsfall(null);
        newRuemva.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        testling.processIncomingMeldung(metadata, newRuemva);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        verify(wbciDaoMock).findWbciGeschaeftsfall(strAenVorabstimmungsId);
        verify(testling).synchChangeDateWithOrder(newRuemva.getWechseltermin(), wbciGeschaeftsfall.getAuftragId());
        Assert.assertEquals(newRuemva.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(witaConfigService).getWbciRuemVaPortingDateDifference();
        final String expected = String.format(WbciMeldungServiceImpl.REASON_RUEM_VA_DIFFERENT_PORTING_DATE,
                Date.from(newPortingDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), portingDateDifference, Date.from(originalPortingDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        verify(wbciGeschaeftsfallService, expectedIsKlaerfall ? times(1) : never())
                .markGfForClarification(eq(geschaeftsfallId), eq(expected), (AKUser) isNull());
    }

    @Test
    public void testProcessIncomingMeldungWithInvalidRequestStatus_ABBM_RUEM_VA() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        akmtr.setWbciGeschaeftsfall(null);
        akmtr.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        doThrow(new InvalidRequestStatusChangeException(1L, ABBM_EMPFANGEN, RUEM_VA_EMPFANGEN)).when(testling)
                .updateCorrelatingRequestForMeldung(akmtr);

        testling.processIncomingMeldung(metadata, akmtr);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()));
        Assert.assertEquals(akmtr.getWbciGeschaeftsfall(), wbciGeschaeftsfall);
        verify(wbciGeschaeftsfallService).markGfForClarification(akmtr.getWbciGeschaeftsfall().getId(), "RUEM-VA direkt nach ABBM", null);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testProcessIncomingMeldungWithInvalidRequestStatus() {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);

        final UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldungTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        akmtr.setWbciGeschaeftsfall(null);
        akmtr.setVorabstimmungsIdRef(wbciGeschaeftsfall.getVorabstimmungsId());
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        when(wbciDaoMock.findWbciGeschaeftsfall(wbciGeschaeftsfall.getVorabstimmungsId())).thenReturn(wbciGeschaeftsfall);
        doThrow(new InvalidRequestStatusChangeException(1L, VA_EMPFANGEN, VA_EMPFANGEN)).when(testling).updateCorrelatingRequestForMeldung(akmtr);

        testling.processIncomingMeldung(metadata, akmtr);
    }

    @DataProvider(name = "vaMeldungenWithExpectedRequestStatus")
    public Object[][] vaMeldungenWithExpectedRequestStatus() {
        // [Meldung, Typ, Direction, Expected Status]
        return new Object[][] {
                { Mockito.mock(Abbruchmeldung.class), IOType.IN, MeldungTyp.ABBM, ABBM_EMPFANGEN },
                { Mockito.mock(Abbruchmeldung.class), IOType.OUT, MeldungTyp.ABBM, ABBM_VERSENDET },
                { Mockito.mock(AbbruchmeldungTechnRessource.class), IOType.IN, MeldungTyp.ABBM_TR, ABBM_TR_EMPFANGEN },
                { Mockito.mock(AbbruchmeldungTechnRessource.class), IOType.OUT, MeldungTyp.ABBM_TR, ABBM_TR_VERSENDET },
                { Mockito.mock(UebernahmeRessourceMeldung.class), IOType.IN, MeldungTyp.AKM_TR, AKM_TR_EMPFANGEN },
                { Mockito.mock(UebernahmeRessourceMeldung.class), IOType.OUT, MeldungTyp.AKM_TR, AKM_TR_VERSENDET }
        };
    }

    @Test(dataProvider = "vaMeldungenWithExpectedRequestStatus")
    public void testRequestStatusUpdateForVaMeldung(Meldung<?> meldungMock, IOType ioType, MeldungTyp meldungTyp,
            WbciRequestStatus expectedIncomingStatus) {
        String vorabstimmungsId = "123";
        WbciGeschaeftsfallStatus newGfStatus = WbciGeschaeftsfallStatus.ACTIVE;
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);

        prepareMocksForStatusUpdate(meldungMock, gfMock, vorabstimmungsId, ioType, meldungTyp, meldungCodes, meldungProcessedAt);
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(expectedIncomingStatus, expectedIncomingStatus, meldungMock)).thenReturn(newGfStatus);
        when(meldungMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getId()).thenReturn(1L);

        testling.updateCorrelatingRequestForMeldung(meldungMock);

        verify(requestStatusUpdateService).updateVorabstimmungsAnfrageStatus(vorabstimmungsId, expectedIncomingStatus, meldungCodes, meldungTyp, meldungProcessedAt);
        verify(gfStatusUpdateServiceMock).updateGeschaeftsfallStatus(1L, newGfStatus);
        verify(wbciGeschaeftsfallService, never()).markGfForClarification(meldungMock.getWbciGeschaeftsfall().getId(), "ABBM-TR empfangen", null);
    }

    @DataProvider(name = "ruemVaWithExpectedRequestStatus")
    public Object[][] ruemVaWithExpectedRequestStatuses() {
        // [Meldung, Typ, Direction, Expected Status]
        return new Object[][] {
                { Mockito.mock(RueckmeldungVorabstimmung.class), IOType.IN, MeldungTyp.RUEM_VA, RUEM_VA_EMPFANGEN, DateCalculationHelper.getDateInWorkingDaysFromNow(7).toLocalDate() },
                { Mockito.mock(RueckmeldungVorabstimmung.class), IOType.OUT, MeldungTyp.RUEM_VA, RUEM_VA_VERSENDET, DateCalculationHelper.getDateInWorkingDaysFromNow(7).toLocalDate() },
        };
    }

    @Test(dataProvider = "ruemVaWithExpectedRequestStatus")
    public void testRequestStatusUpdateForRUEMVA(RueckmeldungVorabstimmung meldungMock, IOType ioType, MeldungTyp meldungTyp, WbciRequestStatus expectedIncomingStatus, LocalDate wechselTermin) {
        String vorabstimmungsId = "123";
        WbciGeschaeftsfallStatus newGfStatus = WbciGeschaeftsfallStatus.ACTIVE;
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);

        prepareMocksForStatusUpdate(meldungMock, gfMock, vorabstimmungsId, ioType, meldungTyp, meldungCodes, meldungProcessedAt);
        when(meldungMock.getWechseltermin()).thenReturn(wechselTermin);
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(expectedIncomingStatus, expectedIncomingStatus, meldungMock)).thenReturn(newGfStatus);
        when(meldungMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getId()).thenReturn(1L);

        testling.updateCorrelatingRequestForMeldung(meldungMock);

        verify(requestStatusUpdateService).updateVorabstimmungsAnfrageStatus(vorabstimmungsId, expectedIncomingStatus, meldungCodes, meldungTyp, meldungProcessedAt);
        verify(wbciGeschaeftsfallService).updateVorabstimmungsAnfrageWechseltermin(vorabstimmungsId, wechselTermin);
        verify(gfStatusUpdateServiceMock).updateGeschaeftsfallStatus(1L, newGfStatus);
    }

    @DataProvider(name = "stornoMeldungenWithExpectedRequestStatuses")
    public Object[][] stornoMeldungenWithExpectedRequestStatuses() {
        // [Meldung, Typ, Direction, Expected Status]
        return new Object[][] {
                { Mockito.mock(AbbruchmeldungStornoAen.class), IOType.IN, MeldungTyp.ABBM, STORNO_ABBM_EMPFANGEN, AKM_TR_VERSENDET },
                { Mockito.mock(AbbruchmeldungStornoAen.class), IOType.OUT, MeldungTyp.ABBM, STORNO_ABBM_VERSENDET, AKM_TR_EMPFANGEN },
                { Mockito.mock(AbbruchmeldungStornoAuf.class), IOType.IN, MeldungTyp.ABBM, STORNO_ABBM_EMPFANGEN, AKM_TR_VERSENDET },
                { Mockito.mock(AbbruchmeldungStornoAuf.class), IOType.OUT, MeldungTyp.ABBM, STORNO_ABBM_VERSENDET, AKM_TR_EMPFANGEN },
                { Mockito.mock(ErledigtmeldungStornoAen.class), IOType.IN, MeldungTyp.ERLM, STORNO_ERLM_EMPFANGEN, AKM_TR_VERSENDET },
                { Mockito.mock(ErledigtmeldungStornoAen.class), IOType.OUT, MeldungTyp.ERLM, STORNO_ERLM_VERSENDET, AKM_TR_EMPFANGEN },
                { Mockito.mock(ErledigtmeldungStornoAuf.class), IOType.IN, MeldungTyp.ERLM, STORNO_ERLM_EMPFANGEN, AKM_TR_VERSENDET },
                { Mockito.mock(ErledigtmeldungStornoAuf.class), IOType.OUT, MeldungTyp.ERLM, STORNO_ERLM_VERSENDET, AKM_TR_EMPFANGEN }
        };
    }

    @Test(dataProvider = "stornoMeldungenWithExpectedRequestStatuses")
    public void testRequestStatusUpdateForStornoMeldung(Meldung<?> meldungMock, IOType ioType, MeldungTyp meldungTyp, WbciRequestStatus expectedStatus, WbciRequestStatus vaRequestStatus) {
        String vorabstimmungsId = "123";
        String stornoId = "234";
        WbciGeschaeftsfallStatus newGfStatus = WbciGeschaeftsfallStatus.ACTIVE;
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        VorabstimmungsAnfrage vaMock = Mockito.mock(VorabstimmungsAnfrage.class);

        when(wbciCommonServiceMock.findVorabstimmungsAnfrage(vorabstimmungsId)).thenReturn(vaMock);
        when(vaMock.getRequestStatus()).thenReturn(vaRequestStatus);
        when(meldungMock.getVorabstimmungsId()).thenReturn(vorabstimmungsId);
        when(meldungMock.getIoType()).thenReturn(ioType);
        when(meldungMock.getTyp()).thenReturn(meldungTyp);
        when(meldungMock.getMeldungsCodes()).thenReturn(meldungCodes);
        when(meldungMock.getProcessedAt()).thenReturn(Date.from(meldungProcessedAt.atZone(ZoneId.systemDefault()).toInstant()));
        if (MeldungTyp.ERLM.equals(meldungTyp)) {
            when(((Erledigtmeldung) meldungMock).getStornoIdRef()).thenReturn(stornoId);
        }
        else {
            when(((Abbruchmeldung) meldungMock).getStornoIdRef()).thenReturn(stornoId);
        }
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(expectedStatus, vaRequestStatus, meldungMock)).thenReturn(newGfStatus);
        when(meldungMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getId()).thenReturn(1L);

        testling.updateCorrelatingRequestForMeldung(meldungMock);

        verify(requestStatusUpdateService).updateStornoAnfrageStatus(vorabstimmungsId, stornoId, expectedStatus, meldungCodes, meldungTyp, meldungProcessedAt);
        verify(wbciGeschaeftsfallService, never()).updateVorabstimmungsAnfrageWechseltermin(anyString(), any(LocalDate.class));
        verify(gfStatusUpdateServiceMock).updateGeschaeftsfallStatus(1L, newGfStatus);
    }

    @DataProvider(name = "tvMeldungenWithExpectedRequestStatuses")
    public Object[][] tvMeldungenWithExpectedRequestStatuses() {
        // [Meldung, Typ, Direction, Expected Status]
        return new Object[][] {
                { Mockito.mock(AbbruchmeldungTerminverschiebung.class), IOType.IN, MeldungTyp.ABBM, TV_ABBM_EMPFANGEN, RUEM_VA_EMPFANGEN },
                { Mockito.mock(AbbruchmeldungTerminverschiebung.class), IOType.OUT, MeldungTyp.ABBM, TV_ABBM_VERSENDET, RUEM_VA_VERSENDET },
                { Mockito.mock(ErledigtmeldungTerminverschiebung.class), IOType.IN, MeldungTyp.ERLM, TV_ERLM_EMPFANGEN, RUEM_VA_EMPFANGEN },
                { Mockito.mock(ErledigtmeldungTerminverschiebung.class), IOType.OUT, MeldungTyp.ERLM, TV_ERLM_VERSENDET, RUEM_VA_VERSENDET }
        };
    }

    @Test(dataProvider = "tvMeldungenWithExpectedRequestStatuses")
    public void testRequestStatusUpdateForTvMeldung(Meldung<?> meldungMock, IOType ioType, MeldungTyp meldungTyp, WbciRequestStatus expectedStatus, WbciRequestStatus vaRequestStatus) {
        String vorabstimmungsId = "123";
        String tvId = "345";
        WbciGeschaeftsfallStatus newGfStatus = WbciGeschaeftsfallStatus.ACTIVE;
        WbciGeschaeftsfall gfMock = Mockito.mock(WbciGeschaeftsfall.class);
        VorabstimmungsAnfrage vaMock = Mockito.mock(VorabstimmungsAnfrage.class);

        when(wbciCommonServiceMock.findVorabstimmungsAnfrage(vorabstimmungsId)).thenReturn(vaMock);
        when(vaMock.getRequestStatus()).thenReturn(vaRequestStatus);
        when(meldungMock.getVorabstimmungsId()).thenReturn(vorabstimmungsId);
        when(meldungMock.getIoType()).thenReturn(ioType);
        when(meldungMock.getTyp()).thenReturn(meldungTyp);
        when(meldungMock.getMeldungsCodes()).thenReturn(meldungCodes);
        when(meldungMock.getProcessedAt()).thenReturn(Date.from(meldungProcessedAt.atZone(ZoneId.systemDefault()).toInstant()));
        if (MeldungTyp.ERLM.equals(meldungTyp)) {
            when(((Erledigtmeldung) meldungMock).getAenderungsIdRef()).thenReturn(tvId);
        }
        else {
            when(((Abbruchmeldung) meldungMock).getAenderungsIdRef()).thenReturn(tvId);
        }
        when(gfStatusUpdateServiceMock.lookupStatusBasedOnRequestStatusChange(expectedStatus, vaRequestStatus, meldungMock)).thenReturn(newGfStatus);
        when(meldungMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getId()).thenReturn(1L);

        testling.updateCorrelatingRequestForMeldung(meldungMock);

        verify(requestStatusUpdateService).updateTerminverschiebungAnfrageStatus(vorabstimmungsId, tvId, expectedStatus, meldungCodes, meldungTyp, meldungProcessedAt);
        if (meldungMock instanceof ErledigtmeldungTerminverschiebung) {
            verify(wbciGeschaeftsfallService).updateVorabstimmungsAnfrageWechseltermin(vorabstimmungsId, ((ErledigtmeldungTerminverschiebung) meldungMock).getWechseltermin());
        }
        else {
            verify(wbciGeschaeftsfallService, never()).updateVorabstimmungsAnfrageWechseltermin(anyString(), any(LocalDate.class));
        }
        verify(gfStatusUpdateServiceMock).updateGeschaeftsfallStatus(1L, newGfStatus);
    }

    @Test
    public void testSendResponseMeldungToInvalidInboundMessage() {
        final Abbruchmeldung meldung = new AbbruchmeldungTestBuilder().buildValid(V1, VA_KUE_MRN);
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfallKueMrn = new WbciGeschaeftsfallKueMrnTestBuilder()
                .buildValid(V1, VA_KUE_MRN);
        final MessageProcessingMetadata metadata = new MessageProcessingMetadata();

        // set meldung attributes to null that should be set by testling
        meldung.setIoType(null);
        meldung.setAbsender(null);
        meldung.setProcessedAt(null);

        testling.sendErrorResponse(metadata, meldung);

        Assert.assertNotNull(meldung.getIoType());
        Assert.assertNotNull(meldung.getAbsender());
        Assert.assertNotNull(meldung.getProcessedAt());
        verify(wbciSendMessageServiceMock).sendAndProcessMessage(metadata, meldung);
    }

    private void prepareMocksForCreateAndSendMeldung(WbciGeschaeftsfall wbciGeschaeftsfall) {
        when(wbciDaoMock.findWbciGeschaeftsfall(eq(wbciGeschaeftsfall.getVorabstimmungsId()))).thenReturn(wbciGeschaeftsfall);
    }

    private void prepareMocksForStatusUpdate(Meldung<?> meldungMock, WbciGeschaeftsfall gfMock, String vorabstimmungsId, IOType ioType, MeldungTyp meldungTyp, String meldungCodes, LocalDateTime meldungProcessedAt) {
        when(meldungMock.getVorabstimmungsId()).thenReturn(vorabstimmungsId);
        when(meldungMock.getIoType()).thenReturn(ioType);
        when(meldungMock.getTyp()).thenReturn(meldungTyp);
        when(meldungMock.getMeldungsCodes()).thenReturn(meldungCodes);
        when(meldungMock.getProcessedAt()).thenReturn(Date.from(meldungProcessedAt.atZone(ZoneId.systemDefault()).toInstant()));
        when(meldungMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
    }

    private <M extends Meldung> void verfiyMeldungCreatedAndSend(M meldung, WbciGeschaeftsfall originalWbciGeschaeftsfall) {
        Assert.assertEquals(meldung.getIoType(), IOType.OUT);
        Assert.assertEquals(meldung.getAbsender(), CarrierCode.MNET);
        Assert.assertEquals(meldung.getWbciGeschaeftsfall().getTyp(), originalWbciGeschaeftsfall.getTyp());
        Assert.assertEquals(meldung.getWbciGeschaeftsfall(), originalWbciGeschaeftsfall);

        verify(wbciDaoMock).findWbciGeschaeftsfall(eq(originalWbciGeschaeftsfall.getVorabstimmungsId()));
        verify(wbciSendMessageServiceMock).sendAndProcessMessage(eq(meldung));
        verify(validationServiceMock).checkWbciMessageForErrors(originalWbciGeschaeftsfall.getAbgebenderEKP(), meldung);

    }

    private void prepareThrowInvalidRequestStatusChangeException(final WbciRequestStatus currentStatus, final WbciRequestStatus newStatus) {
        doThrow(new InvalidRequestStatusChangeException(1L, currentStatus, newStatus)).when(requestStatusUpdateService).updateVorabstimmungsAnfrageStatus(anyString(), eq(newStatus), anyString(), any(MeldungTyp.class), any(LocalDateTime.class));
    }

    @Test
    public void sendAutomatedAkmTr() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(new TechnischeRessourceTestBuilder()
                        .withVertragsnummer("1234567890")
                        .buildValid(V1, VA_KUE_MRN))
                .buildValid(V1, VA_KUE_MRN);
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(1L)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        final String tnbKennung = "D001";
        when(wbciCommonServiceMock.getTnbKennung(wbciGeschaeftsfall.getAuftragId())).thenReturn(tnbKennung);
        doNothing().when(testling).createAndSendWbciMeldung(any(UebernahmeRessourceMeldung.class), anyString());
        doReturn(true).when(testling).isLeitungUebernahmePossible(ruemVa);

        AKUser user = new AKUser();
        testling.sendAutomatedAkmTr(vorabstimmungsId, user);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(testling).createAndSendWbciMeldung(argThat(new ArgumentMatcher<UebernahmeRessourceMeldung>() {
            @Override
            public boolean matches(Object o) {
                final UebernahmeRessourceMeldung akmtr = (UebernahmeRessourceMeldung) o;
                return akmtr.isUebernahme()
                        && !akmtr.isSichererHafen()
                        && akmtr.getPortierungskennungPKIauf().equals(tnbKennung)
                        && akmtr.getLeitungen().iterator().next().getVertragsnummer().equals("1234567890");
            }
        }), eq(vorabstimmungsId));
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(wbciGeschaeftsfall),
                any(UebernahmeRessourceMeldung.class), eq(WBCI_SEND_AKMTR),
                eq(COMPLETED), eq("Die AKM-TR wurde automatisiert und erfolgreich verschickt."), eq(user));
    }

    @Test
    public void sendAutomatedAkmTrWithoutWitaVertragsnr() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .withTechnischeRessourcen(null)
                .build();
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(1L)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        final String tnbKennung = "D001";
        when(wbciCommonServiceMock.getTnbKennung(wbciGeschaeftsfall.getAuftragId())).thenReturn(tnbKennung);
        doNothing().when(testling).createAndSendWbciMeldung(any(UebernahmeRessourceMeldung.class), anyString());
        doReturn(true).when(testling).isLeitungUebernahmePossible(ruemVa);

        AKUser user = new AKUser();
        testling.sendAutomatedAkmTr(vorabstimmungsId, user);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(testling).createAndSendWbciMeldung(argThat(new ArgumentMatcher<UebernahmeRessourceMeldung>() {
            @Override
            public boolean matches(Object o) {
                final UebernahmeRessourceMeldung akmtr = (UebernahmeRessourceMeldung) o;
                System.out.println(akmtr);
                return !akmtr.isUebernahme() && akmtr.isSichererHafen() && akmtr.getPortierungskennungPKIauf().equals(tnbKennung);
            }
        }), eq(vorabstimmungsId));
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(wbciGeschaeftsfall), 
                any(UebernahmeRessourceMeldung.class), eq(WBCI_SEND_AKMTR),
                eq(COMPLETED), eq("Die AKM-TR wurde automatisiert und erfolgreich verschickt."), eq(user));
    }

    @Test
    public void sendAutomatedAkmTrWithTwoWitaVertragsnr() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungTestBuilder()
                .addTechnischeRessource(new TechnischeRessourceTestBuilder()
                        .withVertragsnummer("1234567890")
                        .buildValid(V1, VA_KUE_MRN))
                .addTechnischeRessource(new TechnischeRessourceTestBuilder()
                        .withVertragsnummer("1234567891")
                        .buildValid(V1, VA_KUE_MRN))
                .buildValid(V1, VA_KUE_MRN);
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(1L)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenReturn(ruemVa);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);

        AKUser user = new AKUser();
        testling.sendAutomatedAkmTr(vorabstimmungsId, user);

        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        verify(testling, never()).createAndSendWbciMeldung(any(UebernahmeRessourceMeldung.class), anyString());
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(wbciGeschaeftsfall), eq(WBCI_SEND_AKMTR),
                eq(ERROR), argThat(new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        final String msg = (String) o;
                        return msg.contains("WbciServiceException")
                                && msg.contains("Eine automatisierte AMK-TR kann nur geschickt werden, wenn hoechstens eine WITA-Vertragsnummer in der RuemVA uebermittelt wurde!");
                    }
                }), eq(user)
        );
    }

    @Test
    public void sendAutomatedAkmTrExceptionThrown() {
        String vorabstimmungsId = "DEU.MNET.V000000001";
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withAuftragId(1L)
                .buildValid(V1, VA_KUE_MRN);
        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vorabstimmungsId)).thenReturn(wbciGeschaeftsfall);
        when(wbciCommonServiceMock.findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class)).thenThrow(Exception.class);

        AKUser user = new AKUser();
        testling.sendAutomatedAkmTr(vorabstimmungsId, user);

        verify(wbciCommonServiceMock).findWbciGeschaeftsfall(vorabstimmungsId);
        verify(wbciCommonServiceMock).findLastForVaId(vorabstimmungsId, RueckmeldungVorabstimmung.class);
        verify(testling, never()).createAndSendWbciMeldung(any(UebernahmeRessourceMeldung.class), anyString());
        verify(wbciGeschaeftsfallService).createOrUpdateAutomationTask(eq(wbciGeschaeftsfall), eq(WBCI_SEND_AKMTR),
                eq(ERROR), anyString(), eq(user));
    }

    @DataProvider
    public Object[][] isLeitungUebernahmePossibleDataProvider() {
        return new Object[][] {
                { Technologie.TAL_DSL, Technologie.TAL_ISDN, CarrierCode.DTAG, MeldungsCode.ZWA, "1234567890", true },
                { Technologie.TAL_DSL, Technologie.SONSTIGES, CarrierCode.DTAG, MeldungsCode.ZWA, "1234567890", true },
                { Technologie.TAL_DSL, Technologie.TAL_ISDN, CarrierCode.DTAG, MeldungsCode.ADAORT, "1234567890", false },
                { Technologie.TAL_DSL, Technologie.FTTB, CarrierCode.DTAG, MeldungsCode.ZWA, "1234567890", false },
                { Technologie.TAL_DSL, Technologie.KUPFER, CarrierCode.KABEL_DEUTSCHLAND, MeldungsCode.ZWA, "1234567890", false },
                { Technologie.TAL_DSL, Technologie.KUPFER, CarrierCode.DTAG, MeldungsCode.ZWA, "1234567890", true },
        };
    }

    @Test(dataProvider = "isLeitungUebernahmePossibleDataProvider")
    public void isLeitungUebernahmePossible(Technologie mnetTechnologie, Technologie ruemVaTechnologie,
            CarrierCode partnerCarrier, MeldungsCode meldungsCode, String witaVertragsNr, boolean expected) {
        final WbciGeschaeftsfallKueMrn wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder()
                .withMnetTechnologie(mnetTechnologie)
                .build();
        final RueckmeldungVorabstimmung ruemVa = new RueckmeldungVorabstimmungBuilder()
                .addTechnischeRessource(
                        new TechnischeRessourceBuilder()
                                .withVertragsnummer(witaVertragsNr)
                                .build()
                )
                .withTechnologie(ruemVaTechnologie)
                .withAbsender(partnerCarrier)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .addMeldungPosition(
                        new MeldungPositionRueckmeldungVaBuilder()
                                .withMeldungsCode(meldungsCode)
                                .build()
                )
                .build();
        Assert.assertEquals(testling.isLeitungUebernahmePossible(ruemVa), expected);
    }

    @Test
    public void testFindAutomatableAkmTRsForWitaProcesing() throws Exception {
        UebernahmeRessourceMeldung akmtr = new UebernahmeRessourceMeldung();
        when(wbciDaoMock.findAutomatableAkmTRsForWitaProcesing(anyListOf(Technologie.class))).thenReturn(Arrays.asList(akmtr));

        Collection<UebernahmeRessourceMeldung> result = testling.findAutomatableAkmTRsForWitaProcesing();
        assertNotNull(result);
        assertEquals(result.iterator().next(), akmtr);
}


    @Test
    public void testFindAutomatableTvErlmsForWitaProcessing() throws Exception {
        ErledigtmeldungTerminverschiebung erlmTv = new ErledigtmeldungTerminverschiebung();
        when(wbciDaoMock.findAutomateableTvErlmsForWitaProcessing(anyListOf(Technologie.class))).thenReturn(Arrays.asList(erlmTv));

        Collection<ErledigtmeldungTerminverschiebung> result = testling.findAutomatableTvErlmsForWitaProcessing();
        assertNotNull(result);
        assertEquals(result.iterator().next(), erlmTv);

        when(wbciDaoMock.findAutomateableTvErlmsForWitaProcessing(anyListOf(Technologie.class))).thenReturn(null);
        result = testling.findAutomatableTvErlmsForWitaProcessing();
        assertNotNull(result);
        assertEquals(result.size(), 0);
    }
    
    
    @Test
    public void testFindAutomatableStrAufhErlmsForWitaProcessing() {
        ErledigtmeldungStornoAuf erlmStrAuf = new ErledigtmeldungStornoAuf();
        when(wbciDaoMock.findAutomateableStrAufhErlmsForWitaProcessing(anyListOf(Technologie.class))).thenReturn(Arrays.asList(erlmStrAuf));

        Collection<ErledigtmeldungStornoAuf> result = testling.findAutomatableStrAufhErlmsForWitaProcessing();
        assertNotNull(result);
        assertEquals(result.iterator().next(), erlmStrAuf);

        when(wbciDaoMock.findAutomateableStrAufhErlmsForWitaProcessing(anyListOf(Technologie.class))).thenReturn(null);
        result = testling.findAutomatableStrAufhErlmsForWitaProcessing();
        assertNotNull(result);
        assertEquals(result.size(), 0);
    }


    @DataProvider(name = "testSynchChangeDateWithOrderDP")
    public Object[][] testSynchChangeDateWithOrderDP() {
        return new Object[][]{
                { new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.ERFASSUNG).setPersist(false).build(), true },
                { new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.TECHNISCHE_REALISIERUNG).setPersist(false).build(), false },
                { new AuftragDatenBuilder().withRandomAuftragId().withStatusId(AuftragStatus.IN_BETRIEB).setPersist(false).build(), false },
        };
    }

    @Test(dataProvider = "testSynchChangeDateWithOrderDP")
    public void testSynchChangeDateWithOrder(AuftragDaten auftragDaten, boolean expectChange) throws FindException, StoreException {
        when(ccAuftragService.findAuftragDatenByAuftragId(anyLong())).thenReturn(auftragDaten);

        LocalDate changeDate = LocalDate.now().plusDays(3);
        testling.synchChangeDateWithOrder(changeDate, auftragDaten.getAuftragId());
        verify(ccAuftragService).findAuftragDatenByAuftragId(auftragDaten.getAuftragId());

        if (expectChange) {
            verify(ccAuftragService).saveAuftragDaten(auftragDaten, false);
            assertEquals(auftragDaten.getVorgabeSCV(), Date.from(changeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        else {
            verify(ccAuftragService, never()).saveAuftragDaten(auftragDaten, false);
        }
    }

}
