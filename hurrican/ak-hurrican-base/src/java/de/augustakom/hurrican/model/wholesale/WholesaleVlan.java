/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.03.2012 15:28:07
 */
package de.augustakom.hurrican.model.wholesale;


/**
 * DTO fuer Wholesale Vlan
 */
public class WholesaleVlan {

    private String service;
    private String type;
    private int cvlan;
    private int svlan;
    private int svlanBackbone;
    private String ips;

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

    public int getCvlan() {
        return cvlan;
    }

    public void setCvlan(int cvlan) {
        this.cvlan = cvlan;
    }

    public int getSvlan() {
        return svlan;
    }

    public void setSvlan(int svlan) {
        this.svlan = svlan;
    }

    public int getSvlanBackbone() {
        return svlanBackbone;
    }

    public void setSvlanBackbone(int svlanBackbone) {
        this.svlanBackbone = svlanBackbone;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

}


