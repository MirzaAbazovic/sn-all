/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.11.2014
 */
package de.mnet.hurrican.acceptance.tv;

import com.consol.citrus.dsl.annotations.CitrusTest;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.Produkt;
import de.mnet.hurrican.acceptance.common.SimulatorUseCase;

@Test(groups = BaseTest.ACCEPTANCE)
public class TvProvider_Availability_Test extends AbstractTvFeedTestBuilder {

    @CitrusTest
    public void TV_Provider_Availability_01_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Provider_Availability_01);

        tvProvider().sendTvProviderRequest("tvProviderRequest");
        tvProvider().receiveTvProviderResponse("tvProviderResponse");
    }

    @CitrusTest
    @Test(enabled = true)
    public void TV_Provider_Availability_02_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Provider_Availability_02);

        hurrican().createTvAuftrag(
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG,
                HVTStandort.HVT_STANDORT_TYP_FTTB);

        tvProvider().sendTvProviderRequest("tvProviderRequest");
        tvProvider().receiveTvProviderResponse("tvProviderResponse");
    }

}
