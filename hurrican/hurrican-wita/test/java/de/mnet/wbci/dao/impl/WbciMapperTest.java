package de.mnet.wbci.dao.impl;

import static de.mnet.wbci.model.GeschaeftsfallTyp.*;
import static de.mnet.wbci.model.MeldungTyp.*;
import static de.mnet.wbci.model.WbciGeschaeftsfallStatus.*;
import static de.mnet.wbci.model.WbciRequestStatus.*;
import static org.testng.Assert.*;

import java.math.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.wbci.model.CarrierCode;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.KundenTyp;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.RequestTyp;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Tests the class {@link WbciMapperTest}
 * <p>
 * Created by zieglerch on 06.02.2017.
 */
@Test(groups = BaseTest.UNIT)
public class WbciMapperTest {

    private WbciMapper testee;

    @BeforeMethod
    public void setUp() throws Exception {
        testee = new WbciMapper();
    }

    @Test
    public void testMapObject_MeldungTyp() throws Exception {
        final MeldungTyp result = testee.mapObject("AKM_TR", MeldungTyp.class);

        assertEquals(result, AKM_TR);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_MeldungTyp_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", MeldungTyp.class);
    }

    @Test
    public void testMapObject_WbciRequestStatus() throws Exception {
        final WbciRequestStatus result = testee.mapObject("ABBM_TR_VERSENDET", WbciRequestStatus.class);

        assertEquals(result, ABBM_TR_VERSENDET);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_WbciRequestStatus_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", WbciRequestStatus.class);
    }

    @Test
    public void testMapObject_WbciGeschaeftsfallStatus() throws Exception {
        final WbciGeschaeftsfallStatus result = testee.mapObject("ACTIVE", WbciGeschaeftsfallStatus.class);

        assertEquals(result, ACTIVE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_WbciGeschaeftsfallStatus_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", WbciGeschaeftsfallStatus.class);
    }

    @Test
    public void testMapObject_RequestTyp() throws Exception {
        final RequestTyp result = testee.mapObject("STR-AEN-ABG", RequestTyp.class);

        assertEquals(result, RequestTyp.STR_AEN_ABG);
    }

    @Test
    public void testMapObject_RequestTyp_WrongValue() throws Exception {
        final RequestTyp result = testee.mapObject("UNKNOWN", RequestTyp.class);

        assertEquals(result, RequestTyp.UNBEKANNT);
    }

    @Test
    public void testMapObject_CarrierCode() throws Exception {
        final CarrierCode result = testee.mapObject("MNET", CarrierCode.class);

        assertEquals(result, CarrierCode.MNET);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_CarrierCode_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", CarrierCode.class);
    }

    @Test
    public void testMapObject_KundenTyp() throws Exception {
        final KundenTyp result = testee.mapObject("GK", KundenTyp.class);

        assertEquals(result, KundenTyp.GK);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_KundenTyp_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", KundenTyp.class);
    }

    @Test
    public void testMapObject_Technologie() throws Exception {
        final Technologie result = testee.mapObject("FTTB", Technologie.class);

        assertEquals(result, Technologie.FTTB);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMapObject_Technologie_WrongValue() throws Exception {
        testee.mapObject("UNKNOWN", Technologie.class);
    }

    @Test
    public void testMapObject_GeschaeftsfallTyp() throws Exception {
        final GeschaeftsfallTyp result = testee.mapObject("VA_KUE_MRN", GeschaeftsfallTyp.class);

        assertEquals(result, GeschaeftsfallTyp.VA_KUE_MRN);
    }

    @Test
    public void testMapObject_GeschaeftsfallTyp_WrongValue() throws Exception {
        final GeschaeftsfallTyp result = testee.mapObject("UNKNOWN", GeschaeftsfallTyp.class);

        assertEquals(result, VA_UNBEKANNT);
    }

    @Test
    public void testMapObject_Boolean_EqualOne() throws Exception {
        final Boolean result = testee.mapObject(BigDecimal.valueOf(1), Boolean.class);

        assertEquals(result, Boolean.TRUE);
    }

    @Test
    public void testMapObject_Boolean_NotEqualOne() throws Exception {
        final Boolean result = testee.mapObject(BigDecimal.valueOf(99), Boolean.class);

        assertEquals(result, Boolean.FALSE);
    }

    @Test
    public void testMapObject_BigDecimal() throws Exception {
        final Long result = testee.mapObject(BigDecimal.valueOf(99L), Long.class);

        assertEquals(result, Long.valueOf(99L));
    }
}