/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.07.2014
 */
package de.augustakom.hurrican.service.elektra.builder;

import de.mnet.elektra.services.DialingNumberType;

/**
 *
 */
public class DialingNumberTypeBuilder {

    static final String RESOURCE_NAME_DEFAULT = "ignored";
    static final String COUNTRY_DIALING_CODE_DE = "+49";

    private String countryDialingCode = COUNTRY_DIALING_CODE_DE;
    private String resourceName = RESOURCE_NAME_DEFAULT;
    private String areaDialingCode;
    private String dialingNumber;
    private Integer rangeFrom;
    private Integer rangeTo;
    private Long size;
    private String central;

    public DialingNumberType build() {
        DialingNumberType dialingNumberType = new DialingNumberType();
        dialingNumberType.setCountryDialingCode(countryDialingCode);
        dialingNumberType.setAreaDialingCode(areaDialingCode);
        dialingNumberType.setDialingNumber(dialingNumber);
        dialingNumberType.setResourceName(resourceName);
        dialingNumberType.setCentral(central);
        dialingNumberType.setRangeFrom(rangeFrom);
        dialingNumberType.setRangeTo(rangeTo);
        dialingNumberType.setSize(size);
        return dialingNumberType;
    }

    public DialingNumberTypeBuilder withAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
        return this;
    }

    public DialingNumberTypeBuilder withDialingNumber(String dialingNumber) {
        this.dialingNumber = dialingNumber;
        return this;
    }

    public DialingNumberTypeBuilder withRangeFrom(Integer rangeFrom) {
        this.rangeFrom = rangeFrom;
        return this;
    }

    public DialingNumberTypeBuilder withRangeTo(Integer rangeTo) {
        this.rangeTo = rangeTo;
        return this;
    }

    public DialingNumberTypeBuilder withSize(Long size) {
        this.size = size;
        return this;
    }

    public DialingNumberTypeBuilder withCentral(String central) {
        this.central = central;
        return this;
    }

}
