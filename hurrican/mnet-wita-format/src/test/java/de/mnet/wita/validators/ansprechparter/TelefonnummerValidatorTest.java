/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.10.2011 13:26:10
 */
package de.mnet.wita.validators.ansprechparter;

import static de.mnet.wita.TestGroups.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.mnet.wita.message.auftrag.Ansprechpartner;
import de.mnet.wita.validators.AbstractValidationTest;

@Test(groups = UNIT)
public class TelefonnummerValidatorTest extends AbstractValidationTest<Ansprechpartner> {

    @DataProvider
    public Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "011111111111111111111", false },
                { "01 1111111111111111111", false },
                { "+01123", false },
                { "+011 23", false },
                { "+011111111", false },
                { "+011 111111", false },
                { "+011 111 111", false },
                { "(089]15648-52", false },
                { "0173(1564852)", false },
                { "089/45200-5951", false },
                { "0 89 / 452 00 - 59 51", false },
                { "0821/4500-703221", false },
                { "0228/1815678", false },
                { "+49 89/452003461", false },
                { "+49 821 4500 3284", false },
                { "+49 821 4500-3284", false },
                { "+49821/45003284", false },
                { "+49 821/45003284", false },
                { "+49(821)4500 3284", false },
                { "+49 821 / 4500-3284", false },
                { "+4989/452003461", false },
                { "+4989 452003461", false },
                { "+49 8 21 / 45 00 - 32 84", false },
                { "+49(8 21)4500 3284", false },
                { "+49 (821) 4500 3284", false },
                { "0228/1815678", false },
                { "0173/1564852", false },
                { "(0173)1564852", false },
                { "(01 73) 1 56 48 52", false },
                { "+49 89/452003461", false },
                { "089-15648-52", false },
                { "0173-1564852", false },
                { "0173-1564-852", false },
                { "+1 234 567890" , false },
                { "+353 89 452000", false },

                { null, true },
                { "1", true },
                { "1234567890", true },
                { "3997", true },
                { "0821 2134234", true },
                { "+49 12345 12345678901", true },
                { "+49 821 45003284", true },
                { "0821 2134234", true },
                { "+49 12345 12345678901", true },
            };
        // @formatter:on
    }

    @Test(dataProvider = "testCases")
    public void testValidation(String rufnummer, boolean valid) {
        violations = validator.validate(createAnsprechpartner(rufnummer));
        assertEquals(violations.isEmpty(), valid);
    }

    private Ansprechpartner createAnsprechpartner(String telefonnummer) {
        Ansprechpartner ansprechpartner = new Ansprechpartner();
        ansprechpartner.setTelefonnummer(telefonnummer);
        return ansprechpartner;
    }

}


