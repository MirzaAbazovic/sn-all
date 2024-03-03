/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.12.2014
 */
package de.augustakom.hurrican.model.builder.cdm.archive.v1;

import java.math.*;
import java.util.*;

import de.augustakom.hurrican.model.builder.cdm.archive.DocumentArchiveTypeBuilder;
import de.augustakom.hurrican.service.exmodules.archive.impl.ArchiveDocumentKey;
import de.mnet.esb.cdm.customer.documentarchiveservice.v1.SearchDocuments;

/**
 *
 */
public class SearchCriteriaBuilder implements DocumentArchiveTypeBuilder<SearchDocuments.SearchCriteria> {

    public static enum Operator {
        AND, OR;
    }

    private List<SearchDocuments.SearchCriteria.Key> keys;
    private BigInteger maxHits;
    private String operator;
    private Boolean recursive;

    @Override
    public SearchDocuments.SearchCriteria build() {
        SearchDocuments.SearchCriteria searchCriteria = new SearchDocuments.SearchCriteria();
        searchCriteria.setMaxHits(maxHits);
        searchCriteria.setOperator(operator);
        searchCriteria.setRecursive(recursive);
        if (keys != null) {
            searchCriteria.getKey().addAll(keys);
        }
        return searchCriteria;
    }

    public SearchCriteriaBuilder addKey(ArchiveDocumentKey searchKey, String value) {
        if (keys == null) {
            keys = new ArrayList<>();
        }
        SearchDocuments.SearchCriteria.Key key = new SearchDocuments.SearchCriteria.Key();
        key.setName(searchKey.getKey());
        key.setValue(value);
        keys.add(key);
        return this;
    }

    public SearchCriteriaBuilder addKeys(Map<ArchiveDocumentKey, String> searchKeys) {
        for (Map.Entry<ArchiveDocumentKey, String> searchKeyStringEntry : searchKeys.entrySet()) {
            addKey(searchKeyStringEntry.getKey(), searchKeyStringEntry.getValue());
        }
        return this;
    }

    public SearchCriteriaBuilder withMaxHits(BigInteger maxHits) {
        this.maxHits = maxHits;
        return this;
    }

    public SearchCriteriaBuilder withOperator(Operator operator) {
        this.operator = operator != null ? operator.name() : null;
        return this;
    }

    public SearchCriteriaBuilder withRecursive(Boolean recursive) {
        this.recursive = recursive;
        return this;
    }

}
