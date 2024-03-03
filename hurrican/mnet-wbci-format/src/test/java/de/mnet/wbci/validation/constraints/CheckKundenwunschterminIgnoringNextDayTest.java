/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import java.time.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;

@Test(groups = UNIT)
public class CheckKundenwunschterminIgnoringNextDayTest extends AbstractValidatorTest<CheckKundenwunschterminIgnoringNextDay.KundenwunschterminValidator> {

    @Override
    protected CheckKundenwunschterminIgnoringNextDay.KundenwunschterminValidator createTestling() {
        return new CheckKundenwunschterminIgnoringNextDay.KundenwunschterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.minimumLeadTime = 1;
        testling.defaultMessage = "%s";
    }

    @Test
    public void testNullKundenwunschtermin() throws Exception {
        Assert.assertTrue(testling.isValid(null, contextMock));
        assertErrorMessageSet(true);
    }

    @Test
    public void testInvalidKundenwunschtermin() throws Exception {
        Assert.assertFalse(testling.isValid(LocalDateTime.now().toLocalDate(), contextMock));
        assertErrorMessageSet(false);
    }

    @Test
    public void testValidKundenwunschtermin() throws Exception {
        Assert.assertTrue(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(1).toLocalDate(), contextMock));
        assertErrorMessageSet(true);
    }

    @DataProvider(name = "testHolidayDP")
    public Object[][] testHolidayDP() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime holiday = LocalDateTime.of(now.getYear() + 1, 8, 15, 0, 0, 0, 0);
        LocalDateTime dayBeforeHoliday = holiday.plusDays(1);
        while (!DateCalculationHelper.isWorkingDay(dayBeforeHoliday.toLocalDate())) {
            dayBeforeHoliday = dayBeforeHoliday.minusDays(1);
        }

        return new Object[][] {
                { holiday, false },
                { dayBeforeHoliday, true },
        };
    }

    @Test(dataProvider = "testHolidayDP")
    public void testHoliday(LocalDateTime kwt, boolean dateOk) {
        Assert.assertEquals(testling.isValid(kwt.toLocalDate(), contextMock), dateOk);
    }

}
