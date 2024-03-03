/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.05.2012 10:26:15
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * CPS VLAN Data. Modelliert nach Vorgabe j:\TE\PD\SI\CPS\06_Software\Agenten\ADG_SOAP_SA\Spezifikation\Mnet_SOAP_Data.xsd
 */
@XStreamAlias("VLAN")
public class CPSVlanData extends AbstractCPSServiceOrderDataModel {
    @XStreamAlias("SERVICE")
    private String service = "";

    @XStreamAlias("TYPE")
    private String type = "";

    @XStreamAlias("CVLAN")
    private Integer cVlan = Integer.valueOf(0);

    @XStreamAlias("SVLAN")
    private Integer sVlan = Integer.valueOf(0);

    @XStreamAlias("SVLAN_BACKBONE")
    private Integer sVlanBackbone = Integer.valueOf(0);

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCvlan() {
        return cVlan;
    }

    public void setCvlan(Integer cVlan) {
        this.cVlan = cVlan;
    }

    public Integer getSvlan() {
        return sVlan;
    }

    public void setSvlan(Integer sVlan) {
        this.sVlan = sVlan;
    }

    public Integer getSvlanBackbone() {
        return sVlanBackbone;
    }

    public void setSvlanBackbone(Integer sVlanBackbone) {
        this.sVlanBackbone = sVlanBackbone;
    }

}


