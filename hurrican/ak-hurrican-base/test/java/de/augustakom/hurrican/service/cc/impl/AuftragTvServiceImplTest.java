/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.11.2014
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.dao.cc.AuftragTvDAO;
import de.augustakom.hurrican.model.cc.AuftragDaten;
import de.augustakom.hurrican.model.cc.AuftragStatus;
import de.augustakom.hurrican.model.cc.Endstelle;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.ProduktGruppe;
import de.augustakom.hurrican.model.shared.view.TvFeedView;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.cc.CCAuftragService;
import de.augustakom.hurrican.service.cc.EndstellenService;
import de.augustakom.hurrican.service.cc.ProduktService;

@Test(groups = BaseTest.UNIT)
public class AuftragTvServiceImplTest {
    @InjectMocks
    private AuftragTvServiceImpl testling;

    @Mock
    protected AuftragTvDAO auftragTvDAOMock;

    @Mock
    protected CCAuftragService ccAuftragServiceMock;

    @Mock
    protected EndstellenService endstellenServiceMock;

    @Mock
    protected ProduktService produktServiceMock;

    @Mock
    protected List<AuftragDaten> versorgenderAuftraegeMock;

    @BeforeMethod
    public void setupMockListener() throws Exception {
        testling = new AuftragTvServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFindTvFeedForGeoId() throws Exception {
        Long geoId = 100L;
        long endstelleId = 1L;
        long auftragId = 2L;
        long bundledAuftragId = 3L;
        Integer buendelNr = 123;
        String buendelNrHerkunft = "123";
        Endstelle endstelleMock2 = getEndstelleMock(endstelleId, true);

        AuftragDaten auftragDatenMock = setAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true, buendelNr, buendelNrHerkunft);
        AuftragDaten bundledAuftragDatenMock = getAuftragDatenMock(bundledAuftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true, buendelNr, buendelNrHerkunft);
        TvFeedView tvFeedViewMock = Mockito.mock(TvFeedView.class);

        when(ccAuftragServiceMock.findAuftragDatenByGeoIdProduktIds(eq(geoId), (Long[]) anyVararg())).thenReturn(Arrays.asList(auftragDatenMock));
        when(ccAuftragServiceMock.findAuftragDaten4BuendelTx(buendelNr, buendelNrHerkunft)).thenReturn(Arrays.asList(bundledAuftragDatenMock));
        when(endstellenServiceMock.findEndstelle4Auftrag(bundledAuftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleMock2);
        when(auftragTvDAOMock.findTvFeed4Auftraege(eq(Arrays.asList(bundledAuftragId)), any())).thenReturn(Arrays.asList(tvFeedViewMock));

        Map<Long, List<TvFeedView>> tvFeed4GeoIdViews = testling.findTvFeed4GeoIdViews(Arrays.asList(geoId));

        Assert.assertEquals(tvFeed4GeoIdViews.size(), 1);
        List<TvFeedView> tvFeeds4GeoId = tvFeed4GeoIdViews.get(geoId);
        Assert.assertEquals(tvFeeds4GeoId.size(), 1);
        Assert.assertEquals(tvFeeds4GeoId.get(0), tvFeedViewMock);
    }

    @Test
    public void shouldNotFindTvFeedForGeoId4InactiveAuftraege() throws Exception {
        Long geoId1 = 100L;
        Long geoId2 = 200L;
        long endstelleId = 1L;
        long auftragId = 2L;
        Endstelle endstelleMock = getEndstelleMock(endstelleId, false);
        AuftragDaten auftragDatenMock = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, false);

        when(endstellenServiceMock.findEndstellenByGeoId(geoId1)).thenReturn(Collections.singletonList(endstelleMock));
        when(endstellenServiceMock.findEndstellenByGeoId(geoId2)).thenReturn(Collections.singletonList(endstelleMock));
        when(ccAuftragServiceMock.findAuftragDatenByEndstelleTx(endstelleId)).thenReturn(auftragDatenMock);
        when(auftragTvDAOMock.findTvFeed4Auftraege(Collections.<Long>emptyList(), Collections.<Integer>emptyList())).thenReturn(Collections.<TvFeedView>emptyList());

        Map<Long, List<TvFeedView>> tvFeed4GeoIdViews = testling.findTvFeed4GeoIdViews(Arrays.asList(geoId1, geoId2));
        Assert.assertEquals(tvFeed4GeoIdViews.size(), 2);
        Assert.assertEquals(tvFeed4GeoIdViews.get(geoId1).size(), 0);
        Assert.assertEquals(tvFeed4GeoIdViews.get(geoId2).size(), 0);

        verify(auftragTvDAOMock, times(2)).findTvFeed4Auftraege(any(), any());
    }

