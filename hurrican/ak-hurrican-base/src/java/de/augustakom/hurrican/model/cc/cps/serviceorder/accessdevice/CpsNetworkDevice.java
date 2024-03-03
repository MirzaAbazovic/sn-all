/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by guiber on 02.05.2014.
 */
@XStreamAlias("NETWORK_DEVICE")
public class CpsNetworkDevice extends AbstractCpsDevice {
    @XStreamAlias("IP_GATEWAY")
    private String ipGateway;

    public String getIpGateway() {
        return ipGateway;
    }

    public void setIpGateway(String ipGateway) {
        this.ipGateway = ipGateway;
    }
}
