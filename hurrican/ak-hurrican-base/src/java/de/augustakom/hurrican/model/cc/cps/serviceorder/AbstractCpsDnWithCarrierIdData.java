/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.07.2014 07:55
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.billing.Rufnummer;

/**
 *
 */
public class AbstractCpsDnWithCarrierIdData extends AbstractCPSDNData {
    @XStreamAlias("CARRIER_ID")
    private String carrierId = null;

    protected void transferDNDataWithoutAdjustments(Rufnummer dn) {
        super.transferDNDataWithoutAdjustments(dn);
        setCarrierId(dn.getActCarrierPortKennung());
    }

    public String getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }
}
