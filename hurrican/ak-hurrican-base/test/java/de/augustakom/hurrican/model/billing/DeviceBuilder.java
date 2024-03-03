/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.10.2009 17:15:31
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 *
 */
@SuppressWarnings("unused")
public class DeviceBuilder extends BillingEntityBuilder<DeviceBuilder, Device> {

    private Long devNo;
    private String devType;
    private String manufacturer = randomString(10);
    private String serialNumber = randomString(20);
    private String macAddress;
    private String managementIP;
    private String provisioningSystem;
    private String techName;
    private String storageId;
    private String deviceClass;
    private String deviceExtension;
    private Date validFrom;
    private Long purchaseOrderNo;

    public DeviceBuilder withDevNo(Long devNo) {
        this.devNo = devNo;
        return this;
    }

    public DeviceBuilder withSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public DeviceBuilder withDevType(String devType) {
        this.devType = devType;
        return this;
    }

    public DeviceBuilder withTechName(String techName) {
        this.techName = techName;
        return this;
    }

    public DeviceBuilder withDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
        return this;
    }

    public DeviceBuilder withValidFrom(Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public DeviceBuilder withDeviceExtension(String deviceExtension) {
        this.deviceExtension = deviceExtension;
        return this;
    }

    public DeviceBuilder withRandomId() {
        this.devNo = getLongId();
        return this;
    }

    public DeviceBuilder withManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public DeviceBuilder withPurchaseOrderNo(final Long purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
        return this;
    }
}


