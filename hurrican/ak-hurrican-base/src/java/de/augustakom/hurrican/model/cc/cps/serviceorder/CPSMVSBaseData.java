/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.01.2012 08:46:57
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
public abstract class CPSMVSBaseData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("RESELLER_ID")
    protected Long resellerId;

    @XStreamAlias("ADMIN_ACCOUNT")
    protected CPSMVSAdminAccount adminAccount;

    public Long getResellerId() {
        return resellerId;
    }

    public CPSMVSAdminAccount getAdminAccount() {
        return adminAccount;
    }

}


