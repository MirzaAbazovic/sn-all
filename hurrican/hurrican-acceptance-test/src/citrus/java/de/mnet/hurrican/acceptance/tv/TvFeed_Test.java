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
public class TvFeed_Test extends AbstractTvFeedTestBuilder {

    /**
     * Should not return any result, since there are no matching endstellen for the requested GeoID
     */
    @CitrusTest
    public void TV_FeedData4GeoIds_01_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_GeoIds_01);

        tvFeed().sendTVFeedData4GeoIdsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4GeoIdsResponse("tvFeedResponse");
    }

    /**
     * Should find TV Feed for supplied GeoID
     */
    @CitrusTest
    @Test(enabled = true)
    public void TV_FeedData4GeoIds_02_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_GeoIds_02);

        hurrican().createTvAuftrag(
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG,
                HVTStandort.HVT_STANDORT_TYP_FTTB);

        tvFeed().sendTVFeedData4GeoIdsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4GeoIdsResponse("tvFeedResponse");
    }

    /**
     * Should find multiple TV Feeds. The supplied GeoID corresponds to the Vorsorgte Tv-Feed, which is in turn linked
     * to the Versorgende-Tv-Auftrag.
     */
    @CitrusTest
    @Test(enabled = false)
    //TODO trenkerbe: nach Absprache mit Christoph anpassen und aktivieren
    public void TV_FeedData4GeoIds_03_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_GeoIds_03);

        hurrican().createBuendledTvAuftraege(
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG,
                HVTStandort.HVT_STANDORT_TYP_FTTB);

        tvFeed().sendTVFeedData4GeoIdsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4GeoIdsResponse("tvFeedResponse");
    }

    /**
     * Should not return any result, since there are no matching HVT Gruppen for the requested technical location
     */
    @CitrusTest
    public void TV_TVFeedData4TechLocations_01_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_Data4TechLocations_01);

        tvFeed().sendTVFeedData4TechLocationsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4TechLocationsResponse("tvFeedResponse");
    }

    /**
     * Should find TV Feed for supplied technical location
     */
    @CitrusTest
    public void TV_TVFeedData4TechLocations_02_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_Data4TechLocations_02);

        hurrican().createTvAuftrag(
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG,
                HVTStandort.HVT_STANDORT_TYP_FTTB);

        tvFeed().sendTVFeedData4TechLocationsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4TechLocationsResponse("tvFeedResponse");
    }

    /**
     * Should find TV Feed for supplied technical location
     */
    @CitrusTest
    @Test(enabled = false)
    //TODO trenkerbe: nach Absprache mit Christoph anpassen und aktivieren
    public void TV_TVFeedData4TechLocations_03_Test() {
        simulatorUseCase(SimulatorUseCase.TV_Feed_Data4TechLocations_03);

        hurrican().createBuendledTvAuftraege(
                Produkt.PROD_ID_TV_SIGNALLIEFERUNG,
                HVTStandort.HVT_STANDORT_TYP_FTTB);

        tvFeed().sendTVFeedData4TechLocationsRequest("tvFeedRequest");
        tvFeed().receiveTVFeedData4TechLocationsResponse("tvFeedResponse");
    }

}
