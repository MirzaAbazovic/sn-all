/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import de.mnet.esb.cdm.resource.locationservice.v1.BuildingQuery;
import de.mnet.esb.cdm.resource.locationservice.v1.CityQuery;
import de.mnet.esb.cdm.resource.locationservice.v1.CountryQuery;
import de.mnet.esb.cdm.resource.locationservice.v1.DistrictQuery;
import de.mnet.esb.cdm.resource.locationservice.v1.SearchBuildings;
import de.mnet.esb.cdm.resource.locationservice.v1.StreetSectionQuery;
import de.mnet.esb.cdm.resource.locationservice.v1.ZipCodeQuery;

/**
 *
 */
public class SearchBuildingsBuilder extends SearchRequestBuilder<SearchBuildings> {

    protected Long buildingId;
    protected String countryCode;
    protected String country;
    protected String city;
    protected String district;
    protected String zipCode;
    protected String streetSection;
    protected String houseNumber;
    protected String houseNumberExtension;

    @Override
    public SearchBuildings build() {
        SearchBuildings searchBuildings = new SearchBuildings();

        searchBuildings.setCountry(getCountryQuery());
        searchBuildings.setZipCode(getZipCodeQuery());
        searchBuildings.setCity(getCityQuery());
        searchBuildings.setDistrict(getDistrictQuery());
        searchBuildings.setStreetSection(getStreetSectionQuery());
        searchBuildings.setBuilding(getBuildingQuery());

        return super.enrich(searchBuildings);
    }

    private BuildingQuery getBuildingQuery() {
        if (buildingId != null || houseNumber != null) {
            BuildingQuery buildingQuery = new BuildingQuery();
            buildingQuery.setId(buildingId);
            buildingQuery.setHouseNumber(houseNumber);
            buildingQuery.setHouseNumberExtension(houseNumberExtension);
            return buildingQuery;
        }
        return null;
    }

    private StreetSectionQuery getStreetSectionQuery() {
        if (streetSection != null) {
            StreetSectionQuery streetSectionQuery = new StreetSectionQuery();
            streetSectionQuery.setName(streetSection);
            return streetSectionQuery;
        }
        return null;
    }

    private CityQuery getCityQuery() {
        if (city != null) {
            CityQuery cityQuery = new CityQuery();
            cityQuery.setName(city);
            return cityQuery;
        }
        return null;
    }

    private DistrictQuery getDistrictQuery() {
        if (district != null) {
            DistrictQuery districtQuery = new DistrictQuery();
            districtQuery.setName(district);
            return districtQuery;
        }
        return null;
    }

    private ZipCodeQuery getZipCodeQuery() {
        if (zipCode != null) {
            ZipCodeQuery zipCodeQuery = new ZipCodeQuery();
            zipCodeQuery.setZipCode(zipCode);
            return zipCodeQuery;
        }
        return null;
    }

    private CountryQuery getCountryQuery() {
        if (countryCode != null) {
            CountryQuery countryQuery = new CountryQuery();
            countryQuery.setName(country);
            countryQuery.setCountryCode(countryCode);
            return countryQuery;
        }
        return null;
    }

    public SearchBuildingsBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public SearchBuildingsBuilder withCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public SearchBuildingsBuilder withZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public SearchBuildingsBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public SearchBuildingsBuilder withDistrict(String district) {
        this.district = district;
        return this;
    }

    public SearchBuildingsBuilder withStreetSection(String streetSection) {
        this.streetSection = streetSection;
        return this;
    }

    public SearchBuildingsBuilder withBuildingId(Long buildingId) {
        this.buildingId = buildingId;
        return this;
    }

    public SearchBuildingsBuilder withBuilding(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public SearchBuildingsBuilder withBuilding(String houseNumber, String houseNumberExtension) {
        this.houseNumber = houseNumber;
        this.houseNumberExtension = houseNumberExtension;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "SearchBuildingsBuilder [buildingId=%s" +
                ", countryCode=%s" +
                ", country=%s" +
                ", city=%s" +
                ", district=%s" +
                ", zipCode=%s" +
                ", streetSection=%s" +
                ", houseNumber=%s" +
                ", houseNumberExtension=%s" +
                ", toString=%s]",
                buildingId, countryCode, country, city, district, zipCode, streetSection, houseNumber,
                houseNumberExtension, super.toString());
    }
}
