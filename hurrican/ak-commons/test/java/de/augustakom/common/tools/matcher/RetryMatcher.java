/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 13:12:29
 */
package de.augustakom.common.tools.matcher;

import static org.hamcrest.CoreMatchers.*;

import java.time.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher, der checkt ob ein Wert der RealTimeSeries auf den uebergebenen Matcher matcht. Zu benutzen in Verbindung mit
 * {@link RealTimeSeries}. <p/> <p> Beispiele: <ul> <li><code> assertThat(overTime(checkFunction), eventuallyTrue());
 * </code></li> <li><code> assertThat(overTime(checkFunction), eventually(hasSize(4)); </code></li> </ul> </p>
 */
public class RetryMatcher<T> extends TypeSafeMatcher<RealTimeSeries<T>> {

    public static <T> RetryMatcher<T> eventually(Matcher<T> delegate) {
        return new RetryMatcher<T>(delegate);
    }

    public static RetryMatcher<Boolean> eventuallyTrue() {
        return eventually(equalTo(true));
    }

    private Matcher<T> delegate;
    private T lastMatched;

    public RetryMatcher(Matcher<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void describeMismatchSafely(RealTimeSeries<T> item, Description mismatchDescription) {
        mismatchDescription.appendText("after sampling every ")
                .appendText(formatDuration(item.getSampleInterval()))
                .appendText(" during ")
                .appendText(formatDuration(item.getMaxSampleTime()))
                .appendText(" ");
        delegate.describeMismatch(lastMatched, mismatchDescription);
    }

    private String formatDuration(Duration duration) {
        return duration.toString();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("eventually(").appendDescriptionOf(delegate).appendText(")");

    }

    @Override
    protected boolean matchesSafely(RealTimeSeries<T> items) {
        for (T item : items) {
            if (delegate.matches(item)) {
                return true;
            }
            lastMatched = item;
        }
        return false;
    }

}


