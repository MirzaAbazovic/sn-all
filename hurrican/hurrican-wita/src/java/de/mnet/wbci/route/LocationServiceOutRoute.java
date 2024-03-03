/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.11.13
 */
package de.mnet.wbci.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mnet.common.route.helper.RouteConfigHelper;
import de.mnet.wbci.route.processor.location.MarshalLocationServiceProcessor;
import de.mnet.wbci.route.processor.location.UnmarshalLocationServiceProcessor;

/**
 * Camel-Route, um den 'LocationService' vom Atlas aufzurufen. Ueber den LocationService ist es z.B. moeglich, nach
 * Gebaeuden zu suchen. <br/> Die Route wird momentan nur von WBCI verwendet und liegt daher unter dem 'wbci' Package,
 * aber denkbar waere es, die Route in ein 'common' Package zu verschieben, damit sie auch von anderen Komponenten (z.B.
 * Wita) genutzt werden kann.
 */
public class LocationServiceOutRoute extends SpringRouteBuilder implements WbciCamelConstants {
    @Autowired
    protected RouteConfigHelper routeConfigHelper;

    @Autowired
    private MarshalLocationServiceProcessor marshalLocationServiceProcessor;

    @Autowired
    private UnmarshalLocationServiceProcessor unmarshalLocationServiceProcessor;

    @Override
    public void configure() throws Exception {
        addLocationServiceOutRoute();
    }

    /**
     * Builds Camel out route for sending location service requests to Atlas ESB. Route takes care of marshal/unmarshal
     * operations and returns synchronous response results to caller.
     */
    private void addLocationServiceOutRoute() {
        from("direct:locationService")
                .transacted("ems.propagation_not_supported")
                .routeId(WBCI_LOCATION_OUT_ROUTE_ID)
                .process(marshalLocationServiceProcessor)
                .inOut(getLocationServiceOutUri())
                .process(unmarshalLocationServiceProcessor)
                .end();
    }

    private String getLocationServiceOutUri() {
        return String.format("%s:%s", routeConfigHelper.getAtlasOutComponent(), routeConfigHelper.getLocationServiceOut());
    }
}
