/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceId;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.inventory.service.AbstractOltChildPortResourceMapper;

/**
 *
 */
public class OltChildKarteResourceBuilder extends ResourceBuilder {

    public OltChildKarteResourceBuilder withId(String id) {
        return (OltChildKarteResourceBuilder) super.withId(id);
    }

    public OltChildKarteResourceBuilder withInventory(String inventory) {
        return (OltChildKarteResourceBuilder) super.withInventory(inventory);
    }

    public OltChildKarteResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (OltChildKarteResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public OltChildKarteResourceBuilder withName(String name) {
        return (OltChildKarteResourceBuilder) super.withName(name);
    }

    public OltChildKarteResourceBuilder withParentResources(ResourceId parentResources) {
        return (OltChildKarteResourceBuilder) super.withParentResource(parentResources);
    }

}
