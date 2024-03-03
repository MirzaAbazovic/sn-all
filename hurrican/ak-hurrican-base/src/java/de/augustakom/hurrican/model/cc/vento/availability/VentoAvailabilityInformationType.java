/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.09.2012 10:44:53
 */
package de.augustakom.hurrican.model.cc.vento.availability;

import java.time.*;
import java.util.*;

/**
 *
 */
public class VentoAvailabilityInformationType {

    private List<VentoCablePart> cableParts;
    private VentoConnectionType connection;
    private Integer distanceInMeters;
    private Boolean distanceApproved;
    private Integer maxDownstreamBandwidthInKB;
    private LocalDate start;
    private VentoTechnologyType technology;
    private LocalDate termination;

    public List<VentoCablePart> getCableParts() {
        if (cableParts == null) {
            cableParts = new ArrayList<VentoCablePart>();
        }
        return cableParts;
    }

    public VentoConnectionType getConnection() {
        return connection;
    }

    public void setConnection(VentoConnectionType connection) {
        this.connection = connection;
    }

    public Integer getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(Integer distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public Boolean getDistanceApproved() {
        return distanceApproved;
    }

    public void setDistanceApproved(Boolean distanceApproved) {
        this.distanceApproved = distanceApproved;
    }

    public Integer getMaxDownstreamBandwidthInKB() {
        return maxDownstreamBandwidthInKB;
    }

    public void setMaxDownstreamBandwidthInKB(Integer maxDownstreamBandwidthInKB) {
        this.maxDownstreamBandwidthInKB = maxDownstreamBandwidthInKB;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public VentoTechnologyType getTechnology() {
        return technology;
    }

    public void setTechnology(VentoTechnologyType technology) {
        this.technology = technology;
    }

    public LocalDate getTermination() {
        return termination;
    }

    public void setTermination(LocalDate termination) {
        this.termination = termination;
    }

}


