/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2011 10:40:31
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.service.iface.IServiceObject;
import de.augustakom.common.tools.lang.DateTools;

/**
 * Entity-Builder fuer IPAddress Objekte
 */
@SuppressWarnings("unused")
public class IPAddressBuilder extends AbstractCCIDModelBuilder<IPAddressBuilder, IPAddress> implements IServiceObject {

    private AddressTypeEnum ipType = AddressTypeEnum.IPV6_full;
    private String address = "2001:0db8:a001::";
    private IPAddress prefixRef;
    private java.util.Date gueltigVon = DateTools.minusWorkDays(1);
    private java.util.Date gueltigBis = DateTools.getHurricanEndDate();
    private Long billingOrderNo;
    private Long netId;
    private String userW = "Service Test";
    private Date freigegeben;
    private Reference purpose;

    public IPAddressBuilder withAddressType(AddressTypeEnum addressType) {

        this.ipType = addressType;
        return this;
    }

    public IPAddressBuilder withAddress(String address) {
        this.address = address;
        return this;
    }

    public IPAddressBuilder withPrefixRef(IPAddress prefixRef) {
        this.prefixRef = prefixRef;
        return this;
    }

    public IPAddressBuilder withGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
        return this;
    }

    public IPAddressBuilder withGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
        return this;
    }

    public IPAddressBuilder withBillingOrderNumber(Long billingOrderNo) {
        this.billingOrderNo = billingOrderNo;
        return this;
    }

    public IPAddressBuilder withNetId(Long netId) {
        this.netId = netId;
        return this;
    }

    public IPAddressBuilder withFreigegeben(Date freigegeben) {
        this.freigegeben = freigegeben;
        return this;
    }

    public IPAddressBuilder withPurpose(Reference purpose) {
        this.purpose = purpose;
        return this;
    }

} // end
