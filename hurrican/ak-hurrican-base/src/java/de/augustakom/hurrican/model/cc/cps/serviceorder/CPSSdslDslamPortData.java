/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 14.09.2010 09:14:25
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;


/**
 * Modell-Klasse, in der DSLAM Port Informationen fuer SDSL Ports gesammelt werden.
 */
public class CPSSdslDslamPortData extends AbstractCPSServiceOrderDataModel {

    private String dslamPort;

    public CPSSdslDslamPortData(String dslamPort) {
        this.dslamPort = dslamPort;
    }

    public String getDslamPort() {
        return dslamPort;
    }

    public void setDslamPort(String dslamPort) {
        this.dslamPort = dslamPort;
    }

}


