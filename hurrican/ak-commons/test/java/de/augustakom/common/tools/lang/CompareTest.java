/**
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.03.2010 16:29:58
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 *
 */
public class CompareTest extends BaseTest {
    @Test(groups = "unit")
    public void testCompare() {
        Integer integer = Integer.valueOf(3);
        Integer biggerInteger = Integer.valueOf(13);
        assertTrue(compare(integer, biggerInteger, Integer.class) < 0);
    }

    public int compare(Object o1, Object o2, Class<?> type) {
        if (Comparable.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            Comparable c1 = (Comparable) o1;
            @SuppressWarnings("unchecked")
            Comparable c2 = (Comparable) o2;
            @SuppressWarnings("unchecked")
            final int result = c1.compareTo(c2);
            return result;
        }
        else {
            return o1.toString().compareTo(o2.toString());
        }

    }
}
