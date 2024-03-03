/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.02.2012 13:07:40
 */
package de.mnet.hurrican.e2e.common;

import static org.testng.Assert.*;

import java.util.*;
import org.springframework.test.context.ContextConfiguration;

import de.augustakom.common.AbstractSpringContextTest;

@ContextConfiguration({ "classpath:de/augustakom/hurrican/service/cc/resources/CCBuilders.xml",
        "classpath:spring-ws-context.xml", "classpath:de/augustakom/common/service/resources/HttpClient.xml" })
public class BaseHurricanE2ETest extends AbstractSpringContextTest {

    private static final String FAIL_MESSAGE = "%s expected:<%s> but was:<%s>";

    /**
     * @param collection which is asserted to be <b>not</b> {@code null} and <b>not</b> empty
     * @param message    of the thrown {@link AssertionError} if {@code collection} is {@code null} or empty
     * @throws AssertionError with {@code message} if collection to assert is not empty
     */
    protected static void assertNotEmpty(Collection<?> collection, String message) {
        assertFalse((collection == null) || (collection.isEmpty()),
                String.format(FAIL_MESSAGE, message, "not empty", "empty or null"));
    }

    /**
     * @param collection which is asserted to be <b>not</b> {@code null} and <b>not</b> empty
     * @throws AssertionError with {@code message} if collection to assert is not empty
     */
    protected static void assertNotEmpty(Collection<?> collection) {
        assertNotEmpty(collection, "");
    }

}


