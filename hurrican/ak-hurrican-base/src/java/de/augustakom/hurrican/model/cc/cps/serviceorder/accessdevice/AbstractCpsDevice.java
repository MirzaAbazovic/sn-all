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
public class AbstractCpsDevice extends AbstractCPSServiceOrderDataModel {
    @XStreamAlias("TYPE")
    private String type;

    @XStreamAlias("HARDWARE_MODEL")
    private String hardwareModel;

    @XStreamAlias("MANUFACTURER")
    private String manufacturer;

    /**
     * eindeutiger Identifier der sich NIE aendert
     */
    @XStreamAlias("NAME")
    private String name;

    @XStreamAlias("PORT")
    private String port;
    /**
     * Uebertragung zum EndpointDevice.
     */
    @XStreamAlias("PORT_TYPE")
    private String portType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHardwareModel() {
        return hardwareModel;
    }

    public void setHardwareModel(String hardwareModel) {
        this.hardwareModel = hardwareModel;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }
}
