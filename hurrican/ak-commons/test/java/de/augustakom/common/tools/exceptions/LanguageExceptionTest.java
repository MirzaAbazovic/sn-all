/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.tools.exceptions;

import static org.testng.Assert.*;

import java.util.*;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 * Test-Klasse fuer de.augustakom.common.tools.exceptions.LanguageException
 *
 *
 */
@Test(groups = { "unit" })
public class LanguageExceptionTest extends BaseTest {

    /**
     * Class to test for void LanguageException(String, Locale)
     */
    public void testLanguageExceptionStringLocale() {
        LanguageExceptionTestImpl exDE = new LanguageExceptionTestImpl("100", Locale.GERMAN);
        assertEquals(exDE.getMessage(), "Nachricht 100", "unexpected result");

        LanguageExceptionTestImpl exEN = new LanguageExceptionTestImpl("100", Locale.ENGLISH);
        assertEquals(exEN.getMessage(), "Message 100", "unexpected result");
    }

    /**
     * Class to test for void LanguageException(String, Object[], Locale)
     */
    public void testLanguageExceptionStringObjectArrayLocale() {
        Object[] params = new Object[] { "<param1>", "<param2>" };

        LanguageExceptionTestImpl exDE = new LanguageExceptionTestImpl("101", params, Locale.GERMAN);
        assertEquals(exDE.getMessage(), "Nachricht mit Parameter <param1> <param2>", "unexpected result");

        LanguageExceptionTestImpl exEN = new LanguageExceptionTestImpl("101", params, Locale.ENGLISH);
        assertEquals(exEN.getMessage(), "Message with param <param1> <param2>", "unexpected result");
    }
}
