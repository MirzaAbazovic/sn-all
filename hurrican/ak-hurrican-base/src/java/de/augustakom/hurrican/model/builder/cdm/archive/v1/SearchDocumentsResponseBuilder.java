/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocumentsResponse;

/**
 *
 */
public class SearchDocumentsResponseBuilder implements DocumentArchiveTypeBuilder<SearchDocumentsResponse> {

    private List<SearchDocumentsResponse.Document> documents;

    @Override
    public SearchDocumentsResponse build() {
        SearchDocumentsResponse searchDocumentsResponse = new SearchDocumentsResponse();
        if (documents != null) {
            searchDocumentsResponse.getDocument().addAll(documents);
        }
        return searchDocumentsResponse;
    }

    public SearchDocumentsResponseBuilder addDocument(SearchDocumentsResponse.Document document) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        this.documents.add(document);
        return this;
    }

}
