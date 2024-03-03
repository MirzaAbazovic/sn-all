/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.06.2009 13:09:50
 */
package de.augustakom.hurrican.model.billing;


/**
 * Modell zur Abbildung eines Device-Types.
 *
 *
 */
public class DeviceType extends AbstractBillingModel {

    private String deviceTypeId = null;
    private String deviceTypeConfigId = null;
    private String provisioningSystem = null;

    /**
     * @return the deviceTypeId
     */
    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    /**
     * @param deviceTypeId the deviceTypeId to set
     */
    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    /**
     * @return the deviceTypeConfigId
     */
    public String getDeviceTypeConfigId() {
        return deviceTypeConfigId;
    }

    /**
     * @param deviceTypeConfigId the deviceTypeConfigId to set
     */
    public void setDeviceTypeConfigId(String deviceTypeConfigId) {
        this.deviceTypeConfigId = deviceTypeConfigId;
    }

    /**
     * @return the provisioningSystem
     */
    public String getProvisioningSystem() {
        return provisioningSystem;
    }

    /**
     * @param provisioningSystem the provisioningSystem to set
     */
    public void setProvisioningSystem(String provisioningSystem) {
        this.provisioningSystem = provisioningSystem;
    }

}


