/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 11:15:02
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;

@SuppressWarnings("unused")
public class GeoIdCountryBuilder extends EntityBuilder<GeoIdCountryBuilder, GeoIdCountry> {

    private Long version = Long.valueOf(0);
    private Long id = randomLong(9999999);
    private Long replacedById = null;
    private String technicalId = null;
    private Date modified = new Date();
    private Boolean serviceable = Boolean.FALSE;

    private String name = randomString();

    public GeoIdCountryBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
