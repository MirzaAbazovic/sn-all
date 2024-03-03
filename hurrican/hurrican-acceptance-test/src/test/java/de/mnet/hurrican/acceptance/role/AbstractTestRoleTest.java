package de.mnet.hurrican.acceptance.role;

import static org.testng.Assert.*;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.acceptance.ffm.FfmTestVersion;
import de.mnet.hurrican.acceptance.resource.ResourceInventoryTestVersion;
import de.mnet.hurrican.acceptance.tv.TvFeedTestVersion;

@Test(groups = BaseTest.UNIT)
public class AbstractTestRoleTest {

    private AbstractTestRole testling = new AbstractTestRole();

    @Test
    public void testGetXmlTemplateBasePackage() throws Exception {
        testling.setServiceModelVersison(FfmTestVersion.V1);
        testling.setSimulatorUseCase(SimulatorUseCase.FFM_DeleteOrder_01);
        assertEquals(testling.getXmlTemplateBasePackage(), "templates/ffm/v1/FFM_DeleteOrder_01");
    }

    @Test
    public void testGetXmlTemplateDefaultPackage() throws Exception {
        testling.setServiceModelVersison(FfmTestVersion.V1);
        assertEquals(testling.getXmlTemplateDefaultPackage(), "templates/ffm/v1/FFM_Default");
    }

    @Test(expectedExceptions = CitrusRuntimeException.class)
    public void testGetXmlTemplateDefaultPackageException() throws Exception {
        testling.setServiceModelVersison(TvFeedTestVersion.V1);
        testling.getXmlTemplateDefaultPackage();
    }

    @Test
    public void testGetTemplateFolderName() throws Exception {
        assertEquals(testling.getTemplateFolderName(FfmTestVersion.V1), "ffm");
        assertEquals(testling.getTemplateFolderName(TvFeedTestVersion.V1), "tv");
        assertEquals(testling.getTemplateFolderName(ResourceInventoryTestVersion.V1), "resource");
    }
}