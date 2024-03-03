/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import java.util.*;

import de.mnet.esb.cdm.resource.location.v1.Building;
import de.mnet.esb.cdm.resource.location.v1.StreetSection;

/**
 *
 */
public class BuildingBuilder extends LocationBuilder<Building> {

    private String houseNumber;
    private String houseNumberExtension;
    private Long numberOfApartments;
    private List<Building.CarrierAddress> carrierAddress;
    private Building.TAL tal;
    private Building.Infas infas;
    private String street;

    @Override
    public Building build() {
        Building building = new Building();

        building.setHouseNumber(houseNumber);
        building.setHouseNumberExtension(houseNumberExtension);
        building.setNumberOfApartments(numberOfApartments);
        if (carrierAddress != null) {
            building.getCarrierAddress().addAll(carrierAddress);
        }
        building.setTAL(tal);
        building.setInfas(infas);
        building.setStreet(getStreetSection());

        return super.enrich(building);
    }

    public BuildingBuilder withId(long id) {
        return (BuildingBuilder) super.withId(id);
    }

    public BuildingBuilder withHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public BuildingBuilder withHouseNumberExtension(String houseNumberExtension) {
        this.houseNumberExtension = houseNumberExtension;
        return this;
    }

    public BuildingBuilder withNumberOfApartments(Long numberOfApartments) {
        this.numberOfApartments = numberOfApartments;
        return this;
    }

    public BuildingBuilder withTal(Building.TAL tal) {
        this.tal = tal;
        return this;
    }

    public BuildingBuilder withInfas(Building.Infas infas) {
        this.infas = infas;
        return this;
    }

    private StreetSection getStreetSection() {
        StreetSection streetSection = new StreetSection();
        streetSection.setName(street);
        return streetSection;
    }

    public BuildingBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public BuildingBuilder withCarrierAddresses(List<Building.CarrierAddress> carrierAddress) {
        this.carrierAddress = carrierAddress;
        return this;
    }

    public BuildingBuilder addCarrierAddress(Building.CarrierAddress address) {
        if (this.carrierAddress == null) {
            this.carrierAddress = new ArrayList<>();
        }
        this.carrierAddress.add(address);
        return this;
    }

}
