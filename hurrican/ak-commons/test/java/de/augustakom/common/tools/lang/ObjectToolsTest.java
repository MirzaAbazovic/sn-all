/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2005 10:30:19
 */
package de.augustakom.common.tools.lang;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * TestNG Test-Case fuer <code>ObjectTools</code>.
 *
 *
 */
@Test(groups = { "unit" })
public class ObjectToolsTest extends BaseTest {

    /**
     * Test method for 'ObjectTools.isInstanceOf(java.lang.Object, java.lang.Class)
     */
    public void testIsInstanceOf() {
        Object obj = Boolean.TRUE;
        Class<Boolean> clazz = Boolean.class;

        Assert.assertTrue(ObjectTools.isInstanceOf(obj, clazz), "Boolean-Objekt ist nicht instanceof Boolean!");
        Assert.assertFalse(ObjectTools.isInstanceOf(null, clazz), "null wurde als instanceof Boolean akzeptiert!");
    }

}


