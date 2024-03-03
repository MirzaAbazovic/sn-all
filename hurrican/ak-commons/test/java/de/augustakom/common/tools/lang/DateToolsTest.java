/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.08.2004 08:09:29
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import java.text.*;
import java.time.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * TestNG-Test fuer die DateTools.
 *
 *
 */
@Test(groups = { BaseTest.UNIT })
public class DateToolsTest extends BaseTest {

    private static final Logger LOGGER = Logger.getLogger(DateToolsTest.class);

    public void testGetDaysOfMonth() {
        GregorianCalendar januar = new GregorianCalendar();
        januar.set(GregorianCalendar.YEAR, 2005);
        januar.set(GregorianCalendar.MONTH, 0);
        januar.set(GregorianCalendar.DAY_OF_MONTH, 5);

        int days = DateTools.getDaysOfMonth(januar.getTime());
        assertEquals(days, 31, "");

        GregorianCalendar februar = new GregorianCalendar();
        februar.set(GregorianCalendar.YEAR, 2005);
        februar.set(GregorianCalendar.MONTH, 1);
        februar.set(GregorianCalendar.DAY_OF_MONTH, 5);
        days = DateTools.getDaysOfMonth(februar.getTime());
        assertEquals(days, 28, "");

        GregorianCalendar feb2004 = new GregorianCalendar();
        feb2004.set(GregorianCalendar.YEAR, 2004);
        feb2004.set(GregorianCalendar.MONTH, 1);
        feb2004.set(GregorianCalendar.DAY_OF_MONTH, 5);
        days = DateTools.getDaysOfMonth(feb2004.getTime());
        assertEquals(days, 29, "");

        GregorianCalendar april = new GregorianCalendar();
        april.set(GregorianCalendar.YEAR, 2005);
        april.set(GregorianCalendar.MONTH, 3);
        april.set(GregorianCalendar.DAY_OF_MONTH, 5);
        days = DateTools.getDaysOfMonth(april.getTime());
        assertEquals(days, 30, "");
    }

    public void testIsBefore() {
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();

        cal1.set(2004, 06, 10);
        cal2.set(2004, 06, 12);
        assertTrue(DateTools.isBefore(cal1.getTime(), cal2.getTime()));

        cal1.set(2004, 06, 10);
        cal2.set(2004, 06, 8);
        assertFalse(DateTools.isBefore(cal1.getTime(), cal2.getTime()));

        cal1.set(2004, 06, 10, 9, 45);
        cal2.set(2004, 06, 12, 10, 55);
        assertTrue(DateTools.isBefore(cal1.getTime(), cal2.getTime()));

        cal1.set(2004, 06, 10, 9, 45);
        cal2.set(2004, 06, 8, 8, 30);
        assertFalse(DateTools.isBefore(cal1.getTime(), cal2.getTime()));
    }

    public void testIsDateEqualWithNull() {
        Date now = new Date();
        assertFalse(DateTools.isDateEqual(now, null));

        assertTrue(DateTools.isDateEqual(now, now));
    }

    public void testIsDateBefore() {
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();

        cal1.set(2004, 06, 10);
        cal2.set(2004, 06, 12);
        assertTrue(DateTools.isDateBefore(cal1.getTime(), cal2.getTime()));

        cal1.set(2004, 06, 10);
        cal2.set(2004, 06, 8);
        assertFalse(DateTools.isDateBefore(cal1.getTime(), cal2.getTime()));

        assertFalse(DateTools.isDateBefore(cal1.getTime(), null));
        assertFalse(DateTools.isDateBefore(null, cal2.getTime()));
    }

    public void testIsDateAfter() {
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();

        cal1.set(2004, 06, 10); // 10.06.2004
        cal2.set(2004, 06, 12); // 12.06.2004
        assertFalse(DateTools.isDateAfter(cal1.getTime(), cal2.getTime()));

        cal1.set(2004, 06, 10); // 10.06.2004
        cal2.set(2004, 06, 8); // 08.06.2004
        assertTrue(DateTools.isDateAfter(cal1.getTime(), cal2.getTime()));

        cal1.set(9999, 12, 31); // 31.12.9999
        cal2.set(2006, 07, 31); // 31.07.2006
        assertTrue(DateTools.isDateAfter(cal1.getTime(), cal2.getTime()));

        assertFalse(DateTools.isDateAfter(cal1.getTime(), null));
        assertFalse(DateTools.isDateAfter(null, cal2.getTime()));
    }

    public void testGetHourOfDay() {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.set(2009, 8, 19, 12, 25, 10);
        int hour = DateTools.getHourOfDay(cal.getTime());
        assertEquals(hour, 12);
    }

