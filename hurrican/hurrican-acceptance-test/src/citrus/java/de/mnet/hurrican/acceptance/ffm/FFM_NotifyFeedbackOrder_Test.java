/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.14
 */
package de.mnet.hurrican.acceptance.ffm;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.ffm.FFMNotificationConsumer;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.VariableNames;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_NotifyFeedbackOrder_Test extends AbstractFfmTestBuilder {

    /**
     * Test sends out a new FFM feedback notification message and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Feedback includes neither material nor text so message should be ignored.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderFeedback       ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyFeedbackOrder_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyFeedbackOrder_01);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        atlas().sendNotifyOrderFeedback("notifyOrderFeedback");
        hurrican().assertAbteilungBemerkung(null, bauauftrag.getId());
        assertMaterialCount(0L);
    }

    /**
     * Test sends out a new FFM feedback notification message and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Feedback includes material that should be saved to Hurrican database accordingly.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderFeedback       ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyFeedbackOrder_02_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyFeedbackOrder_02);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        variables().add(VariableNames.MATERIAL_ID, "citrus:randomNumber(10)");
        variables().add(VariableNames.QUANTITY, "2");

        assertMaterialCount(0L);

        atlas().sendNotifyOrderFeedback("notifyMaterialFeedback");

        assertMaterialCount(1L);
        assertLastMaterialCreated();

        variables().add(VariableNames.MATERIAL_ID, "citrus:randomNumber(10)");
        variables().add(VariableNames.QUANTITY, "3");

        atlas().sendNotifyOrderFeedback("notifyMaterialFeedback");

        assertMaterialCount(2L);
        assertLastMaterialCreated();
        hurrican().assertAbteilungBemerkung(null, bauauftrag.getId());
    }

    /**
     * Test sends out a new FFM feedback notification message and should test the the behaviour of the  {@link
     * FFMNotificationConsumer}. Feedback includes info text so message should be saved to Hurrican bauauftrag
     * accordingly.
     * <p/>
     * <p/>
     * <pre>
     *     CITRUS                                              Hurrican
     *     workforceNotifcation#notifyOrderFeedback       ->
     * </pre>
     */
    @CitrusTest
    public void FFM_NotifyFeedbackOrder_03_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_NotifyFeedbackOrder_03);

        String workforceOrderId = createWorkforceOrderId();

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(workforceOrderId);

        hurrican().save(bauauftrag);

        variables().add(VariableNames.FEEDBACK_TEXT, "FFM rocks!");

        atlas().sendNotifyOrderFeedback("notifyTextFeedback");
        hurrican().assertAbteilungBemerkung("FFM rocks!", bauauftrag.getId());
        assertMaterialCount(0L);
    }

    /**
     * Checks number of saved material entries in Hurrican database for current workforce order id.
     *
     * @param count
     */
    private void assertMaterialCount(Long count) {
        repeatOnError(
                query(getHurricanDataSource())
                        .statement(String.format("SELECT COUNT(*) AS MATERIAL_COUNT FROM T_FFM_FEEDBACK_MATERIAL WHERE WORKFORCE_ORDER_ID = '${%s}'", VariableNames.WORKFROCE_ORDER_ID))
                        .validate("MATERIAL_COUNT", String.valueOf(count))).until("i gt 15");
    }

    /**
     * Checks last material created in Hurrican database. Uses current workforce order id and material id test
     * variables.
     */
    private void assertLastMaterialCreated() {
        repeatOnError(
                query(getHurricanDataSource())
                        .statement(String.format("SELECT MATERIAL_ID, QUANTITY FROM T_FFM_FEEDBACK_MATERIAL WHERE WORKFORCE_ORDER_ID = '${%s}' and MATERIAL_ID = '${%s}'",
                                VariableNames.WORKFROCE_ORDER_ID, VariableNames.MATERIAL_ID))
                        .validate("MATERIAL_ID", "${" + VariableNames.MATERIAL_ID + "}")
                        .validate("QUANTITY", "${" + VariableNames.QUANTITY + "}")).until("i gt 15");
    }
}
