/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.08.2011 16:21:19
 */
package de.mnet.wita.service.impl;

import static de.mnet.wita.message.GeschaeftsfallTyp.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.service.holiday.DateCalculationHelper;
import de.mnet.wbci.service.WbciCommonService;
import de.mnet.wita.message.GeschaeftsfallTyp;
import de.mnet.wita.message.auftrag.Kundenwunschtermin.Zeitfenster;
import de.mnet.wita.service.WitaConfigService;

@Test(groups = BaseTest.UNIT)
public class DateTimeCalculationServiceTest extends BaseTest {

    @InjectMocks
    private DateTimeCalculationService underTest;

    @Mock
    private WitaConfigService witaConfigServiceMock;

    @Mock
    private WbciCommonService wbciCommonServiceMock;

    @BeforeMethod
    public void setUp() {
        underTest = new DateTimeCalculationService();
        MockitoAnnotations.initMocks(this);
    }

    public void testTvValid() {
        DateTimeCalculationService spy = Mockito.spy(underTest);
        Mockito.reset(spy);

        LocalDate terminVerschiebung = DateCalculationHelper.asWorkingDay(LocalDate.now().plusDays(21));

        GeschaeftsfallTyp geschaeftsfallTyp = BEREITSTELLUNG;
        doReturn(true).when(spy).isKundenwunschTerminValid(any(LocalDateTime.class), any(LocalDateTime.class), any(Boolean.class),
                any(Boolean.class), eq(geschaeftsfallTyp), (String) isNull(), any(Boolean.class));
        boolean result = spy.isTerminverschiebungValid(terminVerschiebung, Zeitfenster.SLOT_2, false, geschaeftsfallTyp, null, false);
        verify(spy).isKundenwunschTerminValid(any(LocalDateTime.class), any(LocalDateTime.class), any(Boolean.class),
                any(Boolean.class), eq(geschaeftsfallTyp), (String) isNull(), any(Boolean.class));
        assertEquals(result, true);
    }

    @DataProvider
    public Object[][] timelyVLT() {
        return new Object[][] {
                { LocalDateTime.of(2013, 12, 16, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 17, 10, 0, 0, 0), Zeitfenster.SLOT_3, false }, // only 24 hours difference
                { LocalDateTime.of(2013, 12, 16, 7, 0, 0, 0), LocalDateTime.of(2013, 12, 17, 20, 0, 0, 0), Zeitfenster.SLOT_3, true }, // 36 hours difference
                { LocalDateTime.of(2013, 12, 16, 7, 0, 0, 0), LocalDateTime.of(2013, 12, 17, 20, 0, 0, 0), Zeitfenster.SLOT_2, false }, // 36 hours difference, but time slot 2
                { LocalDateTime.of(2013, 12, 20, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 23, 10, 0, 0, 0), Zeitfenster.SLOT_2, false }, // only 24 hours difference and weekend in-between
                { LocalDateTime.of(2013, 12, 20, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 24, 10, 0, 0, 0), Zeitfenster.SLOT_2, true }, // 36 hours difference
                { LocalDateTime.of(2013, 12, 23, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 27, 10, 0, 0, 0), Zeitfenster.SLOT_2, true }, // 36 hours difference
                { LocalDateTime.of(2013, 12, 24, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 27, 10, 0, 0, 0), Zeitfenster.SLOT_2, false }, // only 24 hours difference and holidays in-between
                { LocalDateTime.of(2013, 12, 24, 10, 0, 0, 0), LocalDateTime.of(2013, 12, 28, 10, 0, 0, 0), Zeitfenster.SLOT_2, false }, // more than 36 hours difference
        };
    }

    @Test(dataProvider = "timelyVLT")
    public void testTimelyVLT(LocalDateTime stornoDate, LocalDateTime realisierungsDate, Zeitfenster zeitfenster, boolean expected) {
        assertThat(
                String.format("%s is not timely wrt %s and zeitfenster %s", stornoDate, realisierungsDate, zeitfenster),
                underTest.isTimelyBeforeVLT(stornoDate, realisierungsDate, zeitfenster, 0), equalTo(expected));
    }

