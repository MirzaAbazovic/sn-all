/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.12.2010 11:43:28
 */
package de.augustakom.hurrican.model.cc;

import de.augustakom.common.model.EntityBuilder;


/**
 * Entity-Builder fuer {@link IpRoute} Objekte.
 */
@SuppressWarnings("unused")
public class IpRouteBuilder extends EntityBuilder<IpRouteBuilder, IpRoute> {

    private AuftragBuilder auftragBuilder;
    private String ipAddress = "192.168.75.123";
    private Long prefixLength = Long.valueOf(27);
    private Long metrik = Long.valueOf(1);
    private String description = randomString(10);
    private Boolean deleted = Boolean.FALSE;
    private String userW = "user";
    private IPAddressBuilder ipAddressRefBuilder;

    @Override
    protected void beforeBuild() {
        if (auftragBuilder == null) {
            auftragBuilder = getBuilder(AuftragBuilder.class);
        }
    }

    public IpRouteBuilder withAuftragBuilder(AuftragBuilder auftragBuilder) {
        this.auftragBuilder = auftragBuilder;
        return this;
    }

    public IpRouteBuilder withIpAddressRefBuilder(IPAddressBuilder ipAddressRefBuilder) {
        this.ipAddressRefBuilder = ipAddressRefBuilder;
        return this;
    }

    public IpRouteBuilder withPrefixLength(Long prefixLength) {
        this.prefixLength = prefixLength;
        return this;
    }

}


