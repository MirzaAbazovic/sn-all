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
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.Item;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocumentsResponse;

/**
 *
 */
public class SearchDocumentsResponseDocumentBuilder implements DocumentArchiveTypeBuilder<SearchDocumentsResponse.Document> {

    private String id;
    private String documentClass;
    private List<SearchDocumentsResponse.Document.Key> keys;
    private List<Item> items;

    @Override
    public SearchDocumentsResponse.Document build() {
        SearchDocumentsResponse.Document document = new SearchDocumentsResponse.Document();
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

    public SearchDocumentsResponseDocumentBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public SearchDocumentsResponseDocumentBuilder withDocumentClass(ArchiveDocumentClass archiveDocumentClass) {
        this.documentClass = archiveDocumentClass.getDocumentClass();
        return this;
    }

    public SearchDocumentsResponseDocumentBuilder addKey(ArchiveDocumentKey archiveDocumentKey, String value, String type) {
        if (this.keys == null) {
            this.keys = new ArrayList<>();
        }
        SearchDocumentsResponse.Document.Key key = new SearchDocumentsResponse.Document.Key();
        key.setName(archiveDocumentKey.getKey());
        key.setValue(value);
        key.setType(type);
        keys.add(key);
        return this;
    }

    public SearchDocumentsResponseDocumentBuilder addItem(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        return this;
    }

}
