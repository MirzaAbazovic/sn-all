/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 06.11.13
 */
package de.augustakom.hurrican.model.builder.cdm.location.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.location.LocationServiceTypeBuilder;
import de.mnet.esb.cdm.resource.location.v1.Location;

/**
 *
 */
public abstract class LocationBuilder<T extends Location> implements LocationServiceTypeBuilder<T> {

    protected long id;
    protected String technicalId;
    protected Date modified;
    protected boolean serviceable;

    public T enrich(T location) {
        location.setId(id);
        location.setTechnicalId(technicalId);
        location.setModified(modified);
        location.setServiceable(serviceable);
        return location;
    }

    public LocationBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public LocationBuilder withTechnicalId(String technicalId) {
        this.technicalId = technicalId;
        return this;
    }

    public LocationBuilder withModified(Date modified) {
        this.modified = modified;
        return this;
    }

    public LocationBuilder withServiceable(boolean serviceable) {
        this.serviceable = serviceable;
        return this;
    }

}
