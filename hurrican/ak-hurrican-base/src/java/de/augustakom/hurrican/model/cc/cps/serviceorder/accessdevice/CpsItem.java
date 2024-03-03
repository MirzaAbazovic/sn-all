/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.05.2014
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder.accessdevice;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.augustakom.hurrican.model.cc.cps.serviceorder.AbstractCPSServiceOrderDataModel;

/**
 * Created by guiber on 02.05.2014.
 */
@XStreamAlias("ITEM")
public class CpsItem extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("DEACTIVATE")
    private int deactivate;

    @XStreamAlias("NETWORK_DEVICE")
    private CpsNetworkDevice networkDevice;

    @XStreamAlias("ENDPOINT_DEVICE")
    private CpsEndpointDevice endpointDevice;

    @XStreamAlias("LAYER2_CONFIG")
    private CpsLayer2Config layer2Config;

    public int getDeactivate() {
        return deactivate;
    }

    public void setDeactivate(int deactivate) {
        this.deactivate = deactivate;
    }

    public CpsNetworkDevice getNetworkDevice() {
        return networkDevice;
    }

    public void setNetworkDevice(CpsNetworkDevice networkDevice) {
        this.networkDevice = networkDevice;
    }

    public CpsEndpointDevice getEndpointDevice() {
        return endpointDevice;
    }

    public void setEndpointDevice(CpsEndpointDevice endpointDevice) {
        this.endpointDevice = endpointDevice;
    }

    public CpsLayer2Config getLayer2Config() {
        return layer2Config;
    }

    public void setLayer2Config(CpsLayer2Config layer2Config) {
        this.layer2Config = layer2Config;
    }

}