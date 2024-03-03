/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.augustakom.hurrican.service.exmodules.archive.impl;

/**
 *
 */
public enum ArchiveDocumentClass {
    DOC_CLASS_TOP("TOP"),
    DOC_CLASS_AUFTRAG_UNTERLAGEN("Auftraege / Unterlagen");
    private final String documentClass;

    private ArchiveDocumentClass(String documentClass) {
        this.documentClass = documentClass;
    }

    public String getDocumentClass() {
        return documentClass;
    }

}
