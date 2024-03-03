/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;

/**
 *
 */
public class ResourceSpecIdBuilder {

    private String id;
    private String inventory;

    public ResourceSpecId build() {
        ResourceSpecId resourceSpecId = new ResourceSpecId();
        resourceSpecId.setId(id);
        resourceSpecId.setInventory(inventory);
        return resourceSpecId;
    }

    public ResourceSpecIdBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ResourceSpecIdBuilder withInventory(String inventory) {
        this.inventory = inventory;
        return this;
    }

}
