/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.12.2008 08:35:39
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;

import de.augustakom.common.tools.lang.DateTools;


/**
 * Modell fuer die Abbildung eines Endgeraets. <br> Es handelt sich hier um kein 'richtiges' Modell, sondern um ein
 * View-Objekt ueber mehrere Taifun-Tabellen.
 *
 *
 */
public class Device extends AbstractBillingModel {

    /**
     * Konstante fuer <code>provisioningSystem</code> die das Syste 'Hurrican' als Provisionierungssystem fuer einen
     * Geraetetyp spezifiziert.
     */
    public static final String PROV_SYSTEM_HURRICAN = "HURRICAN";

    public static final String DEVICE_EXTENSION_FRITZBOX = "DEVICE__FRITZBOX";

    /**
     * Konstante fuer <code>deviceClass</code> kennzeichnet ein ONT-Device.
     */
    public static final String DEVICE_CLASS_ONT = "ONT";
    /**
     * Konstante fuer <code>deviceClass</code> kennzeichnet ein IAD-Device.
     */
    public static final String DEVICE_CLASS_IAD = "IAD";

    private Long devNo = null;
    private String devType = null;
    private String manufacturer = null;
    private String serialNumber = null;
    private String macAddress = null;
    private String managementIP = null;
    private String provisioningSystem = null;
    private String techName = null;
    private String deviceClass = null;
    private String deviceExtension = null;
    private Date validFrom = null;
    private Date validTo = null;
    private Long purchaseOrderNo = null;

    /**
     * Die Felder validFrom und validTo sind nur gesetzt, wenn die Device-Objekte zum Auftrag ermittelt werden. Die
     * Funktion isValid prueft, ob das Device noch aktive dem Auftrag zugeordnet ist.
     *
     * @return boolean-Wert
     */
    public boolean isValid(Date toCheck) {
        if (validFrom == null) {
            return false;
        }
        else if (validTo == null) {
            return DateTools.isDateBeforeOrEqual(validFrom, toCheck);
        }
        else {
            return DateTools.isDateBeforeOrEqual(validFrom, toCheck)
                    && DateTools.isDateAfterOrEqual(validTo, toCheck);
        }
    }

    public Long getDevNo() {
        return devNo;
    }
    public void setDevNo(Long devNo) {
        this.devNo = devNo;
    }

    public String getDevType() {
        return devType;
    }
    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getManagementIP() {
        return managementIP;
    }
    public void setManagementIP(String managementIP) {
        this.managementIP = managementIP;
    }

    public String getProvisioningSystem() {
        return provisioningSystem;
    }
    public void setProvisioningSystem(String provisioningSystem) {
        this.provisioningSystem = provisioningSystem;
    }

    public String getTechName() {
        return techName;
    }
    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getDeviceClass() {
        return deviceClass;
    }
    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }

    public Date getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getDeviceExtension() {
        return deviceExtension;
    }
    public void setDeviceExtension(String deviceExtension) {
        this.deviceExtension = deviceExtension;
    }


    public Long getPurchaseOrderNo() {
        return purchaseOrderNo;
    }
    public void setPurchaseOrderNo(Long purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

}