    public void testChangeDate() {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.set(2009, 8, 19, 12, 25, 10);
        Date changed = DateTools.changeDate(cal.getTime(), Calendar.DAY_OF_MONTH, -7);
        cal.set(2009, 8, 12, 12, 25, 10);
        assertEquals(changed, cal.getTime());
    }

    /**
     * Test method for {@link DateTools#plusWorkDays(int)}
     */
    public void testPlusWorkDays() {
        Date result = DateTools.plusWorkDays(11);
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTime(result);
        assertFalse(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
        assertFalse(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    /**
     * Test method for {@link DateTools#plusWorkDays(int)}
     */
    public void testMinusWorkDays() {
        Date result = DateTools.minusWorkDays(11);
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTime(result);
        assertFalse(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
        assertFalse(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    /**
     * Test method for {@link DateTools#getLastDateOfMonth(int, int)}
     */
    public void testGetLastDateOfMonth() {
        Date lastOfMonth = DateTools.getLastDateOfMonth(2007, 1);
        LOGGER.debug("last date of month: " + lastOfMonth);
    }

    public void testGetPreviousDateMay25th() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date may25th = dateFormat.parse("25.05.2011");
        Date previousDay = dateFormat.parse("24.05.2011");
        assertTrue(previousDay.equals(DateTools.getPreviousDay(may25th)), "Vortag nicht korrekt!");
    }

    public void testGetPreviousDateMay1st() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date may25th = dateFormat.parse("01.05.2011");
        Date previousDay = dateFormat.parse("30.04.2011");
        assertTrue(previousDay.equals(DateTools.getPreviousDay(may25th)), "Vortag nicht korrekt!");
    }

    public void testGetPreviousDateJanuary1st() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date may25th = dateFormat.parse("01.01.2011");
        Date previousDay = dateFormat.parse("31.12.2010");
        assertTrue(previousDay.equals(DateTools.getPreviousDay(may25th)), "Vortag nicht korrekt!");
    }

    @DataProvider
    public Object[][] dataProviderGetNextCarrierRefNr() {
        // @formatter:off
        return new Object[][] {
                { DateTools.createDate(2012, 1, 1),  DateTools.createDate(2012, 1, 1),    0 },
                { DateTools.createDate(2012, 1, 1),  DateTools.createDate(2012, 1, 2),    1 },
                { DateTools.createDate(2012, 1, 2),  DateTools.createDate(2012, 1, 1),   -1 },
                { DateTools.createDate(2012, 1, 1), DateTools.createDate(2012, 1, 10),    9 },
                { DateTools.createDate(2012, 1, 10),  DateTools.createDate(2012, 1, 1),  -9 },
                { DateTools.createDate(2012, 1, 1),  DateTools.createDate(2012, 2, 1),   29 },
                { DateTools.createDate(2012, 2, 1),  DateTools.createDate(2012, 1, 1),  -29 },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderGetNextCarrierRefNr")
    public void testgetDiffInDays(Date start, Date stop, long expectedDiffInDays) {
        long diffInDays = DateTools.getDiffInDays(start, stop);
        assertEquals(diffInDays, expectedDiffInDays);
    }

    @DataProvider(name = "dataProviderMaxDate")
    Object[][] dataProviderMaxDate() {
        Date today = new Date();
        Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Object[][] {
                { today, today, today },
                { today, tomorrow, tomorrow },
                { tomorrow, today, tomorrow },
                { null, today, today },
                { today, null, today },
                { null, null, null }
        };
    }

    @Test(dataProvider = "dataProviderMaxDate")
    public void testMaxDate(Date date1, Date date2, Date expectedResult) {
        Date result = DateTools.maxDate(date1, date2);
        assertEquals(result, expectedResult);
    }

    @DataProvider
    Object[][] dataProviderMinDate() {
        Date today = new Date();
        Date tomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Object[][] {
                { today, today, today },
                { today, tomorrow, today },
                { tomorrow, today, today },
                { null, today, today },
                { today, null, today },
                { null, null, null }
        };
    }

    @Test(dataProvider = "dataProviderMinDate")
    public void testMinDate(Date date1, Date date2, Date expectedResult) {
        Date result = DateTools.minDate(date1, date2);
        assertEquals(result, expectedResult);
    }

    @Test
    public void testStripTimeFromDate() {
        LocalDateTime result = DateTools.stripTimeFromDate(LocalDateTime.now());
        assertEquals(result.getHour(), 0);
        assertEquals(result.getMinute(), 0);
        assertEquals(result.getSecond(), 0);
    }

    @Test
    public void testStripTimeFromDateWithNull() {
        Assert.assertNull(DateTools.stripTimeFromDate((LocalDateTime) null));
    }

}
