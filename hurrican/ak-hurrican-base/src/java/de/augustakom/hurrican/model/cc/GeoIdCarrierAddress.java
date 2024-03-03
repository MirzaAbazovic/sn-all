/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.05.2013 15:45:18
 */
package de.augustakom.hurrican.model.cc;

import javax.annotation.*;
import javax.persistence.*;

@Embeddable
public class GeoIdCarrierAddress extends AbstractCCModel {

    public static final String CARRIER_DTAG = "DTAG";

    public static final String STREET = "street";
    private String street;

    public static final String HOUSENUM = "houseNum";
    @Nullable
    private String houseNum;
    public static final String HOUSENUM_EXT = "houseNumExtension";
    @Nullable
    private String houseNumExtension;

    public static final String ZIPCODE = "zipCode";
    private String zipCode;

    public static final String DISTRICT = "district";
    @Nullable
    private String district;

    public static final String CITY = "city";
    private String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Nullable
    public String getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(@Nullable String houseNum) {
        this.houseNum = houseNum;
    }

    @Nullable
    public String getHouseNumExtension() {
        return houseNumExtension;
    }

    public void setHouseNumExtension(@Nullable String houseNumExtension) {
        this.houseNumExtension = houseNumExtension;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(@Nullable String zipCode) {
        this.zipCode = zipCode;
    }

    @Nullable
    public String getDistrict() {
        return district;
    }

    public void setDistrict(@Nullable String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
