/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.01.2012 10:58:38
 */
package de.augustakom.hurrican.service.cc.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.augustakom.common.BaseTest;
import de.augustakom.hurrican.model.billing.BAuftrag;
import de.augustakom.hurrican.model.billing.BAuftragPos;
import de.augustakom.hurrican.model.billing.Leistung;
import de.augustakom.hurrican.model.billing.OE;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungView;
import de.augustakom.hurrican.model.billing.view.BAuftragLeistungViewBuilder;
import de.augustakom.hurrican.model.cc.GeoId2TechLocation;
import de.augustakom.hurrican.model.cc.GeoId2TechLocationBuilder;
import de.augustakom.hurrican.model.cc.HVTStandort;
import de.augustakom.hurrican.model.cc.HVTStandortBuilder;
import de.augustakom.hurrican.model.cc.Produkt;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationType;
import de.augustakom.hurrican.model.cc.Produkt2TechLocationTypeBuilder;
import de.augustakom.hurrican.model.cc.ProduktBuilder;
import de.augustakom.hurrican.model.cc.ProduktMapping;
import de.augustakom.hurrican.model.cc.ProduktMappingBuilder;
import de.augustakom.hurrican.model.cc.TechLeistung;
import de.augustakom.hurrican.model.cc.TechLeistungBuilder;
import de.augustakom.hurrican.model.shared.view.Billing2HurricanProdMapping;
import de.augustakom.hurrican.service.base.exceptions.FindException;
import de.augustakom.hurrican.service.billing.BillingAuftragService;
import de.augustakom.hurrican.service.cc.HVTService;

@Test(groups = BaseTest.UNIT)
public class ProduktServiceImplTest extends BaseTest {

    @InjectMocks
    @Spy
    private ProduktServiceImpl cut;

    @Mock
    private HVTService hvtService;

    @Mock
    BillingAuftragService billingAuftragService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DataProvider(name = "dataProviderBilling2HurricanMappings")
    protected Object[][] dataProviderBilling2HurricanMappings() {
        @SuppressWarnings("unchecked")
        List<List<Long>> combiMatch = Arrays.asList(Arrays.asList(Long.valueOf(20), Long.valueOf(21)));
        @SuppressWarnings("unchecked")
        List<List<Long>> noCombiMatch = Arrays.asList(Arrays.asList(Long.valueOf(30), Long.valueOf(40)));
        @SuppressWarnings("unchecked")
        List<List<Long>> noCombiMatch2 = Arrays.asList(Arrays.asList(Long.valueOf(30)),
                Arrays.asList(Long.valueOf(30), Long.valueOf(40)));

        // Aufbau der ProduktMappings (Key=ProduktMapping-Gruppe; Value=Liste mit den EXT_PROD_NOs)
        Map<Long, List<Long>> pmGroup2ExtProdNos = new HashMap<Long, List<Long>>();
        pmGroup2ExtProdNos.put(Long.valueOf(1), Arrays.asList(Long.valueOf(10), Long.valueOf(11)));
        pmGroup2ExtProdNos.put(Long.valueOf(2), Arrays.asList(Long.valueOf(20), Long.valueOf(21)));
        pmGroup2ExtProdNos.put(Long.valueOf(3), Arrays.asList(Long.valueOf(30)));
        pmGroup2ExtProdNos.put(Long.valueOf(4), Arrays.asList(Long.valueOf(40)));

        // @formatter:off
        return new Object[][] {
                // erwartet ein Match mit Kombination EXT_PROD__NOs 20+21
                { combiMatch, pmGroup2ExtProdNos, 1, 0, 1,
                    Arrays.asList(Long.valueOf(200))},
                // erwartet einzelne Matches fuer EXT_PROD__NOs 30+40, kein Kombi-Match!
                { noCombiMatch, pmGroup2ExtProdNos, 2, 2, 0,
                    Arrays.asList(Long.valueOf(300), Long.valueOf(400))},
                // erwartet einzelne Matches fuer EXT_PROD__NOs 30+30+40, kein Kombi-Match!
                { noCombiMatch2, pmGroup2ExtProdNos, 3, 2, 1,
                    Arrays.asList(Long.valueOf(300), Long.valueOf(300), Long.valueOf(400))},
        };
        // @formatter:on
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "dataProviderBilling2HurricanMappings")
    public void testCreateBilling2HurricanMappings(List<List<Long>> splittedExtProdNos,
            Map<Long, List<Long>> pmGroup2ExtProdNos, int expectedResultSize, int findProdTimes,
            int filterProdTimes, List<Long> expectedProdIds)
            throws FindException {
        List<Billing2HurricanProdMapping> result = new ArrayList<Billing2HurricanProdMapping>();

        doReturn(Long.valueOf(200)).when(cut).filterBestProduktId(any(Long.class), any(List.class));
        doReturn(Long.valueOf(300)).when(cut).filterBestProduktId(any(Long.class),
                eq(Arrays.asList(Long.valueOf(3))));
        doReturn(Long.valueOf(300)).when(cut).findProdId4MappingGroup(Long.valueOf(3));
        doReturn(Long.valueOf(400)).when(cut).findProdId4MappingGroup(Long.valueOf(4));

        cut.createBilling2HurricanMappings(new ArrayList<BAuftragLeistungView>(), result, pmGroup2ExtProdNos, null, splittedExtProdNos);

        verify(cut, times(findProdTimes)).findProdId4MappingGroup(any(Long.class));
        verify(cut, times(filterProdTimes)).filterBestProduktId(any(Long.class), any(List.class));

        assertEquals(result.size(), expectedResultSize);

        List<Long> resultProdIds = new ArrayList<Long>();
        for (Billing2HurricanProdMapping mapping : result) {
            resultProdIds.add(mapping.getProdId());
        }
        assertTrue(CollectionUtils.isEqualCollection(resultProdIds, expectedProdIds));
    }


