/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2015
 */
package de.augustakom.common.tools.lang;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class OptionalTools {
    private OptionalTools() {
    }

    public static <T> List<T> listOf(Optional<T> o) {
        return o.map(Collections::singletonList).orElse(Collections.emptyList());
    }

    /**
     * Returns o1 if a value is present, otherwise, returns the value produced by o2.
     */
    public static <T> Optional<T> orElse(Optional<T> o1, Supplier<Optional<T>> o2) {
        return o1.map(Optional::of).orElseGet(o2);
    }

    public static <T> Stream<T> streamOf(Optional<T> o) {
        return listOf(o).stream();
    }
}
