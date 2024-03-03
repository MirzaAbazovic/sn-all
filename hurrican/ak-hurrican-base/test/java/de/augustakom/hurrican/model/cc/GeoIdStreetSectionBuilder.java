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
public class GeoIdStreetSectionBuilder extends EntityBuilder<GeoIdStreetSectionBuilder, GeoIdStreetSection> {

    private Long version = Long.valueOf(0);
    private Long id = randomLong(999999999);
    private Long replacedById = null;
    private String technicalId = null;
    private Date modified = new Date();
    private Boolean serviceable = Boolean.FALSE;

    private String name = randomString();
    @ReferencedEntityId("zipCode")
    private GeoIdZipCodeBuilder zipCodeBuilder = new GeoIdZipCodeBuilder();
    @ReferencedEntityId("district")
    // district ist optional, daher mit null und nicht mit Builder init
    private GeoIdDistrictBuilder districtBuilder = null;

    public GeoIdStreetSectionBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public GeoIdZipCodeBuilder getZipCodeBuilder() {
        return zipCodeBuilder;
    }

    public GeoIdStreetSectionBuilder withZipCodeBuilder(GeoIdZipCodeBuilder zipCodeBuilder) {
        this.zipCodeBuilder = zipCodeBuilder;
        return this;
    }

    public GeoIdDistrictBuilder getDistrictBuilder() {
        return districtBuilder;
    }

    public GeoIdStreetSectionBuilder withDistrictBuilder(GeoIdDistrictBuilder districtBuilder) {
        this.districtBuilder = districtBuilder;
        return this;
    }
}
