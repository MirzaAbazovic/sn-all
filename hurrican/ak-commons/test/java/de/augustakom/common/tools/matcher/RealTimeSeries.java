/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.01.2012 11:47:03
 */
package de.augustakom.common.tools.matcher;

import java.time.*;
import java.util.*;
import com.google.common.base.Function;

/**
 * Wertet eine Function ueber eine gewisse Zeit aus. Configuration via {@code sampledEvery} und {@code within}. Der
 * Iterator wartet bei jedem next das {@code sampleInterval} ab bis maximal {@code maxSampleTime}. Sinnvoll in
 * Verbindung mit dem {@link RetryMatcher}. <p/> <p> Beispiel: <code> overTime(checkFunction).sampledEvery(5000).within(Duration.standardMinutes(3))
 * </code> </p>
 */
public class RealTimeSeries<T> implements Iterable<T> {

    public static <S> RealTimeSeries<S> overTime(Function<?, S> sampleFunction) {
        return new RealTimeSeries<S>(sampleFunction);
    }

    private Duration sampleInterval = Duration.ofMillis(100);
    private Duration maxSampleTime = Duration.ofSeconds(10);
    private Function<?, T> sampleFunction;

    public RealTimeSeries(Function<?, T> sampleFunction) {
        this.sampleFunction = sampleFunction;
    }

    /**
     * Setzt das Sample Interval
     *
     * @param sampleInterval das Interval, das zwischen zwei Aufrufen der sampleFunction gewartet wird
     */
    public RealTimeSeries<T> sampledEvery(Duration sampleInterval) {
        this.sampleInterval = sampleInterval;
        return this;
    }

    /**
     * Setzt das Sample Interval
     *
     * @param sampleIntervalMillis das Interval, das zwischen zwei Aufrufen der sampleFunction gewartet wird. Der Default ist
     *                       100 Millisekunden.
     */
    public RealTimeSeries<T> sampledEvery(long sampleIntervalMillis) {
        this.sampleInterval = Duration.ofMillis(sampleIntervalMillis);
        return this;
    }

    /**
     * Setzt die maximale Sample Zeit, nach der nicht mehr weiter berechnet wird. Der Iterator liefert kein naechstes
     * Element mehr zurueck, wenn mehr als diese Zeit seit der Abfrage des ersten Elements aus dem Iterator vergangen
     * ist. Der Default ist 10 Sekunden.
     */
    public RealTimeSeries<T> within(Duration max) {
        this.maxSampleTime = max;
        return this;
    }

    /**
     * Setzt die maximale Sample Zeit, nach der nicht mehr weiter berechnet wird. Der Iterator liefert kein naechstes
     * Element mehr zurueck, wenn mehr als diese Zeit seit der Abfrage des ersten Elements aus dem Iterator vergangen
     * ist.
     */
    public RealTimeSeries<T> within(long maxMillis) {
        this.maxSampleTime = Duration.ofMillis(maxMillis);
        return this;
    }

    public Duration getSampleInterval() {
        return sampleInterval;
    }

    public Duration getMaxSampleTime() {
        return maxSampleTime;
    }

    @Override
    public Iterator<T> iterator() {
        return new SampleIterator();
    }

    private class SampleIterator implements Iterator<T> {
        boolean firstSample = true;
        private long expectedEnd;
        private T lastSample;

        @Override
        public boolean hasNext() {
            if (firstSample) {
                return true;
            }
            long endOfNextSample = System.currentTimeMillis() + sampleInterval.toMillis();
            return endOfNextSample <= expectedEnd;
        }

        @Override
        public T next() {
            if (firstSample) {
                expectedEnd = System.currentTimeMillis() + maxSampleTime.toMillis();
                firstSample = false;
            }
            else {
                try {
                    Thread.sleep(sampleInterval.toMillis());
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return sample();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public T sample() {
            lastSample = sampleFunction.apply(null);
            return lastSample;
        }

    }

}


