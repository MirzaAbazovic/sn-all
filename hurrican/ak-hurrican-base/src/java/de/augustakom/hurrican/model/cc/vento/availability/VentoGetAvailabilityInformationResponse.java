/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.2012 17:11:52
 */
package de.augustakom.hurrican.model.cc.vento.availability;

import java.util.*;

/**
 *
 */
public class VentoGetAvailabilityInformationResponse {

    protected List<VentoAvailabilityInformationType> availabilityInformationTypes;

    public List<VentoAvailabilityInformationType> getAvailabilityInformationTypes() {
        if (availabilityInformationTypes == null) {
            availabilityInformationTypes = new ArrayList<VentoAvailabilityInformationType>();
        }
        return availabilityInformationTypes;
    }
}


