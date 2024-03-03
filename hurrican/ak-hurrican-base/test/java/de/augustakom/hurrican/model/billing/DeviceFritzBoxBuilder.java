/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 17:15:31
 */
package de.augustakom.hurrican.model.billing;

/**
 *
 */
@SuppressWarnings("unused")
public class DeviceFritzBoxBuilder extends BillingEntityBuilder<DeviceFritzBoxBuilder, DeviceFritzBox> {

    private Long deviceNo = null;
    private String cwmpId = null;
    private String passphrase = null;

    public DeviceFritzBoxBuilder withDeviceNo(Long deviceNo) {
        this.deviceNo = deviceNo;
        return this;
    }

    public DeviceFritzBoxBuilder withCwmpId(String cwmpId) {
        this.cwmpId = cwmpId;
        return this;
    }

    public DeviceFritzBoxBuilder withPassphrase(String passphrase) {
        this.passphrase = passphrase;
        return this;
    }

}


