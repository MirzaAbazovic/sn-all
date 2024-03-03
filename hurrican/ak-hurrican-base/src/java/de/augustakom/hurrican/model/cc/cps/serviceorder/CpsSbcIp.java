/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.03.14 09:09
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 */
@XStreamAlias("SBC-IP")
public class CpsSbcIp {

    public CpsSbcIp(final String address, final String addressType, final int netmask) {
        this.address = address;
        this.addressType = addressType;
        this.netmask = netmask;
    }

    @XStreamAlias("ADDRESS")
    public final String address;
    @XStreamAlias("ADDRESS_TYPE")
    public final String addressType;
    @XStreamAlias("NETMASK")
    public final int netmask;
    @XStreamAlias("LAYER2_PRIORITY")
    public static final int layer2Priority = 5;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CpsSbcIp cpsSbcIp = (CpsSbcIp) o;

        if (layer2Priority != cpsSbcIp.layer2Priority)
            return false;
        if (netmask != cpsSbcIp.netmask)
            return false;
        if (!address.equals(cpsSbcIp.address))
            return false;
        if (!addressType.equals(cpsSbcIp.addressType))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + addressType.hashCode();
        result = 31 * result + netmask;
        result = 31 * result + layer2Priority;
        return result;
    }

    public static class CpsSbcIpBuilder {
        private String address;
        private String addressType;
        private int netmask;

        public CpsSbcIpBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public CpsSbcIpBuilder withAddressType(String addressType) {
            this.addressType = addressType;
            return this;
        }

        public CpsSbcIpBuilder withNetmask(int netmask) {
            this.netmask = netmask;
            return this;
        }

        public CpsSbcIp build() {
            return new CpsSbcIp(address, addressType, netmask);
        }
    }
}
