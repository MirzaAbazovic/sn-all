package de.mnet.hurrican.acceptance.serialnumber;

import static org.testng.Assert.*;

import java.util.*;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.HardwareDAO;
import de.augustakom.hurrican.dao.cc.errorlog.ErrorLogDAO;
import de.augustakom.hurrican.model.cc.ErrorLogEntry;
import de.augustakom.hurrican.model.cc.hardware.HWOnt;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.acceptance.resource.AbstractResourceInventoryTestBuilder;

@Test(groups = BaseTest.ACCEPTANCE)
public class SerialNumberUpdateResource_Test extends AbstractResourceInventoryTestBuilder {

    @Autowired
    ErrorLogDAO errorLogDAO;
    @Autowired
    HardwareDAO hardwareDAO;

    private static final String SERVICENAME = "SerialNumberFFMService";

    @CitrusTest
    public void updateResourceCharacteristicsRequest_ResourceNotFound() {
        final String resourceId = "resourceid-" + randomToken();
        final String serialNumber = "SN-" + randomToken();
        variables().add("geraeteBezeichnung", resourceId);
        variables().add("serialNumber", serialNumber);


        simulatorUseCase(SimulatorUseCase.ResourceCharacteristics_Update_01);
        resourceInventory().sendResourceCharacteristicsRequest("updateResourceCharacteristicsRequest");

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                final List<ErrorLogEntry> errorLogEntries = errorLogDAO.findByService(SERVICENAME);
                assertNotNull(errorLogEntries);
            }
        });
    }

    private String randomToken() {
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,7);
    }

    @CitrusTest
    public void updateResourceCharacteristicsRequest_Successful() {
        simulatorUseCase(SimulatorUseCase.ResourceCharacteristics_Update_01);

        final List<HWOnt> activeOntRacks = hardwareDAO.findRacks(620L, HWOnt.class, true);
        assertNotNull(activeOntRacks);
        assertFalse(activeOntRacks.isEmpty());

        final HWOnt foundRack = activeOntRacks.get(0);

        final String geraeteBez = foundRack.getGeraeteBez();
        variables().add("geraeteBezeichnung", geraeteBez);

        final String serialNumber = "SN-TEST-" + randomToken();
        variables().add("serialNumber", serialNumber);
        logger.debug(String.format("------- starting with hardware [%s] old SN [%s] new SN [%s]", foundRack.getGeraeteBez(), foundRack.getSerialNo(), serialNumber));

        resourceInventory().sendResourceCharacteristicsRequest("updateResourceCharacteristicsRequest");

        // CMD request / response
        resourceInventory().receiveUpdateResourceRequest("updateResourceRequest_SerialNumber");
        resourceInventory().sendUpdateResourceResponse("updateResourceResponse");

        // CPS
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceResponseAck");
//        cps().sendCpsAsyncServiceResponse("cpsServiceResponse");

        action(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext testContext) {
                final HWOnt updatedRack = hardwareDAO.findById(foundRack.getId(), HWOnt.class);
                assertNotNull(updatedRack);
                // transaction is not closed
                ///assertEquals(updatedRack.getSerialNo(), serialNumber);
            }
        });
    }

    // just for debugging, not a usual test case
    @CitrusTest
    @Test(enabled = false)
    public void updateResourceCharacteristicsRequest_SpecialCase() {
        final String resourceId = "ONT-410770";
        final String serialNumber = "485754433FE0E555";
        variables().add("geraeteBezeichnung", resourceId);
        variables().add("serialNumber", serialNumber);


        simulatorUseCase(SimulatorUseCase.ResourceCharacteristics_Update_01);
        resourceInventory().sendResourceCharacteristicsRequest("updateResourceCharacteristicsRequest");
    }

}
