package de.mnet.wbci.dao.impl;

import static de.mnet.wbci.helper.AnswerDeadlineCalculationHelper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Tests the class {@link PreAgreementVOTransformer}
 * <p>
 * Created by zieglerch on 06.02.2017.
 */
@Test(groups = BaseTest.UNIT)
public class PreAgreementVOTransformerTest {

    private static final String VA_ID = "VA_ID";
    private static final Long AUFTRAG_ID = 77L;
    private static final Long AUFTRAG_NO_ORIG = 66L;
    private static final KundenTyp KUNDEN_TYP = KundenTyp.GK;
    private static final GeschaeftsfallTyp GF_TYPE = GeschaeftsfallTyp.VA_RRNP;
    private static final RequestTyp AENDERUNGSKZ = RequestTyp.STR_AEN_ABG;
    private static final CarrierCode EKP_ABG = CarrierCode.DTAG;
    private static final CarrierCode EKP_AUF = CarrierCode.EINS_UND_EINS;
    private static final Date VORGABE_DATUM = new Date(LocalDate.of(2016, 5, 22).toEpochDay());
    private static final Date WECHSELTERMIN = new Date(LocalDate.of(2016, 8, 12).toEpochDay());
    private static final WbciGeschaeftsfallStatus GF_STATUS = WbciGeschaeftsfallStatus.COMPLETE;
    private static final WbciRequestStatus STATUS = WbciRequestStatus.ABBM_TR_VERSENDET;
    private static final Date PROCESSED_AT = new Date(LocalDate.of(2016, 3, 16).toEpochDay());
    private static final MeldungTyp RUECKMELDUNG = MeldungTyp.AKM_TR;
    private static final Date RUECKMELDE_DATUM = new Date(LocalDate.of(2016, 12, 11).toEpochDay());
    private static final String MELDUNG_CODES = "MELDUNG_CODES";
    private static final Long REQUEST_ID = 55L;
    private static final Long USER_ID = 44L;
    private static final String USER_NAME = "USER_NAME";
    private static final Long TEAM_ID = 33L;
    private static final Long CURRENT_USER_ID = 22L;
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    private static final Technologie MNET_TECHNOLOGIE = Technologie.FTTB;
    private static final Boolean KLAERFALL = true;
    private static final Boolean AUTOMATABLE = true;
    private static final String INTERNAL_STATUS = "INTERNAL_STATUS";
    private static final Date ANSWER_DEADLINE = new Date(LocalDate.of(2016, 11, 18).toEpochDay());
    private static final Boolean IS_MNET_DEADLINE = true;
    private static final String NIEDERLASSUNG = "NIEDERLASSUNG";
    private static final Long NUMBER_OF_AUTOMATION_ERRORS = 11L;

    @InjectMocks
    private PreAgreementVOTransformer testee;

    @Mock
    private WbciMapper wbciMapper;

