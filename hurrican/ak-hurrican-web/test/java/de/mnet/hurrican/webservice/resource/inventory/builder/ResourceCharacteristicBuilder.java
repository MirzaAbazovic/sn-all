/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.11.2014
 */
package de.mnet.hurrican.webservice.resource.inventory.builder;

import java.util.*;

import de.mnet.hurrican.adm.resourceinventoryservice.v1.ResourceCharacteristic;

/**
 *
 */
public class ResourceCharacteristicBuilder {
    protected String name;
    protected List<String> values;

    public ResourceCharacteristic build() {
        ResourceCharacteristic resourceCharacteristic = new ResourceCharacteristic();
        resourceCharacteristic.setName(name);
        if (values != null) {
            resourceCharacteristic.getValue().addAll(values);
        }
        return resourceCharacteristic;
    }

    public ResourceCharacteristicBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ResourceCharacteristicBuilder withValues(List<String> values) {
        this.values = values;
        return this;
    }

}