    @DataProvider
    public Object[][] vltDayInZeitfenster() {
        final TemporalField fieldISO = WeekFields.of(Locale.GERMANY).dayOfWeek();
        LocalDateTime monday = LocalDateTime.now().with(fieldISO ,1).withHour(10);  // monday
        LocalDateTime tuesday = monday.plusDays(1);
        LocalDateTime wednesday = monday.plusDays(2);
        LocalDateTime thursday = monday.plusDays(3);
        LocalDateTime friday = monday.plusDays(4);

        return new Object[][] { { monday, Zeitfenster.SLOT_2, true }, { monday, Zeitfenster.SLOT_3, false },
                { monday, Zeitfenster.SLOT_4, false }, { monday, Zeitfenster.SLOT_6, false },
                { monday, Zeitfenster.SLOT_7, true }, { tuesday, Zeitfenster.SLOT_2, true },
                { tuesday, Zeitfenster.SLOT_3, true }, { tuesday, Zeitfenster.SLOT_4, false },
                { tuesday, Zeitfenster.SLOT_6, false }, { tuesday, Zeitfenster.SLOT_7, true },
                { wednesday, Zeitfenster.SLOT_2, true }, { wednesday, Zeitfenster.SLOT_3, false },
                { wednesday, Zeitfenster.SLOT_4, false }, { wednesday, Zeitfenster.SLOT_6, true },
                { wednesday, Zeitfenster.SLOT_7, true }, { thursday, Zeitfenster.SLOT_2, true },
                { thursday, Zeitfenster.SLOT_3, false }, { thursday, Zeitfenster.SLOT_4, false },
                { thursday, Zeitfenster.SLOT_6, false }, { thursday, Zeitfenster.SLOT_7, true },
                { friday, Zeitfenster.SLOT_2, true }, { friday, Zeitfenster.SLOT_3, false },
                { friday, Zeitfenster.SLOT_4, true }, { friday, Zeitfenster.SLOT_6, false },
                { friday, Zeitfenster.SLOT_7, true }, { monday, null, true }, { tuesday, null, true },
                { wednesday, null, true }, { thursday, null, true }, { friday, null, true }, };
    }

    @Test(dataProvider = "vltDayInZeitfenster")
    public void testDayInZeitfensterValid(LocalDateTime realisierungsDate, Zeitfenster zeitfenster, boolean expected) {
        assertThat(String.format("Realiserung (%s) and zeitfenster %s are not valid", realisierungsDate, zeitfenster),
                underTest.isKwtDayInZeitfenster(Date.from(realisierungsDate.atZone(ZoneId.systemDefault()).toInstant()), zeitfenster), equalTo(expected));
    }

    @DataProvider
    public Object[][] kwtValid() {
        LocalDateTime referenceDate = dt(2013, 12, 17);
        return new Object[][] {
                { referenceDate, dt(2014, 10, 31), false, false, false,
                        VERBUNDLEISTUNG, null, false }, // it's a friday, but the next day is a holiday
                { referenceDate, dt(2014, 1, 4), false, false, false,
                        VERBUNDLEISTUNG, null, false }, // it's a saturday
                { referenceDate, dt(2014, 1, 5), false, false, false,
                        VERBUNDLEISTUNG, null, false }, // it's a sunday
                { referenceDate, dt(2014, 1, 6), false, false, false,
                        VERBUNDLEISTUNG, null, false }, // it's a holiday
                { referenceDate, dt(2013, 12, 31), false, false, false,
                        BEREITSTELLUNG, "DEU.MNET.1897263912", false }, // the day after is a holiday
                { referenceDate, dt(2013, 12, 31), false, false, false,
                        VERBUNDLEISTUNG, null, false }, // the day after is a holiday
                { referenceDate, dt(2013, 12, 31), false, false, false,
                        PROVIDERWECHSEL, null, false }, // the day after is a holiday

                // tests for HVt nach KVz (Vorlaufzeit > 16 Arbeitstage)
                { referenceDate, dt(2014, 1, 14), false, false, true,
                        BEREITSTELLUNG, null, false }, // at least 16 working days must be in between
                { referenceDate, dt(2014, 1, 15), false, false, true,
                        BEREITSTELLUNG, null, true }, // 16 working days are in between
                { referenceDate, dt(2014, 1, 18), false, false, true,
                        BEREITSTELLUNG, null, false }, // more than 16 working days are in between, but it's saturday
                // tests for HVt nach KVz (Terminverschiebung >= 7 AT)
                { referenceDate, dt(2013, 12, 27), false, true, true,
                        BEREITSTELLUNG, null, false }, // TV less than 7 AT in future
                { referenceDate, dt(2014, 1, 2), false, true, true,
                        BEREITSTELLUNG, null, true }, // TV >= 7 AT in future
        };
    }

