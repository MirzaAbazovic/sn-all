/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentClass;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ArchiveDocument;

/**
 *
 */
public class ArchiveDocumentDocumentBuilder implements DocumentArchiveTypeBuilder<ArchiveDocument.Document> {

    private String documentClass;
    private List<ArchiveDocument.Document.Item> items;

    @Override
    public ArchiveDocument.Document build() {
        ArchiveDocument.Document document = new ArchiveDocument.Document();
        document.setClazz(documentClass);
        if (items != null) {
            document.getItem().addAll(items);
        }
        return document;
    }

    public ArchiveDocumentDocumentBuilder withDocumentClass(ArchiveDocumentClass docClass) {
        this.documentClass = docClass.getDocumentClass();
        return this;
    }

    public ArchiveDocumentDocumentBuilder addItem(String fileName, byte[] fileData) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        ArchiveDocument.Document.Item item = new ArchiveDocument.Document.Item();
        item.setFileName(fileName);
        item.setFileData(fileData);
        this.items.add(item);
        return this;
    }

}
