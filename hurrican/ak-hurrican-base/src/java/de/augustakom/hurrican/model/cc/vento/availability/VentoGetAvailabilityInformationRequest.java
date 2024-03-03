/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2012 17:05:07
 */
package de.augustakom.hurrican.model.cc.vento.availability;

/**
 *
 */
public class VentoGetAvailabilityInformationRequest {
    private Long geoId;
    private String onkz;
    private String asb;
    private String kvz;

    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getAsb() {
        return asb;
    }

    public void setAsb(String asb) {
        this.asb = asb;
    }

    public String getKvz() {
        return kvz;
    }

    public void setKvz(String kvz) {
        this.kvz = kvz;
    }

}


