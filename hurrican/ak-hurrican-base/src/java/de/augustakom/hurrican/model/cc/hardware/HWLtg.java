/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 12:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

/**
 * Modell, um eine Hardware-LTG abzubilden.
 *
 *
 */
public class HWLtg extends HWRack {


    private String ltgNumber = null;
    private String ltgType = null;
    private String ltgLDARP = null;
    private HWSwitch hwSwitch = null;
    private String mediaGatewayName = null;

    /**
     * @return ltgLDARP
     */
    public String getLtgLDARP() {
        return ltgLDARP;
    }

    /**
     * @param ltgLDARP Festzulegender ltgLDARP
     */
    public void setLtgLDARP(String ltgLDARP) {
        this.ltgLDARP = ltgLDARP;
    }

    /**
     * @return ltgNumber
     */
    public String getLtgNumber() {
        return ltgNumber;
    }

    /**
     * @param ltgNumber Festzulegender ltgNumber
     */
    public void setLtgNumber(String ltgNumber) {
        this.ltgNumber = ltgNumber;
    }

    /**
     * @return ltgType
     */
    public String getLtgType() {
        return ltgType;
    }

    /**
     * @param ltgType Festzulegender ltgType
     */
    public void setLtgType(String ltgType) {
        this.ltgType = ltgType;
    }

    /**
     * @return the hwSwitch
     */
    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    /**
     * @param hwSwitch the hwSwitch to set
     */
    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    /**
     * @return the mediaGatewayName
     */
    public String getMediaGatewayName() {
        return mediaGatewayName;
    }

    /**
     * @param mediaGatewayName the mediaGatewayName to set
     */
    public void setMediaGatewayName(String mediaGatewayName) {
        this.mediaGatewayName = mediaGatewayName;
    }

}


