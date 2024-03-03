package de.mnet.hurrican.atlas.simulator.wita;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class WitaLineOrderXpathExpressionsTest {

    @Test
    public void testXpath() throws Exception {
        Assert.assertTrue(WitaLineOrderXpathExpressions.CUSTOMER_ID.getXpath(WitaLineOrderServiceVersion.V1).startsWith("//*[local-name() = 'order']"));
    }
}
