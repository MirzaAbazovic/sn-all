/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.UpdateResource;

/**
 *
 */
public class UpdateResourceBuilder {

    protected List<Resource> resources;

    public UpdateResource build() {
        UpdateResource updateResource = new UpdateResource();
        if (resources != null) {
            updateResource.getResource().addAll(resources);
        }
        return updateResource;
    }

    public UpdateResourceBuilder withResources(List<Resource> resources) {
        this.resources = resources;
        return this;
    }

    public UpdateResourceBuilder addResource(Resource resource) {
        if (this.resources == null) {
            this.resources = new ArrayList<>();
        }
        this.resources.add(resource);
        return this;
    }

}
