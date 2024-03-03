/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.10.13
 */
package de.mnet.wbci.service.impl;

import static de.mnet.wbci.TestGroups.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.*;
import java.time.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import javax.validation.metadata.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.augustakom.common.tools.lang.Pair;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.exception.WbciValidationException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.ErledigtmeldungTerminverschiebung;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Meldung;
import de.mnet.wbci.model.MeldungsCode;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.RueckmeldungVorabstimmung;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciGeschaeftsfallKueMrn;
import de.mnet.wbci.model.WbciGeschaeftsfallKueOrn;
import de.mnet.wbci.model.WbciMessage;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.ErledigtmeldungTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAufAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueOrnTestBuilder;
import de.mnet.wbci.model.helper.WbciRequestHelperTest;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciValidationService;
import de.mnet.wbci.validation.constraints.CheckTvTerminNotBroughtForward;
import de.mnet.wbci.validation.groups.V1;
import de.mnet.wbci.validation.groups.V1Meldung;
import de.mnet.wbci.validation.groups.V1MeldungStorno;
import de.mnet.wbci.validation.groups.V1MeldungTv;
import de.mnet.wbci.validation.groups.V1MeldungVa;
import de.mnet.wbci.validation.groups.V1Request;
import de.mnet.wbci.validation.groups.V1RequestVa;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrn;
import de.mnet.wbci.validation.groups.V1RequestVaKueMrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaKueOrnWarn;
import de.mnet.wbci.validation.groups.V1RequestVaWarn;
import de.mnet.wbci.validation.helper.ValidationHelper;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = UNIT)
public class WbciValidationServiceImplTest {

    @InjectMocks
    private final WbciValidationService testling = new WbciValidationServiceImpl();

    @Mock
    private ValidationHelper validationHelperMock;

    @Mock
    private WbciDao wbciDaoMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private WitaConfigService configServiceMock;

    @Mock
    private WbciCommonService wbciCommonServiceMock;

    @Mock
    private VorabstimmungsAnfrage<WbciGeschaeftsfallKueMrn> vaMock;

    @Mock
    private TerminverschiebungsAnfrage<WbciGeschaeftsfallKueMrn> tvMock;

    @Mock
    private StornoAenderungAbgAnfrage<WbciGeschaeftsfallKueMrn> stornoMock;

    @Mock
    private RueckmeldungVorabstimmung ruemVaMock;

    @Mock
    private WbciGeschaeftsfallKueMrn gfMock;


