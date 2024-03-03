/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.12.13
 */
package de.mnet.hurrican.simulator.function;

import java.util.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class AsWorkingDayFunctionTest {

    private AsWorkingDayFunction testling = new AsWorkingDayFunction();

    @DataProvider
    public Object[][] dataProvider() {
        // @formatter:off
        return new Object[][] {
                { "yyyy-MM-dd", "2014-01-01", "2014-01-02" },
                { "yyyy-MM-dd", "2014-01-02", "2014-01-02" },
                { "yyyy-MM-dd", "2014-01-04", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-05", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-06", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-07", "2014-01-07" },
                { "yyyy-MM-dd", "2014-08-08", "2014-08-11" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProvider")
    public void testExecute(String pattern, String date, String expected) throws Exception {
        Assert.assertEquals(testling.execute(Arrays.asList(pattern, date), null), expected);
    }

    @DataProvider
    public Object[][] dataProviderWithOffset() {
        // @formatter:off
        return new Object[][] {
                { "yyyy-MM-dd", "2014-01-01", "+0d", "2014-01-02" },
                { "yyyy-MM-dd", "2014-01-01", "+1d", "2014-01-02" },
                { "yyyy-MM-dd", "2014-01-01", "+2d", "2014-01-03" },
                { "yyyy-MM-dd", "2014-01-01", "+3d", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-01", "+4d", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-01", "+5d", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-01", "+6d", "2014-01-07" },
                { "yyyy-MM-dd", "2014-01-01", "+7d", "2014-01-08" },
                { "yyyy-MM-dd", "2014-01-01", "+1M", "2014-02-03" },
                { "yyyy-MM-dd", "2014-01-03", "+1M", "2014-02-03" },
                { "yyyy-MM-dd", "2014-07-08", "+1M", "2014-08-11" },
        };
        // @formatter:on
    }

    @Test(dataProvider = "dataProviderWithOffset")
    public void testExecuteWithOffset(String pattern, String date, String offset, String expected) throws Exception {
        Assert.assertEquals(testling.execute(Arrays.asList(pattern, date, offset), null), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteInvalidNumberOfParameters() throws Exception {
        List<String> parameterList = new ArrayList<>();
        parameterList.add("yyyy-MM-dd");
        testling.execute(parameterList, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteWrongParameterDateFormat() throws Exception {
        List<String> parameterList = new ArrayList<>();
        parameterList.add("yyyy-MM-dd");
        parameterList.add("2014.01.01");
        testling.execute(parameterList, null);
    }

}
