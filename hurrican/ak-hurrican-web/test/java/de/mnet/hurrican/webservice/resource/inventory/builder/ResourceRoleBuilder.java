/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceRole;

/**
 *
 */
public class ResourceRoleBuilder {

    private String name;
    private String description;

    public ResourceRole build() {
        ResourceRole resourceRole = new ResourceRole();
        resourceRole.setName(name);
        resourceRole.setDescription(description);
        return resourceRole;
    }

    public ResourceRoleBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceRoleBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

}
