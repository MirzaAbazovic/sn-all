/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.10.13
 */
package de.mnet.common.route.helper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used for accessing all Camel route related properties.
 * <p/>
 * Use this class for {@code Value}s rather than placing the {@code Value}s directly within the routes, since this makes
 * unit testing more difficult.
 */
@Component
public class RouteConfigHelper {

    @Value("${atlas.out.component}")
    protected String atlasOutComponent;

    @Value("${atlas.errorhandlingservice.out.queue}")
    private String errorHandlingServiceOut;

    @Value("${atlas.customerservice.out.queue}")
    private String customerServiceOut;

    @Value("${atlas.in.component}")
    private String atlasInComponent;

    @Value("${atlas.carriernegotiationservice.v1.in.queue}")
    private String carrierNegotiationServiceV1In;

    @Value("${atlas.carriernegotiationservice.in.parameters}")
    private String carrierNegotiationServiceInParameters;

    @Value("${atlas.locationservice.out.queue}")
    private String locationServiceOut;

    @Value("${atlas.locationservice.timeout}")
    private String locationServiceTimeout;

    @Value("${mail.error.recipients}")
    private String mailErrorRecipients;

    @Value("${atlas.lineorderservice.v1.in.queue}")
    private String lineOrderServiceV1In;

    @Value("${atlas.lineorderservice.v2.in.queue}")
    private String lineOrderServiceV2In;

    @Value("${atlas.lineorderservice.in.parameters}")
    private String lineOrderServiceInParameters;

    public String getErrorHandlingServiceOut() {
        return errorHandlingServiceOut;
    }

    public String getCustomerServiceOut() {
        return customerServiceOut;
    }

    public String getLocationServiceOut() {
        return String.format(locationServiceOut, locationServiceTimeout);
    }

    public String getAtlasOutComponent() {
        return atlasOutComponent;
    }

    public String getAtlasInComponent() {
        return atlasInComponent;
    }

    public String getCarrierNegotiationServiceV1In() {
        return carrierNegotiationServiceV1In;
    }

    public String getCarrierNegotiationServiceInParameters() {
        return carrierNegotiationServiceInParameters;
    }

    public String getLineOrderServiceV1In() {
        return lineOrderServiceV1In;
    }

    public String getLineOrderServiceV2In() {
        return lineOrderServiceV2In;
    }

    public String getLineOrderServiceInParameters() {
        return lineOrderServiceInParameters;
    }

}
