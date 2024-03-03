/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.12.2010 09:24:21
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.common.tools.lang.BooleanTools;
import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Modell-Klasse fuer die Abbildung der Mappings zwischen einer Geo-ID und einem technischen Standort.
 */
@Entity
@Table(name = "T_GEO_ID_2_TECH_LOCATION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_GEO_ID_2_TECH_LOCATION_0", allocationSize = 1)
public class GeoId2TechLocation extends AbstractCCModel implements HvtIdStandortModel {

    private static final long serialVersionUID = -5010701949336539778L;

    private Long id;
    private Long geoId;
    private Long hvtIdStandort;
    private Long talLength;
    private String kvzNumber;
    private Long maxBandwidthAdsl;
    private Long maxBandwidthSdsl;
    private Long maxBandwidthVdsl;
    private String userW;
    private Boolean talLengthTrusted;
    private Calendar vdslAnHvtAvailableSince;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "GEO_ID")
    @NotNull
    public Long getGeoId() {
        return geoId;
    }

    public void setGeoId(Long geoId) {
        this.geoId = geoId;
    }

    @Column(name = "HVT_ID_STANDORT")
    @NotNull
    @Override
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    @Column(name = "TAL_LENGTH")
    public Long getTalLength() {
        return talLength;
    }

    public void setTalLength(Long talLength) {
        this.talLength = talLength;
    }

    @Column(name = "KVZ_NUMBER")
    public String getKvzNumber() {
        return kvzNumber;
    }

    public void setKvzNumber(String kvzNumber) {
        this.kvzNumber = kvzNumber;
    }

    @Column(name = "MAX_BANDWIDTH_ADSL")
    public Long getMaxBandwidthAdsl() {
        return maxBandwidthAdsl;
    }

    public void setMaxBandwidthAdsl(Long maxBandwidthAdsl) {
        this.maxBandwidthAdsl = maxBandwidthAdsl;
    }

    @Column(name = "MAX_BANDWIDTH_SDSL")
    public Long getMaxBandwidthSdsl() {
        return maxBandwidthSdsl;
    }

    public void setMaxBandwidthSdsl(Long maxBandwidthSdsl) {
        this.maxBandwidthSdsl = maxBandwidthSdsl;
    }

    @Column(name = "MAX_BANDWIDTH_VDSL")
    public Long getMaxBandwidthVdsl() {
        return maxBandwidthVdsl;
    }

    public void setMaxBandwidthVdsl(Long maxBandwidthVdsl) {
        this.maxBandwidthVdsl = maxBandwidthVdsl;
    }

    @Column(name = "USERW")
    @NotNull
    public String getUserW() {
        return userW;
    }

    public void setUserW(String userW) {
        this.userW = userW;
    }

    @Column(name = "TAL_LENGTH_TRUSTED")
    public Boolean getTalLengthTrusted() {
        return talLengthTrusted;
    }

    public void setTalLengthTrusted(Boolean talLengthTrusted) {
        this.talLengthTrusted = talLengthTrusted;
    }


    @Temporal(TemporalType.DATE)
    @Column(name = "VDSL_AN_HVT_AVAILABLE_SINCE")
    public Calendar getVdslAnHvtAvailableSince() {
        return vdslAnHvtAvailableSince;
    }

    public void setVdslAnHvtAvailableSince(Calendar vdslAnHvtAvailableSince) {
        this.vdslAnHvtAvailableSince = vdslAnHvtAvailableSince;
    }

    /**
     * Führt zwei techn. Standorte zusammen. ID und Geo-ID werden allerdings ausgespart. {@code this} ist Quelle und
     * {@code techLocationDest} ist Ziel.
     */
    @Transient
    public void merge(GeoId2TechLocation techLocationDest, String user) {
        techLocationDest.setHvtIdStandort(getHvtIdStandort());
        techLocationDest.setKvzNumber(getKvzNumber());
        techLocationDest.setMaxBandwidthAdsl(getMaxBandwidthAdsl());
        techLocationDest.setMaxBandwidthSdsl(getMaxBandwidthSdsl());
        techLocationDest.setMaxBandwidthVdsl(getMaxBandwidthVdsl());
        techLocationDest.setUserW(user);
        techLocationDest.setVdslAnHvtAvailableSince(getVdslAnHvtAvailableSince());
        if (BooleanTools.nullToFalse(getTalLengthTrusted())) {
            techLocationDest.setTalLengthTrusted(Boolean.TRUE);
            techLocationDest.setTalLength(getTalLength());
        }
    }
}
