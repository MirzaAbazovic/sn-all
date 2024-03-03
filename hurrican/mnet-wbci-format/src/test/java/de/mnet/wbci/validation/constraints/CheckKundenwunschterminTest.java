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
public class CheckKundenwunschterminTest extends AbstractValidatorTest<CheckKundenwunschtermin.KundenwunschterminValidator> {

    @Override
    protected CheckKundenwunschtermin.KundenwunschterminValidator createTestling() {
        return new CheckKundenwunschtermin.KundenwunschterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.minimumLeadTime = 7;
        testling.defaultMessage = "%s";
    }

    @Test
    public void testNullKundenwunschtermin() throws Exception {
        Assert.assertTrue(testling.isValid(null, contextMock));
        assertErrorMessageSet(true);
    }

    @Test
    public void testInvalidKundenwunschtermin() throws Exception {
        Assert.assertFalse(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), contextMock));
        assertErrorMessageSet(false);
    }

    @Test
    public void testValidKundenwunschtermin() throws Exception {
        Assert.assertTrue(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(7), contextMock));
        assertErrorMessageSet(true);
        Assert.assertTrue(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNowAndNextDayNotHoliday(8), contextMock));
        assertErrorMessageSet(true);
    }


    @DataProvider(name = "testHolidayDP")
    public Object[][] testHolidayDP() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime holiday = LocalDateTime.of(now.getYear() + 1, 8, 15, 0, 0, 0, 0);
        LocalDateTime dayBeforeHoliday = holiday.minusDays(1);

        return new Object[][] {
                { holiday },
                { dayBeforeHoliday },
        };
    }


    @Test(dataProvider = "testHolidayDP")
    public void testHoliday(LocalDateTime kwt) {
        Assert.assertFalse(testling.isValid(kwt.toLocalDate(), contextMock));
    }

}
