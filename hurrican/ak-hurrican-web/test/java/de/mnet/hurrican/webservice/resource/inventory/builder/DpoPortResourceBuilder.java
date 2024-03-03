/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceId;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.inventory.service.DpoPortResourceMapper;

/**
 *
 */
public class DpoPortResourceBuilder extends OltChildPortResourceBuilder {

    public DpoPortResourceBuilder withId(String id) {
        return (DpoPortResourceBuilder) super.withId(id);
    }

    public DpoPortResourceBuilder withInventory(String inventory) {
        return (DpoPortResourceBuilder) super.withInventory(inventory);
    }

    public DpoPortResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (DpoPortResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public DpoPortResourceBuilder withName(String name) {
        return (DpoPortResourceBuilder) super.withName(name);
    }

    public DpoPortResourceBuilder withParentResources(ResourceId parentResources) {
        return (DpoPortResourceBuilder) super.withParentResource(parentResources);
    }

    public DpoPortResourceBuilder withSchnittstelle(String schnittstelle) {
        return (DpoPortResourceBuilder) super.withSchnittstelle(schnittstelle);
    }

    public DpoPortResourceBuilder withLeiste(String leiste) {
        return (DpoPortResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpoPortResourceMapper.CHARACTERISTIC_LEISTE)
                        .withValues(Arrays.asList(leiste))
                        .build()
        );
    }

    public DpoPortResourceBuilder withStift(String stift) {
        return (DpoPortResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpoPortResourceMapper.CHARACTERISTIC_STIFT)
                        .withValues(Arrays.asList(stift))
                        .build()
        );
    }

}
