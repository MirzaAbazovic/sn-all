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
public enum ArchiveDocumentKey {
    DOCUMENT_TYPE("Art des Dokuments"),
    VERTRAG("Vertrag"),
    UUID_KEY("UUID"),
    CUSTOMER_NR("Kundennr."),
    DEBITOR_NR("Debitornr."),
    TAIFUN_ORDER_NR("TAI_AuftragsNr"),
    DOCUMENT_DATE("Dokumentendatum");

    private final String key;

    private ArchiveDocumentKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
