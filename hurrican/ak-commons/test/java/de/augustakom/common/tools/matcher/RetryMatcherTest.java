/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 13:45:55
 */
package de.augustakom.common.tools.matcher;

import static com.google.common.base.Functions.*;
import static com.google.common.collect.ImmutableList.*;
import static de.augustakom.common.tools.matcher.RealTimeSeries.*;
import static de.augustakom.common.tools.matcher.RegularExpressionFinder.*;
import static de.augustakom.common.tools.matcher.RetryMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.SLOW)
public class RetryMatcherTest {
    private static final Logger LOGGER = Logger.getLogger(RetryMatcherTest.class);

    @Test(timeOut = 300)
    public void retryShouldMatchImmediately() {
        assertThat(overTime(constant(1)),
                eventually(equalTo(1)));
    }

    @Test(timeOut = 300)
    public void retryShouldMatchEventually() {
        assertThat(overTime(FunctionFromIterable.fromIterable(of(1, 2, 3)))
                        .sampledEvery(1),
                eventually(equalTo(3))
        );
    }

    public void retryShouldHaveGoodMessage() {
        try {
            assertThat(overTime(Functions.constant(1)).sampledEvery(10).within(15), eventually(equalTo(3)));
        }
        catch (AssertionError e) {
            LOGGER.error(e);
            assertThat(e.getMessage(), findsPattern(".*Expected: .*<3>"));
            assertThat(e.getMessage(), findsPattern(".*but: .*was <1>"));
            assertThat(e.getMessage(), findsPattern(".*but: .*during PT0.015S"));
            assertThat(e.getMessage(), findsPattern(".*but: .*every PT0.01S"));
            return;
        }
        fail();
    }

    private static class FunctionFromIterable<T> implements Function<Void, T> {
        private final Iterator<T> iterator;

        public FunctionFromIterable(Iterable<T> iterable) {
            this.iterator = iterable.iterator();
        }

        public static <T> FunctionFromIterable<T> fromIterable(Iterable<T> iterable) {
            return new FunctionFromIterable<T>(iterable);
        }

        @Override
        public T apply(Void input) {
            return iterator.next();
        }

    }

}
