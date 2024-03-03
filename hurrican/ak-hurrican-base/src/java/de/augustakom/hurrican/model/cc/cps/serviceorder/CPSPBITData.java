/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13 14:40
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
@XStreamAlias("PBIT")
public class CPSPBITData extends AbstractCPSServiceOrderDataModel {

    //DO NOT USE, for framework usage only
    public CPSPBITData() {
    }

    public CPSPBITData(final String service, final long limit) {
        this.service = service;
        this.limit = limit;
    }

    @XStreamAlias("SERVICE")
    public String service;
    @XStreamAlias("LIMIT")
    public long limit;

}
