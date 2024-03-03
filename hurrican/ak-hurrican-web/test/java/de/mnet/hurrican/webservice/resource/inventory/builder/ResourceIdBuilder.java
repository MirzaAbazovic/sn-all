/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceId;

/**
 *
 */
public class ResourceIdBuilder {

    private String id;
    private String inventory;

    public ResourceId build() {
        ResourceId resourceId = new ResourceId();
        resourceId.setId(id);
        resourceId.setInventory(inventory);
        return resourceId;
    }

    public ResourceIdBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ResourceIdBuilder withInventory(String inventory) {
        this.inventory = inventory;
        return this;
    }

}
