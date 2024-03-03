package de.mnet.hurrican.webservice.resource;

import java.io.*;
import javax.xml.bind.*;
import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResource;
import de.mnet.hurrican.webservice.resource.inventory.ResourceValidator;

//@ContextConfiguration({ "classpath:de/mnet/hurrican/webservice/resource/resource-test-context.xml" })
//@Test(groups = BaseTest.UNIT, enabled = false)
public class ResourceValidatorTest {//extends AbstractTestNGSpringContextTests {

    private static final Logger LOGGER = Logger.getLogger(ResourceValidatorTest.class);

    private static final String  BASE_PATH =  "/de/mnet/hurrican/webservice/resource";
    private static final String  ONT_PATH =  BASE_PATH + "/ont";
    private static final String  DPO_PATH =  BASE_PATH + "/dpo";

    @javax.annotation.Resource(name = "resourceValidator")
    private ResourceValidator cut;

    @DataProvider
    private Object[][] getResources() {
        return new Object[][] {
                {ONT_PATH + "/ResourceValidatorTestOntValid.xml", true},
                {ONT_PATH + "/ResourceValidatorTestOntInValid.xml", false},
                {ONT_PATH + "/ResourceValidatorTestOntPortValid.xml", true},
                {ONT_PATH + "/ResourceValidatorTestOntPortWithoutParent.xml", false},
                {ONT_PATH + "/ResourceValidatorTestOntPortWithInconsistentParentInventory.xml", false},

                {DPO_PATH + "/ResourceValidatorTestDpoValid.xml", true},
                {DPO_PATH + "/ResourceValidatorTestDpoInValid.xml", false},
                {DPO_PATH + "/ResourceValidatorTestDpoPortValid.xml", true},
                {DPO_PATH + "/ResourceValidatorTestDpoPortWithoutParent.xml", false},
                {DPO_PATH + "/ResourceValidatorTestDpoPortWithInconsistentParentInventory.xml", false}
        };
    }

    //    @Test(dataProvider = "getResources")
    //    public void testResources(String resourcePath, boolean shouldBeValid) throws JAXBException, ResourceValidationException {
    //        try {
    //            cut.validateResource(createResource(resourcePath));
    //            assertTrue(shouldBeValid);
    //        }
    //        catch (ResourceValidationException exc) {
    //            LOGGER.warn(exc.getMessage(), exc);
    //            assertFalse(shouldBeValid);
    //        }
    //    }

    private Resource createResource(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UpdateResource.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = getClass().getResourceAsStream(name);
        return ((UpdateResource) unmarshaller.unmarshal(inputStream)).getResource().get(0);
    }

}
