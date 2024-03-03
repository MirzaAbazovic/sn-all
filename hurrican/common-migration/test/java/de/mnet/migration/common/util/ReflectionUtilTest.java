/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.07.2010 13:39:05
 */
package de.mnet.migration.common.util;

import static org.testng.Assert.*;

import java.lang.reflect.*;
import java.util.*;
import org.testng.annotations.Test;

import de.mnet.migration.base.MigrationBaseTest;


/**
 *
 */
@Test(groups = "unit")
@SuppressWarnings("unused")
public class ReflectionUtilTest extends MigrationBaseTest {
    private class A {
        private Integer i;
        public String s;
    }

    private class B extends A {
        public static final String bling = "A";
        private Long l;
        protected Character c;
    }

    private class C extends B {
        Short sh;
    }

    public void testGetAllFieldsBC() {
        Set<String> names = new HashSet<String>();
        names.add("sh");
        names.add("c");
        names.add("l");
        names.add("bling");
        List<Field> allFields = ReflectionUtil.getAllFields(B.class, C.class);
        for (Field field : allFields) {
            assertTrue(names.contains(field.getName()));
            names.remove(field.getName());
        }
    }


    public void testGetAllFieldsAB() {
        Set<String> names = new HashSet<String>();
        names.add("i");
        names.add("s");
        names.add("c");
        names.add("l");
        names.add("bling");
        List<Field> allFields = ReflectionUtil.getAllFields(A.class, B.class);
        for (Field field : allFields) {
            assertTrue(names.contains(field.getName()));
            names.remove(field.getName());
        }
    }


    public void testCreatePossibleNames_ATest() {
        Set<String> possibleNames = new HashSet<String>();
        possibleNames.add("ATest");
        possibleNames.add("A Test");
        possibleNames.add("A_Test");
        possibleNames.add("aTest");
        possibleNames.add("a Test");
        possibleNames.add("a_Test");
        assertEquals(ReflectionUtil.createPossibleNames("aTest"), possibleNames);
    }


    public void testCreatePossibleNames_no0815Test() {
        Set<String> possibleNames = new HashSet<String>();
        possibleNames.add("no0815Test");
        possibleNames.add("No0815Test");
        possibleNames.add("no0815 Test");
        possibleNames.add("No0815 Test");
        possibleNames.add("no0815_Test");
        possibleNames.add("No0815_Test");
        possibleNames.add("no 0815Test");
        possibleNames.add("No 0815Test");
        possibleNames.add("no 0815 Test");
        possibleNames.add("No 0815 Test");
        possibleNames.add("no 0815_Test");
        possibleNames.add("No 0815_Test");
        possibleNames.add("no_0815Test");
        possibleNames.add("No_0815Test");
        possibleNames.add("no_0815 Test");
        possibleNames.add("No_0815 Test");
        possibleNames.add("no_0815_Test");
        possibleNames.add("No_0815_Test");
        assertEquals(ReflectionUtil.createPossibleNames("no0815Test"), possibleNames);
    }


    public void testCreatePossibleNames_superXYZThing() {
        Set<String> possibleNames = new HashSet<String>();
        possibleNames.add("superXYZThing");
        possibleNames.add("SuperXYZThing");
        possibleNames.add("superXYZ Thing");
        possibleNames.add("SuperXYZ Thing");
        possibleNames.add("superXYZ_Thing");
        possibleNames.add("SuperXYZ_Thing");
        possibleNames.add("super XYZThing");
        possibleNames.add("Super XYZThing");
        possibleNames.add("super XYZ Thing");
        possibleNames.add("Super XYZ Thing");
        possibleNames.add("super XYZ_Thing");
        possibleNames.add("Super XYZ_Thing");
        possibleNames.add("super_XYZThing");
        possibleNames.add("Super_XYZThing");
        possibleNames.add("super_XYZ Thing");
        possibleNames.add("Super_XYZ Thing");
        possibleNames.add("super_XYZ_Thing");
        possibleNames.add("Super_XYZ_Thing");
        assertEquals(ReflectionUtil.createPossibleNames("superXYZThing"), possibleNames);
    }
}
