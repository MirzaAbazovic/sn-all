/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2011 14:52:42
 */

package de.augustakom.hurrican.model.cc.query;

import org.apache.commons.lang.StringUtils;


/**
 * Query fuer das Geo ID Search Frame.
 */
public class GeoIdSearchQuery extends GeoIdQuery {

    private Long id = null;
    private String onkz = null;
    private Integer asb = null;
    private String kvz = null;

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }
        if (StringUtils.isNotBlank(getOnkz())) {
            return false;
        }
        if (asb != null) {
            return false;
        }
        if (id != null) {
            return false;
        }
        if (kvz != null) {
            return false;
        }
        return true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public Integer getAsb() {
        return asb;
    }

    public String getKvz() {
        return kvz;
    }

    public void setKvz(String kvz) {
        this.kvz = kvz;
    }

}
