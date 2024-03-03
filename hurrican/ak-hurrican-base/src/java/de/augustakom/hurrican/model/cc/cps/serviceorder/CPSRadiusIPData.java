/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2009 10:56:59
 */
package de.augustakom.hurrican.model.cc.cps.serviceorder;


/**
 * Modell-Klasse fuer die Abbildung von (fixen) IP-Adressen.
 *
 *
 */
public class CPSRadiusIPData extends AbstractCPSServiceOrderDataModel {

    private String ipAddress = null;

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}


