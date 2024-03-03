/*
 * Copyright (c) 2008 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.2008 08:52:35
 */
package de.augustakom.hurrican.model.billing.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer Geo-Daten.
 *
 *
 */
public class GeoQuery extends AbstractHurricanQuery {

    private String zipCode = null;
    private String city = null;
    private String street = null;

    /**
     * @return Returns the zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode The zipCode to set.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return Returns the street.
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street The street to set.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getStreet())) {
            return false;
        }
        if (StringUtils.isNotBlank(getCity())) {
            return false;
        }
        if (StringUtils.isNotBlank(getZipCode())) {
            return false;
        }
        return true;
    }

}


