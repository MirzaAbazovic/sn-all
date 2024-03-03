/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.04.2009 10:30:54
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Modell-Klasse fuer die Abbildung eines EWSD-Ports fuer die CPS-Provisionierung
 *
 *
 */
@XStreamAlias("PORT")
public class CPSPortData extends AbstractCPSServiceOrderDataModel {

    @XStreamAlias("SWITCH")
    private String switchName = null;
    @XStreamAlias("EQN")
    private String eqn = null;
    @XStreamAlias("V52_PORT")
    private String v5Port = null;
    @XStreamAlias("HIG1600")
    private String mgName = null;
    @XStreamAlias("ACCESS_CONTROLLER")
    private String accessController = null;

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public String getEqn() {
        return eqn;
    }

    public void setEqn(String eqn) {
        this.eqn = eqn;
    }

    public String getV5Port() {
        return v5Port;
    }

    public void setV5Port(String v5Port) {
        this.v5Port = v5Port;
    }

    public String getMgName() {
        return mgName;
    }

    public void setMgName(String mgName) {
        this.mgName = mgName;
    }


    public String getAccessController() {
        return accessController;
    }

    public void setAccessController(String accessController) {
        this.accessController = accessController;
    }

}


