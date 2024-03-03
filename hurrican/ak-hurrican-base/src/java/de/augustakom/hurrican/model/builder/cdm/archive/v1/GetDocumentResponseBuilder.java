/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.GetDocumentResponse;

/**
 *
 */
public class GetDocumentResponseBuilder implements DocumentArchiveTypeBuilder<GetDocumentResponse> {

    private GetDocumentResponse.Document document;

    @Override
    public GetDocumentResponse build() {
        GetDocumentResponse response = new GetDocumentResponse();
        response.setDocument(document);
        return response;
    }

    public GetDocumentResponseBuilder withDocument(GetDocumentResponse.Document document) {
        this.document = document;
        return this;
    }

}
