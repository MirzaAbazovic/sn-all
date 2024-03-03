/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2014
 */
package de.mnet.wita.route;

import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mnet.wita.route.processor.lineorder.in.ConvertCdmV2ToWitaFormatProcessor;

/**
 * LineOrder-In-Route for CDM v2
 */
@Component
public class LineOrderInV2Route extends AbstractLineOrderInRoute {

    @Autowired
    private ConvertCdmV2ToWitaFormatProcessor convertCdmV2ToWitaFormatProcessor;

    @Override
    protected String getLineOrderInBizRouteId() {
        return WitaCamelConstants.WITA_LINE_ORDER_IN_BIZ_V2_ROUTE_ID;
    }

    @Override
    protected String getLineOrderInEmsTxRouteId() {
        return WitaCamelConstants.WITA_LINE_ORDER_IN_EMS_TX_V2_ROUTE_ID;
    }

    @Override
    protected Processor getCdmToLineOrderMeldungProcessor() {
        return convertCdmV2ToWitaFormatProcessor;
    }

    @Override
    protected String getLineOrderInRouteId() {
        return WitaCamelConstants.WITA_LINE_ORDER_IN_V2_ROUTE_ID;
    }

    @Override
    protected String getInComponent() {
        return routeConfigHelper.getAtlasInComponent();
    }

    @Override
    protected String getLineOrderIn() {
        return routeConfigHelper.getLineOrderServiceV2In();
    }

    @Override
    protected String getInParameters() {
        return routeConfigHelper.getLineOrderServiceInParameters();
    }

}
