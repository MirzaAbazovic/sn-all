/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 21.09.2011 15:49:56
 */
package de.augustakom.hurrican.model.exmodules.archive;

/**
 * Enum mit der Angabe der moeglichen Dokument-Typen, die von der AIL unterstuetzt werden.
 */
public enum ArchiveDocumentType {

    CUDA_KUENDIGUNG("CuDa-Kündigung"),
    AUFTRAG("Auftrag"),
    LAGEPLAN("Lageplan"),
    LETZTE_TELEKOM_RECHNUNG("letzte Telekom Rechnung"),
    WITA_SONSTIGES("WITA sonstiges"),
    PORTIERUNGSAUFTRAG("Portierungsauftrag");

    private String documentTypeName;

    private ArchiveDocumentType(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

}
