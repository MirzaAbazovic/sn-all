/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 07:51:53
 */
package de.augustakom.common.tools.lang;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;


/**
 * TestNG Test fuer die Klasse <code>de.augustakom.common.tools.lang.WildcardTools</code>
 *
 *
 */
@Test(groups = { "unit" })
public class WildcardToolsTest extends BaseTest {

    /**
     * Test-Method
     */
    public void testReplaceWildcards() {
        String toTest = "*String*With*Wildcards*";
        String expected = "%String%With%Wildcards%";

        String replaced = WildcardTools.replaceWildcards(toTest);
        Assert.assertEquals(replaced, expected, "Die Wildcards wurden nicht richtig ersetzt!");
    }

}


