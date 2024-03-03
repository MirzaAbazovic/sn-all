/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 14.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import static de.mnet.hurrican.ffm.citrus.VariableNames.*;

import java.time.*;
import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

/**
 * Created by petersde on 14.04.2016.
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class ReleasePortStornoTest extends AbstractPortTestBuilder {

    /**
     * Test f&uuml;r Portfreigabe. Test f&uuml;r den Fall: Die LineId ist nicht korrekt.
     *
     * @throws Exception
     */
    @CitrusTest
    public void ResourceOrder_ReleasePort_Wrong_LineId_Error_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);

        final String lineId = "UNKNOWN_LINE_ID";
        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(LINE_ID, lineId);
        variables().add(NOTIFICATION_TYPE, "releasePortResponse");
        variables().add(ERROR_CODE, "HUR-0007");
        variables().add(ERROR_DESCRIPTION, "");

        resourceInventory().sendReleasePortResourceOrderManagementServiceRequest("releasePort");

        atlas().receiveResourceOrderManagementNotification("errorNotification");
    }

    /**
     * Test f&uuml;r Portfreigabe. Test f&uuml;r den Fall: Der Port wird korrekt freigegeben.
     *
     * @throws Exception
     */
    @CitrusTest
    public void ResourceOrder_ReleasePort_Success_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);

        final ReceiveMessageActionDefinition success = reservePort("2199-05-06");
        success.extractFromPayload("lineId", LINE_ID);
        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(NOTIFICATION_TYPE, "releasePortResponse");
        variables().add(DESIRED_EXECUTION_DATE, "" + LocalDate.now());

        resourceInventory().sendReleasePortResourceOrderManagementServiceRequest("releasePort");

        atlas().receiveResourceOrderManagementNotification("releasePortSuccessNotification");
    }







}
