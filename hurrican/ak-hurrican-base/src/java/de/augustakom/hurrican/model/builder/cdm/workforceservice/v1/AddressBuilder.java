/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.09.2014
 */
package de.augustakom.hurrican.model.builder.cdm.workforceservice.v1;

import de.augustakom.common.tools.lang.StringTools;
import de.augustakom.hurrican.model.builder.cdm.workforceservice.WorkforceTypeBuilder;
import de.augustakom.hurrican.model.shared.iface.AddressModel;
import de.mnet.esb.cdm.resource.workforceservice.v1.Address;

/**
 *
 */
public class AddressBuilder implements WorkforceTypeBuilder<Address> {

    private String street;
    private String houseNumber;
    private String floor;
    private String zipCode;
    private String city;
    private String country;

    @Override
    public Address build() {
        Address address = new Address();
        address.setStreet(this.street);
        address.setHouseNumber(this.houseNumber);
        address.setFloor(this.floor);
        address.setZipCode(this.zipCode);
        address.setCity(this.city);
        address.setCountry(this.country);
        return address;
    }

    public AddressBuilder withAddressModel(AddressModel addressModel) {
        return new AddressBuilder()
                .withCountry(addressModel.getLandId())
                .withCity(addressModel.getCombinedOrtOrtsteil())
                .withZipCode(addressModel.getPlzTrimmed())
                .withStreet(addressModel.getStrasse())
                .withHouseNumber(StringTools.join(
                        new String[] { addressModel.getNummer(), addressModel.getHausnummerZusatz() }, " ", true))
                .withFloor(addressModel.getStrasseAdd());
    }

    public AddressBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public AddressBuilder withHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public AddressBuilder withFloor(String floor) {
        this.floor = floor;
        return this;
    }

    public AddressBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public AddressBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressBuilder withCountry(String country) {
        this.country = country;
        return this;
    }
}