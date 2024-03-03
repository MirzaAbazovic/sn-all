/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.06.2010 15:12:51
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = "unit")
public class RecursivePropertyResolverTest extends BaseTest {

    public void testResolve() {
        Properties props = new Properties();
        props.setProperty("sample", "test");
        props.setProperty("replace", "${sample}");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("replace"), "test");
    }

    public void testResolveRecursive() {
        Properties props = new Properties();
        props.setProperty("sample", "test");
        props.setProperty("replace", "${sample}");
        props.setProperty("recursive", "${replace}");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("recursive"), "test");
    }

    public void testResolveInlineRecursive() {
        Properties props = new Properties();
        props.setProperty("sample", "st");
        props.setProperty("recursive", "--${te${sample}.bar}--");
        props.setProperty("test.bar", "unique");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("recursive"), "--unique--");
    }

    public void testResolveInlineRecursiveDollarProp() {
        Properties props = new Properties();
        props.setProperty("$sample", "test");
        props.setProperty("recursive", "${${$sample}.bar}");
        props.setProperty("test.bar", "unique");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("recursive"), "unique");
    }

    public void testResolveRecursiveInlineRecursiveDollarProp() {
        Properties props = new Properties();
        props.setProperty("sample", "te");
        props.setProperty("$rec1", "${sample}st");
        props.setProperty("recursive", "--${${$rec1}.bar}--");
        props.setProperty("test.bar", "unique");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("recursive"), "--unique--");
    }

    public void testResolveRecursivePropertyDefinition() {
        Properties props = new Properties();
        props.setProperty("recursive", "${recursive}");
        int storedSize = props.size();

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        resolver.resolve(props);

        assertTrue(props.size() == storedSize);
        assertEquals(props.getProperty("recursive"), "${recursive}");
    }

    public void testCircularPropertyDefinitions() {
        Properties props = new Properties();
        props.setProperty("cycle", "${${cycle}}");

        RecursivePropertyResolver resolver = new RecursivePropertyResolver();
        // throws an java.lang.OutOfMemoryError: Java heap space
        //resolver.resolve(props);
    }
}
