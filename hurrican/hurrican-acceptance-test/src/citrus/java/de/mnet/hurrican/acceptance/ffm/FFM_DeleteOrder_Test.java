/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.09.14
 */
package de.mnet.hurrican.acceptance.ffm;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Verlauf;
import de.mnet.hurrican.acceptance.builder.VerlaufTestBuilder;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;
import de.mnet.hurrican.ffm.citrus.actions.DeleteOrderServiceAction;

@Test(groups = BaseTest.ACCEPTANCE)
public class FFM_DeleteOrder_Test extends AbstractFfmTestBuilder {

    // @formatter:off
    /**
     * Tests triggers delete order FFM operation on Atlas ESB.
     * <p/>
     * <p/>
     * <pre>
     *     Hurrican                         AtlasESB
     *     deleteOrder         ->
     * </pre>
     */
    // @formatter:on
    @CitrusTest
    public void FFM_DeleteOrder_01_Test() {
        simulatorUseCase(SimulatorUseCase.FFM_DeleteOrder_01);

        final VerlaufTestBuilder.CreatedData createdData = hurrican().getVerlaufTestBuilder()
                .withProdId(Produkt.PROD_ID_MAXI_DSL_UND_ISDN)
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT)
                .buildBauauftrag();
        Verlauf bauauftrag = createdData.verlauf;
        bauauftrag.setWorkforceOrderId(createWorkforceOrderId());

        hurrican().save(bauauftrag);

        action(new DeleteOrderServiceAction(hurrican().getFFMService(), bauauftrag));

        atlas().receiveDeleteOrder("deleteOrder");
    }

}
