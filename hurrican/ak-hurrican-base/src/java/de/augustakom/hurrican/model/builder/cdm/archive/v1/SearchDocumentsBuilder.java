/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentClass;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocuments;

/**
 *
 */
public class SearchDocumentsBuilder implements DocumentArchiveTypeBuilder<SearchDocuments> {

    private String endUserId;
    private String docClass;
    private SearchDocuments.SearchCriteria searchCriteria;

    @Override
    public SearchDocuments build() {
        SearchDocuments searchDocuments = new SearchDocuments();
        searchDocuments.setEndUserId(endUserId);
        searchDocuments.setDocClass(docClass);
        searchDocuments.setSearchCriteria(searchCriteria);
        return searchDocuments;
    }

    public SearchDocumentsBuilder withEndUserId(String endUserId) {
        this.endUserId = endUserId;
        return this;
    }

    public SearchDocumentsBuilder withDocClass(ArchiveDocumentClass docClass) {
        this.docClass = docClass.getDocumentClass();
        return this;
    }

    public SearchDocumentsBuilder withSearchCriteria(SearchDocuments.SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
        return this;
    }

}
