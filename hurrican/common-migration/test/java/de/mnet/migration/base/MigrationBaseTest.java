/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.08.2009 08:50:02
 */
package de.mnet.migration.base;


import static org.testng.Assert.*;

import java.lang.reflect.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.testng.annotations.BeforeSuite;

import de.augustakom.common.InitializeLog4J;


/**
 * Basisklasse von der alle TestNG Tests ableiten. Konfiguriert das Logging-Framework.
 */
public abstract class MigrationBaseTest {

    protected static final String MIGRATION_USERW = "NAVI_MIG";

    @BeforeSuite(alwaysRun = true)
    public void setupTests() {
        InitializeLog4J.initializeLog4J("log4j-test");
        // To make BeanUtils copy nulls as nulls!
        // Do not remove (except if you have a better idea where to put it)!
        BeanUtilsBean.getInstance().getConvertUtils().register(false, true, 0);
    }


    static public void assertNotEquals(Object actual, Object expected) {
        assertNotEquals(actual, expected, null);
    }

    static public void assertNotEquals(Object actual, Object expected, String message) {
        if ((expected == null) && (actual != null)) {
            return;
        }
        if ((expected != null) && (!expected.equals(actual))) {
            return;
        }
        failEquals(actual, expected, message);
    }

    static private void failEquals(Object actual, Object expected, String message) {
        fail(format(actual, expected, message));
    }

    static String format(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + "not expected:<" + expected + "> but was:<" + actual + ">";
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
}
