/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentKey;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.ArchiveDocument;

/**
 *
 */
public class ArchiveDocumentBulder implements DocumentArchiveTypeBuilder<ArchiveDocument> {

    private String endUserId;
    private ArchiveDocument.Document document;
    private List<ArchiveDocument.ArchiveKey> archiveKeys;

    @Override
    public ArchiveDocument build() {
        ArchiveDocument archiveDocument = new ArchiveDocument();
        archiveDocument.setEndUserId(endUserId);
        archiveDocument.setDocument(document);
        if (archiveKeys != null) {
            archiveDocument.getArchiveKey().addAll(archiveKeys);
        }
        return archiveDocument;
    }

    public ArchiveDocumentBulder withEndUserId(String endUserId) {
        this.endUserId = endUserId;
        return this;
    }

    public ArchiveDocumentBulder withDocument(ArchiveDocument.Document document) {
        this.document = document;
        return this;
    }

    public ArchiveDocumentBulder addArchiveKey(ArchiveDocumentKey archiveDocumentKey, String value) {
        if (archiveKeys == null) {
            archiveKeys = new ArrayList<>();
        }
        ArchiveDocument.ArchiveKey archiveKey = new ArchiveDocument.ArchiveKey();
        archiveKey.setName(archiveDocumentKey.getKey());
        archiveKey.setValue(value);
        this.archiveKeys.add(archiveKey);
        return this;
    }

}
