/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.mnet.hurrican.acceptance.ffm;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.model.cc.ffm.FfmOrderState;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 * Acceptance tests for testing the error handling behaviour of the hurrican FFM component.
 *
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_ErrorService_Test extends AbstractFfmTestBuilder {

    /**
     * Test sends out an FFM Notification message 'notifyOrderUpdate' which match to an an existing Bauauftrag. But
     * there the sub dataset BauauftragAbteilung will be missing, so an Exception will created and send to
     * AtlasEsbErrorService
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate    -->
     *                                               <--   atlasErrorHandlingService (Handle-Error-Message)
     * </pre>
     */
    @CitrusTest
    public void FFM_ErrorService_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_ErrorService_01);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withFfmAbteilung(false)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.ON_SITE.name());
        variables().add(VariableNames.FFM_RESOURCE_ID, "DISPATCH_12345678");

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");
        atlas().receiveErrorHandlingServiceMessage("errorNotification");
    }

    /**
     * Test sends out an FFM Notification message 'notifyOrderFeedback' which have an empty material Id. When Hurrican
     * tries to persist the message, an Exception will be created and send to AtlasEsbErrorService
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderFeedback    -->
     *                                               <--   atlasErrorHandlingService (Handle-Error-Message)
     * </pre>
     */
    @CitrusTest
    public void FFM_ErrorService_02_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_ErrorService_02);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        atlas().sendNotifyOrderFeedback("notifyOrderFeedback");
        atlas().receiveErrorHandlingServiceMessage("errorNotification");
    }

    /**
     * Test sends out a schema invalid FFM Notification message which should lead to error handling in Atlas ESB.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate        -->
     *                                                   <--   atlasErrorHandlingService (Handle-Error-Message)
     * </pre>
     */
    @CitrusTest
    public void FFM_ErrorService_03_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_ErrorService_03);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        atlas().receiveErrorHandlingServiceMessage("errorNotification");
    }

}
