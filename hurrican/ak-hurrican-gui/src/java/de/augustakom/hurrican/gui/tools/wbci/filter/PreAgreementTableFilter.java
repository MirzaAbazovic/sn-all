/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.01.14
 */
package de.augustakom.hurrican.gui.tools.wbci.filter;

import static de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel.ColumnMetaData.*;

import java.util.*;

import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel;
import de.mnet.wbci.model.PreAgreementType;
import de.mnet.wbci.model.MeldungTyp;
import de.mnet.wbci.model.WbciGeschaeftsfallStatus;

/**
 * Filter enumeration representing different filters, which of it refers to a single table column and have a single
 * filter operation like equal, less or equal etc.
 *
 *
 */
public enum PreAgreementTableFilter {

    ACTIVE(COL_GF_STATUS, FilterOperators.EQ, WbciGeschaeftsfallStatus.ACTIVE.getDescription()),
    PASSIVE(COL_GF_STATUS, FilterOperators.EQ, WbciGeschaeftsfallStatus.PASSIVE.getDescription()),
    NEW_VA(COL_GF_STATUS, FilterOperators.EQ, WbciGeschaeftsfallStatus.NEW_VA.getDescription()),
    NEW_VA_EXPIRED(COL_GF_STATUS, FilterOperators.EQ, WbciGeschaeftsfallStatus.NEW_VA_EXPIRED.getDescription()),

    DUE_SOON_MNET(COL_DAYS_UNTILL_DEADLINE_MNET, FilterOperators.LT_EQ, 2),
    DUE_SOON_PARTNER(COL_DAYS_UNTILL_DEADLINE_PARTNER, FilterOperators.LT_EQ, 2),
    KLAERFAELLE(COL_KLAERFALL, FilterOperators.EQ, Boolean.TRUE),

    AKM_TR(COL_RUECKMELDUNG, FilterOperators.EQ, MeldungTyp.AKM_TR.toString()),
    RUEM_VA(COL_RUECKMELDUNG, FilterOperators.EQ, MeldungTyp.RUEM_VA.toString()),
    ERLM(COL_RUECKMELDUNG, FilterOperators.EQ, MeldungTyp.ERLM.toString()),

    ABBM(COL_RUECKMELDUNG, FilterOperators.EQ, MeldungTyp.ABBM.toString()),
    ABBM_TR(COL_RUECKMELDUNG, FilterOperators.EQ, MeldungTyp.ABBM_TR.toString()),

    MELDUNG_PROCESSED_TODAY(COL_RUECKMELDE_DATUM, FilterOperators.EQ, new Date()),
    REQUEST_PROCESSED_TODAY(COL_UEBERMITTELT, FilterOperators.EQ, new Date()),

    INCOMMING(COL_STATUS, FilterOperators.EQ, Boolean.TRUE, "inbound"),
    OUTGOING(COL_STATUS, FilterOperators.EQ, Boolean.FALSE, "inbound"),

    PK(COL_PRE_AGREEMENT_TYPE, FilterOperators.EQ, PreAgreementType.PK),
    GK(COL_PRE_AGREEMENT_TYPE, FilterOperators.EQ, PreAgreementType.GK),
    WS(COL_PRE_AGREEMENT_TYPE, FilterOperators.EQ, PreAgreementType.WS),



    NO_TEAM_SET(COL_TEAM, FilterOperators.NOT_EQ, "*");

    private final Object filterValue;
    private final FilterOperators filterOperator;
    private final PreAgreementTableModel.ColumnMetaData columnMetaData;
    private final String propertyName;

    private PreAgreementTableFilter(PreAgreementTableModel.ColumnMetaData columnMetaData,
            FilterOperators filterOperator, Object filterValue) {
        this(columnMetaData, filterOperator, filterValue, null);
    }

    private PreAgreementTableFilter(PreAgreementTableModel.ColumnMetaData columnMetaData,
            FilterOperators filterOperator, Object filterValue, String propertyName) {
        this.columnMetaData = columnMetaData;
        this.filterOperator = filterOperator;
        this.filterValue = filterValue;
        this.propertyName = propertyName;
    }

    public Object getFilterValue() {
        return filterValue;
    }

    public FilterOperators getFilterOperator() {
        return filterOperator;
    }

    public PreAgreementTableModel.ColumnMetaData getColumnMetaData() {
        return columnMetaData;
    }

    public String getPropertyName() {
        return propertyName;
    }

}
