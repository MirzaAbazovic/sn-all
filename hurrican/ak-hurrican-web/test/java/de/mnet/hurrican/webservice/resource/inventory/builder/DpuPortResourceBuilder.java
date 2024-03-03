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
import de.mnet.hurrican.webservice.resource.inventory.service.DpuPortResourceMapper;

/**
 *
 */
public class DpuPortResourceBuilder extends OltChildPortResourceBuilder {

    public DpuPortResourceBuilder withId(String id) {
        return (DpuPortResourceBuilder) super.withId(id);
    }

    public DpuPortResourceBuilder withInventory(String inventory) {
        return (DpuPortResourceBuilder) super.withInventory(inventory);
    }

    public DpuPortResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (DpuPortResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public DpuPortResourceBuilder withName(String name) {
        return (DpuPortResourceBuilder) super.withName(name);
    }

    public DpuPortResourceBuilder withParentResources(ResourceId parentResources) {
        return (DpuPortResourceBuilder) super.withParentResource(parentResources);
    }

    public DpuPortResourceBuilder withSchnittstelle(String schnittstelle) {
        return (DpuPortResourceBuilder) super.withSchnittstelle(schnittstelle);
    }

    public DpuPortResourceBuilder withLeiste(String leiste) {
        return (DpuPortResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpuPortResourceMapper.CHARACTERISTIC_LEISTE)
                        .withValues(Arrays.asList(leiste))
                        .build()
        );
    }

    public DpuPortResourceBuilder withStift(String stift) {
        return (DpuPortResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpuPortResourceMapper.CHARACTERISTIC_STIFT)
                        .withValues(Arrays.asList(stift))
                        .build()
        );
    }

}
