package de.mnet.hurrican.simulator.helper;

import junit.framework.TestCase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class LocaliseXPathTest extends TestCase {


    @DataProvider
    public Object[][] xpathExpressions() {
        // @formatter:off
        return new Object[][] {
                { "/a/b", "/*[local-name() = 'a']/*[local-name() = 'b']"},
                { "//a/b", "//*[local-name() = 'a']/*[local-name() = 'b']"},
        };
        // @formatter:on
    }

    @Test(dataProvider = "xpathExpressions")
    public void testLocalise(String in, String expOut) throws Exception {
        Assert.assertEquals(LocaliseXPath.localise(in), expOut);
    }
}