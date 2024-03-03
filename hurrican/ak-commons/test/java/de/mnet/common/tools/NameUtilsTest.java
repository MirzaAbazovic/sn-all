/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.03.14
 */
package de.mnet.common.tools;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.tools.lang.Pair;

@Test(groups = UNIT)
public class NameUtilsTest {


    @DataProvider
    public Object[][] testStrings() {
        return new Object[][] {
                { null, null, null, null },
                { "string1 with under 30 chars", "string2 with under 30 chars", "string1 with under 30 chars", "string2 with under 30 chars" },
                { "string1 16 chars", "7 chars", "string1 16 chars 7 chars", null },
                { "string1-16 chars", "7-chars", "string1-16 chars 7-chars", null },
                { "string1 16 chars", "string2 with under 30 chars", "string1 16 chars string2 with", "under 30 chars" },
                { "string1 16 chars", "second string with more then 30 chars", "string1 16 chars second string", "with more then 30 chars" },
                { "string1 16 chars", "secondStringWithMoreThen30CharsInOneWord", "string1 16 chars", "secondStringWithMoreThen30Char" },
                { "firstStringWithMoreThen30CharsInOneWord", "secondStringWithMoreThen30CharsInOneWord", "firstStringWithMoreThen30Chars", "InOneWord secondStringWithMore" },
                { "first string with more then 30 chars", "second string with more then 30 chars", "first string with more then 30", "chars second string with more" },
                { "first string with more then 30-chars", "second string with more then 30-chars", "first string with more then 30", "-chars second string with more" },
                { "first string with more then30-chars", "second string with more then 30-chars", "first string with more then30-", "chars second string with more" },
                { "first string with more then 30charsWithNoSep", "second string with more then 30-chars", "first string with more then", "30charsWithNoSep second string" },
                { "first string with more then-30charsWithNoSep", "second-string-with-more-then-30-chars", "first string with more then-", "30charsWithNoSep second-string" },
                { "first string with more then-30charsWithNoSep", "6chars", "first string with more then-", "30charsWithNoSep 6chars" },
                { "first string with more then30charsWithNoSep", "6chars", "first string with more", "then30charsWithNoSep 6chars" },
        };
    }

    @Test(dataProvider = "testStrings")
    public void testNormalizeToLength(String string1, String string2, String expectedString1, String expectedString2) {
        Pair<String, String> result = NameUtils.normalizeToLength(string1, string2, 30);
        assertEquals(result.getFirst(), expectedString1);
        assertEquals(result.getSecond(), expectedString2);
    }
}