    private Endstelle getEndstelleMock(Long id, boolean hasRangierung) {
        Endstelle endstelleMock = Mockito.mock(Endstelle.class);
        when(endstelleMock.getId()).thenReturn(id);
        when(endstelleMock.hasRangierung()).thenReturn(hasRangierung);
        return endstelleMock;
    }

    private AuftragDaten getAuftragDatenMock(Long id, Long prodId, boolean isAuftragActive, Integer buendelNr, String buendelNrHerkunft) {
        AuftragDaten auftragDatenMock = Mockito.mock(AuftragDaten.class);
        when(auftragDatenMock.getProdId()).thenReturn(prodId);
        when(auftragDatenMock.getAuftragId()).thenReturn(id);
        when(auftragDatenMock.getStatusId()).thenReturn(isAuftragActive ? AuftragStatus.IN_BETRIEB : AuftragStatus.AUFTRAG_GEKUENDIGT);
        when(auftragDatenMock.getBuendelNr()).thenReturn(buendelNr);
        when(auftragDatenMock.getBuendelNrHerkunft()).thenReturn(buendelNrHerkunft);
        return auftragDatenMock;
    }

    private AuftragDaten setAuftragDatenMock(Long id, Long prodId, boolean isAuftragActive, Integer buendelNr, String buendelNrHerkunft) {
        AuftragDaten auftragDatenMock = new AuftragDaten();
        auftragDatenMock.setProdId(prodId);
        auftragDatenMock.setAuftragId(id);
        auftragDatenMock.setStatusId(isAuftragActive ? AuftragStatus.IN_BETRIEB : AuftragStatus.AUFTRAG_GEKUENDIGT);
        auftragDatenMock.setBuendelNr(buendelNr);
        auftragDatenMock.setBuendelNrHerkunft(buendelNrHerkunft);
        return auftragDatenMock;
    }

    private AuftragDaten getAuftragDatenMock(Long id, Long prodId, boolean isAuftragActive) {
        return getAuftragDatenMock(id, prodId, isAuftragActive, null, null);
    }

