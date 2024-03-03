/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.12.2005 08:31:29
 */
package de.augustakom.hurrican.model.cc.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query fuer die Suche nach HVTs.
 *
 *
 */
public class HVTQuery extends AbstractHurricanQuery {

    private static final long serialVersionUID = 7768524329784297552L;
    private String onkz = null;
    private Integer asb = null;
    private String ortsteil = null;
    private String ort = null;
    private Long standortTypRefId = null;
    private Long niederlassungId = null;
    private Long hvtIdStandort = null;
    private String gesicherteRealisierung = null;
    private Integer maxResultSize = null;

    public Integer getMaxResultSize() {
        return maxResultSize;
    }

    public void setMaxResultSize(Integer maxResultSize) {
        this.maxResultSize = maxResultSize;
    }

    public String getGesicherteRealisierung() {
        return gesicherteRealisierung;
    }

    public void setGesicherteRealisierung(String gesicherteRealisierung) {
        this.gesicherteRealisierung = gesicherteRealisierung;
    }

    public String getOnkz() {
        return onkz;
    }

    public void setOnkz(String onkz) {
        this.onkz = onkz;
    }

    public Integer getAsb() {
        return asb;
    }

    public void setAsb(Integer asb) {
        this.asb = asb;
    }

    public String getOrtsteil() {
        return ortsteil;
    }

    public void setOrtsteil(String ortsteil) {
        this.ortsteil = ortsteil;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public Long getStandortTypRefId() {
        return standortTypRefId;
    }

    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }

    public Long getNiederlassungId() {
        return niederlassungId;
    }

    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    public HVTQuery withHvtIdStandort(Long hvtIdStandort) {
        setHvtIdStandort(hvtIdStandort);
        return this;
    }

    @Override
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getOnkz())) {
            return false;
        }
        if (StringUtils.isNotBlank(getOrtsteil())) {
            return false;
        }
        if (StringUtils.isNotBlank(getOrt())) {
            return false;
        }
        if (getAsb() != null) {
            return false;
        }
        if (getStandortTypRefId() != null) {
            return false;
        }
        if (getNiederlassungId() != null) {
            return false;
        }
        if (getHvtIdStandort() != null) {
            return false;
        }
        return true;
    }

}


