/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.tools.lang;

import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * Test-Klasse fuer de.augustakom.common.tools.lang.ResourceReader
 *
 *
 */
@Test(groups = { "unit" })
public class ResourceReaderTest extends BaseTest {

    private static final String resource = "de.augustakom.common.tools.lang.ResourceReaderTest";


    /**
     * Test fuer die Methode ResourceReader.getResourceBundle
     */
    public void testGetResourceBundle() {
        ResourceReader rr = new ResourceReader(resource);
        Assert.assertNotNull(rr.getResourceBundle(), "ResourceBundle with file " + resource + " is null!");

        rr = new ResourceReader("xxx");
        Assert.assertNull(rr.getResourceBundle(), "ResourceBundle with file 'xxx' found - but should not!");
    }


    /**
     * Test fuer die Methode ResourceReader.getValue
     */
    public void testGetValue() {
        Locale.setDefault(Locale.GERMAN);
        getValue(null);
        getValue(Locale.GERMAN);
        getValue(Locale.ENGLISH);
        getValue(Locale.ITALIAN);
    }

    /* Hilfsmethode, fuer testGetValue */
    private void getValue(Locale locale) {
        ResourceReader rr = new ResourceReader(resource, locale);

        String value = rr.getValue("my.key");
        String expected = "";

        if (locale != null && StringUtils.equals(locale.getLanguage(), Locale.ENGLISH.getLanguage())) {
            expected = "my value";
        }
        else {
            expected = "Mein Wert";
        }
        Assert.assertEquals(value, expected);
    }

    /**
     * Test fuer die Methode ResourceReader.getValue(key, params)
     */
    public void testGetValueParam() {
        Locale.setDefault(Locale.GERMAN);
        getValueParam(null);
        getValueParam(Locale.GERMAN);
        getValueParam(Locale.ENGLISH);
        getValueParam(Locale.ITALIAN);
    }

    /* Hilfsmethode, fuer testGetValueParam */
    private void getValueParam(Locale locale) {
        ResourceReader rr = new ResourceReader(resource, locale);

        String value = rr.getValue("param.key", new Object[] { "<param1>", "<param2>" });
        String expected = "";

        if (locale != null && StringUtils.equals(locale.getLanguage(), Locale.ENGLISH.getLanguage())) {
            expected = "First param: <param1>, Second param: <param2>";
        }
        else {
            expected = "Zweiter Parameter: <param2>, Erster Parameter: <param1>";
        }
        Assert.assertEquals(value, expected);
    }

}
