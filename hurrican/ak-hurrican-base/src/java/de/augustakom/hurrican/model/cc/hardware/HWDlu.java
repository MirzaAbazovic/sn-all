/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.12.2008 12:23:17
 */
package de.augustakom.hurrican.model.cc.hardware;

/**
 * Modell, um eine Hardware-DLU abzubilden.
 *
 *
 */
public class HWDlu extends HWRack {

    private String dluNumber = null;
    private String dluType = null;
    private HWSwitch hwSwitch = null;
    private String mediaGatewayName = null;
    private String accessController = null;

    /**
     * If a new instance of {@link HWDlu} is created the type of the rack will be set to {@link HWRack#RACK_TYPE_DLU}.
     */
    public HWDlu() {
        setRackTyp(HWRack.RACK_TYPE_DLU);
    }

    public String getDluNumber() {
        return dluNumber;
    }

    public void setDluNumber(String dluNumber) {
        this.dluNumber = dluNumber;
    }

    public String getDluType() {
        return dluType;
    }

    public void setDluType(String dluType) {
        this.dluType = dluType;
    }

    public HWSwitch getHwSwitch() {
        return hwSwitch;
    }

    public void setHwSwitch(HWSwitch hwSwitch) {
        this.hwSwitch = hwSwitch;
    }

    public String getMediaGatewayName() {
        return mediaGatewayName;
    }

    public void setMediaGatewayName(String mediaGatewayName) {
        this.mediaGatewayName = mediaGatewayName;
    }

    public String getAccessController() {
        return accessController;
    }

    public void setAccessController(String accessController) {
        this.accessController = accessController;
    }

}


