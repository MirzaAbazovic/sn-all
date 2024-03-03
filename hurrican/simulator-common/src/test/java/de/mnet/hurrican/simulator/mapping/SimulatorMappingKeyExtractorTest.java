package de.mnet.hurrican.simulator.mapping;

import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;

/**
 *
 */
@Test(groups = BaseTest.UNIT)
public class SimulatorMappingKeyExtractorTest {

    private SimulatorMappingKeyExtractor mappingKeyExtractor = new SimulatorMappingKeyExtractor();

    @Test
    public void testExtractMappingKey() throws Exception {
        Message testMessage = new DefaultMessage("<TestRequest><Text>TEST</Text></TestRequest>");

        mappingKeyExtractor.setXpathExpression("/TestRequest/Text");
        Assert.assertEquals(mappingKeyExtractor.extractMappingKey(testMessage), "TEST");

        mappingKeyExtractor.setXpathExpression("//Text");
        Assert.assertEquals(mappingKeyExtractor.extractMappingKey(testMessage), "TEST");

        mappingKeyExtractor.setXpathExpression("//Unknown");
        Assert.assertEquals(mappingKeyExtractor.extractMappingKey(testMessage), "INTERMEDIATE_MESSAGE");
    }

    @Test
    public void testTranslateMappingKey() throws Exception {
        Assert.assertEquals(mappingKeyExtractor.translateMappingKey("TEST_BUILDER"), "TEST_BUILDER");
        Assert.assertEquals(mappingKeyExtractor.translateMappingKey("Ä-TEST-BUILDER"), "Ae_TEST_BUILDER");
        Assert.assertEquals(mappingKeyExtractor.translateMappingKey("Ä_TEST_BUILDER"), "Ae_TEST_BUILDER");
        Assert.assertEquals(mappingKeyExtractor.translateMappingKey("TEST_ÄäÖöÜü_BUILDER"), "TEST_AeaeOeoeUeue_BUILDER");
        Assert.assertEquals(mappingKeyExtractor.translateMappingKey("TEST_BUILDER_Ü"), "TEST_BUILDER_Ue");
    }
}
