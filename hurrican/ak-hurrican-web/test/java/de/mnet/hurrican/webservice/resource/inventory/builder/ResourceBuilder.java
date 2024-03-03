/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.Resource;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceId;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceRole;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;

/**
 *
 */
public abstract class ResourceBuilder {

    private String id;
    private String inventory;
    private ResourceSpecId resourceSpec;
    private String name;
    private List<ResourceRole> roles;
    private List<ResourceCharacteristic> characteristics;
    private ResourceId parentResources;

    public Resource build() {
        Resource resource = new Resource();
        resource.setId(id);
        resource.setInventory(inventory);
        resource.setName(name);
        resource.setResourceSpec(resourceSpec);
        resource.setParentResource(parentResources);
        if (roles != null) {
            resource.getRole().addAll(roles);
        }
        if (characteristics != null) {
            resource.getCharacteristic().addAll(characteristics);
        }
        return resource;
    }

    public ResourceBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ResourceBuilder withInventory(String inventory) {
        this.inventory = inventory;
        return this;
    }

    public ResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        this.resourceSpec = resourceSpec;
        return this;
    }

    public ResourceBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceBuilder withRoles(List<ResourceRole> roles) {
        this.roles = roles;
        return this;
    }

    public ResourceBuilder addRole(ResourceRole role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
        return this;
    }

    public ResourceBuilder withCharacteristics(List<ResourceCharacteristic> characteristics) {
        this.characteristics = characteristics;
        return this;
    }

    public ResourceBuilder addCharacteristic(ResourceCharacteristic characteristic) {
        if (this.characteristics == null) {
            this.characteristics = new ArrayList<>();
        }
        this.characteristics.add(characteristic);
        return this;
    }

    public ResourceBuilder withParentResource(ResourceId parentResource) {
        this.parentResources = parentResource;
        return this;
    }

}
