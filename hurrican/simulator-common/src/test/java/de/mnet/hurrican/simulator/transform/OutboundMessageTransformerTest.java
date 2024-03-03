package de.mnet.hurrican.simulator.transform;

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

@Test(groups = BaseTest.UNIT)
public class OutboundMessageTransformerTest {

    @Test
    public void testTransform() throws Exception {
        OutboundMessageTransformer transformer = new OutboundMessageTransformer();
        String result = transformer.transform("<ns:TestMessage xmlns:ns=\"http://www.mnet.de\"><Body><Text language=\"eng\" priority=\"1\">Hello</Text><Empty/></Body><Empty></Empty></ns:TestMessage>");
        Assert.assertEquals(result, "<ns:TestMessage xmlns:ns=\"http://www.mnet.de\"><Body><Text language=\"eng\" priority=\"1\">Hello</Text></Body></ns:TestMessage>");

        result = transformer.transform(new ClassPathResource("messagePayload.xml", this.getClass()));
        Assert.assertTrue(result.contains("kundennummer"));
        Assert.assertFalse(result.contains("kundennummerBesteller"));
    }
}