/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.11.2014
 */
package de.mnet.hurrican.acceptance.resource;

import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.behavior.SendUpdateResource_Behavior;
import de.mnet.hurrican.acceptance.behavior.SendUpdateResource_SerialNr_Behavior;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

@Test(groups = BaseTest.ACCEPTANCE)
public class ResourceInventory_UpdateOntResource_Test extends AbstractResourceInventoryTestBuilder {

    private final String hwRackType = HWRack.RACK_TYPE_ONT;

    /**
     * Simulates the command application by sending ONT and ONT-Port update resource requests to
     * hurrican. After each update resource request a check is made to ensure that the resources was
     * created correctly in hurrican.
     *
     */
    @CitrusTest
    public void UpdateOntResource_01_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateOntResource_01);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTH, hwRackType);
        hurrican().createOntBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_Port");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyOntPortCreated();
    }

    /**
     * Simulates the command application by sending two ONT update resource requests to
     * hurrican. The first request contains no serial number. The second request contains a serial number
     * which results in a CPS request being sent to CPS.
     */
    @CitrusTest
    @Test
    public void UpdateOntResource_02_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateOntResource_02);
        // TODO MM Validate SOData
        final String serialNumber = RandomStringUtils.randomAlphanumeric(11).toUpperCase();

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTH, hwRackType);
        hurrican().createOntBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));
        hurrican().verifyOntSerialNumber(null);

        applyBehavior(new SendUpdateResource_SerialNr_Behavior(hwRackType, serialNumber));
    }

    /**
     * Simulates the command application by sending three ONT update resource requests to
     * hurrican. The first request creates a ONT Resource. The second request sets the installation status
     * to "GELOESCHT". The third request modifies the "modellnummer" characteristic resulting in a
     * new ONT resource being created in hurrican.
     */
    @CitrusTest
    public void UpdateOntResource_03_Test() {
        simulatorUseCase(SimulatorUseCase.ResourceInventory_UpdateOntResource_03);

        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTH, hwRackType);
        hurrican().createOntBezeichnung();

        applyBehavior(new SendUpdateResource_Behavior(hwRackType));

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_2");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyOntChild().withGueltigBisBeforeOrEqual(new Date());

        resourceInventory().sendUpdateResourceRequest("updateResourceRequest_3");
        resourceInventory().receiveUpdateResourceResponse("updateResourceResponse");

        hurrican().verifyOntCreated();
        hurrican().verifyOntChild().withGueltigBisEqual(DateTools.getHurricanEndDate());
    }

}
