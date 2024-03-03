/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.13
 */
package de.mnet.wbci.route;

import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.wbci.route.processor.carriernegotiation.in.ConvertCdmV1ToWbciFormatProcessor;

/**
 * WbciInRoute fuer CDM v1
 */
public class CarrierNegotiationInV1Route extends AbstractCarrierNegotiationInRoute {

    @Autowired
    private ConvertCdmV1ToWbciFormatProcessor convertCdmToWbciMeldungProcessor;

    @Override
    protected Processor getCdmToWbciMeldungProcessor() {
        return convertCdmToWbciMeldungProcessor;
    }

    @Override
    protected String getWbciInRouteId() {
        return WbciCamelConstants.WBCI_CARRIER_NEGOTIATION_IN_V1_ROUTE_ID;
    }

    @Override
    protected String getWbciInBizRouteId() {
        return WbciCamelConstants.WBCI_IN_BIZ_V1_ROUTE_ID;
    }

    @Override
    protected String getWbciInEmsTxRouteId() {
        return WbciCamelConstants.WBCI_IN_EMS_TX_V1_ROUTE_ID;
    }

    @Override
    protected String getInComponent() {
        return routeConfigHelper.getAtlasInComponent();
    }

    @Override
    protected String getCarrierNegotiationServiceIn() {
        return routeConfigHelper.getCarrierNegotiationServiceV1In();
    }

    @Override
    protected String getInParameters() {
        return routeConfigHelper.getCarrierNegotiationServiceInParameters();
    }

}
