/*
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.12.2010 08:13:28
 */
package de.augustakom.hurrican.model.cc;

import java.util.*;

import de.augustakom.common.model.EntityBuilder;
import de.augustakom.common.model.ReferencedEntityId;


/**
 * Entity-Builder fuer {@link GeoId2TechLocation} Objekte.
 */
@SuppressWarnings("unused")
public class GeoId2TechLocationBuilder extends EntityBuilder<GeoId2TechLocationBuilder, GeoId2TechLocation> {

    @ReferencedEntityId("geoId")
    private GeoIdBuilder geoIdBuilder;
    @ReferencedEntityId("hvtIdStandort")
    private HVTStandortBuilder hvtStandortBuilder;
    private Long talLength;
    private String kvzNumber;
    private Long maxBandwidthAdsl;
    private Long maxBandwidthSdsl;
    private Long maxBandwidthVdsl;
    private Boolean talLengthTrusted = Boolean.FALSE;
    private String userW = randomString(25);
    private Calendar vdslAnHvtAvailable = Calendar.getInstance();

    @Override
    protected void beforeBuild() {
        if (this.hvtStandortBuilder == null) {
            this.hvtStandortBuilder = getBuilder(HVTStandortBuilder.class);
        }
        if (this.geoIdBuilder == null) {
            this.geoIdBuilder = getBuilder(GeoIdBuilder.class);
        }
    }

    public GeoId2TechLocationBuilder withHvtStandortBuilder(HVTStandortBuilder hvtStandortBuilder) {
        this.hvtStandortBuilder = hvtStandortBuilder;
        return this;
    }

    public GeoId2TechLocationBuilder withTalLength(Long talLength) {
        this.talLength = talLength;
        return this;
    }

    public GeoId2TechLocationBuilder withKvzNumber(String kvzNumber) {
        this.kvzNumber = kvzNumber;
        return this;
    }

    public GeoId2TechLocationBuilder withMaxBandwidthAdsl(Long maxBandwidthAdsl) {
        this.maxBandwidthAdsl = maxBandwidthAdsl;
        return this;
    }

    public GeoId2TechLocationBuilder withMaxBandwidthSdsl(Long maxBandwidthSdsl) {
        this.maxBandwidthSdsl = maxBandwidthSdsl;
        return this;
    }

    public GeoId2TechLocationBuilder withMaxBandwidthVdsl(Long maxBandwidthVdsl) {
        this.maxBandwidthVdsl = maxBandwidthVdsl;
        return this;
    }

    public GeoId2TechLocationBuilder withVdslAnHvtAvailableSince(Calendar vdslAnHvtAvailableSince) {
        this.vdslAnHvtAvailable = vdslAnHvtAvailableSince;
        return this;
    }

    public GeoId2TechLocationBuilder withGeoIdBuilder(GeoIdBuilder geoIdBuilder) {
        this.geoIdBuilder = geoIdBuilder;
        return this;
    }

    public HVTStandortBuilder getHvtStandortBuilder() {
        return hvtStandortBuilder;
    }

    public GeoIdBuilder getGeoIdBuilder() {
        return geoIdBuilder;
    }
}


