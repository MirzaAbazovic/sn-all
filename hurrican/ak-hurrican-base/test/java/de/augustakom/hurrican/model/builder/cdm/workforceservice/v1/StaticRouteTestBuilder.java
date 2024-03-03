/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

/**
 *
 */
public class StaticRouteTestBuilder extends StaticRouteBuilder {

    public StaticRouteTestBuilder() {
        withNextHop("next hop");
        withTargetAddress("target address");
    }
}