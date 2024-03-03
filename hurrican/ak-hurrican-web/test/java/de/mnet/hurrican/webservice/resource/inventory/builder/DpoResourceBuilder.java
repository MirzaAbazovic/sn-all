/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 17.03.2015
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.webservice.resource.inventory.service.DpoResourceMapper;

/**
 *
 */
public class DpoResourceBuilder extends OltChildResourceBuilder {

    public DpoResourceBuilder withChassisIdentifier(String chassisIdentifier) {
        return (DpoResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpoResourceMapper.CHARACTERISTIC_CHASSISBEZEICHNUNG)
                        .withValues(Arrays.asList(chassisIdentifier))
                        .build()
        );
    }

    public DpoResourceBuilder withChassisSlot(String chassisSlot) {
        return (DpoResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(DpoResourceMapper.CHARACTERISTIC_POSITION_IN_CHASSIS)
                        .withValues(Arrays.asList(chassisSlot))
                        .build()
        );
    }

}
