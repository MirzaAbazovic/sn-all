/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.11.2014
 */
package de.mnet.hurrican.e2e.tvprovider;

import static de.augustakom.common.BaseTest.*;
import static org.testng.Assert.*;

import javax.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.testng.annotations.Test;

import de.mnet.hurrican.e2e.common.BaseHurricanE2ETest;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationRequest;
import de.mnet.hurrican.tvprovider.GetTvAvailabilityInformationResponse;

/**
 * Preuft die Funktion des Endpoints und die Serialisierbarkeit der Daten. Funktionale Test des Endpoints werden im
 * TvProviderEndpointTest geprueft.
 */
@Test(groups = E2E)
public class TvProviderE2ETest extends BaseHurricanE2ETest {

    @Resource(name = "tvProviderWebServiceTemplate")
    protected WebServiceTemplate webServiceTemplate;

    @SuppressWarnings("unchecked")
    private <T> T doRequest(Object request) {
        return (T) webServiceTemplate.marshalSendAndReceive(request);
    }

    public void testRequestWithValidGeoId() throws Exception {
        Long geoId = Long.valueOf(1000000);
        GetTvAvailabilityInformationRequest request = new GetTvAvailabilityInformationRequest();
        request.setGeoId(geoId);

        GetTvAvailabilityInformationResponse response = doRequest(request);
        assertNotNull(response);
        assertNotNull(response.getTvAvailabilityInformation());
        assertTrue(response.getTvAvailabilityInformation().isEmpty());
    }

}
