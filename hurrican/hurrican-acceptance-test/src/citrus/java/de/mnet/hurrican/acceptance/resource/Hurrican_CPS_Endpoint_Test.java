/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.mnet.hurrican.acceptance.resource;

import javax.sql.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Acceptance test to check the hurrican endpoint 'CpsEndpoint'
 *
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class Hurrican_CPS_Endpoint_Test extends AbstractResourceInventoryTestBuilder {

    @Autowired
    @Qualifier("taifunDataSource")
    private DataSource dataSource;

    @CitrusTest
    public void Hurrican_CPS_Endpoint_Test_01() throws FindException {
        simulatorUseCase(SimulatorUseCase.Hurrican_CPS_Endpoint_01);

        hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);

        cps().sendSoDataRequest("SO_DATA_REQUEST");
        cps().receiveSoDataRepsonse("SO_DATA_RESPONSE");
    }

    @CitrusTest
    public void Hurrican_CPS_Endpoint_Test_02() throws FindException {
        simulatorUseCase(SimulatorUseCase.Hurrican_CPS_Endpoint_02);

        hurrican().createFttxDslFonWithVoipAccountsAuftrag(this);

        cps().sendProvisionRequest("PROVISION_REQUEST").fork(true);
        cps().receiveCpsAsyncServiceRequest("cpsServiceRequest_CreateSubscriber");
        cps().sendCpsAsyncServiceRequestAck("cpsServiceRequestAck");
        cps().receiveProvisionRepsonse("PROVISION_RESPONSE");
    }

}
