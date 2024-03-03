/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.augustakom.common.service.holiday;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static org.testng.Assert.*;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 */
public class DateCalculationHelperTest {

    final static LocalDate MONDAY_16_12_2013 = LocalDate.of(2013, 12, 16);
    final static LocalDate FRIDAY_20_12_2013 = LocalDate.of(2013, 12, 20);

    @DataProvider
    public Object[][] daysBetween() {
        // @formatter:off
        return new Object[][] {
                {MONDAY_16_12_2013, 1, DateCalculationMode.WEEKDAYS, 1},
                {MONDAY_16_12_2013, 4, DateCalculationMode.WEEKDAYS, 4},
                {MONDAY_16_12_2013, 5, DateCalculationMode.WEEKDAYS, 4},
                {MONDAY_16_12_2013, 6, DateCalculationMode.WEEKDAYS, 4},
                {MONDAY_16_12_2013, 7, DateCalculationMode.WEEKDAYS, 5},
                {FRIDAY_20_12_2013, 1, DateCalculationMode.WEEKDAYS, 0},
                {FRIDAY_20_12_2013, 3, DateCalculationMode.WEEKDAYS, 1},
                {FRIDAY_20_12_2013, 1, DateCalculationMode.WORKINGDAYS, 0},
                {FRIDAY_20_12_2013, 3, DateCalculationMode.WORKINGDAYS, 1},
                {FRIDAY_20_12_2013, 5, DateCalculationMode.WORKINGDAYS, 2},
                {FRIDAY_20_12_2013, 6, DateCalculationMode.WORKINGDAYS, 2},
                {FRIDAY_20_12_2013, 7, DateCalculationMode.WORKINGDAYS, 3},
                {FRIDAY_20_12_2013, -1, DateCalculationMode.WORKINGDAYS, 0},
                {FRIDAY_20_12_2013, 1, DateCalculationMode.ALL, 1},
                {FRIDAY_20_12_2013, 3, DateCalculationMode.ALL, 3},
                {FRIDAY_20_12_2013, 5, DateCalculationMode.ALL, 5},
                {FRIDAY_20_12_2013, 6, DateCalculationMode.ALL, 6},
                {FRIDAY_20_12_2013, 7, DateCalculationMode.ALL, 7},
        };
        // @formatter:on
    }

    @Test(dataProvider = "daysBetween")
    public void testGetDaysBetween(LocalDate date, int difference, DateCalculationMode calculationMode, int expectedResult) throws Exception {
        Assert.assertEquals(getDaysBetween(date, date.plusDays(difference), calculationMode), expectedResult);
    }

    @DataProvider
    public Object[][] daysBetweenAlsoNegative() {
        // @formatter:off
        return new Object[][] {
                {MONDAY_16_12_2013, LocalDate.of(2013, 12, 12), DateCalculationMode.WEEKDAYS, -2},
                {MONDAY_16_12_2013, LocalDate.of(2013, 12, 13), DateCalculationMode.WEEKDAYS, -1},
                {MONDAY_16_12_2013, LocalDate.of(2013, 12, 14), DateCalculationMode.WEEKDAYS, -1},
                {MONDAY_16_12_2013, LocalDate.of(2013, 12, 15), DateCalculationMode.WEEKDAYS, -1},
                {FRIDAY_20_12_2013, LocalDate.of(2013, 12, 19), DateCalculationMode.WEEKDAYS, -1},
                {FRIDAY_20_12_2013, LocalDate.of(2013, 12, 18), DateCalculationMode.WEEKDAYS, -2},
                {FRIDAY_20_12_2013, LocalDate.of(2013, 12, 21), DateCalculationMode.WEEKDAYS, 0},
                {FRIDAY_20_12_2013, LocalDate.of(2013, 12, 23), DateCalculationMode.WEEKDAYS, 1},
        };
        // @formatter:on
    }

    @Test(dataProvider = "daysBetweenAlsoNegative")
    public void testGetDaysBetweenAlsoNegative(LocalDate firstDate, LocalDate secondDate, DateCalculationMode calculationMode, int expectedResult) throws Exception {
        Assert.assertEquals(getDifferenceInDays(firstDate, secondDate, calculationMode), expectedResult);
    }

