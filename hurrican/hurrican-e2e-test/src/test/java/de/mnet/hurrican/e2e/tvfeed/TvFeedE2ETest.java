/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.11.2012 09:13:39
 */
package de.mnet.hurrican.e2e.tvfeed;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import javax.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurrican.tvfeed.GetTVFeedData4GeoIdsRequest;
import de.mnet.hurrican.tvfeed.GetTVFeedData4GeoIdsResponse;
import de.mnet.hurrican.tvfeed.GetTVFeedData4TechLocationsRequest;
import de.mnet.hurrican.tvfeed.GetTVFeedData4TechLocationsResponse;
import de.mnet.hurrican.tvfeed.TechLocationQueryType;

/**
 * Preuft fuer Gut und Fail-Fall die Funktion des Endpoints und die Serialisierbarkeit der Daten. Funktionale Test des
 * Endpoints werden im Service-Test TvFeedEndpointServiceTest bzw. TvFeedEndpointUnitTest geprueft.
 */
@Test(groups = E2E)
public class TvFeedE2ETest extends BaseHurricanE2ETest {

    @Resource(name = "tvFeedWebServiceTemplate")
    protected WebServiceTemplate webServiceTemplate;

    @SuppressWarnings("unchecked")
    private <T> T doRequest(Object request) {
        Object response = webServiceTemplate.marshalSendAndReceive(request);
        return (T) response;
    }

    // ----------------- Geo ID Requests ---------------------
    @Test(expectedExceptions = SoapFaultClientException.class)
    public void testRequestWithoutAnyGeoId() throws Exception {
        GetTVFeedData4GeoIdsRequest request = new GetTVFeedData4GeoIdsRequest();

        doRequest(request);
    }

    @Test(expectedExceptions = SoapFaultClientException.class)
    public void testRequestWithNullGeoId() throws Exception {
        GetTVFeedData4GeoIdsRequest request = new GetTVFeedData4GeoIdsRequest();
        request.getGeoIds().add(null);

        doRequest(request);
    }

    public void testRequestWithNegativeGeoId() throws Exception {
        Long geoId = Long.valueOf(-1);
        GetTVFeedData4GeoIdsRequest request = new GetTVFeedData4GeoIdsRequest();
        request.getGeoIds().add(geoId);

        GetTVFeedData4GeoIdsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getGeoIdResponses());
        assertTrue(response.getGeoIdResponses().size() == 1);
        assertTrue(geoId.equals(response.getGeoIdResponses().get(0).getQueriedGeoId()));
        assertTrue(response.getGeoIdResponses().get(0).getResultSets().size() == 0);
    }

    public void testRequestWithValidGeoId() throws Exception {
        Long geoId = Long.valueOf(1000000);
        GetTVFeedData4GeoIdsRequest request = new GetTVFeedData4GeoIdsRequest();
        request.getGeoIds().add(geoId);

        GetTVFeedData4GeoIdsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getGeoIdResponses());
        assertTrue(response.getGeoIdResponses().size() == 1);
        assertTrue(geoId.equals(response.getGeoIdResponses().get(0).getQueriedGeoId()));
    }

    public void testRequestWithTwoValidGeoId() throws Exception {
        Long geoId1 = Long.valueOf(1000000);
        Long geoId2 = Long.valueOf(1000001);
        GetTVFeedData4GeoIdsRequest request = new GetTVFeedData4GeoIdsRequest();
        request.getGeoIds().add(geoId1);
        request.getGeoIds().add(geoId2);

        GetTVFeedData4GeoIdsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getGeoIdResponses());
        assertTrue(response.getGeoIdResponses().size() == 2);
        // Die Geo ID Reihenfolge im Response entspricht der Reihenfolge im
        // Request
        assertTrue(geoId1.equals(response.getGeoIdResponses().get(0).getQueriedGeoId()));
        assertTrue(geoId2.equals(response.getGeoIdResponses().get(1).getQueriedGeoId()));
    }

    // ----------------- Technikkuerzel(Ortsteil) Requests ---------------------
    @Test(expectedExceptions = SoapFaultClientException.class)
    public void testRequestWithoutAnyTechLocations() throws Exception {
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();

        doRequest(request);
    }

    @Test(expectedExceptions = SoapFaultClientException.class)
    public void testRequestWithNullQuery() throws Exception {
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();
        request.getTechLocationQuerys().add(null);

        doRequest(request);
    }

    @Test(expectedExceptions = SoapFaultClientException.class)
    public void testRequestWithNullTechLocation() throws Exception {
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();
        TechLocationQueryType query = new TechLocationQueryType();
        request.getTechLocationQuerys().add(query);

        doRequest(request);
    }

    public void testRequestWithBlankTechLocation() throws Exception {
        String techLocation = "";
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();
        TechLocationQueryType query = new TechLocationQueryType();
        query.setTechLocationName(techLocation);
        request.getTechLocationQuerys().add(query);

        GetTVFeedData4TechLocationsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getTechLocationResponses());
        assertTrue(response.getTechLocationResponses().size() == 1);
        assertTrue(techLocation.equals(response.getTechLocationResponses().get(0).getQueriedTechLocation()
                .getTechLocationName()));
        assertTrue(response.getTechLocationResponses().get(0).getResultSets().size() == 0);
    }

    public void testRequestWithValidTechLocation() throws Exception {
        String techLocation = "NoExistingTechLocation";
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();
        TechLocationQueryType query = new TechLocationQueryType();
        query.setTechLocationName(techLocation);
        request.getTechLocationQuerys().add(query);

        GetTVFeedData4TechLocationsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getTechLocationResponses());
        assertTrue(response.getTechLocationResponses().size() == 1);
        assertTrue(techLocation.equals(response.getTechLocationResponses().get(0).getQueriedTechLocation()
                .getTechLocationName()));
    }

    public void testRequestWithTwoValidTechLocation() throws Exception {
        String techLocation1 = "NoExistingTechLocation1";
        String techLocation2 = "NoExistingTechLocation2";
        GetTVFeedData4TechLocationsRequest request = new GetTVFeedData4TechLocationsRequest();
        TechLocationQueryType query1 = new TechLocationQueryType();
        query1.setTechLocationName(techLocation1);
        TechLocationQueryType query2 = new TechLocationQueryType();
        query2.setTechLocationName(techLocation2);
        request.getTechLocationQuerys().add(query1);
        request.getTechLocationQuerys().add(query2);

        GetTVFeedData4TechLocationsResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getTechLocationResponses());
        assertTrue(response.getTechLocationResponses().size() == 2);
        // Die TechLocation Reihenfolge im Response entspricht der Reihenfolge
        // im Request
        assertTrue(techLocation1.equals(response.getTechLocationResponses().get(0).getQueriedTechLocation()
                .getTechLocationName()));
        assertTrue(techLocation2.equals(response.getTechLocationResponses().get(1).getQueriedTechLocation()
                .getTechLocationName()));
    }
}


