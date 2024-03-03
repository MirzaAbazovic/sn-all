/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2009 14:13:47
 */
package de.augustakom.hurrican.model.cc.hardware;

/**
 * Modell-Klasse, um eine MDU abzubilden. <br> (MDU = Muli-Dwelling-Units)
 *
 *
 */
public class HWMdu extends HWOltChild {

    private static final long serialVersionUID = -8868613601853413800L;
    /**
     * bisherige Huawei MDUs
     */
    public static String MDU_TYPE_MA5652G = "MA5652G";
    /**
     * neue Huawei MDUs
     */
    public static String MDU_TYPE_O881VP = "O-881V-P";
    /**
     * neue Alcatel MDUs
     */
    public static String MDU_TYPE_MA5616 = "MA5616";

    private Boolean catvOnline = null;
    private String mduType = null;
    private String ipAddress = null;

    public Boolean getCatvOnline() {
        return catvOnline;
    }

    public void setCatvOnline(Boolean catvOnline) {
        this.catvOnline = catvOnline;
    }

    public String getMduType() {
        return mduType;
    }

    public void setMduType(String mduType) {
        this.mduType = mduType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}


