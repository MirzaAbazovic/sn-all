/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.02.2015
 */
package de.mnet.hurrican.acceptance.ffm;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.exceptions.FFMServiceException;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.augustakom.hurrican.service.cc.ffm.FFMService;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.actions.UpdateAndSendOrderServiceAction;

/**
 *
 */
@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_UpdateOrder_Test extends AbstractFfmTestBuilder {

    // @formatter:off
    /**
     * Test creates a new 'complete' hurrican 'Bauauftrag' with manually assigned WorkforceOrderId and triggers
     * the {@link FFMService#updateAndSendOrder( Verlauf )} method. <br/>
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     updateAndSendOrder     ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_UpdateOrder_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_UpdateOrder_01);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_FTTX_TELEFONIE)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB)
                .withVpn(true)
                .buildBauauftrag();

        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId("assigned-workforce-order-id");

        hurrican().updateAndSendOrder(bauauftrag);

        atlas().receiveUpdateOrder("updateOrder");
    }

    // @formatter:off
    /**
     * Test creates a new hurrican 'Bauauftrag' without WorkforceOrderId and triggers
     * the {@link FFMService#updateAndSendOrder( Verlauf )} method. <br/>
     * <p/>
     * <pre>
     *     Hurrican                      AtlasESB
     *     updateAndSendOrder     ->
     *        |
     *        -->  expect FFMServiceException
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_UpdateOrder_02_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_UpdateOrder_02);
        //check if an FFMServiceException have been thrown
        Verlauf verlauf = new Verlauf();
        verlauf.setAuftragId(123L);
        verlauf.setAkt(true);
        action(new UpdateAndSendOrderServiceAction(hurrican().getFFMService(), verlauf)
                        .expectException(FFMServiceException.class)
                        .expectExceptionMessage("Fehler beim Update der FFM WorkforceOrder: Der angegebene Bauaftrag '123' besitzt keine aktive FFM WorkforceOrder!")
        );
    }

}
