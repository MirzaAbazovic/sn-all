/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.webservice.resource.inventory.service.DpoResourceMapper;
import de.mnet.hurrican.webservice.resource.inventory.service.DpuResourceMapper;

/**
 *
 */
public class DpuResourceBuilder extends OltChildResourceBuilder {

    public DpuResourceBuilder withReversePower(boolean reversePower) {
        return (DpuResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpuResourceMapper.CHARACTERISTIC_REVERSE_POWER)
                        .withValues(Arrays.asList(String.valueOf(reversePower)))
                        .build()
        );
    }

}
