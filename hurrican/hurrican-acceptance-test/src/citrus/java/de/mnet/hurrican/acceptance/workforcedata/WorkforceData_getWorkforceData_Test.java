/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.02.2015
 */
package de.mnet.hurrican.acceptance.workforcedata;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.AbstractHurricanTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class WorkforceData_getWorkforceData_Test extends AbstractHurricanTestBuilder {

    /**
     * Test für getWorkforceData mit nicht existierender Auftragsnummer.
     */
    @CitrusTest
    public void getWorkforceDataTest_keinAuftrag() {
        simulatorUseCase(SimulatorUseCase.WorkforceData_01, WorkforceDataTestVersion.V1);
        setVariable(VariableNames.AUFTRAG_ID, "-1");
        setVariable(VariableNames.INCIDENT_REASON, "Absage");
        workforceData().sendGetWorkforceDataRequest("getWorkforceDataRequest");

        workforceData().receiveGetWorkforceDataResponse("getWorkforceDataResponse_keinAuftrag");
    }

    /**
     * Test für getWorkforceData mit nicht existierendem Störungsgrund.
     */
    @CitrusTest
    public void getWorkforceDataTest_keinStoerungsgrund() {
        simulatorUseCase(SimulatorUseCase.WorkforceData_01, WorkforceDataTestVersion.V1);

        hurrican().createTaifunAuftragMaxiGlasfaserDsl(1);
        hurrican().createHurricanFttxTelefonieAuftrag();

        setVariable(VariableNames.INCIDENT_REASON, "some_not_existing_reason");
        workforceData().sendGetWorkforceDataRequest("getWorkforceDataRequest");

        workforceData().receiveGetWorkforceDataResponse("getWorkforceDataResponse_keinStoerungsgrund");
    }


    /**
     * Positiv Test für getWorkforceData. Der Inhalt der "techParams" wird nicht validiert (die Anwesenheit schon) =>
     * Inhalt wird bei FFM Tests validiert.
     */
    @CitrusTest
    public void getWorkforceDataTest_Success() {
        simulatorUseCase(SimulatorUseCase.WorkforceData_01, WorkforceDataTestVersion.V1);

        hurrican().createTaifunAuftragMaxiGlasfaserDsl(1);
        hurrican().createHurricanFttxTelefonieAuftrag();

        setVariable(VariableNames.INCIDENT_REASON, "Totalausfall");
        workforceData().sendGetWorkforceDataRequest("getWorkforceDataRequest");

        workforceData().receiveGetWorkforceDataResponse("getWorkforceDataResponse_success");
    }

}