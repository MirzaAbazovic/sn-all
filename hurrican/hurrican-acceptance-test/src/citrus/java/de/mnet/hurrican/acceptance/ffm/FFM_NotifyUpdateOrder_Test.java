
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
import de.augustakom.hurrican.model.cc.VerlaufAbteilung;
import de.augustakom.hurrican.model.cc.VerlaufStatus;
import de.augustakom.hurrican.model.cc.ffm.FfmOrderState;
import de.augustakom.hurrican.service.cc.ffm.FFMNotificationConsumer;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_NotifyUpdateOrder_Test extends AbstractFfmTestBuilder {

    /**
     * Test sends out a new FFM Notification message ON_SITE and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Order status in Hurrican for FFM should be changed accordingly.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate         ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyUpdateOrder_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyUpdateOrder_01);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK, VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.ON_SITE.name());
        String resourceId = "DISPATCH_12345678";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK,
                VerlaufStatus.STATUS_IN_BEARBEITUNG, null, null, resourceId);
    }

    /**
     * Test sends out a new FFM Notification message DONE and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Order status in Hurrican for FFM should be changed accordingly.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate         ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyUpdateOrder_02_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyUpdateOrder_02);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK, VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.DONE.name());
        String resourceId = "DISPATCH_12345678";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.VERLAUF_ABGESCHLOSSEN,
                VerlaufStatus.STATUS_ERLEDIGT, false, null, resourceId);
    }

    /**
     * Test sends out a new FFM Notification messages that should be ignored.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate         ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyUpdateOrder_03_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyUpdateOrder_03);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK, VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, "NEW");
        String resourceId = "DISPATCH_12345671";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK,
                VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.ON_SITE.name());

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK,
                VerlaufStatus.STATUS_IN_BEARBEITUNG, null, null, resourceId);

        variables().add(VariableNames.FFM_STATE, "SEEN");

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK,
                VerlaufStatus.STATUS_IN_BEARBEITUNG, null, null, resourceId);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.DONE.name());
        resourceId = "DISPATCH_12345678";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.VERLAUF_ABGESCHLOSSEN,
                VerlaufStatus.STATUS_ERLEDIGT, false, null, resourceId);

    }

    /**
     * Test sends out a new FFM Notification message TNFE and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Order status in Hurrican for FFM should be changed accordingly and not possible flag
     * should be set.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate         ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyUpdateOrder_04_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyUpdateOrder_04);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK, VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.TNFE.name());
        String resourceId = "DISPATCH_12345678";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.RUECKLAEUFER_DISPO,
                VerlaufStatus.STATUS_ERLEDIGT, true, VerlaufAbteilung.NOT_POSSIBLE_REF_ID_TNFE, resourceId);
    }

    /**
     * Test sends out a new FFM Notification message CUST and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Order status in Hurrican for FFM should be changed accordingly and not possible flag
     * should be set.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderUpdate         ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyUpdateOrder_05_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyUpdateOrder_05);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.BEI_TECHNIK, VerlaufStatus.BEI_DISPO);

        variables().add(VariableNames.FFM_STATE, FfmOrderState.CUST.name());
        String resourceId = "DISPATCH_12345678";
        variables().add(VariableNames.FFM_RESOURCE_ID, resourceId);

        atlas().sendNotifyOrderUpdate("notifyOrderUpdate");

        hurrican().assertOrderState(workforceOrderId, VerlaufStatus.RUECKLAEUFER_DISPO,
                VerlaufStatus.STATUS_ERLEDIGT, true, VerlaufAbteilung.NOT_POSSIBLE_REF_ID_CUST, resourceId);
    }

    /**
     * TestCase fuer manuelle(!!!) Ueberpruefung wg. blocking messages auf Tibco EMS.
     * siehe hierzu HUR-20115 und HUR-20211
     * bzw. auch Intranet-Artikel: https://intranet.m-net.de/display/MAITAppHurrican/2015/01/29/JMS+TroubleShooting
     */
    @CitrusTest
    public void FFM_JMS_Troubleshooting_Test() {
//        simulatorUseCase(SimulatorUseCase.FFM_JMS_Troubleshooting);
//
//        variables().add(VariableNames.FFM_STATE, "ON_SITE");
//        variables().add("workforceOrderId", "HUR_61e28d7c-c4bd-4f5a-9541-9d829b0a7569");
//
//        atlas().sendLocationNotification("notifyUpdateLocation");
//        atlas().sendLocationNotification("notifyUpdateLocation");
//        atlas().sendLocationNotification("notifyUpdateLocation");
//        atlas().sendLocationNotification("notifyUpdateLocation");
//        atlas().sendNotifyOrderUpdate("notifyOrderUpdate", 100);
//        atlas().sendLocationNotification("notifyUpdateLocation");
    }

}
