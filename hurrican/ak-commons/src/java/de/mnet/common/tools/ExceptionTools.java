/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.07.2015
 */
package de.mnet.common.tools;

import java.util.*;
import java.util.stream.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ExceptionTools {
    private ExceptionTools() {
    }

    /**
     * Returns the list of messages in the exception chain.
     */
    public static List<String> getMessageList(Throwable throwable) {
        return ExceptionUtils.getThrowableList(throwable).stream()
                .map(Throwable::getMessage)
                .collect(Collectors.toList());
    }

    public static String showDistinctMessageList(Throwable throwable) {
        final List<String> distinctMessages = getMessageList(throwable).stream()
                .distinct()
                .collect(Collectors.toList());
        return StringUtils.join(distinctMessages, "\n");
    }
}
