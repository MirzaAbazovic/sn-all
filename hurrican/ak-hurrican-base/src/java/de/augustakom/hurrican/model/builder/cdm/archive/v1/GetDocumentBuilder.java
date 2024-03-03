/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocument;

/**
 *
 */
public class GetDocumentBuilder implements DocumentArchiveTypeBuilder<GetDocument> {

    private String endUserId;
    private String documentId;

    @Override
    public GetDocument build() {
        GetDocument getDocument = new GetDocument();
        getDocument.setDocumentId(documentId);
        getDocument.setEndUserId(endUserId);
        return getDocument;
    }

    public GetDocumentBuilder withEndUserId(String endUserId) {
        this.endUserId = endUserId;
        return this;
    }

    public GetDocumentBuilder withDocumentId(String documentId) {
        this.documentId = documentId;
        return this;
    }

}
