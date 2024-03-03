/**
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 01.02.2011 09:12:35
 */

package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 *
 */
public class GeoId2TechLocationView extends AbstractCCIDModel implements HvtIdStandortModel {

    private Long geoId;
    private Long hvtIdStandort;
    private String standort;
    private Long standortTypRefId;
    private String standortTyp;
    private Long talLength;
    private Boolean talLengthTrusted;
    private String kvzNumber;
    private Long maxBandwidthAdsl;
    private Long maxBandwidthSdsl;
    private Long maxBandwidthVdsl;
    private Date vdslAnHvtAvailableSince;
    private String onkz;
    private Integer asb;
    private String switchKennung;


    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    public Long getGeoId() {
        return geoId;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public String getStandort() {
        return standort;
    }

    public Long getStandortTypRefId() {
        return standortTypRefId;
    }

    public void setStandortTypRefId(Long standortTypRefId) {
        this.standortTypRefId = standortTypRefId;
    }

    public void setStandortTyp(String standortTyp) {
        this.standortTyp = standortTyp;
    }

    public String getStandortTyp() {
        return standortTyp;
    }

    public void setTalLength(Long talLength) {
        this.talLength = talLength;
    }

    public Long getTalLength() {
        return talLength;
    }

    public void setTalLengthTrusted(Boolean talLengthTrusted) {
        this.talLengthTrusted = talLengthTrusted;
    }

    public Boolean getTalLengthTrusted() {
        return talLengthTrusted;
    }

    public void setKvzNumber(String kvzNumber) {
        this.kvzNumber = kvzNumber;
    }

    public String getKvzNumber() {
        return kvzNumber;
    }

    public void setMaxBandwidthAdsl(Long maxBandwidthAdsl) {
        this.maxBandwidthAdsl = maxBandwidthAdsl;
    }

    public Long getMaxBandwidthAdsl() {
        return maxBandwidthAdsl;
    }

    public void setMaxBandwidthSdsl(Long maxBandwidthSdsl) {
        this.maxBandwidthSdsl = maxBandwidthSdsl;
    }

    public Long getMaxBandwidthSdsl() {
        return maxBandwidthSdsl;
    }

    public void setMaxBandwidthVdsl(Long maxBandwidthVdsl) {
        this.maxBandwidthVdsl = maxBandwidthVdsl;
    }

    public Long getMaxBandwidthVdsl() {
        return maxBandwidthVdsl;
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

    public Date getVdslAnHvtAvailableSince() {
        return vdslAnHvtAvailableSince;
    }

    public void setVdslAnHvtAvailableSince(Date vdslAnHvtAvailableSince) {
        this.vdslAnHvtAvailableSince = vdslAnHvtAvailableSince;
    }

    public String getSwitchKennung() {
        return switchKennung;
    }

    public void setSwitchKennung(String switchKennung) {
        this.switchKennung = switchKennung;
    }

}