    private Meldung wbciMeldung = new Meldung() {

        private static final long serialVersionUID = 9206668521617918118L;

        @NotNull
        String getA() {
            return null;
        }

        @NotNull(groups = V1.class)
        String getB() {
            return null;
        }

        @NotNull(groups = V1Meldung.class)
        String getC() {
            return null;
        }

        @NotNull(groups = V1MeldungVa.class)
        String getD() {
            return null;
        }

        @NotNull(groups = V1MeldungStorno.class)
        String getE() {
            return null;
        }

        @NotNull(groups = V1MeldungTv.class)
        String getF() {
            return null;
        }

    };
    private WbciRequest wbciRequest = new WbciRequest() {
        private static final long serialVersionUID = -3984255901202670746L;

        @NotNull
        String getA() {
            return null;
        }

        @NotNull(groups = V1.class)
        String getB() {
            return null;
        }

        @NotNull(groups = V1Request.class)
        String getC() {
            return null;
        }

        @NotNull(groups = V1RequestVa.class)
        String getD() {
            return null;
        }

        @NotNull(groups = V1RequestVaKueMrn.class)
        String getE() {
            return null;
        }

        @NotNull(groups = V1RequestVaWarn.class)
        String getF() {
            return null;
        }

        @NotNull(groups = V1RequestVaKueMrnWarn.class)
        String getG() {
            return null;
        }

        @NotNull(groups = V1RequestVaKueOrnWarn.class)
        String getH() {
            return null;
        }

        @Override
        public RequestTyp getTyp() {
            return RequestTyp.STR_AEN_ABG;
        }
    };

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(configServiceMock.getWbciCdmVersion(CarrierCode.DTAG)).thenReturn(WbciCdmVersion.V1);
    }

    @DataProvider(name = "validationErrors")
    public Object[][] validationErrors() {
        return new Object[][] {
                { wbciRequest, V1.class },
                { wbciRequest, V1Request.class },
                { wbciRequest, V1RequestVa.class },
                { wbciRequest, V1RequestVaKueMrn.class },
                { wbciMeldung, V1Meldung.class },
                { wbciMeldung, V1MeldungVa.class },
                { wbciMeldung, V1MeldungStorno.class },
                { wbciMeldung, V1MeldungTv.class },
        };
    }

    @DataProvider(name = "validationWarnings")
    public Object[][] validationWarnings() {
        return new Object[][] {
                { wbciRequest, V1RequestVaWarn.class },
                { wbciRequest, V1RequestVaKueMrnWarn.class },
                { wbciRequest, V1RequestVaKueOrnWarn.class },
                { wbciRequest, V1RequestVaKueOrnWarn.class },
                { wbciMeldung, null },
        };
    }

    @Test(dataProvider = "validationErrors")
    public void testCheckWbciMessageForErrors(WbciMessage wbciMessage, Class<?> group) {
        Class<?>[] groups = new Class<?>[] { group };
        when(validationHelperMock.getErrorValidationGroups(WbciCdmVersion.V1, wbciMessage)).thenReturn(
                groups);
        testling.checkWbciMessageForErrors(WbciCdmVersion.V1, wbciMessage);
        verify(validatorMock).validate(wbciMessage, groups);
        reset(validationHelperMock);
    }

    @Test(dataProvider = "validationErrors")
    public void testCheckWbciMessageForErrorsWithCarrierCode(WbciMessage wbciMessage, Class<?> group) {
        Class<?>[] groups = new Class<?>[] { group };
        when(validationHelperMock.getErrorValidationGroups(WbciCdmVersion.V1, wbciMessage)).thenReturn(
                groups);

        testling.checkWbciMessageForErrors(CarrierCode.DTAG, wbciMessage);
        verify(validatorMock).validate(wbciMessage, groups);
        verify(configServiceMock).getWbciCdmVersion(CarrierCode.DTAG);
        reset(validationHelperMock);
    }

    @Test(dataProvider = "validationWarnings")
    public void testCheckWbciMessageForWarning(WbciMessage wbciMessage, Class<?> group) {
        Class<?>[] groups = new Class<?>[] { group };
        when(validationHelperMock.getWarningValidationGroups(WbciCdmVersion.V1, wbciMessage)).thenReturn(
                groups);
        testling.checkWbciMessageForWarnings(WbciCdmVersion.V1, wbciMessage);
        verify(validatorMock).validate(wbciMessage, groups);
        reset(validationHelperMock);
    }

    @Test(dataProvider = "validationWarnings")
    public void testCheckWbciMessageForWarningsgWithCarrierCode(WbciMessage wbciMessage, Class<?> group) {
        Class<?>[] groups = new Class<?>[] { group };
        when(validationHelperMock.getWarningValidationGroups(WbciCdmVersion.V1, wbciMessage)).thenReturn(
                groups);

        testling.checkWbciMessageForWarnings(CarrierCode.DTAG, wbciMessage);
        verify(validatorMock).validate(wbciMessage, groups);
        verify(configServiceMock).getWbciCdmVersion(CarrierCode.DTAG);
        reset(validationHelperMock);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testExceptionNoEntityForErros() throws Exception {
        testling.checkWbciMessageForErrors(WbciCdmVersion.V1, null);
    }

    @Test(expectedExceptions = WbciServiceException.class)
    public void testExceptionNoEntityForWarnings() throws Exception {
        testling.checkWbciMessageForWarnings(WbciCdmVersion.V1, null);
    }

    @Test
    public void testAbgebenderEKPMatch() throws Exception {
        String strAenVorabstimmungsId = "DEU.MNET.V12345678";
        WbciGeschaeftsfallKueOrn cancelledGf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.EINS_UND_EINS)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        when(wbciDaoMock.findWbciGeschaeftsfall(strAenVorabstimmungsId)).thenReturn(cancelledGf);

        testling.assertAbgebenderEKPMatchesLinkedAbgebenderEKP(CarrierCode.EINS_UND_EINS, strAenVorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Der abgebende EKP der stornierten Vorabstimmung \\(EINS_UND_EINS\\) stimmt nicht mit dem abgebenden EKP der neuen Vorabstimmung \\(DTAG\\) ueberein")
    public void testAbgebenderEKPMismatch() throws Exception {
        String strAenVorabstimmungsId = "DEU.MNET.V12345678";
        WbciGeschaeftsfallKueOrn cancelledGf = new WbciGeschaeftsfallKueOrnTestBuilder()
                .withAbgebenderEKP(CarrierCode.EINS_UND_EINS)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_ORN);

        when(wbciDaoMock.findWbciGeschaeftsfall(strAenVorabstimmungsId)).thenReturn(cancelledGf);

        testling.assertAbgebenderEKPMatchesLinkedAbgebenderEKP(CarrierCode.DTAG, strAenVorabstimmungsId);
    }

    @Test
    public void testLinkedVaWithoutTodos() throws Exception {
        String strAenVorabstimmungsId = "DEU.MNET.V12345678";

        when(wbciDaoMock.hasFaultyAutomationTasks(strAenVorabstimmungsId)).thenReturn(false);

        testling.assertLinkedVaHasNoFaultyAutomationTasks(strAenVorabstimmungsId);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Eine neue Vorabstimmung kann erst erzeugt werden nachdem alle Automatisierungsschritte, die als automatisierbar gekennzeichnet sind, der stornierten Vorabstimmung \\(DEU.MNET.V12345678\\) abgeschlossen sind")
    public void testLinkedVaWithTodos() throws Exception {
        String strAenVorabstimmungsId = "DEU.MNET.V12345678";

        when(wbciDaoMock.hasFaultyAutomationTasks(strAenVorabstimmungsId)).thenReturn(true);

        testling.assertLinkedVaHasNoFaultyAutomationTasks(strAenVorabstimmungsId);
    }

    @Test
    public void testVerifyErlmTvTermin() throws Exception {
        final TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder().withTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate()).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final ErledigtmeldungTerminverschiebung erlm = new ErledigtmeldungTestBuilder()
                .withVorabstimmungsIdRef(tv.getVorabstimmungsId())
                .withAenderungsIdRef(tv.getAenderungsId())
                .withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate())
                .buildForTv(MeldungsCode.TV_OK);

        when(wbciDaoMock.findWbciRequestByChangeId(tv.getVorabstimmungsId(), tv.getAenderungsId(), TerminverschiebungsAnfrage.class))
                .thenReturn(tv);
        testling.assertErlmTvTermin(erlm);
    }

    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Die Aenderungs-ID der Erledigtmeldung zur Terminverschiebung ist nicht gültig.")
    public void testVerifyErlmTvTerminNull() throws Exception {
        final TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder().withTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate()).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final ErledigtmeldungTerminverschiebung erlm = new ErledigtmeldungTestBuilder()
                .withVorabstimmungsIdRef(tv.getVorabstimmungsId())
                .withAenderungsIdRef(tv.getAenderungsId())
                .withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate())
                .buildForTv(MeldungsCode.TV_OK);

        when(wbciDaoMock.findWbciRequestByChangeId(tv.getVorabstimmungsId(), tv.getAenderungsId(), TerminverschiebungsAnfrage.class))
                .thenReturn(null);
        testling.assertErlmTvTermin(erlm);
    }

    @Test(expectedExceptions = WbciValidationException.class, expectedExceptionsMessageRegExp = "Der Termin in der Erledigtmeldung zur Terminverschiebung .* stimmt nicht mit dem Termin der Terminverschiebungsanfrage überein!")
    public void testVerifyErlmTvTerminNotEquales() throws Exception {
        final TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder().withTvTermin(DateCalculationHelper.getDateInWorkingDaysFromNow(20).toLocalDate()).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final ErledigtmeldungTerminverschiebung erlm = new ErledigtmeldungTestBuilder()
                .withVorabstimmungsIdRef(tv.getVorabstimmungsId())
                .withAenderungsIdRef(tv.getAenderungsId())
                .withWechseltermin(DateCalculationHelper.getDateInWorkingDaysFromNow(10).toLocalDate())
                .buildForTv(MeldungsCode.TV_OK);

        when(wbciDaoMock.findWbciRequestByChangeId(tv.getVorabstimmungsId(), tv.getAenderungsId(), TerminverschiebungsAnfrage.class))
                .thenReturn(tv);
        testling.assertErlmTvTermin(erlm);
    }

    @Test
    public void testDuplicateVaRequestAssertionWithUniqueRequest() {
        String vaId = "DEU.MNET.V12345678";
        when(vaMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);

        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vaId)).thenReturn(null);

        Assert.assertFalse(testling.isDuplicateVaRequest(vaMock));
    }

    @Test
    public void testDuplicateVaRequestAssertionWithDuplicateRequest() {
        String vaId = "DEU.MNET.V12345678";
        when(vaMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.getVorabstimmungsId()).thenReturn(vaId);

        when(wbciCommonServiceMock.findWbciGeschaeftsfall(vaId)).thenReturn(gfMock);

        Assert.assertTrue(testling.isDuplicateVaRequest(vaMock));
    }

    private void prepareTvMock(String vaIdRef, String aenderungsId) {
        when(tvMock.getVorabstimmungsIdRef()).thenReturn(vaIdRef);
        when(tvMock.getAenderungsId()).thenReturn(aenderungsId);
    }

    @Test
    public void testDuplicateTvRequestWithUniqueRequest() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareTvMock(vaIdRef, aenderungsId);

        when(wbciCommonServiceMock.findWbciRequestByChangeId(vaIdRef, aenderungsId)).thenReturn(null);

        Assert.assertFalse(testling.isDuplicateTvRequest(tvMock));
    }

    @Test
    public void testDuplicateTvRequestWithDuplicateRequest() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareTvMock(vaIdRef, aenderungsId);

        WbciRequest request = new TerminverschiebungsAnfrage();
        when(wbciCommonServiceMock.findWbciRequestByChangeId(vaIdRef, aenderungsId)).thenReturn(request);

        Assert.assertTrue(testling.isDuplicateTvRequest(tvMock));
    }

    @Test
    public void testIsTooEarlyForTvRequestWhenRuemVaSent() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareTvMock(vaIdRef, aenderungsId);

        when(wbciCommonServiceMock.findLastForVaId(vaIdRef, RueckmeldungVorabstimmung.class)).thenReturn(ruemVaMock);
        when(ruemVaMock.getProcessedAt()).thenReturn(new Date());

        Assert.assertFalse(testling.isTooEarlyForTvRequest(tvMock));
    }

    @Test
    public void testIsTooEarlyForTvRequestWhenNoRuemVaSent() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareTvMock(vaIdRef, aenderungsId);

        when(wbciCommonServiceMock.findLastForVaId(vaIdRef, RueckmeldungVorabstimmung.class)).thenReturn(null);

        Assert.assertTrue(testling.isTooEarlyForTvRequest(tvMock));
    }

    @Test
    public void testIsMNetReceivingCarrier() {
        when(tvMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
        when(gfMock.isMNetReceivingCarrier()).thenReturn(true);
        Assert.assertTrue(testling.isMNetReceivingCarrier(tvMock));
    }

    private void prepareStornoMock(String vaIdRef, String aenderungsId) {
        when(stornoMock.getVorabstimmungsIdRef()).thenReturn(vaIdRef);
        when(stornoMock.getAenderungsId()).thenReturn(aenderungsId);
        when(stornoMock.getWbciGeschaeftsfall()).thenReturn(gfMock);
    }

    @Test
    public void shouldNotBeTooEarlySinceTheStornoWasSentByTheReceivingCarrierBeforeTheRuemVaWasSent() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.S12345678";
        prepareStornoMock(vaIdRef, aenderungsId);
        when(gfMock.isMNetReceivingCarrier()).thenReturn(false);

        when(wbciCommonServiceMock.findLastForVaId(vaIdRef, RueckmeldungVorabstimmung.class)).thenReturn(null);

        Assert.assertFalse(testling.isTooEarlyForReceivingStornoRequest(stornoMock));
    }

    @Test
    public void shouldBeTooEarlySinceTheStornoWasSentByTheDonatingCarrierBeforeTheRuemVaWasSent() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.S12345678";
        prepareStornoMock(vaIdRef, aenderungsId);
        when(gfMock.isMNetReceivingCarrier()).thenReturn(true);

        when(wbciCommonServiceMock.findLastForVaId(vaIdRef, RueckmeldungVorabstimmung.class)).thenReturn(null);

        Assert.assertTrue(testling.isTooEarlyForReceivingStornoRequest(stornoMock));
    }

    @Test
    public void testDuplicateStornoRequestWithDuplicateRequest() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareStornoMock(vaIdRef, aenderungsId);

        WbciRequest request = new StornoAenderungAbgAnfrage();
        when(wbciCommonServiceMock.findWbciRequestByChangeId(vaIdRef, aenderungsId)).thenReturn(request);

        Assert.assertTrue(testling.isDuplicateStornoRequest(stornoMock));
    }

    @DataProvider(name = "isFristUnterschrittenDP")
    public Object[][] isFristUnterschrittenDP() {
        return new Object[][]{
                { LocalDateTime.of(2014, 6, 23, 0, 0, 0), null,                      3, false }, //kein Wechseltermin, kein Fehler
                { LocalDateTime.of(2014, 6, 23, 0, 0, 0), LocalDate.of(2014, 6, 25), 3, true },  //2 AT Differenz
                { LocalDateTime.of(2014, 6, 23, 0, 0, 0), LocalDate.of(2014, 6, 26), 3, false }, //3 AT Differenz
                { LocalDateTime.of(2014, 6, 23, 0, 0, 0), LocalDate.of(2014, 6, 27), 3, false }, //4 AT Differenz
                { LocalDateTime.of(2014, 6, 23, 0, 0, 0), LocalDate.of(2014, 6, 26), 4, true },  //3 AT Differenz
                { LocalDateTime.of(2014, 6, 6, 0, 0, 0),  LocalDate.of(2014, 6, 11), 3, true },  // 2 AT Differenz - 09.06.2014 ist ein Feiertag
        };
    }

    @Test(dataProvider = "isFristUnterschrittenDP")
    public void isFristUnterschritten(LocalDateTime income, LocalDate wechseltermin, int frist, boolean expectedError) {
        when(configServiceMock.getWbciTvFristEingehend()).thenReturn(frist);
        when(configServiceMock.getWbciStornoFristEingehend()).thenReturn(frist);
        WbciGeschaeftsfall wbciGeschaeftsfall = new WbciGeschaeftsfallKueMrnTestBuilder().withWechseltermin(wechseltermin).buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        boolean tvResult = testling.isTvFristUnterschritten(new TerminverschiebungsAnfrageTestBuilder<>()
                .withCreationDate(income)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .build());
        Assert.assertEquals(tvResult, expectedError);

        boolean stornoAufhebungAufResult = testling.isStornoFristUnterschritten(new StornoAufhebungAufAnfrageTestBuilder<>()
                .withCreationDate(income)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .build());
        Assert.assertEquals(stornoAufhebungAufResult, expectedError);

        boolean stornoAufhebungAbgResult = testling.isStornoFristUnterschritten(new StornoAufhebungAbgAnfrageTestBuilder<>()
                .withCreationDate(income)
                .withWbciGeschaeftsfall(wbciGeschaeftsfall)
                .build());
        Assert.assertEquals(stornoAufhebungAbgResult, expectedError);
        
        verify(configServiceMock).getWbciTvFristEingehend();
        verify(configServiceMock, times(2)).getWbciStornoFristEingehend();
    }
    
    
    public void isStornoFristUnterschrittenAlwaysFalseForStornoAenderung() {
        Assert.assertFalse(testling.isStornoFristUnterschritten(new StornoAenderungAbgAnfrageTestBuilder().build()));
        Assert.assertFalse(testling.isStornoFristUnterschritten(new StornoAenderungAufAnfrageTestBuilder().build()));
        verify(wbciCommonServiceMock, never()).findLastForVaId(anyString());
        verify(configServiceMock, never()).getWbciStornoFristEingehend();
    }
    
    
    @DataProvider
    public Object[][] activeChangeRequestsProvider() {
        return new Object[][] {
                {RequestTyp.STR_AUFH_AUF, WbciRequestStatus.STORNO_ERLM_EMPFANGEN, true},
                {RequestTyp.STR_AUFH_ABG, WbciRequestStatus.STORNO_VERSENDET, false},
                {RequestTyp.TV, WbciRequestStatus.TV_ERLM_VERSENDET, true},
                {RequestTyp.TV, WbciRequestStatus.TV_VERSENDET, false},
        };
    }

    @Test(dataProvider = "activeChangeRequestsProvider")
    public void assertGeschaeftsfallHasNoActiveChangeRequests(RequestTyp reqType, WbciRequestStatus reqStatus, boolean okExpected) {
        String vaId = "123";
        WbciRequest requestMock = Mockito.mock(WbciRequest.class);
        when(requestMock.getTyp()).thenReturn(reqType);

        when(requestMock.getRequestStatus()).thenReturn(reqStatus);
        when(wbciCommonServiceMock.findWbciRequestByType(vaId, WbciRequest.class)).thenReturn(Arrays.asList(requestMock));

        try {
            testling.assertGeschaeftsfallHasNoActiveChangeRequests(vaId);
            Assert.assertTrue(okExpected);
        } catch (WbciServiceException e) {
            Assert.assertFalse(okExpected);
        }
    }

    @DataProvider
    public Object[][] assertGeschaeftsfallHasNoStornoErlmDataProvider() {
        return new Object[][]{
                { RequestTyp.STR_AEN_AUF, WbciRequestStatus.RUEM_VA_EMPFANGEN, true },
                { RequestTyp.STR_AEN_AUF, WbciRequestStatus.STORNO_ERLM_EMPFANGEN, false },
                { RequestTyp.TV, WbciRequestStatus.RUEM_VA_EMPFANGEN, true },
                { RequestTyp.TV, WbciRequestStatus.STORNO_ERLM_EMPFANGEN, false },
        };
    }


    @Test(dataProvider = "assertGeschaeftsfallHasNoStornoErlmDataProvider")
    public void assertGeschaeftsfallHasNoStornoErlm(RequestTyp reqType, WbciRequestStatus reqStatus, boolean okExpected) {
        String vaId = "456";
        WbciRequest requestMock = Mockito.mock(WbciRequest.class);
        when(requestMock.getTyp()).thenReturn(reqType);

        when(requestMock.getRequestStatus()).thenReturn(reqStatus);
        when(wbciCommonServiceMock.findWbciRequestByType(vaId, WbciRequest.class)).thenReturn(Arrays.asList(requestMock));

        try {
            testling.assertGeschaeftsfallHasNoStornoErlm(vaId);
            Assert.assertTrue(okExpected);
        } catch (WbciServiceException e) {
            Assert.assertFalse(okExpected);
        }

        verify(wbciCommonServiceMock).findWbciRequestByType(vaId, WbciRequest.class);
    }

    @DataProvider
    public Object[][] constraintViolationProvider() {
        return new Object[][] {
                { TerminverschiebungsAnfrage.class.getAnnotation(CheckTvTerminNotBroughtForward.class), true },
                { TerminverschiebungsAnfrage.class.getAnnotation(DiscriminatorValue.class), false },
        };
    }

    @Test(dataProvider = "constraintViolationProvider")
    public void testGetConstraintViolation(Annotation annotation, boolean expectedResult) {
        Set<ConstraintViolation<TerminverschiebungsAnfrage>> violations = new HashSet<>();
        ConstraintViolation violationMock = Mockito.mock(ConstraintViolation.class);
        violations.add(violationMock);
        ConstraintDescriptor constraintDescriptorMock = Mockito.mock(ConstraintDescriptor.class);

        when(violationMock.getConstraintDescriptor()).thenReturn(constraintDescriptorMock);
        when(constraintDescriptorMock.getAnnotation()).thenReturn(annotation);

        Assert.assertEquals(testling.hasConstraintViolation(violations, CheckTvTerminNotBroughtForward.class), expectedResult);

    }

    @DataProvider(name = "activeTvOrStorno")
    public static Object[][] activeTvOrStorno() {
        return WbciRequestHelperTest.requests();
    }

    @Test(dataProvider = "activeTvOrStorno")
    public void testIsTvOrStornoActive(List<WbciRequest> requests, boolean active) throws Exception {
        String vaId = "DEU.MNET.V001";
        when(wbciCommonServiceMock.findWbciRequestByType(vaId, WbciRequest.class)).thenReturn(requests);
        Assert.assertEquals(testling.isTvOrStornoActive(vaId), active);
    }

    @Test
    public void testGetActiveChangeRequestsWithNoActiveRequests() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareStornoMock(vaIdRef, aenderungsId);

        StornoAnfrage inactiveStorno = new StornoAenderungAbgAnfrageTestBuilder()
                .withRequestStatus(WbciRequestStatus.STORNO_ABBM_VERSENDET)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        List<StornoAnfrage> stornoRequests = Arrays.asList(inactiveStorno);
        when(wbciCommonServiceMock.findWbciRequestByType(vaIdRef, StornoAnfrage.class)).thenReturn(stornoRequests);

        Assert.assertTrue(testling.getActiveChangeRequests(vaIdRef, aenderungsId).isEmpty());
    }

    @Test
    public void testGetActiveChangeRequestsWithAnActiveRequest() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareStornoMock(vaIdRef, aenderungsId);

        StornoAnfrage activeStorno = new StornoAenderungAbgAnfrageTestBuilder()
                .withRequestStatus(WbciRequestStatus.STORNO_VERSENDET)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        List<WbciRequest> stornoRequests = Arrays.<WbciRequest>asList(activeStorno);
        when(wbciCommonServiceMock.findWbciRequestByType(vaIdRef, WbciRequest.class)).thenReturn(stornoRequests);

        Set<Pair<RequestTyp, WbciRequestStatus>> activeReqs = testling.getActiveChangeRequests(vaIdRef, aenderungsId);
        Assert.assertEquals(activeReqs.size(), 1);

        Pair<RequestTyp, WbciRequestStatus> activeReq = activeReqs.iterator().next();
        Assert.assertEquals(activeReq.getFirst(), RequestTyp.STR_AEN_ABG);
        Assert.assertEquals(activeReq.getSecond(), WbciRequestStatus.STORNO_VERSENDET);
    }

    @Test
    public void testGetActiveChangeRequestsExcludingChangeId() {
        String vaIdRef = "DEU.MNET.V12345678";
        String aenderungsId = "DEU.MNET.T12345678";
        prepareStornoMock(vaIdRef, aenderungsId);

        StornoAnfrage activeStorno = new StornoAenderungAbgAnfrageTestBuilder()
                .withAenderungsId(aenderungsId)
                .withRequestStatus(WbciRequestStatus.STORNO_VERSENDET)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

        List<WbciRequest> stornoRequests = Arrays.<WbciRequest>asList(activeStorno);
        when(wbciCommonServiceMock.findWbciRequestByType(vaIdRef, WbciRequest.class)).thenReturn(stornoRequests);

        Assert.assertTrue(testling.getActiveChangeRequests(vaIdRef, aenderungsId).isEmpty());
    }


}
