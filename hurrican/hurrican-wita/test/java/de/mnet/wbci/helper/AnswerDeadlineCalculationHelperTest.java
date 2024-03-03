/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.04.2014
 */
package de.mnet.wbci.helper;

import static de.augustakom.common.service.holiday.DateCalculationHelper.*;
import static de.mnet.wbci.helper.AnswerDeadlineCalculationHelper.*;

import java.time.*;
import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AnswerDeadlineCalculationHelperTest {

    @DataProvider
    public static Object[][] calculateDaysUntilDeadlineMnetDataProvider() {
        return new Object[][] {
                { Date.from(getDateInWorkingDaysFromNow(5).atZone(ZoneId.systemDefault()).toInstant()), true, 5 },
                { null, true, null },
                { null, false, null },
                { Date.from(getDateInWorkingDaysFromNow(4).atZone(ZoneId.systemDefault()).toInstant()), false, null },
                { Date.from(getDateInWorkingDaysFromNow(5).atZone(ZoneId.systemDefault()).toInstant()), null, null },
        };
    }

    @Test(dataProvider = "calculateDaysUntilDeadlineMnetDataProvider")
    public void testCalculateDaysUntilDeadlineMnet(Date answerDeadline, Boolean isMnetDeadline, Integer expectedResult) {
        final Integer daysUntilDeadline = calculateDaysUntilDeadlineMnet(answerDeadline, isMnetDeadline);
        Assert.assertEquals(daysUntilDeadline, expectedResult);
    }

    @DataProvider
    public static Object[][] calculateDaysUntilDeadlinePartnerDataProvider() {
        return new Object[][] {
                { Date.from(getDateInWorkingDaysFromNow(5).atZone(ZoneId.systemDefault()).toInstant()), false, 5 },
                { Date.from(getDateInWorkingDaysFromNow(5).atZone(ZoneId.systemDefault()).toInstant()), null, 5 },
                { null, false, null },
                { null, true, null },
                { Date.from(getDateInWorkingDaysFromNow(4).atZone(ZoneId.systemDefault()).toInstant()), true, null },
        };
    }

    @Test(dataProvider = "calculateDaysUntilDeadlinePartnerDataProvider")
    public void testCalculateDaysUntilDeadlinePartner(Date answerDeadline, Boolean isMnetDeadline, Integer expectedResult) {
        final Integer daysUntilDeadline = calculateDaysUntilDeadlinePartner(answerDeadline, isMnetDeadline);
        Assert.assertEquals(daysUntilDeadline, expectedResult);
    }

}
