/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.2012 13:34:04
 */
package de.mnet.hurrican.webservice.tvfeed;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.*;
import com.beust.jcommander.internal.Lists;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.shared.view.TvFeedView;

/**
 * Unit Tests fuer {@link TvFeedEndpoint}.
 */
@Test(groups = BaseTest.UNIT)
public class TvFeedEndpointUnitTest extends BaseTest {

    private TvFeedView createTvFeedView(String geraetebezeichnung, Long auftragId, Long geoId) {
        TvFeedView out = new TvFeedView();
        out.setGeraetebezeichnung(geraetebezeichnung);
        out.setAuftragsId(auftragId);
        out.setGeoId(geoId);
        return out;
    }

    private List<TvFeedView> createTvFeedViewList(String geraetebezeichner) {
        TvFeedView view1 = createTvFeedView(geraetebezeichner, 1L, 1L);
        TvFeedView view2 = createTvFeedView(geraetebezeichner, 1L, 2L);
        TvFeedView view3 = createTvFeedView(null, 2L, 1L);
        TvFeedView view4 = createTvFeedView("notToBeFound", 3L, 3L);
        TvFeedView view5 = createTvFeedView("notToBeFound", 3L, 4L);
        List<TvFeedView> out = new ArrayList<>();
        out.add(view1);
        out.add(view2);
        out.add(view3);
        out.add(view4);
        out.add(view5);
        return out;
    }

    public void testPartitionTvFeedViewsWithTwoSegments() {
        String geraetebezeichner = "toBeFound";
        List<TvFeedView> in = createTvFeedViewList(geraetebezeichner);

        List<List<TvFeedView>> result = TvFeedEndpoint.partitionTvFeedViews(in, null);
        assertThat(result, hasSize(3));
        assertThat(result.get(0), hasSize(2));
        assertThat(result.get(0), contains(in.get(0), in.get(1)));
        assertThat(result.get(1), hasSize(1));
        assertThat(result.get(1), contains(in.get(2)));
        assertThat(result.get(2), hasSize(2));
        assertThat(result.get(2), contains(in.get(3), in.get(4)));
    }

    public void testPartitionTvFeedViewsWithDeviceIdentifier() {
        String geraetebezeichner = "toBeFound";
        List<TvFeedView> in = createTvFeedViewList(geraetebezeichner);

        List<List<TvFeedView>> result = TvFeedEndpoint.partitionTvFeedViews(in, geraetebezeichner);
        assertThat(result, hasSize(1));
        assertThat(result.get(0), hasSize(2));
        assertThat(result.get(0), contains(in.get(0), in.get(1)));
    }

    public void testPartitionTvFeedViewsUnordered() {
        // Dies testet die aktuelle Implementierung und nicht notwendigerweise die Spezifikation.
        TvFeedView view1 = createTvFeedView("findMe", 1L, 1L);
        TvFeedView view2 = createTvFeedView("notToBeFound", 3L, 3L);
        TvFeedView view3 = createTvFeedView("findMe", 1L, 2L);
        TvFeedView view4 = createTvFeedView("notToBeFound", 3L, 4L);
        final List<TvFeedView> tvFeedViews = Lists.newArrayList(view1, view2, view3, view4);

        final List<List<TvFeedView>> result = TvFeedEndpoint.partitionTvFeedViews(tvFeedViews, null);
        assertThat(result, hasSize(4));
    }
}


