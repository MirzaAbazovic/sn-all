/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2009 08:50:02
 */
package de.augustakom.common;

import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import de.augustakom.common.tools.lang.PropertyUtil;

/**
 * Basisklasse von der alle TestNG Tests ableiten. Konfiguriert das Logging-Framework.
 */
@Listeners({ TestNGEventListener.class })
public abstract class BaseTest {

    // Group constants
    public static final String UNIT = "unit";
    public static final String SERVICE = "service";
    /**
     * Akzeptanztests mit Simulator und KFT-Tests
     */
    public static final String ACCEPTANCE = "acceptance";
    /**
     * E2E Tests gegen den Hurrican Server
     */
    public static final String E2E = "e2e";
    /**
     * Integration mit anderen Systemen mit einem komplett gestartetem Hurrican
     */
    public static final String ACCEPTANCE_INTEGRATION = "acceptance-integration";
    /**
     * WBCI KFT-Akzeptanztests
     */
    public static final String KFT = "kft";
    public static final String KFT_V2 = "kft-v2";
    /**
     * Struktur benoetigter Views
     */
    public static final String VIEW = "view";
    /**
     * Slow Service Tests which will be called only in the nightly build.
     * These tests are called in sequence.
     */
    public static final String SLOW = "slow";
    private static final String FAIL_MESSAGE = "%s expected:<%s> but was:<%s>";

    protected static void assertRegExMatch(String string, String regex) {
        assertTrue(string.matches(regex),
                String.format("string '%s' won't match to regex '%s'", string, regex));
    }

    /**
     * @param collection which is asserted to be {@code null} or empty
     * @param message    of the thrown {@link AssertionError} if {@code collection} is neither {@code null} nor not
     *                   empty
     * @throws AssertionError with {@code message} if collection to assert is not empty
     */
    protected static void assertEmpty(Collection<?> collection, String message) {
        assertFalse((collection != null) && (!collection.isEmpty()),
                String.format(FAIL_MESSAGE, message, "empty or null", "not empty"));
    }

    protected static void assertEmpty(Collection<?> collection) {
        assertEmpty(collection, "");
    }

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

    @BeforeSuite(alwaysRun = true)
    public void testSetupLogging() {
        InitializeLog4J.initializeLog4J("log4j-test");
        SLF4JBridgeHandler.install();
    }

    /** Check environment not to be prod - abort test execution if necessary */
    static {
        String configUser = System.getProperty(PropertyUtil.SYSTEM_PROPERTY_TO_OVERRIDE_PROPERTY_MECHANISM);
        if (org.springframework.util.StringUtils.hasText(configUser) && configUser.contains("prod")) {
            throw new RuntimeException("Trying to execute tests in prod environment - abort execution!");
        }
    }

    protected void assertNotEmpty(String toTest) {
        assertTrue(StringUtils.isNotBlank(toTest));
    }

    /**
     * @param <T>   the type of the returned empty array
     * @param clazz the class of which the array is created
     * @return an empty array for type of {@link Class} {@code clazz}
     */
    protected <T> T[] array(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, 0);
        return result;
    }

    /**
     * <b>Warning:</b> an empty array will always be of type {@link Object}. Use {@code this.<T>array()}, where {@code
     * <T>} is the arbitrary type of the array, or see {@link #array(Class)} for creating an empty array of an proper
     * type {@code <T>}.
     *
     * @param <T>     the type of the returned array
     * @param objects the objects which should be contained by the array
     * @return an array containing the supplied {@code objects}
     */
    protected <T> T[] array(T... objects) {
        return objects;
    }

    /**
     * <b>Warning:</b> an empty set will always be of type {@link Object}. Use {@code this.<T>array()}, where {@code
     * <T>} is the arbitrary type of the array, or see {@link #array(Class)} for creating an empty array of an proper
     * type {@code <T>}.
     *
     * @param <T>     the type of the returned set
     * @param objects the objects which should be contained by the set
     * @return an set containing the supplied {@code objects}
     */
    protected <T> Set<T> set(T... objects) {
        Set<T> result = new HashSet<>();
        for (T object : objects) {
            result.add(object);
        }
        return result;
    }
}
