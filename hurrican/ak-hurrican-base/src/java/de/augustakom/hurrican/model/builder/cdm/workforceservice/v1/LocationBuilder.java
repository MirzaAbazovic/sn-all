/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.mnet.esb.cdm.resource.workforceservice.v1.OrderTechnicalParams;

/**
 *
 */
public class LocationBuilder implements WorkforceTypeBuilder<OrderTechnicalParams.Site.Location> {

    private String tae1;
    private String street;
    private String houseNumber;
    private String city;
    private String zipCode;

    @Override
    public OrderTechnicalParams.Site.Location build() {
        OrderTechnicalParams.Site.Location location = new OrderTechnicalParams.Site.Location();
        location.setTAE1(this.tae1);
        location.setStreet(this.street);
        location.setHouseNumber(this.houseNumber);
        location.setCity(this.city);
        location.setZipCode(this.zipCode);
        return location;
    }

    public LocationBuilder withTAE1(String tae1) {
        this.tae1 = tae1;
        return this;
    }

    public LocationBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public LocationBuilder withHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public LocationBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public LocationBuilder withCity(String city) {
        this.city = city;
        return this;
    }
}