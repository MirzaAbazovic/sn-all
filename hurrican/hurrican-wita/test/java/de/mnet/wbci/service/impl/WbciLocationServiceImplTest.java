/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.mnet.wbci.service.impl;

import static de.augustakom.common.BaseTest.*;
import static de.augustakom.hurrican.service.location.CamelProxyLookupService.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import de.augustakom.hurrican.model.cc.Feature;
import de.augustakom.hurrican.service.cc.FeatureService;
import de.augustakom.hurrican.service.location.CamelProxyLookupService;
import de.augustakom.hurrican.service.location.LocationService;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildingsResponse;
import de.mnet.wbci.model.GeschaeftsfallTyp;
import de.mnet.wbci.model.Standort;
import de.mnet.wbci.model.WbciCdmVersion;
import de.mnet.wbci.model.builder.StandortTestBuilder;
import de.mnet.wbci.model.builder.cdm.location.v1.BuildingTestBuilder;

/**
 *
 */
@Test(groups = UNIT)
public class WbciLocationServiceImplTest {
    @InjectMocks
    @Spy
    private WbciLocationServiceImpl testling = new WbciLocationServiceImpl();

    @Mock
    private CamelProxyLookupService camelProxyLookupServiceMock;

    @Mock
    private LocationService locationServiceMock;

    @Mock
    private FeatureService featureService;

    private Standort standort = new StandortTestBuilder().buildValid(WbciCdmVersion.V1, GeschaeftsfallTyp.VA_KUE_MRN);

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(camelProxyLookupServiceMock.lookupCamelProxy(PROXY_LOCATION_SERVICE, LocationService.class)).thenReturn(locationServiceMock);
    }

    @Test
    public void testSearchBuildings() throws Exception {
        final SearchBuildingsResponse response = new SearchBuildingsResponse();
        response.getBuilding().add(new BuildingTestBuilder()
                .withId(1001L)
                .build());

        when(locationServiceMock.searchLocations(any(SearchBuildings.class))).thenAnswer(new Answer<SearchBuildingsResponse>() {
            @Override
            public SearchBuildingsResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                SearchBuildings searchBuildings = (SearchBuildings) invocationOnMock.getArguments()[0];
                assertSearchBuildings(searchBuildings, standort);
                Assert.assertNull(searchBuildings.getStrategy());

                return response;
            }
        });

        List<Long> geoIds = testling.getLocationGeoIds(standort);
        Assert.assertEquals(geoIds.size(), response.getBuilding().size());
        Assert.assertEquals(geoIds.get(0), new Long(response.getBuilding().get(0).getId()));

        verify(locationServiceMock).searchLocations(any(SearchBuildings.class));
        verify(featureService, times(0)).isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH);
    }

    @Test
    public void testSearchBuildingsPhoneticSearchDisabled() throws Exception {
        final SearchBuildingsResponse response = new SearchBuildingsResponse();

        when(locationServiceMock.searchLocations(any(SearchBuildings.class))).thenAnswer(new Answer<SearchBuildingsResponse>() {
            @Override
            public SearchBuildingsResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                SearchBuildings searchBuildings = (SearchBuildings) invocationOnMock.getArguments()[0];
                assertSearchBuildings(searchBuildings, standort);
                Assert.assertNull(searchBuildings.getStrategy());

                return response;
            }
        });

        when(featureService.isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH)).thenReturn(false);

        List<Long> geoIds = testling.getLocationGeoIds(standort);
        Assert.assertEquals(geoIds.size(), response.getBuilding().size());

        verify(locationServiceMock).searchLocations(any(SearchBuildings.class));
        verify(featureService).isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH);
    }

    @Test
    public void testSearchBuildingsTimeOutException() throws Exception {
        doThrow(new ExchangeTimedOutException(mock(Exchange.class), 1, "TEST-TIME-OUT")).when(locationServiceMock)
                .searchLocations(any(SearchBuildings.class));

        List<Long> geoIds = testling.getLocationGeoIds(standort);
        Assert.assertEquals(geoIds.size(), 0);
        Assert.assertTrue(geoIds.isEmpty());
        verify(locationServiceMock).searchLocations(any(SearchBuildings.class));
        verify(featureService, times(0)).isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH);
    }

    @Test
    public void testSearchBuildingsPhonetic() throws Exception {
        final SearchBuildingsResponse response = new SearchBuildingsResponse();
        response.getBuilding().add(new BuildingTestBuilder().withId(100L).build());
        SearchBuildings exactQuery = mock(SearchBuildings.class);
        doReturn(exactQuery).when(testling).getExactMatchBuildingQuery(standort);
        SearchBuildings phonaticQuery = mock(SearchBuildings.class);
        doReturn(phonaticQuery).when(testling).getPhoneticBuildingQuery(standort);

        // first exact match query - return empty results in order to force phonetic search
        when(locationServiceMock.searchLocations(exactQuery)).thenReturn(new SearchBuildingsResponse());
        when(locationServiceMock.searchLocations(phonaticQuery)).thenReturn(response);

        when(featureService.isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH)).thenReturn(true);

        List<Long> geoIds = testling.getLocationGeoIds(standort);
        Assert.assertEquals(geoIds.size(), response.getBuilding().size());

        verify(locationServiceMock, times(2)).searchLocations(any(SearchBuildings.class));
        verify(featureService).isFeatureOnline(Feature.FeatureName.WBCI_PHONETIC_SEARCH);
    }

    private void assertSearchBuildings(SearchBuildings searchBuildings, Standort standort) {
        if (StringUtils.hasText(searchBuildings.getStrategy())) {
            Assert.assertNull(searchBuildings.getZipCode());
            Assert.assertNull(searchBuildings.getBuilding().getHouseNumberExtension());
        }
        else {
            Assert.assertEquals(searchBuildings.getZipCode().getZipCode(), standort.getPostleitzahl());
            Assert.assertEquals(searchBuildings.getBuilding().getHouseNumberExtension(), standort.getStrasse().getHausnummernZusatz());
        }

        Assert.assertNotNull(searchBuildings.getCity());
        Assert.assertEquals(searchBuildings.getCity().getName(), standort.getOrt());
        Assert.assertNotNull(searchBuildings.getStreetSection());
        Assert.assertEquals(searchBuildings.getStreetSection().getName(), standort.getStrasse().getStrassenname());
        Assert.assertEquals(searchBuildings.getBuilding().getHouseNumber(), standort.getStrasse().getHausnummer());
    }

}
