/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.05.2009 14:13:47
 */
package de.augustakom.hurrican.model.cc.hardware;

/**
 * Modell-Klasse, um eine DPU abzubilden. <br> (DPU = Distribution Point Unit)
 */
public class HWDpu extends HWOltChild {
    private static final long serialVersionUID = -8868613601853413800L;
    /**
     * Huawei DPU MA5811S mit lizenzierten 4 Ports
     */
    public static String DPU_TYPE_MA5811S_AE04 = "MA5811S_AE04";
    /**
     * Huawei DPU MA5811S_AE08
     */
    public static String DPU_TYPE_MA5811S_AE08 = "MA5811S_AE08";
    /**
     * Huawei DPU MA5811S_DE16
     */
    public static String DPU_TYPE_MA5811S_DE16 = "MA5811S_DE16";

    private String dpuType = null;
    private Boolean reversePower = null;
    private String tddProfil;
    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDpuType() {
        return dpuType;
    }

    public void setDpuType(String dpuType) {
        this.dpuType = dpuType;
    }

    public Boolean getReversePower() {
        return reversePower;
    }

    public void setReversePower(Boolean reversePower) {
        this.reversePower = reversePower;
    }

    public String getTddProfil() {
        return tddProfil;
    }

    public void setTddProfil(String tddProfil) {
        this.tddProfil = tddProfil;
    }
}