    @DataProvider
    public Object[][] isVersorgenderAuftragDP() {
        long auftragId = 1L;
        AuftragDaten auftrag = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, false);
        Endstelle endstelleWithRangierung = getEndstelleMock(2L, true);
        Endstelle endstelleWithoutRangierung = getEndstelleMock(3L, false);
        //@formatter:off
        return new Object[][] {
                { auftragId, auftrag, endstelleWithRangierung, true},
                { auftragId, auftrag, endstelleWithoutRangierung, false},
                { auftragId, auftrag, null, false},
        };
        //@formatter:on
    }

    @Test(dataProvider = "isVersorgenderAuftragDP")
    public void testIsVersorgenderAuftrag(Long auftragId, AuftragDaten auftragDaten, Endstelle endstelle, boolean expectedResult) throws FindException {
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);
        Assert.assertEquals(testling.isVersorgenderAuftrag(auftragDaten), expectedResult);
    }

    @Test(dataProvider = "isVersorgenderAuftragDP")
    public void testAddVersorgenderAuftraege(Long auftragId, AuftragDaten auftragDaten, Endstelle endstelle, boolean expectedResult) throws FindException {
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelle);

        testling.addVersorgenderAuftraege(versorgenderAuftraegeMock, Collections.singletonList(auftragDaten));

        if (expectedResult) {
            verify(versorgenderAuftraegeMock).add(auftragDaten);
        }
        else {
            verify(versorgenderAuftraegeMock, never()).add(any(AuftragDaten.class));
        }
    }

    @DataProvider
    public Object[][] findAssociatedActiveAuftraegeDP() {
        long auftragId = 1L;
        AuftragDaten auftragDatenWithBundle = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true, 2, "abc");
        AuftragDaten auftragDatenWithoutBundle = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true);

        AuftragDaten bundledAuftragActive = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true);
        AuftragDaten bundledAuftragInactive = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, false);

        //@formatter:off
        return new Object[][] {
                // bundleNr empty -> should return auftrag
                { auftragDatenWithoutBundle, Collections.<AuftragDaten>emptyList(), Collections.singletonList(auftragDatenWithoutBundle)},
                // no matching bundled auftrage found -> should return empty list
                { auftragDatenWithBundle, Collections.<AuftragDaten>emptyList(), Collections.<AuftragDaten>emptyList()},
                // multiple entries, some of which active -> should only return active auftrage
                { auftragDatenWithBundle, new ArrayList(Arrays.asList(bundledAuftragActive, bundledAuftragInactive)), Arrays.asList(bundledAuftragActive)},
        };
        //@formatter:on
    }


    @Test(dataProvider = "findAssociatedActiveAuftraegeDP")
    public void testFindAssociatedActiveAuftraege(AuftragDaten auftragDaten, List<AuftragDaten> bundledAuftragDaten, List<AuftragDaten> expectedResult) throws FindException {
        when(ccAuftragServiceMock.findAuftragDaten4BuendelTx(auftragDaten.getBuendelNr(), auftragDaten.getBuendelNrHerkunft())).thenReturn(bundledAuftragDaten);

        List<AuftragDaten> associatedActiveAuftraege = testling.findAssociatedActiveAuftraege(auftragDaten);

        Assert.assertEquals(associatedActiveAuftraege.size(), expectedResult.size());
        for (int i = 0; i < associatedActiveAuftraege.size(); i++) {
            AuftragDaten associatedAuftrag = associatedActiveAuftraege.get(i);
            Assert.assertEquals(associatedAuftrag, expectedResult.get(i));
        }
    }

    @Test
    public void shouldFindTvFeedForLocationName() throws Exception {
        Long auftragId = 1L;
        long endstelleId = 1L;
        String tvProduktGruppe = "TV";
        String techLocationName = "techLocation1";
        ProduktGruppe produktGruppeMock = Mockito.mock(ProduktGruppe.class);
        Endstelle endstelleMock = getEndstelleMock(endstelleId, false);
        AuftragDaten auftragDaten = getAuftragDatenMock(auftragId, Produkt.PROD_ID_TV_SIGNALLIEFERUNG, true);
        TvFeedView tvFeedViewMock = Mockito.mock(TvFeedView.class);

        when(produktGruppeMock.getProduktGruppe()).thenReturn(tvProduktGruppe);
        when(produktServiceMock.findProduktGruppe(ProduktGruppe.TV)).thenReturn(produktGruppeMock);
        when(ccAuftragServiceMock.findAktiveAuftragDatenByOrtsteilAndProduktGroup(techLocationName, tvProduktGruppe)).thenReturn(Collections.singletonList(auftragDaten));
        when(endstellenServiceMock.findEndstelle4Auftrag(auftragId, Endstelle.ENDSTELLEN_TYP_B)).thenReturn(endstelleMock);
        when(endstelleMock.hasRangierung()).thenReturn(true);
        when(auftragTvDAOMock.findTvFeed4Auftraege(Arrays.asList(auftragId, auftragId), Collections.<Integer>emptyList())).thenReturn(Arrays.asList(tvFeedViewMock));

        Map<String, List<TvFeedView>> tvFeed4TechLocationNameViews = testling.findTvFeed4TechLocationNameViews(Collections.singletonList(techLocationName));

        Assert.assertEquals(tvFeed4TechLocationNameViews.size(), 1);
        List<TvFeedView> tvFeedViews = tvFeed4TechLocationNameViews.get(techLocationName);
        Assert.assertEquals(tvFeedViews.size(), 1);
        Assert.assertEquals(tvFeedViews.get(0), tvFeedViewMock);
    }

    @Test
    public void shouldNotFindTvFeedForUnknownLocationName() throws Exception {
        String tvProduktGruppe = "TV";
        String techLocationName = "techLocation1";
        ProduktGruppe produktGruppeMock = Mockito.mock(ProduktGruppe.class);

        when(produktGruppeMock.getProduktGruppe()).thenReturn(tvProduktGruppe);
        when(produktServiceMock.findProduktGruppe(ProduktGruppe.TV)).thenReturn(produktGruppeMock);
        when(ccAuftragServiceMock.findAktiveAuftragDatenByOrtsteilAndProduktGroup(techLocationName, tvProduktGruppe)).thenReturn(Collections.<AuftragDaten>emptyList());
        when(auftragTvDAOMock.findTvFeed4Auftraege(Collections.<Long>emptyList(), Collections.<Integer>emptyList())).thenReturn(Collections.<TvFeedView>emptyList());

        Map<String, List<TvFeedView>> tvFeed4TechLocationNameViews = testling.findTvFeed4TechLocationNameViews(Collections.singletonList(techLocationName));

        Assert.assertEquals(tvFeed4TechLocationNameViews.size(), 1);
        List<TvFeedView> tvFeedViews = tvFeed4TechLocationNameViews.get(techLocationName);
        Assert.assertEquals(tvFeedViews.size(), 0);
    }

}
