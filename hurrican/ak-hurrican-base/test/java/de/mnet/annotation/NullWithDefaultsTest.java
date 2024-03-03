/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2012 13:45:48
 */
package de.mnet.annotation;


@ObjectsAreNonnullByDefault
public class NullWithDefaultsTest {
    String nonnullString = null;

    void testMethodParameter(String arg) {
    }

    void testNullMethodCall() {
        testMethodParameter("aaa");
        testMethodParameter(null);
    }
}
