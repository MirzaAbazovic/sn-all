/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 13:37:49
 */
package de.augustakom.common.tools.matcher;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.SLOW)
public class RealTimeSeriesTest extends BaseTest {

    private Function<Object, Integer> returnOne = Functions.constant(1);

    public void realTimeSeriesShouldSample() {
        RealTimeSeries<Integer> series = RealTimeSeries.overTime(returnOne).sampledEvery(10).within(57);
        int sum = 0;
        for (Integer integer : series) {
            sum += integer;
        }

        assertThat(sum, greaterThanOrEqualTo(3));
    }
}
