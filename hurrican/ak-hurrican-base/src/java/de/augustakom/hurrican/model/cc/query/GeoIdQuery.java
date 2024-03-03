/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2005 08:31:29
 */
package de.augustakom.hurrican.model.cc.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query fuer die Suche nach GeoIDs ueber div. Parameter.
 *
 *
 */
public class GeoIdQuery extends AbstractHurricanQuery {

    private String street = null;
    private String houseNum = null;
    private String houseNumExt = null;
    private String city = null;
    private String zipCode = null;
    private String District = null;
    private Boolean serviceable = null;

    @Override
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getCity())) {
            return false;
        }
        if (StringUtils.isNotBlank(getStreet())) {
            return false;
        }
        if (StringUtils.isNotBlank(getZipCode())) {
            return false;
        }
        if (StringUtils.isNotBlank(getHouseNum())) {
            return false;
        }
        if (StringUtils.isNotBlank(getHouseNumExt())) {
            return false;
        }
        if (StringUtils.isNotBlank(getDistrict())) {
            return false;
        }
        if (null != getServiceable()) {
            return false;
        }
        return true;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getHouseNumExt() {
        return houseNumExt;
    }

    public void setHouseNumExt(String houseNumExt) {
        this.houseNumExt = houseNumExt;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public Boolean getServiceable() {
        return serviceable;
    }

    public void setServiceable(Boolean serviceable) {
        this.serviceable = serviceable;
    }
}


