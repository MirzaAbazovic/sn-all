/*

 * Copyright (c) 2016 - M-net Telekommunikations GmbH

 * All rights reserved.

 * -------------------------------------------------------

 * File created: 29.04.2016

 */

package de.mnet.hurrican.acceptance.resourceorder;


import static de.mnet.hurrican.ffm.citrus.VariableNames.*;

import com.consol.citrus.dsl.annotations.CitrusTest;
import com.consol.citrus.dsl.definition.ReceiveMessageActionDefinition;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.common.tools.lang.DateTools;
import de.mnet.common.tools.DateConverterUtils;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

@Test(groups = BaseTest.ACCEPTANCE)
public class ModifyPortReservationDateTest extends AbstractPortTestBuilder {

    /**
     * Test f&uuml;r Terminverschiebung in die Zukunft.
     */
    @CitrusTest
    public void ResourceOrder_ModifyPortReservationDate_Success() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);

        final ReceiveMessageActionDefinition reservePortSuccess = reservePort("2199-05-06");
        reservePortSuccess.extractFromPayload("lineId", LINE_ID);
        reservePortSuccess.extractFromPayload("executionDate", "executionDate");

        variables().add(ORDER_ID, "HER_CITRUS_TEST");
        variables().add(NOTIFICATION_TYPE, "releasePortResponse");
        variables().add("modifyDate", "" + DateConverterUtils.asLocalDate(DateTools.getHurricanEndDate()));

        bauauftragAnFFMVerteilen();

        atlas().receiveCreateOrder("ffmCreateOrder");

        resourceInventory().sendModifyPortReservationDateResourceOrderManagementServiceRequest("modifyPortReservationDate");

        sleep(10000L);

        atlas().receiveDeleteOrder("ffmDeleteOrder");

        sleep(20000L);

        atlas().receiveCreateOrder("ffmReCreateOrder");

        atlas().receiveResourceOrderManagementNotification("modifyPortReservationDateSuccessNotification");

    }

    @CitrusTest
    public void ResourceOrder_ModifyPortReservationDateAfterReleasePort_Test() throws Exception {
        simulatorUseCase(SimulatorUseCase.Resource_Order_Management);
        releasePortKuendigung();

        variables().add("modifyDate", "" + DateConverterUtils.asLocalDate(DateTools.getHurricanEndDate()).minusDays(1L));
        resourceInventory().sendModifyPortReservationDateResourceOrderManagementServiceRequest("modifyPortReservationDate");
        atlas().receiveResourceOrderManagementNotification("modifyPortReservationDateSuccessNotification");
    }

}