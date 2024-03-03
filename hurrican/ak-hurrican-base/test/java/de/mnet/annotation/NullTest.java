/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2012 10:34:18
 */
package de.mnet.annotation;

import javax.annotation.*;

public class NullTest {
    @Nonnull
    String stringNonnull = "abc";

    @CheckForNull
    String stringNullable = "abc";

    String stringNoAnnotation = "abc";

    void testNonnullFieldDirectAssignment() {
        stringNonnull = null;
    }

    void testCheckForNullFieldIndirectAssignment() {
        String abc = (System.currentTimeMillis() % 2) == 0 ? "abc" : null;
        stringNonnull = abc;
    }

    int testDereferenceCheckForNull() {
        return stringNullable.length();
    }

    @Nonnull
    String testNonnullMethodReturnsNull() {
        return null;
    }

    @Nonnull
    String testNonnullMethodReturnsCheckForNullField() {
        return stringNullable;
    }

    @Nonnull
    String testNonnullMethodReturnsNonnullField() {
        // ok - stringNonnull wird niemals null
        return stringNonnull;
    }

    @Nonnull
    String testNonnullMethodReturnsUnannotatedField() {
        // keine Annotation -> kein Bugreport
        return stringNoAnnotation;
    }

    @CheckForNull
    String testCheckForNullMethod() {
        return null;
    }

    void testCheckForNullMethodCall() {
        String abc = testCheckForNullMethod();
        abc.length();
    }

    void testNonnullMethodParameter(@Nonnull String arg) {
    }

    void testNullMethodCall() {
        testNonnullMethodParameter("aaa");
        testNonnullMethodParameter(null);
        testNonnullMethodParameter(stringNonnull);
        // hier kein Report durch Findbugs - Bug in Findbugs?
        testNonnullMethodParameter(stringNullable);

        // hier kein Bugreport durch Findbugs - gleicher Bug in Findbugs?
        String bbb = null;
        testNonnullMethodParameter(bbb);
    }

    void testCheckForNullMethodParameter(@CheckForNull String arg) {
        arg.length();
    }

}


