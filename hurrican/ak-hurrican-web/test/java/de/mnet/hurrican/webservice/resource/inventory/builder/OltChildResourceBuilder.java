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
import de.mnet.hurrican.webservice.resource.inventory.service.AbstractOltChildResourceMapper;

/**
 *
 */
public class OltChildResourceBuilder extends ResourceBuilder {

    public OltChildResourceBuilder withId(String id) {
        return (OltChildResourceBuilder) super.withId(id);
    }

    public OltChildResourceBuilder withInventory(String inventory) {
        return (OltChildResourceBuilder) super.withInventory(inventory);
    }

    public OltChildResourceBuilder withResourceSpec(ResourceSpecId resourceSpec) {
        return (OltChildResourceBuilder) super.withResourceSpec(resourceSpec);
    }

    public OltChildResourceBuilder withName(String name) {
        return (OltChildResourceBuilder) super.withName(name);
    }

    public OltChildResourceBuilder withParentResources(ResourceId parentResources) {
        return (OltChildResourceBuilder) super.withParentResource(parentResources);
    }

    public OltChildResourceBuilder withBezeichnung(String bezeichnung) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_BEZEICHNUNG)
                        .withValues(Arrays.asList(bezeichnung))
                        .build()
        );
    }

    public OltChildResourceBuilder withHersteller(String hersteller) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_HERSTELLER)
                        .withValues(Arrays.asList(hersteller))
                        .build()
        );
    }

    public OltChildResourceBuilder withSeriennummer(String seriennummer) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_SERIENNUMMER)
                        .withValues(Arrays.asList(seriennummer))
                        .build()
        );
    }

    public OltChildResourceBuilder withModellnummer(String modellnummer) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_MODELLNUMMER)
                        .withValues(Arrays.asList(modellnummer))
                        .build()
        );
    }

    public OltChildResourceBuilder withOlt(String olt) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLT)
                        .withValues(Arrays.asList(olt))
                        .build()
        );
    }

    public OltChildResourceBuilder withOltRack(String oltrack) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLTRACK)
                        .withValues(Arrays.asList(oltrack))
                        .build()
        );
    }

    public OltChildResourceBuilder withOltSubrack(String oltsubrack) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLTSUBRACK)
                        .withValues(Arrays.asList(oltsubrack))
                        .build()
        );
    }

    public OltChildResourceBuilder withOltSlot(String oltslot) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLTSLOT)
                        .withValues(Arrays.asList(oltslot))
                        .build()
        );
    }

    public OltChildResourceBuilder withOltPort(String oltport) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLTPORT)
                        .withValues(Arrays.asList(oltport))
                        .build()
        );
    }

    public OltChildResourceBuilder withOltGponId(String oltgponid) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_OLTGPONID)
                        .withValues(Arrays.asList(oltgponid))
                        .build()
        );
    }

    public OltChildResourceBuilder withStandort(String standort) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_STANDORT)
                        .withValues(Arrays.asList(standort))
                        .build()
        );
    }

    public OltChildResourceBuilder withRaumbezeichnung(String raumbezeichung) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_RAUMBEZEICHNUNG)
                        .withValues(Arrays.asList(raumbezeichung))
                        .build()
        );
    }

    public OltChildResourceBuilder withInstallationsstatus(String installationsstatus) {
        return (OltChildResourceBuilder) super.addCharacteristic(
                new ResourceCharacteristicBuilder()
                        .withName(AbstractOltChildResourceMapper.CHARACTERISTIC_INSTALLATIONSSTATUS)
                        .withValues(Arrays.asList(installationsstatus))
                        .build()
        );
    }
}
