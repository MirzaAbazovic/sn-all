/* 
 * Copyright (c) 2014 - M-net Telekommunikations GmbH 
 * All rights reserved. 
 * ------------------------------------------------------- 
 * File created: 17.01.14 
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.wbci.dao.WbciDao;
import de.mnet.wbci.model.Antwortfrist;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.StornoAenderungAbgAnfrage;
import de.mnet.wbci.model.StornoAufhebungAbgAnfrage;
import de.mnet.wbci.model.TerminverschiebungsAnfrage;
import de.mnet.wbci.model.VorabstimmungsAnfrage;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.WbciGeschaeftsfall;
import de.mnet.wbci.model.WbciRequest;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AntwortfristBuilder;
import de.mnet.wbci.model.builder.StornoAenderungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.StornoAufhebungAbgAnfrageTestBuilder;
import de.mnet.wbci.model.builder.TerminverschiebungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.VorabstimmungsAnfrageTestBuilder;
import de.mnet.wbci.model.builder.WbciGeschaeftsfallKueMrnTestBuilder;
import de.mnet.wbci.service.WbciCommonService;

@Test(groups = UNIT)
public class WbciDeadlineServiceImplTest {

    @InjectMocks
    @Spy
    private WbciDeadlineServiceImpl testling;

    @Mock
    private WbciDao wbciDao;
    @Mock
    private WbciCommonService wbciCommonService;

    @BeforeMethod
    public void setUp() {
        testling = new WbciDeadlineServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "answerDeadline")
    public Object[][] updateAnswerDeadlineProvider() {
        final LocalDateTime updatedAt = LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0);
        final VorabstimmungsAnfrage va = new VorabstimmungsAnfrageTestBuilder<>()
                .withUpdatedAt(updatedAt)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>()
                .withUpdatedAt(updatedAt)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final StornoAenderungAbgAnfrage<WbciGeschaeftsfall> stornoAenderungAbgAnfrage = new StornoAenderungAbgAnfrageTestBuilder<>()
                .withUpdatedAt(updatedAt)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        final StornoAufhebungAbgAnfrage<WbciGeschaeftsfall> stornoAufhebungAbgAnfrage = new StornoAufhebungAbgAnfrageTestBuilder<>()
                .withUpdatedAt(updatedAt)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        return new Object[][] {
                { va, VA_EMPFANGEN, true },
                { va, VA_VERSENDET, false },
                { va, RUEM_VA_EMPFANGEN, true },
                { tv, TV_VERSENDET, false },
                { tv, TV_EMPFANGEN, true },
                { stornoAenderungAbgAnfrage, STORNO_VERSENDET, false },
                { stornoAenderungAbgAnfrage, STORNO_EMPFANGEN, true },
                { stornoAufhebungAbgAnfrage, STORNO_VERSENDET, false },
                { stornoAufhebungAbgAnfrage, STORNO_EMPFANGEN, true },
        };
    }

    @Test(dataProvider = "answerDeadline")
    public void testUpdateAnswerDeadline(WbciRequest wbciRequest, WbciRequestStatus requestStatus,
            boolean expectedIsMnetDeadline) {
        wbciRequest.setRequestStatus(requestStatus);
        final Antwortfrist antwortfrist = new AntwortfristBuilder()
                .withRequestTyp(wbciRequest.getTyp())
                .withRequestStatus(requestStatus)
                .withFristInStunden(72L)
                .build();
        when(wbciDao.findAntwortfrist(wbciRequest.getTyp(), requestStatus)).thenReturn(antwortfrist);
        LocalDate answerDeadline = LocalDate.of(2014, 1, 5);
        doReturn(answerDeadline).when(testling).calculateAnswerDeadline(wbciRequest, antwortfrist);

        testling.updateAnswerDeadline(wbciRequest);

        Mockito.verify(wbciDao).findAntwortfrist(wbciRequest.getTyp(), requestStatus);
        Mockito.verify(wbciDao).store(wbciRequest);
        Mockito.verify(testling).calculateAnswerDeadline(wbciRequest, antwortfrist);

        assertNotNull(wbciRequest.getIsMnetDeadline());
        assertEquals(wbciRequest.getIsMnetDeadline().booleanValue(), expectedIsMnetDeadline);
        assertNotNull(wbciRequest.getAnswerDeadline());
        assertEquals(wbciRequest.getAnswerDeadline(), answerDeadline);
    }

    @DataProvider(name = "antwortfristen")
    public Object[][] getAntwortfristen() {

        return new Object[][] {
                { new VorabstimmungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0))
                        .withRequestStatus(VA_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 72L, LocalDate.of(2014, 1, 9) },
                { new VorabstimmungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 6, 2, 18, 0, 0, 0))
                        .withRequestStatus(VA_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 72L, LocalDate.of(2014, 6, 5) },
                { new TerminverschiebungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0))
                        .withRequestStatus(TV_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 60L, LocalDate.of(2014, 1, 9) },
                { new TerminverschiebungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0))
                        .withRequestStatus(TV_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 48L, LocalDate.of(2014, 1, 8) },
                { new TerminverschiebungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0))
                        .withRequestStatus(TV_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), null, null },
                { new TerminverschiebungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 1, 5, 0, 0, 0, 0))
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), null, null },
                { new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder()
                                //                                .withWechseltermin(LocalDateTime.of(2014, 6, 18, 18, 0, 0, 0))
                                .withWechseltermin(LocalDate.of(2014, 6, 18))
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        .withRequestStatus(RUEM_VA_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 72L, LocalDate.of(2014, 6, 13) },
                { new VorabstimmungsAnfrageTestBuilder<>()
                        .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder()
                                //                                .withWechseltermin(LocalDateTime.of(2014, 6, 18, 18, 0, 0, 0))
                                .withWechseltermin(LocalDate.of(2014, 6, 18))
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        .withRequestStatus(RUEM_VA_VERSENDET)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 72L, LocalDate.of(2014, 6, 13) },
                { new VorabstimmungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 6, 2, 18, 0, 0, 0))
                        .withRequestStatus(RUEM_VA_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP), 72L, null },
                { new VorabstimmungsAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 6, 2, 18, 0, 0, 0))
                        .withRequestStatus(RUEM_VA_VERSENDET)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_RRNP), 72L, null },
                { new StornoAenderungAbgAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 6, 2, 18, 0, 0, 0))
                        .withWbciGeschaeftsfall(new WbciGeschaeftsfallKueMrnTestBuilder()
                                .withWechseltermin(LocalDate.of(2014, 6, 18))
                                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN))
                        .withRequestStatus(STORNO_ERLM_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 168L, LocalDate.of(2014, 6, 6) },
                { new StornoAufhebungAbgAnfrageTestBuilder<>().withUpdatedAt(LocalDateTime.of(2014, 6, 2, 18, 0, 0, 0))
                        .withRequestStatus(STORNO_ERLM_EMPFANGEN)
                        .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN), 24L, LocalDate.of(2014, 6, 3) },
        };
    }

    @Test(dataProvider = "antwortfristen")
    public void calculateAnswerDeadline(WbciRequest wbciRequest, Long fristInStunden,
            LocalDate expectedAnswerDeadline) {
        final Antwortfrist antwortfrist = wbciRequest == null ? null : new AntwortfristBuilder()
                .withRequestStatus(wbciRequest.getRequestStatus())
                .withFristInStunden(fristInStunden)
                .build();
        assertEquals(testling.calculateAnswerDeadline(wbciRequest, antwortfrist), expectedAnswerDeadline);
    }

    @DataProvider(name = "isMnetDeadlineProvider")
    public Object[][] isMnetDeadlineProvider() {

        return new Object[][] {
                { STORNO_ERLM_EMPFANGEN, CarrierCode.MNET, LocalDate.of(2014, 1, 9), true },
                { STORNO_ERLM_VERSENDET, CarrierCode.MNET, LocalDate.of(2014, 1, 9), true },
                { STORNO_ERLM_EMPFANGEN, CarrierCode.DTAG, LocalDate.of(2014, 1, 9), false },
                { STORNO_ERLM_VERSENDET, CarrierCode.DTAG, LocalDate.of(2014, 1, 9), false },
                { STORNO_ERLM_EMPFANGEN, CarrierCode.MNET, null, false },
                { STORNO_VERSENDET, CarrierCode.MNET, null, false },
                { STORNO_VERSENDET, CarrierCode.MNET, LocalDate.of(2014, 1, 9), false },
                { STORNO_VERSENDET, CarrierCode.DTAG, LocalDate.of(2014, 1, 9), false },
                { STORNO_EMPFANGEN, CarrierCode.MNET, LocalDate.of(2014, 1, 9), true },
                { STORNO_EMPFANGEN, CarrierCode.DTAG, LocalDate.of(2014, 1, 9), true },
        };
    }

    @Test(dataProvider = "isMnetDeadlineProvider")
    public void isMnetDeadline(WbciRequestStatus requestStatus, CarrierCode aufnehmenderEkp, LocalDate answerDeadline, Boolean isMnetDeadline) {
        final VorabstimmungsAnfrage<WbciGeschaeftsfall> anfrage = new VorabstimmungsAnfrageTestBuilder<>()
                .withRequestStatus(requestStatus)
                .withAnswerDeadline(answerDeadline)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        anfrage.getWbciGeschaeftsfall().setAufnehmenderEKP(aufnehmenderEkp);
        assertEquals(testling.isMnetDeadline(anfrage), isMnetDeadline);
    }

    @Test
    public void testFindAntwortfrist() throws FindException {
        WbciRequestStatus wbciRequestStatus = WbciRequestStatus.VA_VERSENDET;
        RequestTyp typ = RequestTyp.VA;
        testling.findAntwortfrist(typ, wbciRequestStatus);
        verify(wbciDao).findAntwortfrist(typ, wbciRequestStatus);
    }

    @DataProvider(name = "updateTvAndVaRequest")
    public Object[][] updateTvAndVaRequest() {
        return new Object[][] {
                { TV_ERLM_EMPFANGEN, true },
                { TV_ERLM_VERSENDET, true },
                { TV_ABBM_EMPFANGEN, false },
                { TV_ABBM_VERSENDET, false },
        };
    }

    @Test(dataProvider = "updateTvAndVaRequest")
    public void testUpdateTvAndVaRequestDeadlines(WbciRequestStatus tvStatus, boolean updateVaExpected) throws Exception {
        LocalDate wechselterminTv = LocalDate.of(2014, 7, 4);
        LocalDateTime updatedTvDate = LocalDateTime.of(2014, 7, 1, 0, 0, 0, 0);
        TerminverschiebungsAnfrage tv = new TerminverschiebungsAnfrageTestBuilder<>()
                .withTvTermin(wechselterminTv)
                .withUpdatedAt(updatedTvDate)
                .withRequestStatus(tvStatus)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        tv.getWbciGeschaeftsfall().setWechseltermin(wechselterminTv);

        VorabstimmungsAnfrage<WbciGeschaeftsfall> va = new VorabstimmungsAnfrageTestBuilder<>()
                .withWbciGeschaeftsfall(tv.getWbciGeschaeftsfall())
                .withRequestStatus(RUEM_VA_VERSENDET)
                .withAnswerDeadline(wechselterminTv)
                .buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);
        when(wbciCommonService.findVorabstimmungsAnfrage(tv.getVorabstimmungsId())).thenReturn(va);

        Antwortfrist fristVA = new AntwortfristBuilder().withFristInStunden(72L).withRequestStatus(RUEM_VA_VERSENDET).build();
        doReturn(fristVA).when(wbciDao).findAntwortfrist(RequestTyp.VA, RUEM_VA_VERSENDET);
        for (WbciRequestStatus tvAnswerState : Arrays.asList(TV_ABBM_VERSENDET, TV_ABBM_EMPFANGEN, TV_ERLM_VERSENDET, TV_ERLM_EMPFANGEN)) {
            Antwortfrist fristTv = new AntwortfristBuilder().withFristInStunden(24L).withRequestStatus(tvAnswerState).build();
            doReturn(fristTv).when(wbciDao).findAntwortfrist(RequestTyp.TV, tvAnswerState);
        }

        testling.updateAnswerDeadline(tv);

        verify(wbciDao).store(tv);
        int timesOfStoreVa = updateVaExpected ? 1 : 0;
        verify(wbciDao, times(timesOfStoreVa)).store(va);
        verify(wbciCommonService, times(timesOfStoreVa)).findVorabstimmungsAnfrage(tv.getVorabstimmungsId());

        assertEquals(tv.getAnswerDeadline(), updatedTvDate.plusDays(1).toLocalDate());
        int expectedDeadlineDaysVa = updateVaExpected ? 3 : 0;
        assertEquals(va.getAnswerDeadline(), wechselterminTv.minusDays(expectedDeadlineDaysVa));
    }
}
