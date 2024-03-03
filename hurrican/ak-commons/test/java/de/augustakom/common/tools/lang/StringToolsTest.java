/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 */
package de.augustakom.common.tools.lang;

import static org.testng.Assert.*;

import java.util.*;
import java.util.regex.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * Test-Klasse fuer de.augustakom.common.tools.lang.StringTools
 */
@Test(groups = { "unit" })
public class StringToolsTest extends BaseTest {
    private static final Logger LOGGER = Logger.getLogger(StringToolsTest.class);
    private static final String TO_FORMAT_ONE = "First param: {0}, Second param: {1}";
    private static final String TO_FORMAT_TWO = "Third param: {2}, First param: {0}, Second param: {1}";


    public void testJoin() {
        String result = StringTools.join(new String[] { "eins", "zwei", "drei" }, " - ", true);
        assertEquals(result, "eins - zwei - drei");
    }

    public void testJoinWithNull() {
        String result = StringTools.join(new String[] { "eins", null, "drei" }, " - ", true);
        assertEquals(result, "eins - drei");
    }


    /**
     * Test fuer Methode StringTools.testFormatString
     */
    public void testFormatString() {
        String error = "Methode StringTools.formatString liefert falsches Ergebnis";

        Object[] paramsOne = new Object[] { "<param1>", "<param2>" };
        String s1 = StringTools.formatString(TO_FORMAT_ONE, paramsOne, null);
        assertEquals(s1, "First param: <param1>, Second param: <param2>", error);

        Object[] paramsTwo = new Object[] { "<param1>", "<param2>", "<param3>" };
        String s2 = StringTools.formatString(TO_FORMAT_TWO, paramsTwo, null);
        assertEquals(s2, "Third param: <param3>, First param: <param1>, Second param: <param2>", error);

        String s3 = StringTools.formatString(TO_FORMAT_TWO, null, null);
        assertEquals(s3, TO_FORMAT_TWO, error);

        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.set(2009, 9, 19, 12, 25, 10);
        String s4 = StringTools.formatString("Zeit: {0,time,long}", new Object[] { cal.getTime() }, Locale.GERMAN);
        assertEquals(s4, "Zeit: 12:25:10 MESZ", error);
    }

    /**
     * Test fuer die Methode StringTools#startsWith(String, String)
     */
    public void testStartsWith() {
        String toCheck = "TestString";
        String prefix = "T";

        boolean check = StringTools.startsWith(toCheck, null);
        assertFalse(check, "Es wurde eine Uebereinstimmung gefunden - sollte jedoch nicht sein!");

        check = StringTools.startsWith(toCheck, prefix);
        assertTrue(check, "Es wurde keine Uebereinstimmung gefunden - sollte jedoch der Fall sein!");
    }

    public void testTokenizer() {
        String test = "nur && ein && test";
        StringTokenizer st = new StringTokenizer(test, "&&");
        while (st.hasMoreTokens()) {
            LOGGER.debug(".. token:" + st.nextToken("&&") + "!");
        }
    }

    public void tesRemoveStart() {
        assertNull(StringTools.removeStartToEmpty(null, 'a'));
        assertEquals(StringTools.removeStartToEmpty("000111a", '0'), "111a");
        assertEquals(StringTools.removeStartToEmpty("", '0'), "");
        assertEquals(StringTools.removeStartToEmpty("abcaba", 'a'), "bcaba");
    }

