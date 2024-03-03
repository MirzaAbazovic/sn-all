/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2012 16:43:05
 */
package de.mnet.hurrican.webservice.vento.availability;

import javax.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationRequest;
import de.augustakom.hurrican.model.cc.vento.availability.VentoGetAvailabilityInformationResponse;
import de.mnet.annotation.ObjectsAreNonnullByDefault;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationRequest;
import de.mnet.hurrican.vento.availability.GetAvailabilityInformationResponse;
import de.mnet.hurrican.webservice.vento.availability.service.AvailabilityEndpointService;

/**
 * SOAP 1.1 endpoint fuer Availability Service (Vento)
 */
@Endpoint
@ObjectsAreNonnullByDefault
public class AvailabilityEndpoint {
    private static final Logger LOGGER = Logger.getLogger(AvailabilityEndpoint.class);
    private static final String CPS_NAMESPACE = "http://vento.mnet.de/hurricanavailabilityproviderservice/";

    @Resource(name = "de.mnet.hurrican.webservice.vento.availability.service.AvailabilityEndpointService")
    private AvailabilityEndpointService availabilityEndpointService;

    @SuppressWarnings("UnusedDeclaration")
    @PayloadRoot(localPart = "getAvailabilityInformationRequest", namespace = CPS_NAMESPACE)
    @ResponsePayload
    public GetAvailabilityInformationResponse getAvailabilityInformation(
            @RequestPayload GetAvailabilityInformationRequest request) throws TechnicalException {
        LOGGER.debug("GetAvailabilityInformationRequestEndpoint called!");
        try {
            VentoGetAvailabilityInformationRequest ventoRequest = AvailabilityServiceFunctions
                    .toVentoGetAvailabilityInformationRequest.apply(request);
            //noinspection ConstantConditions
            LOGGER.debug(String.format(
                    "GetAvailabilityInformationRequestEndpoint: Geo ID %d, ONKZ %s, ASB %s, KVZ Nr %s",
                    ventoRequest.getGeoId(), ventoRequest.getOnkz(), ventoRequest.getAsb(), ventoRequest.getKvz()));
            VentoGetAvailabilityInformationResponse ventoResponse = availabilityEndpointService
                    .getAvailabilityInformation(ventoRequest);
            return AvailabilityServiceFunctions.toGetAvailabilityInformationResponse
                    .apply(ventoResponse);
        }
        catch (Exception e) {
            LOGGER.error("getAvailabilityInformation failed", e);
            throw new TechnicalException(e.getMessage());
        }
    }
}
