/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.10.13
 */
package de.mnet.wbci.validation.constraints;

import static de.mnet.wbci.TestGroups.*;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.common.service.holiday.DateCalculationHelper;

@Test(groups = UNIT)
public class CheckKundenwunschterminNotInRangeTest extends AbstractValidatorTest<CheckKundenwunschterminNotInRange.KundenwunschterminValidator> {

    @Override
    protected CheckKundenwunschterminNotInRange.KundenwunschterminValidator createTestling() {
        return new CheckKundenwunschterminNotInRange.KundenwunschterminValidator();
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testling.rangeFrom = 7;
        testling.rangeTo = 10;
        testling.defaultMessage = "%s";
    }

    @Test
    public void testNullKundenwunschtermin() throws Exception {
        Assert.assertTrue(testling.isValid(null, contextMock));
        assertErrorMessageSet(true);
    }

    @Test
    public void testUnderKundenwunschterminRange() throws Exception {
        Assert.assertTrue(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(6).toLocalDate(), contextMock));
        assertErrorMessageSet(true);
    }

    @Test
    public void testAtBeginningOfKundenwunschterminRange() throws Exception {
        Assert.assertFalse(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(7).toLocalDate(), contextMock));
        assertErrorMessageSet(false);
    }

    @Test
    public void testAtEndOfKundenwunschterminRange() throws Exception {
        Assert.assertFalse(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(10).toLocalDate(), contextMock));
        assertErrorMessageSet(false);
    }

    @Test
    public void testOverKundenwunschterminRange() throws Exception {
        Assert.assertTrue(testling.isValid(DateCalculationHelper.getDateInWorkingDaysFromNow(11).toLocalDate(), contextMock));
        assertErrorMessageSet(true);
    }
}