    private Object[] rowData;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        rowData = new Object[30];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = new Object();
        }
        int index = 0;
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(VA_ID);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(AUFTRAG_ID);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(AUFTRAG_NO_ORIG);
        when(wbciMapper.mapObject(rowData[index++], KundenTyp.class)).thenReturn(KUNDEN_TYP);
        when(wbciMapper.mapObject(rowData[index++], GeschaeftsfallTyp.class)).thenReturn(GF_TYPE);
        when(wbciMapper.mapObject(rowData[index++], RequestTyp.class)).thenReturn(AENDERUNGSKZ);
        when(wbciMapper.mapObject(rowData[index++], CarrierCode.class)).thenReturn(EKP_ABG);
        when(wbciMapper.mapObject(rowData[index++], CarrierCode.class)).thenReturn(EKP_AUF);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(VORGABE_DATUM);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(WECHSELTERMIN);
        when(wbciMapper.mapObject(rowData[index++], WbciGeschaeftsfallStatus.class)).thenReturn(GF_STATUS);
        when(wbciMapper.mapObject(rowData[index++], WbciRequestStatus.class)).thenReturn(STATUS);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(PROCESSED_AT);
        when(wbciMapper.mapObject(rowData[index++], MeldungTyp.class)).thenReturn(RUECKMELDUNG);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(RUECKMELDE_DATUM);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(MELDUNG_CODES);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(REQUEST_ID);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(USER_ID);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(USER_NAME);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(TEAM_ID);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(CURRENT_USER_ID);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(CURRENT_USER_NAME);
        when(wbciMapper.mapObject(rowData[index++], Technologie.class)).thenReturn(MNET_TECHNOLOGIE);
        when(wbciMapper.mapObject(rowData[index++], Boolean.class)).thenReturn(KLAERFALL);
        when(wbciMapper.mapObject(rowData[index++], Boolean.class)).thenReturn(AUTOMATABLE);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(INTERNAL_STATUS);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(ANSWER_DEADLINE);
        when(wbciMapper.mapObject(rowData[index++], Boolean.class)).thenReturn(IS_MNET_DEADLINE);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(NIEDERLASSUNG);
        when(wbciMapper.mapObject(rowData[index], Long.class)).thenReturn(NUMBER_OF_AUTOMATION_ERRORS);
    }

    @Test
    public void testMapObject() throws Exception {
        final PreAgreementVO result = (PreAgreementVO) testee.transformTuple(rowData, null);

        assertThat(result, Matchers.isA(PreAgreementVO.class));
        assertThat(result.getVaid(), is(VA_ID));
        assertThat(result.getAuftragId(), is(AUFTRAG_ID));
        assertThat(result.getAuftragNoOrig(), is(AUFTRAG_NO_ORIG));
        assertThat(result.getGfType(), is(GF_TYPE));
        assertThat(result.getAenderungskz(), is(AENDERUNGSKZ.getShortName()));
        assertThat(result.getEkpAbg(), is(EKP_ABG));
        assertThat(result.getEkpAuf(), is(EKP_AUF));
        assertThat(result.getVorgabeDatum(), is(VORGABE_DATUM));
        assertThat(result.getWechseltermin(), is(WECHSELTERMIN));
        assertThat(result.getRequestStatus(), is(STATUS));
        assertThat(result.getGeschaeftsfallStatus(), is(GF_STATUS));
        assertThat(result.getProcessedAt(), is(PROCESSED_AT));
        assertThat(result.getRueckmeldung(), is(RUECKMELDUNG.getShortName()));
        assertThat(result.getRueckmeldeDatum(), is(RUECKMELDE_DATUM));
        assertThat(result.getMeldungCodes(), is(MELDUNG_CODES));
        assertThat(result.getWbciRequestId(), is(REQUEST_ID));
        assertThat(result.getUserId(), is(USER_ID));
        assertThat(result.getUserName(), is(USER_NAME));
        assertThat(result.getTeamId(), is(TEAM_ID));
        assertThat(result.getCurrentUserId(), is(CURRENT_USER_ID));
        assertThat(result.getCurrentUserName(), is(CURRENT_USER_NAME));
        assertThat(result.getNiederlassung(), is(NIEDERLASSUNG));
        assertThat(result.getMnetTechnologie(), is(MNET_TECHNOLOGIE.getWbciTechnologieCode()));
        assertThat(result.getPreAgreementType(), is(PreAgreementType.GK));
        assertThat(result.getKlaerfall(), is(KLAERFALL));
        assertThat(result.getAutomatable(), is(AUTOMATABLE));
        assertThat(result.getInternalStatus(), is(INTERNAL_STATUS));
        assertThat(result.getDaysUntilDeadlineMnet(), is(calculateDaysUntilDeadlineMnet(ANSWER_DEADLINE, IS_MNET_DEADLINE)));
        assertThat(result.getDaysUntilDeadlinePartner(), is(calculateDaysUntilDeadlinePartner(ANSWER_DEADLINE, IS_MNET_DEADLINE)));
        assertThat(result.isAutomationErrors(), is(true));
    }

    @DataProvider(name = "preAgreementVOMapping")
    public Object[][] findBAVerlaufViews4AbtAktDP() {
        return new Object[][] {
                { KundenTyp.PK, CarrierCode.EINS_UND_EINS, CarrierCode.MNET, Technologie.FTTB, PreAgreementType.WS },
                { KundenTyp.PK, CarrierCode.MNET, CarrierCode.EINS_UND_EINS, Technologie.FTTB, PreAgreementType.WS },
                { KundenTyp.PK, CarrierCode.EINS_UND_EINS, CarrierCode.MNET, Technologie.FTTH, PreAgreementType.WS },
                { KundenTyp.PK, CarrierCode.MNET, CarrierCode.EINS_UND_EINS, Technologie.FTTH, PreAgreementType.WS },
                { KundenTyp.PK, CarrierCode.MNET, CarrierCode.EINS_UND_EINS, Technologie.FTTC, PreAgreementType.PK },
                { KundenTyp.PK, CarrierCode.MNET, CarrierCode.DTAG, Technologie.FTTB, PreAgreementType.PK },
                { KundenTyp.PK, CarrierCode.DTAG, CarrierCode.MNET, Technologie.FTTB, PreAgreementType.PK },
                { KundenTyp.GK, CarrierCode.MNET, CarrierCode.EINS_UND_EINS, Technologie.FTTH, PreAgreementType.GK },
                { KundenTyp.PK, CarrierCode.DTAG, CarrierCode.DEU_QSC, Technologie.FTTH, PreAgreementType.PK }
        };
    }

    @Test(dataProvider = "preAgreementVOMapping")
    public void testMapObject_PreAgreementVO(KundenTyp kundenTyp, CarrierCode ekpAb, CarrierCode ekpAuf,
            Technologie technologie, PreAgreementType expectedType) throws Exception {
        when(wbciMapper.mapObject(rowData[3], KundenTyp.class)).thenReturn(kundenTyp);
        when(wbciMapper.mapObject(rowData[6], CarrierCode.class)).thenReturn(ekpAb);
        when(wbciMapper.mapObject(rowData[7], CarrierCode.class)).thenReturn(ekpAuf);
        when(wbciMapper.mapObject(rowData[22], Technologie.class)).thenReturn(technologie);

        final PreAgreementVO result = (PreAgreementVO) testee.transformTuple(rowData, null);

        assertThat(result.getPreAgreementType(), is(expectedType));
    }
}