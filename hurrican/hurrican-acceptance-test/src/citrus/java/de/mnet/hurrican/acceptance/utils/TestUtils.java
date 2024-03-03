/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.12.2014
 */
package de.mnet.hurrican.acceptance.utils;

import com.consol.citrus.context.TestContext;
import org.testng.Assert;

import de.mnet.hurrican.ffm.citrus.VariableNames;

public class TestUtils {

    private TestUtils() {
    }

    public static String getOltBezeichnung(TestContext context) {
        return readMandatoryVariableFromTestContext(context, VariableNames.OLT_BEZEICHNUNG);
    }

    /**
     * Reads the variable value from the test context.
     *
     * @param context      the test context holding the test variables.
     * @param variableName the name of the variable.
     * @return the non-null value of the variable
     */
    public static String readMandatoryVariableFromTestContext(TestContext context, String variableName) {
        final String variableValue = (String) context.getVariableObject(variableName);
        Assert.assertNotNull(variableValue, String.format("No non-null variable found in the test context matching the variable name '%s'", variableName));
        return variableValue;
    }

}
