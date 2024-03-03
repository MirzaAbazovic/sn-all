/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentClass;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentKey;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocumentResponse;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.Item;

/**
 *
 */
public class GetDocumentResponseDocumentBuilder implements DocumentArchiveTypeBuilder<GetDocumentResponse.Document> {

    private String id;
    private String documentClass;
    private List<GetDocumentResponse.Document.Key> keys;
    private List<Item> items;

    @Override
    public GetDocumentResponse.Document build() {
        GetDocumentResponse.Document document = new GetDocumentResponse.Document();
        document.setId(id);
        document.setClazz(documentClass);
        if (keys != null) {
            document.getKey().addAll(keys);
        }
        if (items != null) {
            document.getItem().addAll(items);
        }
        return document;
    }

    public GetDocumentResponseDocumentBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public GetDocumentResponseDocumentBuilder withDocumentClass(ArchiveDocumentClass archiveDocumentClass) {
        this.documentClass = archiveDocumentClass.getDocumentClass();
        return this;
    }

    public GetDocumentResponseDocumentBuilder addKey(ArchiveDocumentKey archiveDocumentKey, String value, String type) {
        if (this.keys == null) {
            this.keys = new ArrayList<>();
        }
        GetDocumentResponse.Document.Key key = new GetDocumentResponse.Document.Key();
        key.setName(archiveDocumentKey.getKey());
        key.setValue(value);
        key.setType(type);
        keys.add(key);
        return this;
    }

    public GetDocumentResponseDocumentBuilder addItem(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        return this;
    }

}