    @Test
    public void testPlusWorkingDays() throws Exception {
        Assert.assertEquals(addWorkingDays(LocalDate.of(2013, 12, 20), 0), LocalDate.of(2013, 12, 20));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2013, 12, 20), 1), LocalDate.of(2013, 12, 23));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2013, 12, 20), 3), LocalDate.of(2013, 12, 27));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2013, 12, 20), 5), LocalDate.of(2013, 12, 31));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2013, 12, 20), 6), LocalDate.of(2014, 1, 2));
    }

    @Test
    public void testPlusWorkingDaysNegative() throws Exception {
        Assert.assertEquals(addWorkingDays(LocalDate.of(2014, 1, 2), -0), LocalDate.of(2014, 1, 2));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2014, 1, 2), -1), LocalDate.of(2013, 12, 31));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2014, 1, 2), -3), LocalDate.of(2013, 12, 27));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2014, 1, 2), -4), LocalDate.of(2013, 12, 24));
        Assert.assertEquals(addWorkingDays(LocalDate.of(2014, 1, 2), -6), LocalDate.of(2013, 12, 20));
    }

    @DataProvider
    public Object[][] weekday() {
        // @formatter:off
        return new Object[][] {
                { LocalDate.now().with(DayOfWeek.MONDAY), true },
                { LocalDate.now().with(DayOfWeek.TUESDAY), true },
                { LocalDate.now().with(DayOfWeek.WEDNESDAY), true },
                { LocalDate.now().with(DayOfWeek.THURSDAY), true },
                { LocalDate.now().with(DayOfWeek.FRIDAY), true },
                { LocalDate.now().with(DayOfWeek.SATURDAY), false },
                { LocalDate.now().with(DayOfWeek.SUNDAY), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "weekday")
    public void testIsWeekday(LocalDate date, boolean expectedResult) {
        assertEquals(isWeekday(date), expectedResult);
    }

    @DataProvider
    public Object[][] workingDay() {
        // @formatter:off
        return new Object[][] {
                { LocalDate.of(2013, 12, 20), true },
                { LocalDate.of(2013, 12, 21), false },
                { LocalDate.of(2013, 12, 22), false },
                { LocalDate.of(2013, 12, 23), true },
                { LocalDate.of(2013, 12, 24), true },
                { LocalDate.of(2013, 12, 25), false },
                { LocalDate.of(2013, 12, 26), false },
                { LocalDate.of(2013, 12, 27), true },
                { LocalDate.of(2017, 03, 24), false },
                { LocalDate.of(2017, 10, 31), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "workingDay")
    public void testIsWorkingDay(LocalDate date, boolean expectedResult) {
        assertEquals(isWorkingDay(date), expectedResult);
    }

    @DataProvider
    public Object[][] nextDayNotAHoliday() {
        // @formatter:off
        return new Object[][] {
                { LocalDate.of(2013, 12, 20), true }, // friday
                { LocalDate.of(2013, 12, 21), true }, // saturday
                { LocalDate.of(2013, 12, 22), true },
                { LocalDate.of(2013, 12, 23), true },
                { LocalDate.of(2013, 12, 24), false },
                { LocalDate.of(2013, 12, 25), false },
                { LocalDate.of(2013, 12, 26), true },
                { LocalDate.of(2013, 12, 27), true },
                { LocalDate.of(2013, 10, 31), false },
                { LocalDate.of(2017, 10, 30), false },
        };
        // @formatter:on
    }

    @Test(dataProvider = "nextDayNotAHoliday")
    public void testIsNextDayNotAHoliday(LocalDate date, boolean expectedResult) {
        assertEquals(isNextDayNotAHoliday(date), expectedResult);
    }

    @Test
    public void testIsDateBetween() throws Exception {
        Assert.assertTrue(isDateBetween(MONDAY_16_12_2013, MONDAY_16_12_2013, MONDAY_16_12_2013));
        Assert.assertTrue(isDateBetween(MONDAY_16_12_2013, MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(1)));
        Assert.assertTrue(isDateBetween(MONDAY_16_12_2013.plusDays(1), MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(1)));
        Assert.assertTrue(isDateBetween(MONDAY_16_12_2013.plusDays(1), MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(2)));

        Assert.assertFalse(isDateBetween(MONDAY_16_12_2013.plusDays(2), MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(1)));
        Assert.assertFalse(isDateBetween(MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(1), MONDAY_16_12_2013.plusDays(2)));
        Assert.assertFalse(isDateBetween(MONDAY_16_12_2013, MONDAY_16_12_2013.plusDays(2), MONDAY_16_12_2013.plusDays(1)));
    }

    @DataProvider
    public Object[][] dataProviderAsWorkingDay() {
        // @formatter:off
       return new Object[][] {
                { LocalDate.of(2013, 12, 20),     LocalDate.of(2013, 12, 20) },
                { LocalDate.of(2013, 12, 21),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 22),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 23),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 24),     LocalDate.of(2013, 12, 24) },
                { LocalDate.of(2013, 12, 25),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2013, 12, 26),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2013, 12, 27),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2014,  1,  4),     LocalDate.of(2014,  1,  7) },
                { LocalDate.of(2014,  1,  5),     LocalDate.of(2014,  1,  7) },
                { LocalDate.of(2014,  1,  6),     LocalDate.of(2014,  1,  7) },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderAsWorkingDay")
    public void testAsWorkingDay(LocalDate date, LocalDate expected) {
        assertEquals(asWorkingDay(date), expected);
    }

    @DataProvider
    public Object[][] dataProviderAsWorkingDayAndNextDayIsWorkingDay() {
        // @formatter:off
        return new Object[][] {
                { LocalDate.of(2013, 12, 20),     LocalDate.of(2013, 12, 20) },
                { LocalDate.of(2013, 12, 21),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 22),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 23),     LocalDate.of(2013, 12, 23) },
                { LocalDate.of(2013, 12, 24),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2013, 12, 25),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2013, 12, 26),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2013, 12, 27),     LocalDate.of(2013, 12, 27) },
                { LocalDate.of(2014,  1,  4),     LocalDate.of(2014,  1,  7) },
                { LocalDate.of(2014,  1,  5),     LocalDate.of(2014,  1,  7) },
                { LocalDate.of(2014,  1,  6),     LocalDate.of(2014,  1,  7) },
                { LocalDate.of(2014, 10, 30),     LocalDate.of(2014, 10, 30) },
                { LocalDate.of(2014,  4, 17),     LocalDate.of(2014,  4, 22) },
                { LocalDate.of(2014,  8, 14),     LocalDate.of(2014,  8, 18) }, // 14.8.2014 is a thursday but next day is a holiday
                { LocalDate.of(2014, 10, 31),     LocalDate.of(2014, 11,  3) }, // 1.11.2014 is a saturday but also a holiday
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderAsWorkingDayAndNextDayIsWorkingDay")
    public void testAsWorkingDayAndNextDayIsWorkingDay(LocalDate date, LocalDate expected) {
        assertEquals(asWorkingDayAndNextDayNotHoliday(date), expected);
    }

    @DataProvider(name = "dataProviderIsWorkingDayAndNextDayNotHoliday")
    public Object[][] dataProviderIsWorkingDayAndNextDayNotHoliday() {
        // @formatter:off
        return new Object[][] {
                { LocalDate.of(2014,  5, 22), true },  // Thursday
                { LocalDate.of(2014,  5, 23), true },  // Friday
                { LocalDate.of(2014,  5, 28), false }, // 29.05.2014 is a holiday
                { LocalDate.of(2014,  5, 29), false }, // 29.05.2014 is a holiday
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderIsWorkingDayAndNextDayNotHoliday")
    public void testIsWorkingDayAndNextDayNotHoliday(LocalDate toCheck, boolean expected) {
        assertEquals(isWorkingDayAndNextDayNotHoliday(toCheck), expected);
    }

    @Test
    public void testGetDateInWorkingDays() {
        final int days = 2;
        final LocalDate baseDate = LocalDate.of(2015, 5, 1); //friday and holiday
        final LocalDate result = getDateInWorkingDays(baseDate, days);
        assertEquals(result.getYear(), 2015);
        assertEquals(result.getMonthValue(), 5);
        assertEquals(result.getDayOfMonth(), 6);
    }
}
