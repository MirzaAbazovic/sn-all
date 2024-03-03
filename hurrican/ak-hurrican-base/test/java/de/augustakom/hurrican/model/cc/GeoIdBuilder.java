/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:15:02
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import com.google.common.collect.Maps;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;

@SuppressWarnings("unused")
public class GeoIdBuilder extends EntityBuilder<GeoIdBuilder, GeoId> {

    private Long version = Long.valueOf(0);
    private Long id = randomLong(999999999);
    private Long replacedById = null;
    private String technicalId = null;
    private Date modified = new Date();
    private Boolean serviceable = Boolean.FALSE;

    private String houseNum = Integer.toString(randomInt(1, 100));
    private String houseNumExtension;
    private String onkz;
    private String asb;
    private String kvz;
    private String agsn;
    private Map<String, GeoIdCarrierAddress> carrierAddresses = Maps.newHashMap();

    @ReferencedEntityId("streetSection")
    private GeoIdStreetSectionBuilder streetSectionBuilder = new GeoIdStreetSectionBuilder();

    public GeoIdBuilder withStreet(String street) {
        streetSectionBuilder.withName(street);
        return this;
    }

    public GeoIdBuilder withHouseNum(String houseNum) {
        this.houseNum = houseNum;
        return this;
    }

    public GeoIdBuilder withHouseNumExtension(String houseNumExtension) {
        this.houseNumExtension = houseNumExtension;
        return this;
    }

    public GeoIdBuilder withZipCode(String zipCode) {
        streetSectionBuilder.getZipCodeBuilder().withZipCode(zipCode);
        return this;
    }

    public GeoIdBuilder withDistrict(String district) {
        if (streetSectionBuilder.getDistrictBuilder() == null) {
            streetSectionBuilder.withDistrictBuilder(new GeoIdDistrictBuilder());
        }
        streetSectionBuilder.getDistrictBuilder().withName(district);
        return this;
    }

    public GeoIdBuilder withCity(String city) {
        streetSectionBuilder.getZipCodeBuilder().getCityBuilder().withName(city);
        return this;
    }

    public GeoIdBuilder withCountry(String country) {
        streetSectionBuilder.getZipCodeBuilder().getCityBuilder().getCountryBuilder().withName(country);
        return this;
    }

    public GeoIdBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public GeoIdBuilder withOnkz(String onkz) {
        this.onkz = onkz;
        return this;
    }

    public GeoIdBuilder withAsb(String asb) {
        this.asb = asb;
        return this;
    }

    public GeoIdBuilder withKvz(String kvz) {
        this.kvz = kvz;
        return this;
    }

    public GeoIdBuilder withAgsn(String agsn) {
        this.agsn = agsn;
        return this;
    }
}
