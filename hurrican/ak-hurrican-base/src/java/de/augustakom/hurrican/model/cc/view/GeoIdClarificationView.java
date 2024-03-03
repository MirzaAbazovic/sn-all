/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.04.2011 15:14:15
 */

package de.augustakom.hurrican.model.cc.view;

import de.augustakom.hurrican.model.cc.Reference;


/**
 * View-Modell, fuer die Geo ID Klärfälle
 */
public class GeoIdClarificationView {

    private Long id;
    private Long geoId;
    private Reference status;
    private Reference type;
    private String info;
    private String street;
    private String houseNo;
    private String houseNoExt;
    private String zipCode;
    private String city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public Reference getStatus() {
        return status;
    }

    public void setStatus(Reference status) {
        this.status = status;
    }

    public Reference getType() {
        return type;
    }

    public void setType(Reference revisionType) {
        this.type = revisionType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String revisionData) {
        this.info = revisionData;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getHouseNoExt() {
        return houseNoExt;
    }

    public void setHouseNoExt(String houseNoExt) {
        this.houseNoExt = houseNoExt;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipcode) {
        this.zipCode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
