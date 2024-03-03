/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2012 13:09:17
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Modellklasse zur Abbildung von MVS-Daten. Enthaelt entweder MVS-Enterprise oder MVS-Site Daten
 */
@XStreamAlias("MVS")
public class CPSMVSData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("ENTERPRISE")
    private CPSMVSEnterpriseData enterpriseData;

    @XStreamAlias("SITE")
    private CPSMVSSiteData siteData;

    public CPSMVSData(CPSMVSEnterpriseData epData) {
        this.enterpriseData = epData;
    }

    public CPSMVSData(CPSMVSSiteData siteData) {
        this.siteData = siteData;
    }

    public CPSMVSEnterpriseData getEnterpriseData() {
        return enterpriseData;
    }

    public CPSMVSSiteData getSiteData() {
        return siteData;
    }
}


