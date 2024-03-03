/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.14
 */
package de.mnet.common.tools;

import java.util.*;
import java.util.regex.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class PhoneticCheckTest extends BaseTest {


    @DataProvider(name = "phoneticCheckDP")
    public Object[][] phoneticCheckDP() {
        return new Object[][] {
                { PhoneticCheck.Codec.COLOGNE, "Ralph", "Ralph", true },
                { PhoneticCheck.Codec.COLOGNE, "Ralph", "Ralf", true },
                { PhoneticCheck.Codec.COLOGNE, "Rolf", "Ralf", true },
                { PhoneticCheck.Codec.COLOGNE, "Meier", "Huber", false },
                { PhoneticCheck.Codec.COLOGNE, null, null, false },
                { PhoneticCheck.Codec.COLOGNE, "Meier", null, false },
                { PhoneticCheck.Codec.COLOGNE, null, "Huber", false },
                { PhoneticCheck.Codec.COLOGNE, "Huber", "Huber", true },
                { PhoneticCheck.Codec.COLOGNE, "Schneel", "Schnell", true },
                { PhoneticCheck.Codec.COLOGNE, "Patrick", "Patrik", true },
        };
    }


    @Test(dataProvider = "phoneticCheckDP")
    public void phoneticCheck(PhoneticCheck.Codec codec, String s1, String s2, boolean expectedMatch) {
        PhoneticCheck phoneticCheck = new PhoneticCheck(codec, true);
        boolean result = phoneticCheck.isPhoneticEqual(s1, s2);
        Assert.assertEquals(result, expectedMatch);
    }


    @DataProvider(name = "normalizeNamePatterns")
    public Object[][] normalizeNamePatterns() {
        List<String[]> manualData = Arrays.asList(
                new String[] { "Ursula von der", "Ursula" },
                new String[] { "von der Goenna", "Goenna" },
                new String[] { "Rainer und Gisela", "Rainer Gisela" },
                new String[] { " von Montecristo", "Montecristo" },
                new String[] { "Rainer u. Gisela", "Rainer Gisela" },
                new String[] { "Graf von KeineAhnung", "KeineAhnung" },
                new String[] { "O'Sullivan", "OSullivan" },
                new String[] { "Maier & Sohn", "Maier Sohn" }
        );

        Set<Pattern> patterns = NormalizePattern.NAME_PATTERNS.getPatterns().keySet();
        //manuelle Testdaten

        Object[][] data = new Object[manualData.size() + patterns.size() * 3][2];
        int i = 0;
        for (String[] manData : manualData) {
            data[i] = manData;
            i++;
        }
        //automatisch erzeugte Testdaten
        for (Pattern nameAffixPattern : patterns) {
            String nameAffix = nameAffixPattern.pattern();
            String prefix = "Henry" + nameAffix.replaceAll(" ", "");
            String suffix = "Viel" + nameAffix.replaceAll(" ", "");

            /**
             * sicherstellen, dass keine Filterung UNTER dem Wort greift: z.B. Henryvan van Vielvan => Henryvan Vielvan
             */
            data[i] = new String[] { prefix + nameAffix + suffix, prefix + " " + suffix };
            i++;

            /**
             * sicherstellen, dass die Filterung am ANFANG dem Worts greift: z.B. van Henryvan => Henryvan
             */
            data[i] = new String[] { nameAffix.trim() + " " + prefix, prefix };
            i++;

            /**
             * sicherstellen, dass die Filterung am ENDE dem Worts greift: z.B. Vielvan van => Vielvan
             */
            data[i] = new String[] { suffix + " " + nameAffix.trim(), suffix };
            i++;
        }

        return data;
    }


    @Test(dataProvider = "normalizeNamePatterns")
    public void normalize(String in, String expectedOut) {
        PhoneticCheck phoneticCheck = new PhoneticCheck(PhoneticCheck.Codec.COLOGNE, true, NormalizePattern.NAME_PATTERNS);
        Assert.assertEquals(phoneticCheck.normalize(in), expectedOut.toLowerCase());
    }
}
