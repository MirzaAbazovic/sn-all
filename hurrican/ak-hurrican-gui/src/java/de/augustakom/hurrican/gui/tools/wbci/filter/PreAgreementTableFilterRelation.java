/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.filter;

import java.util.*;
import javax.validation.constraints.*;

import de.augustakom.common.gui.swing.table.FilterRelations;

/**
 * Filter relation enumeration representing different nested filters. One filter relation has a relation and a list of
 * filter enumerations. Use this filter relation, if you want to combine filters, which refers to different table
 * columns.
 *
 *
 */
public enum PreAgreementTableFilterRelation {

    MELDUNG_INCOMING_PROCESSED_TODAY(FilterRelations.AND, PreAgreementTableFilter.MELDUNG_PROCESSED_TODAY,
            PreAgreementTableFilter.INCOMMING),
    MELDUNG_OUTGOING_PROCESSED_TODAY(FilterRelations.AND, PreAgreementTableFilter.MELDUNG_PROCESSED_TODAY,
            PreAgreementTableFilter.OUTGOING),
    REQUEST_INCOMING_PROCESSED_TODAY(FilterRelations.AND, PreAgreementTableFilter.REQUEST_PROCESSED_TODAY,
            PreAgreementTableFilter.INCOMMING),
    REQUEST_OUTGOING_PROCESSED_TODAY(FilterRelations.AND, PreAgreementTableFilter.REQUEST_PROCESSED_TODAY,
            PreAgreementTableFilter.OUTGOING);

    private final FilterRelations filterRelations;
    private final List<PreAgreementTableFilter> dropDownFilters;

    private PreAgreementTableFilterRelation(@NotNull FilterRelations filterRelations,
            @NotNull PreAgreementTableFilter... dropDownFilters) {
        this.filterRelations = filterRelations;
        this.dropDownFilters = Arrays.asList(dropDownFilters);
    }

    public FilterRelations getFilterRelations() {
        return filterRelations;
    }

    public List<PreAgreementTableFilter> getDropDownFilters() {
        return dropDownFilters;
    }

}