    @Test(dataProvider = "kwtValid")
    public void testKundenwunschTerminValid(LocalDateTime now, LocalDateTime neuerKundenWunschTermin, boolean hasTam,
            boolean isTv, boolean isHvtToKvz, GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmugsId,
            boolean expected) {
        assertEquals(underTest.isKundenwunschTerminValid(now, neuerKundenWunschTermin, hasTam, isTv, geschaeftsfallTyp,
                vorabstimmugsId, isHvtToKvz), expected);
    }

    @DataProvider
    public Object[][] vorgabeDatumTv60() {
        // @formatter:off
        return new Object[][] {
                { dt(2011, 1, 1), VERBUNDLEISTUNG, null, dt(2011, 3, 2)},
                { dt(2011, 1, 11), VERBUNDLEISTUNG, null, dt(2011, 3, 14)},
                { dt(2014, 6, 7), VERBUNDLEISTUNG, null, dt(2014, 8, 6)},
                { dt(2014, 6, 2), VERBUNDLEISTUNG, null, dt(2014, 8, 1)}, // friday is a valid date
                { dt(2014, 6, 3), VERBUNDLEISTUNG, null, dt(2014, 8, 4)},
                { dt(2014, 6, 8), BEREITSTELLUNG, "DEU.MNET.1897263913", dt(2014, 8, 11)},
                { dt(2014, 6, 8), VERBUNDLEISTUNG, null, dt(2014, 8, 11)},
                { dt(2014, 6, 9), VERBUNDLEISTUNG, null, dt(2014, 8, 11)},
                { dt(2014, 6, 10), VERBUNDLEISTUNG, null, dt(2014, 8, 11)},
                { dt(2014, 6, 11), VERBUNDLEISTUNG, null, dt(2014, 8, 11)},
                { dt(2014, 6, 12), VERBUNDLEISTUNG, null, dt(2014, 8, 11)},
                { dt(2014, 6, 13), VERBUNDLEISTUNG, null, dt(2014, 8, 12)},
        };
        // @formatter:on
    }

    private static LocalDateTime dt(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
    }

    @Test(dataProvider = "vorgabeDatumTv60")
    public void getVorgabeDatumTv60(LocalDateTime now, GeschaeftsfallTyp geschaeftsfallTyp, String vorabstimmungsId,
            LocalDateTime expectedVorgabeDatum) {
        assertEquals(underTest.getVorgabeDatumTv60(now, geschaeftsfallTyp, vorabstimmungsId), expectedVorgabeDatum);
    }


    @DataProvider(name = "hasToCheckNextDayDataProvider")
    public Object[][] hasToCheckNextDayDataProvider() {
        String vaId = "DEU.MNET.XXX";
        return new Object[][] {
                { BEREITSTELLUNG, null, false },
                { BEREITSTELLUNG, vaId, true },
                { KUENDIGUNG_KUNDE, null, false },
                { KUENDIGUNG_KUNDE, vaId, true },
                { PROVIDERWECHSEL, null, true },
                { VERBUNDLEISTUNG, null, true },
                { LEISTUNGS_AENDERUNG, null, false },
                { LEISTUNGSMERKMAL_AENDERUNG, null, false },
        };
    }


    @Test(dataProvider = "hasToCheckNextDayDataProvider")
    public void hasToCheckNextDay(GeschaeftsfallTyp gfTyp, String vorabstimmungsId, boolean expected) {
        assertEquals(underTest.hasToCheckNextDay(gfTyp, vorabstimmungsId), expected);
    }

}
