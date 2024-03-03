/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.13
 */
package de.augustakom.common.service.holiday;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class HolidayHelperTest {

    @Test
    public void isGermanHoliday() {
        Assert.assertTrue(HolidayHelper.isGermanHoliday(LocalDate.of(2013, 10, 3)));
        Assert.assertFalse(HolidayHelper.isGermanHoliday(LocalDate.of(2013, 8, 15)));
        Assert.assertFalse(HolidayHelper.isGermanHoliday(LocalDate.of(2013, 1, 6)));
        Assert.assertFalse(HolidayHelper.isGermanHoliday(LocalDate.of(2013, 11, 1)));
    }

    @Test
    public void isAugsburgHoliday() {
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 10, 3)));
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 8, 8)));
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 8, 15)));
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 1, 6)));
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 11, 1)));
        Assert.assertFalse(HolidayHelper.isAugsburgHoliday(LocalDate.of(2013, 11, 2)));
        Assert.assertFalse(HolidayHelper.isAugsburgHoliday(LocalDate.of(2017,3,24)));
        Assert.assertFalse(HolidayHelper.isAugsburgHoliday(LocalDate.of(2018,3,24)));
        Assert.assertTrue(HolidayHelper.isAugsburgHoliday(LocalDate.of(2017,10,31)));
        Assert.assertFalse(HolidayHelper.isAugsburgHoliday(LocalDate.of(2018,10,31)));
    }

    @Test
    public void isBayernHoliday() {
        Assert.assertTrue(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 10, 3)));
        Assert.assertFalse(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 8, 8)));
        Assert.assertTrue(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 8, 15)));
        Assert.assertTrue(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 1, 6)));
        Assert.assertTrue(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 11, 1)));
        Assert.assertFalse(HolidayHelper.isBayernHoliday(LocalDate.of(2013, 11, 2)));
    }

}
