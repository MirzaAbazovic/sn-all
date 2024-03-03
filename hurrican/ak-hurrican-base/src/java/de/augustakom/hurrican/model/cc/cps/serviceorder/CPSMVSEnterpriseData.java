/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.01.2012 13:12:14
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Modellklasse zur Abbildung von MVS-Enterprise-Daten fuer die CPS-Provisionierung.
 */
@XStreamAlias("ENTERPRISE")
public class CPSMVSEnterpriseData extends CPSMVSBaseData {

    @XStreamAlias("DOMAIN")
    private String domain;

    @XStreamAlias("LICENCES")
    private CPSMVSEnterpriseLicences licenses;

    public CPSMVSEnterpriseData(Long resellerId, CPSMVSAdminAccount adminAccount, String domain,
            CPSMVSEnterpriseLicences licenses) {
        this.resellerId = resellerId;
        this.adminAccount = adminAccount;
        this.domain = domain;
        this.licenses = licenses;
    }


    public String getDomain() {
        return domain;
    }

    public CPSMVSEnterpriseLicences getLicenses() {
        return licenses;
    }

}


