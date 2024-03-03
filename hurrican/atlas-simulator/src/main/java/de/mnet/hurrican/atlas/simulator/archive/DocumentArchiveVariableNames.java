/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.12.14
 */
package de.mnet.hurrican.atlas.simulator.archive;

/**
 *
 */
public enum DocumentArchiveVariableNames {

    UUID("uuid"),
    DOCUMENT_ID("documentId"),
    DOCUMENT_TYPE("archiveDocumentType"),
    FILE_DATA("fileData"),
    FILE_EXTENSION("fileExtension"),
    FILE_LENGTH("fileLength"),
    FILE_NAME("fileName");

    private String name;

    /**
     * Constructor using name field.
     */
    private DocumentArchiveVariableNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