    public void testMatch() {
        boolean match = "96U/12345/12345/2".matches("9\\d[A-Z]/\\d{5}/\\d{5}/\\d+");
        LOGGER.debug("Match: " + match);

        match = "/home/glink/TELCOBILL.EXPORT/PRODAK/200601/100000025/100000025-200601000014-100000025-4-0-Rechnung.pdf".matches(
                "[a-zA-Z0-9/.]+[0-9]+-[0-9]+-[0-9]+-[0-9]+-[0-9]+-Rechnung[- /.]pdf");
        LOGGER.debug(".. match: " + match);

        // oder ueber Pattern und Matcher (vorkompiliert fuer viele Vergleiche!)
        Pattern pattern = Pattern.compile("9\\d[A-Z]/\\d{5}/\\d{5}/\\d+");
        Matcher matcher = pattern.matcher("96U/12345/12345/2");
        LOGGER.debug("Match ueber Matcher: " + matcher.matches());
        matcher.reset("96Z/12345/12345/12345");
        LOGGER.debug("Match 2 ueber Matcher: " + matcher.matches());

        LOGGER.debug("Matching: " + "01:25:03.0".matches("\\d\\d:\\d\\d:\\d\\d\\.\\d"));

        LOGGER.debug("SUB: " + StringUtils.substring("M000133121_200612048826_EVN_A_gehend.txt", 11, 23));
        LOGGER.debug("SUB: " + StringUtils.substring("A100000432_200612000100.pdf", 11, 23));
        LOGGER.debug("SUB: " + StringUtils.substring("A100000432ffjdsklfjdskl", 1, 10) + "!");
    }

    public void testReplaceChars() {
        String replace = "|#|";
        Map<String, String> replacingChars = new HashMap<String, String>();
        replacingChars.put("\n", replace);
        replacingChars.put("\r", replace);
        replacingChars.put("\t", replace);
        replacingChars.put(";", replace);

        String test = "just a simple test: semikolon ; line feed\n" +
                "and a carriage return \r char";
        String x = StringTools.replaceChars(test, replacingChars);
        LOGGER.debug("x: " + x);
    }

    public void testReplaceGermanUmlaute() {
        assertEquals(StringTools.replaceGermanUmlaute("ä"), "ae");
        assertEquals(StringTools.replaceGermanUmlaute("ö"), "oe");
        assertEquals(StringTools.replaceGermanUmlaute("ü"), "ue");
        assertEquals(StringTools.replaceGermanUmlaute("Ä"), "Ae");
        assertEquals(StringTools.replaceGermanUmlaute("Ö"), "Oe");
        assertEquals(StringTools.replaceGermanUmlaute("Ü"), "Ue");
        assertEquals(StringTools.replaceGermanUmlaute("ß"), "ss");
    }

    public void testFilterSpecialChars() {
        String withSpecialChars = "abc/()?*+'!§$%&`°_.,abc";
        String filtered = StringTools.filterSpecialChars(withSpecialChars);
        assertEquals(filtered, "abc_abc");
    }

    public void testCompare() {
        assertEquals(StringTools.compare("aaa", "bbb", false), -1);
        assertEquals(StringTools.compare("aaa", "aaa", false), 0);
        assertEquals(StringTools.compare("bbb", "aaa", false), 1);
        assertEquals(StringTools.compare(null, null, false), 0);
        assertEquals(StringTools.compare(null, "bbb", false), -1);
        assertEquals(StringTools.compare(null, "bbb", true), 1);
        assertEquals(StringTools.compare("aaa", null, false), 1);
        assertEquals(StringTools.compare("aaa", null, true), -1);
    }

    public void testFillToSize() {
        assertEquals(StringTools.fillToSize("", 3, 'a', true), "aaa");
        assertEquals(StringTools.fillToSize("  ", 3, 'a', true), "aaa");
        assertEquals(StringTools.fillToSize(null, 3, 'a', true), "aaa");
        assertEquals(StringTools.fillToSize("ab", 2, 'c', true), "ab");
        assertEquals(StringTools.fillToSize("ab", 5, 'c', true), "cccab");
        assertEquals(StringTools.fillToSize("ab", 2, 'c', false), "ab");
        assertEquals(StringTools.fillToSize("ab", 5, 'c', false), "abccc");
        assertEquals(StringTools.fillToSize("abcdef", 5, 'x', false), "abcdef");
    }
}
