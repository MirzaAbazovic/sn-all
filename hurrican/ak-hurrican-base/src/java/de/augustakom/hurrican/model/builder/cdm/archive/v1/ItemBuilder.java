/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.math.*;
import java.time.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.Item;

/**
 *
 */
public class ItemBuilder implements DocumentArchiveTypeBuilder<Item> {

    private String id;
    private String name;
    private String extension;
    private LocalDateTime creationDate;
    private BigInteger size;
    private byte[] data;

    @Override
    public Item build() {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setExtension(extension);
        item.setCreationDate(creationDate);
        item.setSize(size);
        item.setData(data);
        return item;
    }

    public ItemBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public ItemBuilder withCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public ItemBuilder withSize(BigInteger size) {
        this.size = size;
        return this;
    }

    public ItemBuilder withData(byte[] data) {
        this.data = data;
        return this;
    }

}
