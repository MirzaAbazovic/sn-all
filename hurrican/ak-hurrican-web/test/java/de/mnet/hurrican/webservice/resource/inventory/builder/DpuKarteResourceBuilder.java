/*
 * Copyright (c) 2017 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.03.2017
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceId;
import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceSpecId;
import de.mnet.hurrican.webservice.resource.inventory.service.DpuKarteResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.DpuPortResourceMapper;

/**
 * Created by molterfe on 07.03.2017.
 */
public class DpuKarteResourceBuilder  extends OltChildKarteResourceBuilder {

    public DpuKarteResourceBuilder withId(String id) {
        return (DpuKarteResourceBuilder) super.withId(id);
    }

    public DpuKarteResourceBuilder withInventory(String inventory) {
        return (DpuKarteResourceBuilder) super.withInventory(inventory);
    }

    public DpuKarteResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (DpuKarteResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public DpuKarteResourceBuilder withName(String name) {
        return (DpuKarteResourceBuilder) super.withName(name);
    }

    public DpuKarteResourceBuilder withParentResources(ResourceId parentResources) {
        return (DpuKarteResourceBuilder) super.withParentResource(parentResources);
    }

    public DpuKarteResourceBuilder withKartentyp(String kartentyp) {
        return (DpuKarteResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpuKarteResourceMapper.CHARACTERISTIC_KARTENTYP)
                        .withValues(Arrays.asList(kartentyp))
                        .build()
        );
    }
}
