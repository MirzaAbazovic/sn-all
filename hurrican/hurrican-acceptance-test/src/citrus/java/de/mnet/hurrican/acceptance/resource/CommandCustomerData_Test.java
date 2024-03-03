/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.2014
 */
package de.mnet.hurrican.acceptance.resource;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class CommandCustomerData_Test extends AbstractResourceInventoryTestBuilder {

    @CitrusTest
    public void CommandCustomerData_01_Mdu_Test() {
        simulatorUseCase(SimulatorUseCase.CommandCustomerData_01);

        hurrican().prepareCustomerData(HWRack.RACK_TYPE_MDU);

        resourceInventory().sendCommandCustomerDataRequest("commandCustomerDataRequest");
        resourceInventory().receiveCommandCustomerDataResponse("commandCustomerDataResponse", false);
    }

    @CitrusTest
    public void CommandCustomerData_01_Ont_Test() {
        simulatorUseCase(SimulatorUseCase.CommandCustomerData_01);

        hurrican().prepareCustomerData(HWRack.RACK_TYPE_ONT);

        resourceInventory().sendCommandCustomerDataRequest("commandCustomerDataRequest");
        resourceInventory().receiveCommandCustomerDataResponse("commandCustomerDataResponse", false);
    }

    @CitrusTest
    public void CommandCustomerData_01_Dpo_Test() {
        simulatorUseCase(SimulatorUseCase.CommandCustomerData_01);

        hurrican().prepareCustomerData(HWRack.RACK_TYPE_DPO);

        resourceInventory().sendCommandCustomerDataRequest("commandCustomerDataRequest");
        resourceInventory().receiveCommandCustomerDataResponse("commandCustomerDataResponse", false);
    }

}
