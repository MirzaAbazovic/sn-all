package de.mnet.wbci.dao.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.OverdueAbmPvVO;

/**
 * Tests the class {@link OverdueAbmPvVOTransformer}
 * <p>
 * Created by zieglerch on 06.02.2017.
 */
@Test(groups = BaseTest.UNIT)
public class OverdueAbmPvVOTransformerTest {

    private static final String VA_ID = "VA_ID";
    private static final CarrierCode EKP_AUF = CarrierCode.EINS_UND_EINS;
    private static final CarrierCode EKP_ABG = CarrierCode.DTAG;
    private static final Date WECHSELTERMIN = new Date(LocalDate.of(2016, 5, 22).toEpochDay());
    private static final Long AUFTRAG_NO_ORIG = 99L;
    private static final Long AUFTRAG_ID = 88L;
    private static final String VERTRAGS_NUMMER = "VERTRAGS_NUMMER";
    private static final boolean AKM_PV_RECEIVED = true;
    private static final boolean ABBM_PV_RECEIVED = true;

    @InjectMocks
    private OverdueAbmPvVOTransformer testee;

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
        when(wbciMapper.mapObject(rowData[index++], CarrierCode.class)).thenReturn(EKP_AUF);
        when(wbciMapper.mapObject(rowData[index++], CarrierCode.class)).thenReturn(EKP_ABG);
        when(wbciMapper.mapObject(rowData[index++], Date.class)).thenReturn(WECHSELTERMIN);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(AUFTRAG_NO_ORIG);
        when(wbciMapper.mapObject(rowData[index++], Long.class)).thenReturn(AUFTRAG_ID);
        when(wbciMapper.mapObject(rowData[index++], String.class)).thenReturn(VERTRAGS_NUMMER);
        when(wbciMapper.mapObject(rowData[index++], Boolean.class)).thenReturn(AKM_PV_RECEIVED);
        when(wbciMapper.mapObject(rowData[index], Boolean.class)).thenReturn(ABBM_PV_RECEIVED);
    }

    @Test
    public void testTransformTuple() throws Exception {
        final OverdueAbmPvVO result = ((OverdueAbmPvVO) testee.transformTuple(rowData, null));

        assertThat(result, Matchers.isA(OverdueAbmPvVO.class));
        MatcherAssert.assertThat(result.getVaid(), is(VA_ID));
        MatcherAssert.assertThat(result.getEkpAuf(), is(EKP_AUF));
        MatcherAssert.assertThat(result.getEkpAbg(), is(EKP_ABG));
        MatcherAssert.assertThat(result.getWechseltermin(), is(WECHSELTERMIN));
        MatcherAssert.assertThat(result.getAuftragNoOrig(), is(AUFTRAG_NO_ORIG));
        MatcherAssert.assertThat(result.getAuftragId(), is(AUFTRAG_ID));
        MatcherAssert.assertThat(result.getVertragsnummer(), is(VERTRAGS_NUMMER));
        MatcherAssert.assertThat(result.isAkmPvReceived(), is(AKM_PV_RECEIVED));
        MatcherAssert.assertThat(result.isAbbmPvReceived(), is(ABBM_PV_RECEIVED));
    }
}