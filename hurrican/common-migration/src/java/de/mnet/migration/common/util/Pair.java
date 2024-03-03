/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2009 12:41:28
 */
package de.mnet.migration.common.util;


/**
 * Immutable holder for a pair of Objects.
 *
 *
 */
public class Pair<T, V> {
    private final T first;
    private final V second;

    public static <T, V> Pair<T, V> create(T first, V second) {
        return new Pair<T, V>(first, second);
    }

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Pair<T, V> other = (Pair<T, V>) obj;
        if (first == null) {
            if (other.first != null) {
                return false;
            }
        }
        else if (!first.equals(other.first)) {
            return false;
        }
        if (second == null) {
            if (other.second != null) {
                return false;
            }
        }
        else if (!second.equals(other.second)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[first=" + first + ",second=" + second + "]";
    }
}
