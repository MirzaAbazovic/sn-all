/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 11.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import static de.mnet.hurrican.ffm.citrus.VariableNames.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Created by petersde on 11.04.2016.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class ReservePortTest extends AbstractPortTestBuilder {

    /**
     * Test f&uuml;r Portreservierung. Test f&uuml;r den Fall: Realisierungsdatum liegt in der Vergangenheit.
     *
     * @throws Exception
     */
    @CitrusTest
    public void ResourceOrder_ReservePort_Date_In_Past_Error_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);

        createTestdata();
        addXmlVariables();
        final String desiredExecutionDate = "2015-04-04";

        variables().add(EKP_ID, ekpData.ekpFrameContract.getEkpId());
        variables().add(FRAME_CONTRACT_ID, ekpData.ekpFrameContract.getFrameContractId());
        variables().add(GEO_ID, String.valueOf(standortData.geoId.getId()));
        variables().add(DESIRED_EXECUTION_DATE, desiredExecutionDate);
        variables().add(ERROR_CODE, "HUR-0002");
        variables().add(ERROR_DESCRIPTION, "The desired execution date " + desiredExecutionDate + " is in the past!");
        resourceInventory().sendReservePortResourceOrderManagementServiceRequest("reservePort");
        atlas().receiveReservePortResourceOrderManagementNotificationServiceResponse("errorNotification");
    }

    /**
     * Test f&uuml;r Portreservierung. Test f&uuml;r den Fall: Port wird erfolgreich reserviert, es kommt eine LineId
     * zur&uuml;ck.
     *
     * @throws Exception
     */
    @CitrusTest
    public void ResourceOrder_ReservePort_Success_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);

        final ReceiveMessageActionDefinition success = reservePort("2199-05-06");

        success.validate(LINE_ID, "DEU.MNET.\\d*");
    }

}
