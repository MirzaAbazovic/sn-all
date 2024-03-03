/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.10.2009 13:09:50
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung der Zusatztabelle DEVICE_FRITZBOX.
 *
 *
 */
public class DeviceFritzBox extends AbstractBillingModel {

    private Long deviceNo = null;
    private String cwmpId = null;
    private String passphrase = null;

    /**
     * @return the deviceNo
     */
    public Long getDeviceNo() {
        return deviceNo;
    }

    /**
     * @param deviceNo the deviceNo to set
     */
    public void setDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
    }

    /**
     * @return the cwmpId
     */
    public String getCwmpId() {
        return cwmpId;
    }

    /**
     * @param cwmpId the cwmpId to set
     */
    public void setCwmpId(String cwmpId) {
        this.cwmpId = cwmpId;
    }

    /**
     * @return the passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @param passphrase the passphrase to set
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }


}


