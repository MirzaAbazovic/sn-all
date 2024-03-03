/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.04.2014
 */
package de.mnet.wbci.ticketing.escalation;

import static de.mnet.wbci.model.EscalationPreAgreementVO.*;
import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.*;
import static de.mnet.wbci.ticketing.escalation.EscalationReportGenerator.EscalationListType.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.util.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.exception.WbciServiceException;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.EscalationPreAgreementVO;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;
import de.mnet.wbci.model.builder.AntwortfristBuilder;
import de.mnet.wbci.model.builder.BasePreAgreementVOBuilder;
import de.mnet.wbci.model.builder.PreAgreementVOBuilder;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wbci.service.WbciDeadlineService;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class EscalationReportGeneratorTest {
    protected static final String vaid = "DEU.MNET.V001";
    protected static final GeschaeftsfallTyp gfType = GeschaeftsfallTyp.VA_KUE_MRN;
    protected static final Date vorgabeDatum = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    protected static final Date wechseltermin = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    protected static final Date rueckmeldeDatum = Date.from(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant());
    protected static final WbciGeschaeftsfallStatus geschaeftsfallStatus = WbciGeschaeftsfallStatus.ACTIVE;
    protected static final Date processedAt = Date.from(LocalDate.now().minusDays(5).atStartOfDay(ZoneId.systemDefault()).toInstant());
    protected static CarrierCode ekpAbg = CarrierCode.DTAG;
    protected static CarrierCode ekpAuf = CarrierCode.MNET;
    @Mock
    private WbciCommonService wbciCommonService;
    @Mock
    private WbciDeadlineService wbciDeadlineService;
    @Spy
    @InjectMocks
    private EscalationReportGenerator testling;

    @DataProvider
    public static Object[][] carrierPreAgreementVOs() {
        //@formatter:off
        return new Object[][] {
             // RequestTyp, aenderungsKz, WbciRequestStatus, daysUntilDeadlinePartner, expectedEscalationType, expectedEscalationLevel, deadlineInDays

            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_EMPFANGEN, 2, null, null,8L },
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_EMPFANGEN, -1, null, null,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, 0, null, null,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_EMPFANGEN, -1,  null,null,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, -1,  EscalationType.VA_VERSENDET, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, -5,  EscalationType.VA_VERSENDET, EscalationLevel.LEVEL_2,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, -10, EscalationType.VA_VERSENDET, EscalationLevel.LEVEL_3,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, 0, null, null,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_EMPFANGEN, -1,  null, null,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, -1,  EscalationType.RUEM_VA_VERSENDET, EscalationLevel.LEVEL_1,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, -5,  EscalationType.RUEM_VA_VERSENDET, EscalationLevel.LEVEL_2,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, -10, EscalationType.RUEM_VA_VERSENDET, EscalationLevel.LEVEL_3,8L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, 0, null, null,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_EMPFANGEN, -1,  null,null,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, -1,  EscalationType.TV_VERSENDET, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, -2,  EscalationType.TV_VERSENDET, EscalationLevel.LEVEL_2,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, -4, EscalationType.TV_VERSENDET, EscalationLevel.LEVEL_3,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_EMPFANGEN, -1, null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, -1,  EscalationType.STORNO_AEN_ABG_VERSENDET, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, -2,  EscalationType.STORNO_AEN_ABG_VERSENDET, EscalationLevel.LEVEL_2,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, -4, EscalationType.STORNO_AEN_ABG_VERSENDET, EscalationLevel.LEVEL_3,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, -1, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -1,  EscalationType.STORNO_AUFH_ABG_VERSENDET, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -2,  EscalationType.STORNO_AUFH_ABG_VERSENDET, EscalationLevel.LEVEL_2,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -4, EscalationType.STORNO_AUFH_ABG_VERSENDET, EscalationLevel.LEVEL_3,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 0, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 1, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 2, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 3, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 4, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 5, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 6, null, null,7L},
        };
        //@formatter:on

    }

    @DataProvider
    public static Object[][] internalPreAgreementVOsAUF() {
        //@formatter:off
        return new Object[][] {
             // RequestTyp, aenderungsKz, WbciRequestStatus, daysUntilDeadlineMnet, expectedEscalationType, expectedEscalationLevel, deadlineInDays

            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, 0, null, null,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_VERSENDET, -1,  null,null,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_EMPFANGEN, -1,EscalationType.RUEM_VA_EMPFANGEN, EscalationLevel.LEVEL_1,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_EMPFANGEN, 0, null, null,8L },
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, 0, null, null,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_VERSENDET, -1,  null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, -1, null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_EMPFANGEN, 0, null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_EMPFANGEN, -1,  EscalationType.STORNO_AEN_ABG_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -1, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, 0, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, -1,  EscalationType.STORNO_AUFH_ABG_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 0, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 1, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 2, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 3, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 4, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 5, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 6, null, null,7L},
        };
        //@formatter:on

    }

    @DataProvider
    public static Object[][] internalPreAgreementVOsABG() {
        //@formatter:off
        return new Object[][] {
             // RequestTyp, aenderungsKz, WbciRequestStatus, daysUntilDeadlineMnet, expectedEscalationType, expectedEscalationLevel, deadlineInDays

            {RequestTyp.VA, "VA", WbciRequestStatus.VA_EMPFANGEN, 0,  null,null,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.VA_EMPFANGEN, -1,  EscalationType.VA_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, 0, null, null,8L},
            {RequestTyp.VA, "VA", WbciRequestStatus.RUEM_VA_VERSENDET, -1,  null, null,8L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_EMPFANGEN, 0,  null,null,3L},
            {RequestTyp.TV, "TV", WbciRequestStatus.TV_EMPFANGEN, -1,  EscalationType.TV_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_VERSENDET, -1, null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_EMPFANGEN, 0, null,null,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG",WbciRequestStatus.STORNO_EMPFANGEN, -1,  EscalationType.STORNO_AEN_ABG_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, 0, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -1, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, 0, null, null,3L},
            {RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, -1,  EscalationType.STORNO_AUFH_ABG_EMPFANGEN, EscalationLevel.LEVEL_1,3L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 0, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 1, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_3,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 2, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 3, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_2,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 4, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 5, EscalationType.STORNO_AEN_ERLM_EMPFANGEN_ABG, EscalationLevel.LEVEL_1,7L},
            {RequestTyp.STR_AEN_ABG, "STR-AEN-ABG", WbciRequestStatus.STORNO_ERLM_EMPFANGEN, 6, null, null,7L},
        };
        //@formatter:on

    }

    @DataProvider(name = "newVaExpiredPreAgreements")
    public static Object[][] newVaExpiredPreAgreements() {
        return new Object[][] {
                // Geschäftfall_Status, RequestTyp, aenderungsKz, WbciRequestStatus, daysUntilDeadlineMnet, expectedEscalationType, expectedEscalationLevel, deadlineInDays
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, 0 },
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_VERSENDET, -1 },
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, null },
                { WbciGeschaeftsfallStatus.NEW_VA_EXPIRED, RequestTyp.STR_AUFH_ABG, "STR-AUFH-ABG", WbciRequestStatus.STORNO_EMPFANGEN, -1 },
        };

    }

    public static <B extends BasePreAgreementVOBuilder> B buildBaseVO(B builder, RequestTyp typ, WbciRequestStatus status, Integer daysUntilDeadlinePartner, Integer daysUntilDeadlineMNet, CarrierRole mnetRole) {
        if (CarrierRole.ABGEBEND.equals(mnetRole)) {
            ekpAbg = CarrierCode.MNET;
            ekpAuf = CarrierCode.DTAG;
        }
        else {
            ekpAbg = CarrierCode.DTAG;
            ekpAuf = CarrierCode.MNET;
        }

        builder.withRequestStatus(status)
                .withAenderungskz(typ)
                .withDaysUntilDeadlinePartner(daysUntilDeadlinePartner)
                .withDaysUntilDeadlineMnet(daysUntilDeadlineMNet)
                .withVaid(vaid)
                .withGfType(gfType)
                .withEkpAbg(ekpAbg)
                .withEkpAuf(ekpAuf)
                .withVorgabeDatum(vorgabeDatum)
                .withWechseltermin(wechseltermin)
                .withRueckmeldungDatum(rueckmeldeDatum)
                .withGeschaeftsfallStatus(geschaeftsfallStatus)
                .withProcessedAt(processedAt);
        return builder;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateCarrierEscalationOverviewReport() throws Exception {
        List<PreAgreementVO> vos = new ArrayList<>();
        List<EscalationPreAgreementVO> filteredVOs = new ArrayList<>();
        ArgumentCaptor<EscalationListConfiguration> configCaptor = ArgumentCaptor.forClass(EscalationListConfiguration.class);
        doReturn(vos).when(testling).getPreAgreementVOs(null);
        doReturn(filteredVOs).when(testling).filterAndEnrichVAs(anyListOf(PreAgreementVO.class), configCaptor.capture());

        XSSFWorkbook result = testling.generateCarrierEscalationOverviewReport("test-file.xlsx");
        assertNotNull(result);
        verify(testling).getPreAgreementVOs(null);
        assertEquals(configCaptor.getValue().getMnetRole(), null);
        assertEquals(configCaptor.getValue().getPartnerEKP(), null);
        assertEquals(configCaptor.getValue().getType(), CARRIER_OVERVIEW);
    }

    @Test
    public void testGenerateCarrierEscalationReport() throws Exception {
        List<PreAgreementVO> vos = new ArrayList<>();
        List<EscalationPreAgreementVO> filteredVOs = new ArrayList<>();
        ArgumentCaptor<EscalationListConfiguration> configCaptor = ArgumentCaptor.forClass(EscalationListConfiguration.class);
        doReturn(vos).when(testling).getPreAgreementVOs(null);
        doReturn(filteredVOs).when(testling).filterAndEnrichVAs(anyListOf(PreAgreementVO.class), configCaptor.capture());

        XSSFWorkbook result = testling.generateEscalationReportForCarrier(CarrierCode.DTAG, "test-file.xlsx");
        assertNotNull(result);
        verify(testling).getPreAgreementVOs(null);
        assertEquals(configCaptor.getValue().getMnetRole(), null);
        assertEquals(configCaptor.getValue().getPartnerEKP(), CarrierCode.DTAG);
        assertEquals(configCaptor.getValue().getType(), CARRIER_SPECIFIC);
    }

    @Test
    public void testGenerateCarrierEscalationReportIntern() throws Exception {
        List<PreAgreementVO> vos = new ArrayList<>();
        List<EscalationPreAgreementVO> filteredVOs = new ArrayList<>();
        ArgumentCaptor<EscalationListConfiguration> configCaptor = ArgumentCaptor.forClass(EscalationListConfiguration.class);
        doReturn(vos).when(testling).getPreAgreementVOs(CarrierRole.ABGEBEND);
        doReturn(filteredVOs).when(testling).filterAndEnrichVAs(anyListOf(PreAgreementVO.class), configCaptor.capture());

        XSSFWorkbook result = testling.generateInternalDeadlineOverviewReport(CarrierRole.ABGEBEND, "test-file.xlsx");
        assertNotNull(result);
        verify(testling).getPreAgreementVOs(CarrierRole.ABGEBEND);
        assertEquals(configCaptor.getValue().getMnetRole(), CarrierRole.ABGEBEND);
        assertEquals(configCaptor.getValue().getPartnerEKP(), null);
        assertEquals(configCaptor.getValue().getType(), INTERNAL);
    }

    @Test
    public void testGetPreAggreemntVOs() throws Exception {
        PreAgreementVO vo1 = new PreAgreementVOBuilder().withAuftragId(1L).build();
        PreAgreementVO vo2 = new PreAgreementVOBuilder().withAuftragId(2L).build();
        when(wbciCommonService.findPreAgreements(CarrierRole.AUFNEHMEND)).thenReturn(Arrays.asList(vo1));
        when(wbciCommonService.findPreAgreements(CarrierRole.ABGEBEND)).thenReturn(Arrays.asList(vo2));

        List<PreAgreementVO> result = testling.getPreAgreementVOs(null);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), vo1);
        assertEquals(result.get(1), vo2);

        result = testling.getPreAgreementVOs(CarrierRole.AUFNEHMEND);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), vo1);

        result = testling.getPreAgreementVOs(CarrierRole.ABGEBEND);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), vo2);
    }


    @Test(expectedExceptions = WbciServiceException.class, expectedExceptionsMessageRegExp = "Unexpected error during the generation of file 'Pruefliste.xlsx' with EscalationListConfiguration \\[type=INTERNAL, partnerEKP=DTAG, mnetRole=ABGEBEND\\]")
    public void expectExceptionWhenGenerateInternalEscalationOverviewReport() throws Exception {
        final String fileName = "Pruefliste.xlsx";
        doThrow(Exception.class).when(testling).getPreAgreementVOs(any(CarrierRole.class));
        testling.getEscalationReport(new EscalationListConfiguration(INTERNAL, CarrierCode.DTAG, CarrierRole.ABGEBEND), fileName);
    }

    @Test
    public void testSortVOs() throws Exception {
        Date baseDate = new Date();
        PreAgreementVO va1 = new PreAgreementVOBuilder().withWechseltermin(baseDate).build();
        PreAgreementVO va2 = new PreAgreementVOBuilder().withWechseltermin(DateUtils.addDays(baseDate, 1)).build();

        List<PreAgreementVO> result = EscalationReportGenerator.sortVOs(Arrays.asList(va2, va1));
        assertEquals(result.get(0), va1);
        assertEquals(result.get(1), va2);

        va1.setWechseltermin(null);
        va1.setVorgabeDatum(baseDate);
        va2.setWechseltermin(null);
        va2.setVorgabeDatum(DateUtils.addDays(baseDate, -1));

        result = EscalationReportGenerator.sortVOs(Arrays.asList(va1, va2));
        assertEquals(result.get(1), va1);
        assertEquals(result.get(0), va2);
    }

    @Test(dataProvider = "carrierPreAgreementVOs")
    public void testEnrichCarrierPreAggrements(RequestTyp typ, String aenderungsKz, WbciRequestStatus status, Integer daysUntilDeadlinePartner, EscalationType expectedEscalationType, EscalationLevel expectedEscalationLevel, Long deadlineInDays) throws Exception {
        when(wbciDeadlineService.findAntwortfrist(typ, status)).thenReturn(new AntwortfristBuilder().withFristInStunden(deadlineInDays * 24).build());

        //check for carrier overview data
        List<EscalationPreAgreementVO> resultList = testling.filterAndEnrichVAs(
                Arrays.asList(buildBaseVO(new PreAgreementVOBuilder(), typ, status, daysUntilDeadlinePartner, null, CarrierRole.AUFNEHMEND).build()),
                new EscalationListConfiguration(CARRIER_OVERVIEW, null, null));
        assertEscalationVO(resultList, aenderungsKz, status, daysUntilDeadlinePartner, null, expectedEscalationType, expectedEscalationLevel, deadlineInDays, geschaeftsfallStatus);

        //check for carrier == DTAG
        resultList = testling.filterAndEnrichVAs(
                Arrays.asList(buildBaseVO(new PreAgreementVOBuilder(), typ, status, daysUntilDeadlinePartner, null, CarrierRole.AUFNEHMEND).build()),
                new EscalationListConfiguration(CARRIER_SPECIFIC, CarrierCode.DTAG, null));
        assertEscalationVO(resultList, aenderungsKz, status, daysUntilDeadlinePartner, null, expectedEscalationType, expectedEscalationLevel, deadlineInDays, geschaeftsfallStatus);

        //check for carrier != DTAG
        resultList = testling.filterAndEnrichVAs(
                Arrays.asList(buildBaseVO(new PreAgreementVOBuilder(), typ, status, daysUntilDeadlinePartner, null, CarrierRole.ABGEBEND).build()),
                new EscalationListConfiguration(CARRIER_SPECIFIC, CarrierCode.VODAFONE, null));
        assertTrue(resultList.isEmpty());
    }

    @Test(dataProvider = "internalPreAgreementVOsABG")
    public void testEnrichInternalPreAgreementsABG(RequestTyp typ, String aenderungsKz, WbciRequestStatus status, Integer daysUntilDeadlineMnet, EscalationType expectedEscalationType, EscalationLevel expectedEscalationLevel, Long deadlineInDays) throws Exception {
        when(wbciDeadlineService.findAntwortfrist(typ, status)).thenReturn(new AntwortfristBuilder().withFristInStunden(deadlineInDays * 24).build());

        // check for internal overview data
        List<EscalationPreAgreementVO> resultList = testling.filterAndEnrichVAs(
                Arrays.asList(buildBaseVO(new PreAgreementVOBuilder(), typ, status, null, daysUntilDeadlineMnet, CarrierRole.ABGEBEND).build()),
                new EscalationListConfiguration(INTERNAL, null, CarrierRole.ABGEBEND));
        assertEscalationVO(resultList, aenderungsKz, status, null, daysUntilDeadlineMnet, expectedEscalationType, expectedEscalationLevel, deadlineInDays, geschaeftsfallStatus);
    }

    @Test(dataProvider = "internalPreAgreementVOsAUF")
    public void testEnrichInternalPreAgreements(RequestTyp typ, String aenderungsKz, WbciRequestStatus status, Integer daysUntilDeadlineMnet, EscalationType expectedEscalationType, EscalationLevel expectedEscalationLevel, Long deadlineInDays) throws Exception {
        when(wbciDeadlineService.findAntwortfrist(typ, status)).thenReturn(new AntwortfristBuilder().withFristInStunden(deadlineInDays * 24).build());

        // check for internal overview data
        List<EscalationPreAgreementVO> resultList = testling.filterAndEnrichVAs(
                Arrays.asList(buildBaseVO(new PreAgreementVOBuilder(), typ, status, null, daysUntilDeadlineMnet, CarrierRole.AUFNEHMEND).build()),
                new EscalationListConfiguration(INTERNAL, null, CarrierRole.AUFNEHMEND));
        assertEscalationVO(resultList, aenderungsKz, status, null, daysUntilDeadlineMnet, expectedEscalationType, expectedEscalationLevel, deadlineInDays, geschaeftsfallStatus);
    }

    @Test(dataProvider = "newVaExpiredPreAgreements")
    public void testEnrichNewVaExpiredPreAgreements(WbciGeschaeftsfallStatus geschaeftsfallStatus, RequestTyp typ, String aenderungsKz, WbciRequestStatus status, Integer daysUntilDeadlineMnet) throws Exception {
        when(wbciDeadlineService.findAntwortfrist(typ, status)).thenReturn(new AntwortfristBuilder().withFristInStunden(24L).build());

        //check for internal overview data
        PreAgreementVO baseVo = buildBaseVO(new PreAgreementVOBuilder(), typ, status, null, daysUntilDeadlineMnet, CarrierRole.AUFNEHMEND).build();
        baseVo.setGeschaeftsfallStatus(geschaeftsfallStatus);
        List<EscalationPreAgreementVO> resultList = testling.filterAndEnrichVAs(Arrays.asList(baseVo), new EscalationListConfiguration(INTERNAL, null, CarrierRole.AUFNEHMEND));
        assertEscalationVO(resultList, aenderungsKz, status, null, null, EscalationType.NEW_VA_EXPIRED, EscalationLevel.INFO, null, geschaeftsfallStatus);
    }

    private void assertEscalationVO(List<EscalationPreAgreementVO> resultList, String aenderungsKz, WbciRequestStatus status, Integer daysUntilDeadlinePartner, Integer daysUntilDeadlineMnet, EscalationType expectedEscalationType, EscalationLevel expectedEscalationLevel, Long deadlineInDays, WbciGeschaeftsfallStatus gfStatus) {
        if (expectedEscalationType == null) {
            assertTrue(resultList.isEmpty());
        }
        else {
            EscalationPreAgreementVO result = resultList.iterator().next();
            assertEquals(result.getRequestStatus(), status);
            assertEquals(result.getAenderungskz(), aenderungsKz);
            assertEquals(result.getVaid(), vaid);
            assertEquals(result.getGfType(), gfType);
            assertEquals(result.getEkpAbg(), ekpAbg);
            assertEquals(result.getEkpAuf(), ekpAuf);
            assertEquals(result.getVorgabeDatum(), vorgabeDatum);
            assertEquals(result.getWechseltermin(), wechseltermin);
            assertEquals(result.getGeschaeftsfallStatus(), gfStatus);
            assertEquals(result.getProcessedAt(), processedAt);
            assertEquals(result.getRueckmeldeDatum(), rueckmeldeDatum);
            assertEquals(result.getDaysUntilDeadlinePartner(), daysUntilDeadlinePartner);
            assertEquals(result.getDaysUntilDeadlineMnet(), daysUntilDeadlineMnet);
            assertEquals(result.getEscalationType(), expectedEscalationType);
            assertEquals(result.getEscalationLevel(), expectedEscalationLevel);
            assertEquals(result.getDeadlineDays(), deadlineInDays);
        }
    }
}
