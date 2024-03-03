/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.11.2014
 */
package de.mnet.hurrican.acceptance.resource;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.behavior.SendCommandStiftData_Behavior;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class CommandStiftData_Test extends AbstractResourceInventoryTestBuilder {

    @CitrusTest
    public void CommandStiftData_01_Test() {
        simulatorUseCase(SimulatorUseCase.CommandStiftData_01);

        hurrican().createHvtStandort();

        applyBehavior(new SendCommandStiftData_Behavior(null, "3", "01", "02"));
    }

    @CitrusTest
    public void CommandStiftData_02_Test() {
        simulatorUseCase(SimulatorUseCase.CommandStiftData_02);

        hurrican().createHvtStandort();

        applyBehavior(new SendCommandStiftData_Behavior(null, "3", "01", "02"));

        resourceInventory().sendCommandStiftDataRequest("commandStiftDataRequest");
        resourceInventory().receiveCommandStiftDataResponse("commandStiftDataFault", false);
    }

}