    public void testFindProdId4MappingGroup() throws FindException {
        // @formatter:off
        ProduktMapping pm99_1 = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(-99))
            .withProdId(Long.valueOf(420))
            .withExtProdNo(Long.valueOf(-99))
            .setPersist(false)
            .build();
        ProduktMapping pm99_2 = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(-99))
            .withProdId(Long.valueOf(420))
            .withExtProdNo(Long.valueOf(-98))
            .setPersist(false)
            .build();
        ProduktMapping pm95 = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(-95))
            .withProdId(Long.valueOf(420))
            .withExtProdNo(Long.valueOf(-95))
            .setPersist(false)
            .build();
        // @formatter:on
        doReturn(Arrays.asList(pm99_1, pm99_2, pm95)).when(cut).findProduktMappings();

        Long prodIdResult = cut.findProdId4MappingGroup(pm99_1.getMappingGroup());
        assertEquals(prodIdResult, Long.valueOf(420));
    }


    public void testCreateBilling2HurricanMapping() {
        String oeName = "OE-Name";
        Long auftragNo = Long.valueOf(1);
        Long prodId = Long.valueOf(2);
        Integer bundleOrderNo = Integer.valueOf(3);
        List<BAuftragLeistungView> auftragLeistungViewsBilling = new ArrayList<BAuftragLeistungView>();
        // @formatter:off
        auftragLeistungViewsBilling.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(auftragNo)
            .withOeName(oeName)
            .withBundleOrderNo(bundleOrderNo)
            .setPersist(false)
            .build());
        auftragLeistungViewsBilling.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(Long.valueOf(100))
            .setPersist(false)
            .build());
        // @formatter:on

        Billing2HurricanProdMapping prodMapping = cut.createBilling2HurricanMapping(auftragNo, prodId, auftragLeistungViewsBilling);

        assertNotNull(prodMapping);
        assertEquals(prodMapping.getAuftragNoOrig(), auftragNo);
        assertEquals(prodMapping.getProdId(), prodId);
        assertEquals(prodMapping.getOeName(), oeName);
        assertEquals(prodMapping.getBundleOrderNo(), bundleOrderNo);
    }


    @DataProvider(name = "dataProviderSplitExtProdNOs2Lists")
    protected Object[][] dataProviderSplitExtProdNOs2Lists() {
        List<Long> extProdNos1 = Arrays.asList(Long.valueOf(100));

        List<Long> extProdNos2 = Arrays.asList(Long.valueOf(100), Long.valueOf(200));

        List<Long> extProdNos3 = Arrays.asList(Long.valueOf(100), Long.valueOf(100), Long.valueOf(200));
        List<Long> subList3First = Arrays.asList(Long.valueOf(100), Long.valueOf(200));
        List<Long> subList3Second = Arrays.asList(Long.valueOf(100));

        // @formatter:off
        return new Object[][] {
                { extProdNos1, extProdNos1, null },
                { extProdNos2, extProdNos2, null },
                { extProdNos3, subList3First, subList3Second },
        };
        // @formatter:on
    }


    @Test(dataProvider = "dataProviderSplitExtProdNOs2Lists")
    public void testSplitExtProdNOs2Lists(List<Long> extProdNOs, List<Long> valueSubListFirst, List<Long> valuesSubListSecond) {
        List<List<Long>> splitted = new ArrayList<List<Long>>();
        cut.splitExtProdNOs2Lists(extProdNOs, splitted);
        assertNotEmpty(splitted);
        assertEquals(splitted.size(), (valuesSubListSecond != null) ? 2 : 1);
        assertTrue(CollectionUtils.isEqualCollection(splitted.get(0), valueSubListFirst));
        if (valuesSubListSecond != null) {
            assertTrue(CollectionUtils.isEqualCollection(splitted.get(1), valuesSubListSecond));
        }
    }


    public void testCreateExternProdNoList() {
        List<BAuftragLeistungView> views = new ArrayList<BAuftragLeistungView>();
        // @formatter:off
        views.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(Long.valueOf(1))
            .withExternProduktNo(Long.valueOf(10))
            .setPersist(false)
            .build());
        views.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(Long.valueOf(1))
            .withExternProduktNo(Long.valueOf(11))
            .setPersist(false)
            .build());
        views.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(Long.valueOf(1))
            .withExternProduktNo(Long.valueOf(12))
            .setPersist(false)
            .build());
        views.add(new BAuftragLeistungViewBuilder()
            .withAuftragNoOrig(Long.valueOf(2))
            .withExternProduktNo(Long.valueOf(20))
            .setPersist(false)
            .build());
        // @formatter:on

        Map<Long, List<Long>> result = cut.createExternProdNoList(views);
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.containsKey(Long.valueOf(1)));
        assertEquals(result.get(Long.valueOf(1)).size(), 3);
        assertTrue(result.containsKey(Long.valueOf(2)));
        assertEquals(result.get(Long.valueOf(2)).size(), 1);
    }


    public void testGetHurrican2BillingProdMappings() throws FindException {
        // @formatter:off
        ProduktMapping produktMappingX = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(1))
            .withExtProdNo(Long.valueOf(10))
            .setPersist(false)
            .build();
        ProduktMapping produktMappingY1 = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(2))
            .withExtProdNo(Long.valueOf(20))
            .setPersist(false)
            .build();
        ProduktMapping produktMappingY2 = new ProduktMappingBuilder()
            .withMappingGroup(Long.valueOf(2))
            .withExtProdNo(Long.valueOf(21))
            .setPersist(false)
            .build();
        // @formatter:on

        doReturn(Arrays.asList(produktMappingX, produktMappingY1, produktMappingY2)).when(cut).findProduktMappings();

        Map<Long, List<Long>> result = cut.getHurrican2BillingProdMappings();
        assertNotNull(result);
        assertEquals(result.size(), 2);
        assertTrue(result.containsKey(Long.valueOf(1)));
        assertTrue(result.containsKey(Long.valueOf(2)));
        assertEquals(result.get(Long.valueOf(1)).size(), 1);
        assertEquals(result.get(Long.valueOf(2)).size(), 2);
    }


    public void testIsProductPossibleAtGeoId() throws FindException {
        HVTStandortBuilder hvtStandortBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_HVT).withRandomId().setPersist(false);
        HVTStandortBuilder fttbStandortBuilder = new HVTStandortBuilder()
                .withStandortTypRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).withRandomId().setPersist(false);

        GeoId2TechLocation geoId2TechLocationHvt = new GeoId2TechLocationBuilder()
                .withHvtStandortBuilder(hvtStandortBuilder).setPersist(false).build();
        GeoId2TechLocation geoId2TechLocationFttb = new GeoId2TechLocationBuilder()
                .withHvtStandortBuilder(fttbStandortBuilder).setPersist(false).build();
        List<GeoId2TechLocation> geoId2TechLocations = Arrays.asList(geoId2TechLocationHvt, geoId2TechLocationFttb);

        when(hvtService.findHVTStandort(hvtStandortBuilder.getId())).thenReturn(hvtStandortBuilder.get());
        when(hvtService.findHVTStandort(fttbStandortBuilder.getId())).thenReturn(fttbStandortBuilder.get());

        Produkt2TechLocationType prod2Fttb = new Produkt2TechLocationTypeBuilder()
                .withTechLocationTypeRefId(HVTStandort.HVT_STANDORT_TYP_FTTB).setPersist(false).build();
        List<Produkt2TechLocationType> prod2TechLocationTypes = Arrays.asList(prod2Fttb);

        Long prodId = Long.valueOf(512);
        doReturn(prod2TechLocationTypes).when(cut).findProdukt2TechLocationTypes(prodId);

        boolean result = cut.isProductPossibleAtGeoId(geoId2TechLocations, prodId);
        assertTrue(result);
    }

    @DataProvider
    Object[][] doProduktMapping4AuftragNoDataProvider() {
        final ProduktBuilder produktBuilder = new ProduktBuilder().withRandomId();
        final Billing2HurricanProdMapping prodMapping = new Billing2HurricanProdMapping();
        prodMapping.setProdId(produktBuilder.get().getProdId());

        return new Object[][] {
                { produktBuilder, ImmutableList.of(prodMapping) },    //only one mapping found
                { produktBuilder, ImmutableList.of(prodMapping, prodMapping) },   //same mapping multiple times
        };
    }

    @Test(dataProvider = "doProduktMapping4AuftragNoDataProvider")
    public void testDoProduktMapping4AuftragNoWithOneDistinctMappingFound(final ProduktBuilder produktBuilder,
            final List<Billing2HurricanProdMapping> productMappings) throws Exception {
        final Long auftragNo = 815L;
        final BAuftragLeistungView view = new BAuftragLeistungView(new BAuftrag(), new OE(), new BAuftragPos(), new Leistung());
        final Billing2HurricanProdMapping prodMapping = new Billing2HurricanProdMapping();
        prodMapping.setProdId(produktBuilder.get().getProdId());

        doReturn(billingAuftragService).when(cut).getBillingService(BillingAuftragService.class);
        doReturn(productMappings).when(cut).doProductMapping(Lists.newArrayList(view));
        doReturn(produktBuilder.get()).when(cut).findProdukt(produktBuilder.get().getProdId());
        when(billingAuftragService.findAuftragLeistungViews4Auftrag(auftragNo, false, true)).thenReturn(Lists.newArrayList(view));

        assertThat(cut.doProduktMapping4AuftragNo(auftragNo).getProdId(), equalTo(produktBuilder.get().getProdId()));
    }

    public void testDoProduktMapping4AuftragNoWithMultipleMappingsFound() throws Exception {
        final Long auftragNo = 815L;
        final BAuftragLeistungView view1 = new BAuftragLeistungView(new BAuftrag(), new OE(), new BAuftragPos(), new Leistung());
        view1.setExternProduktNo(Long.MAX_VALUE - 1);
        final Billing2HurricanProdMapping prodMapping1 = new Billing2HurricanProdMapping();
        prodMapping1.setProdId(1234L);
        final Billing2HurricanProdMapping prodMapping2 = new Billing2HurricanProdMapping();
        prodMapping2.setProdId(5678L);

        doReturn(billingAuftragService).when(cut).getBillingService(BillingAuftragService.class);
        doReturn(ImmutableList.of(prodMapping1, prodMapping2)).when(cut).doProductMapping(Lists.newArrayList(view1));
        when(billingAuftragService.findAuftragLeistungViews4Auftrag(auftragNo, false, true)).thenReturn(Lists.newArrayList(view1));

        assertNull(cut.doProduktMapping4AuftragNo(auftragNo));
    }


    @DataProvider(name = "generateProduktNameDP")
    public Object[][] generateProduktNameDP() {
        Produkt withDownstream = new ProduktBuilder().withProductNamePattern("ABC {DOWNSTREAM} XYZ").setPersist(false).build();
        Produkt withUpstream = new ProduktBuilder().withProductNamePattern("ABC {UPSTREAM} XYZ").setPersist(false).build();
        Produkt withVoip = new ProduktBuilder().withProductNamePattern("ABC {VOIP} XYZ").setPersist(false).build();
        Produkt withRealvariante = new ProduktBuilder().withProductNamePattern("ABC {REALVARIANTE} XYZ").setPersist(false).build();
        Produkt withCombined = new ProduktBuilder().withProductNamePattern("ABC {REALVARIANTE} {DOWNSTREAM} XYZ").setPersist(false).build();

        TechLeistung tlDownstream = new TechLeistungBuilder().withTyp(TechLeistung.TYP_DOWNSTREAM).withProdNameStr("down").setPersist(false).build();
        TechLeistung tlUpstream = new TechLeistungBuilder().withTyp(TechLeistung.TYP_UPSTREAM).withProdNameStr("up").setPersist(false).build();
        TechLeistung tlVoip = new TechLeistungBuilder().withTyp(TechLeistung.TYP_VOIP).withProdNameStr("voip").setPersist(false).build();
        TechLeistung tlRealvariante = new TechLeistungBuilder().withTyp(TechLeistung.TYP_REALVARIANTE).withProdNameStr("real").setPersist(false).build();
        TechLeistung tlOther = new TechLeistungBuilder().withTyp("undefined").withProdNameStr("undefined").setPersist(false).build();

        // @formatter:off
        return new Object[][] {
                { withDownstream    , Arrays.asList(tlDownstream, tlOther),         "ABC down XYZ" },
                { withDownstream    , Arrays.asList(tlOther),                       "ABC ? XYZ" },

                { withUpstream      , Arrays.asList(tlUpstream, tlOther),           "ABC up XYZ" },
                { withUpstream      , Arrays.asList(tlOther),                       "ABC ? XYZ" },

                { withVoip          , Arrays.asList(tlVoip, tlOther),               "ABC voip XYZ" },
                { withVoip          , Arrays.asList(tlOther),                       "ABC ? XYZ" },

                { withRealvariante  , Arrays.asList(tlRealvariante, tlOther),       "ABC real XYZ" },
                { withRealvariante  , Arrays.asList(tlOther),                       "ABC  XYZ" },

                { withCombined      , Arrays.asList(tlRealvariante, tlDownstream),  "ABC real down XYZ" },
                { withCombined      , Arrays.asList(tlDownstream),                  "ABC  down XYZ" },
                { withCombined      , Arrays.asList(tlOther),                       "ABC  ? XYZ" },

        };
        // @formatter:on
    }


    @Test(dataProvider = "generateProduktNameDP")
    public void testGenerateProduktName(Produkt produkt, List<TechLeistung> techLeistungen, String expected) {
        assertEquals(cut.generateProduktName(produkt, techLeistungen), expected);
    }

}
