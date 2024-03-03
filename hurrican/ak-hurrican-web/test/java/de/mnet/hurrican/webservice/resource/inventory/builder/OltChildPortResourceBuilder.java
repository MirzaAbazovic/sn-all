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
public class OltChildPortResourceBuilder extends ResourceBuilder {

    public OltChildPortResourceBuilder withId(String id) {
        return (OltChildPortResourceBuilder) super.withId(id);
    }

    public OltChildPortResourceBuilder withInventory(String inventory) {
        return (OltChildPortResourceBuilder) super.withInventory(inventory);
    }

    public OltChildPortResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (OltChildPortResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public OltChildPortResourceBuilder withName(String name) {
        return (OltChildPortResourceBuilder) super.withName(name);
    }

    public OltChildPortResourceBuilder withParentResources(ResourceId parentResources) {
        return (OltChildPortResourceBuilder) super.withParentResource(parentResources);
    }

    public OltChildPortResourceBuilder withSchnittstelle(String schnittstelle) {
        return (OltChildPortResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildPortResourceMapper.CHARACTERISTIC_SCHNITTSTELLE)
                        .withValues(Arrays.asList(schnittstelle))
                        .build()
        );
    }

}
