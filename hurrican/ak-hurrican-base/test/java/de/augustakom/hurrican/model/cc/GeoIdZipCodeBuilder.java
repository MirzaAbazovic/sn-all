/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:15:02
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;

@SuppressWarnings("unused")
public class GeoIdZipCodeBuilder extends EntityBuilder<GeoIdZipCodeBuilder, GeoIdZipCode> {

    private Long version = Long.valueOf(0);
    private Long id = randomLong(9999999);
    private Long replacedById = null;
    private String technicalId = null;
    private Date modified = new Date();
    private Boolean serviceable = Boolean.FALSE;

    private String zipCode = randomString();
    @ReferencedEntityId("city")
    private GeoIdCityBuilder cityBuilder = new GeoIdCityBuilder();

    public GeoIdZipCodeBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public GeoIdCityBuilder getCityBuilder() {
        return cityBuilder;
    }

    public GeoIdZipCodeBuilder withCityBuilder(GeoIdCityBuilder cityBuilder) {
        this.cityBuilder = cityBuilder;
        return this;
    }
}
