package de.mnet.hurrican.simulator.helper;

import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
@ContextConfiguration({ "classpath:simulator-test-context.xml" })
public class XPathHelperTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private XPathHelper xPathHelper;

    @Test
    public void testEvaluateAsString() throws Exception {
        String externalOrderId = xPathHelper.evaluateAsString(getSampleV1Message(), "/sim1:TestMessage/Text");
        Assert.assertNotNull(externalOrderId);
        Assert.assertEquals(externalOrderId, "SIM1");

        externalOrderId = xPathHelper.evaluateAsString(getSampleV2Message(), "/sim2:TestMessage/Text");
        Assert.assertNotNull(externalOrderId);
        Assert.assertEquals(externalOrderId, "SIM2");
    }

    @Test
    public void testEvaluateWithError() throws Exception {
        String result = xPathHelper.evaluateAsString(getSampleV2Message(), "//sim1:unknown/element");
        Assert.assertNotNull(result);
        Assert.assertEquals(result, "");
    }

    private Message getSampleV1Message() {
        return new DefaultMessage("<sim1:TestMessage xmlns:sim1=\"http://mnet.de/sim/v1/envelope\"><Text>SIM1</Text></sim1:TestMessage>");
    }

    private Message getSampleV2Message() {
        return new DefaultMessage("<sim2:TestMessage xmlns:sim2=\"http://mnet.de/sim/v2/envelope\"><Text>SIM2</Text></sim2:TestMessage>");
    }
}
