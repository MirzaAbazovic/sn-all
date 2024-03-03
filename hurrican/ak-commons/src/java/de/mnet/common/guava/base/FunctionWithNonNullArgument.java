/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.04.2012 16:11:50
 */
package de.mnet.common.guava.base;

import javax.annotation.*;
import com.google.common.base.Function;

public interface FunctionWithNonNullArgument<F, T> extends Function<F, T> {
    @Override
    public T apply(@Nonnull F input);
}


