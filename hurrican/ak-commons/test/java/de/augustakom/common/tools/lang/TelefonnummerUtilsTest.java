/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.10.2011 18:24:18
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class TelefonnummerUtilsTest {

    @DataProvider
    public Object[][] telefonnummerProvider() {
        // @formatter:off
        return new Object[][] {
                { null, "" },
                { "089/45200-5951", "089 452005951" },
                { "0 89 / 452 00 - 59 51", "089 452005951" },
                { "0821/4500-703221", "0821 4500703221" },
                { "+49 821 45003284", "+49 821 45003284" },
                { "+49 821 4500 3284", "+49 821 45003284" },
                { "+49 821 4500-3284", "+49 821 45003284" },
                { "+49 821/45003284", "+49 821 45003284" },
                { "+49 821/45003284", "+49 821 45003284" },
                { "+49(821)4500 3284", "+49 821 45003284" },
                { "+49 821 / 4500-3284", "+49 821 45003284" },
                { "+49 8 21 / 45 00 - 32 84", "+49 821 45003284" },
                { "+49(8 21)4500 3284", "+49 821 45003284" },
                { "+49 (8 21) 4500 3284", "+49 821 45003284" },
                { "+49 (821) 4500 3284", "+49 821 45003284" },
                { "+49 (821) 4500-3284", "+49 821 45003284" },
                { "+49[821]4500-3284", "+49 821 45003284" },
                { "+49[8 21]4500 3284", "+49 821 45003284" },
                { "+49 [8 21] 4500 3284", "+49 821 45003284" },
                { "+49 [821] 4500 3284", "+49 821 45003284" },
                { "+49[821]4500 3284", "+49 821 45003284" },
                { "(01 73) 1 56 48 52", "0173 1564852" },
                { "3997", "3997" },
                { "+49 89/452003461", "+49 89 452003461" },
                { "+49 89 452003461", "+49 89 452003461" },
                { "01731564852", "01731564852" },
                { "0821 2134234", "0821 2134234" },
                { "0228/1815678", "0228 1815678" },
                { "+49 12345 12345678901", "+49 12345 12345678901" },
                { "+49 89/452003461", "+49 89 452003461" },
                { "0173/1564852", "0173 1564852" },
                { "0173-1564852", "0173 1564852" },
                { "(0173)1564852", "0173 1564852" },
                { "089-15648-52", "089 1564852" },
                { "0173-1564852", "0173 1564852" },
                { "0173-1564-852", "0173 1564852" },
                { "089/45200 0", "089 452000" },
                { "0821/4500-0", "0821 45000" },
                { "+49 (89) 45200 0", "+49 89 452000" },
                { "   +49 (89) 45200 0   ", "+49 89 452000" },
                { "  +49   (  8  9   )   4  5   2   0   0   0  ", "+49 89 452000" },
                { "  +49   (  8  9   )   4  5   2   0   0   0  ", "+49 89 452000" },
                { "+1 234 567890", "+1 234 567890" },
                { "+  1 234 567890", "+1 234 567890" },
                { "   +1 (89) 45200 0   ", "+1 89 452000" },
                { "   +  1 8 9 / 45200 0   ", "+1 89 452000" },
                { "   +  1 8 9/45200 0   ", "+1 89 452000" },
                { "+353 123 4567890", "+353 123 4567890" },
                { "   +353 (89) 45200 0   ", "+353 89 452000" },
                { "  +353   (  8  9   )   4  5   2   0   0   0  ", "+353 89 452000" },
                { "  +  353 (  8  9   )   4  5   2   0   0   0  ", "+353 89 452000" },
            };
        //@formatter:on
    }

    @Test(dataProvider = "telefonnummerProvider")
    public void testConvertTelefonnummer(String input, String expected) {
        assertEquals(TelefonnummerUtils.convertTelefonnummer(input), expected);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Die Telefonnummer .* konnte nicht in ein standardisiertes Format konvertiert werden.*")
    public void testConvertTelefonnummerFails() {
        TelefonnummerUtils.convertTelefonnummer("11111111111111111");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Die Telefonnummer .* konnte nicht in ein standardisiertes Format konvertiert werden.*")
    public void testConvertTelefonnummerFails2() {
        TelefonnummerUtils.convertTelefonnummer("5768686886868868767678686786767");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Die Telefonnummer .* konnte nicht in ein standardisiertes Format konvertiert werden.*")
    public void testConvertTelefonnummerFails3() {
        // nicht eindeutig zu parsen
        TelefonnummerUtils.convertTelefonnummer("0821-4500 3267");
    }

    @Test(dataProvider = "telefonnummerProvider")
    public void testMnetValidity(String input, String expected) {
        if (input != null) {
            assertTrue(TelefonnummerUtils.isMatchingMnetPhoneFormat(expected));
        }
    }
}
