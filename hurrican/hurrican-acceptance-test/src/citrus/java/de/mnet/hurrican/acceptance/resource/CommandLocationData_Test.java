/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.11.2014
 */
package de.mnet.hurrican.acceptance.resource;

import java.util.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.hardware.HWRack;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class CommandLocationData_Test extends AbstractResourceInventoryTestBuilder {


    @CitrusTest
    public void CommandLocationData_01_Test() {
        simulatorUseCase(SimulatorUseCase.CommandLocationData_01);

        variable(VariableNames.ORTSTEIL, UUID.randomUUID().toString());
        hurrican().prepareOltStandort(HVTStandort.HVT_STANDORT_TYP_FTTH, HWRack.RACK_TYPE_ONT);

        resourceInventory().sendCommandLocationDataRequest("commandLocationDataRequest");
        resourceInventory().receiveCommandLocationDataResponse("commandLocationDataResponse");

        hurrican().verifyHvtStandort(HVTStandort.HVT_STANDORT_TYP_FTTB);
    }

}
