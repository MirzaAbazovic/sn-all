/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.07.13
 */
package de.augustakom.hurrican.gui.tools.wbci.tables;

import static de.augustakom.hurrican.gui.tools.wbci.tables.PreAgreementTableModel.ColumnMetaData.*;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

import de.augustakom.authentication.model.AKUser;
import de.augustakom.common.gui.swing.table.AKReferenceAwareTableModel;
import de.augustakom.common.gui.swing.table.FilterOperator;
import de.augustakom.common.gui.swing.table.FilterOperators;
import de.augustakom.common.gui.swing.table.FilterRelation;
import de.augustakom.common.gui.swing.table.FilterRelations;
import de.augustakom.common.gui.swing.table.ReflectionTableMetaDataWithSize;
import de.augustakom.hurrican.gui.HurricanSystemRegistry;
import de.augustakom.hurrican.gui.tools.wbci.filter.PreAgreementTableFilter;
import de.augustakom.hurrican.gui.tools.wbci.filter.PreAgreementTableFilterRelation;
import de.mnet.wbci.model.CarrierRole;
import de.mnet.wbci.model.PreAgreementVO;
import de.mnet.wbci.model.Technologie;
import de.mnet.wbci.model.WbciRequestStatus;

/**
 * Represents the matching between the gui table and the {@link PreAgreementVO} object.
 *
 *
 */
public abstract class PreAgreementTableModel extends AKReferenceAwareTableModel<PreAgreementVO> {

    private static final long serialVersionUID = 5777368826738675875L;

    private static final String EIGENE_TASKS_FILTER_NAME = "eigene.tasks.filter";
    private static final String TEAM_TASKS_FILTER_NAME = "team.tasks.filter";
    private static final String DROPDOWN_FILTER_NAME = "dd.filter";
    private final AKUser user;
    // used for recording the active filters [filterName|filterValue]
    private final Map<String, Object> activeFilters = new HashMap<>();

    public PreAgreementTableModel(ReflectionTableMetaDataWithSize... allMetaData) {
        super(allMetaData);
        user = getCurrentUser();
    }

    /**
     * Creates the table meta-data for the given carrier-role.
     *
     * @param carrierRole
     * @return
     */
    protected static List<ReflectionTableMetaDataWithSize> createTabelModelMetaData(CarrierRole carrierRole) {
        List<ReflectionTableMetaDataWithSize> tableModel = new ArrayList<>();
        for (ColumnMetaData metaData : ColumnMetaData.values()) {
            if (metaData.carrierRole == null || metaData.carrierRole.equals(carrierRole)) {
                tableModel.add(new ReflectionTableMetaDataWithSize(metaData.colName, metaData.propertyName,
                        metaData.colType, metaData.colSize));
            }
        }
        return tableModel;
    }

    protected AKUser getCurrentUser() {
        return HurricanSystemRegistry.instance().getCurrentUser();
    }

    public void showMyTasks() {
        removeColumnFilter(TEAM_TASKS_FILTER_NAME);
        addColumnFilter(EIGENE_TASKS_FILTER_NAME, COL_USER, user.getLoginName());
    }

    public void showTeamTasks() {
        removeColumnFilter(EIGENE_TASKS_FILTER_NAME);
        addColumnFilter(TEAM_TASKS_FILTER_NAME, COL_TEAM, user.getTeam() != null ? user.getTeam().getName() : null);
    }

    public void showAllTasks() {
        removeColumnFilter(EIGENE_TASKS_FILTER_NAME);
        removeColumnFilter(TEAM_TASKS_FILTER_NAME);
    }

    public boolean isOnlyMyTasksFilterSet() {
        return isFilterActive(EIGENE_TASKS_FILTER_NAME);
    }

    public boolean isOnlyTeamTasksFilterSet() {
        return isFilterActive(TEAM_TASKS_FILTER_NAME);
    }

    /**
     * Applies the drop-down filter. If the drop-down filter has already been applied with the same {@code filterValue}
     * then nothing happens. If the drop-down filter has already been applied BUT with a different {@code filterValue}
     * then this filter is removed first before applying the new filter.
     *
     * @param filterEntry the column to be filtered
     */
    public void filterByDropdownSelection(PreAgreementTableFilterEntry filterEntry) {
        if (isFilterActive(DROPDOWN_FILTER_NAME) && getFilterValue(DROPDOWN_FILTER_NAME).equals(filterEntry.getDescription())) {
            // nothing to do since this filter is already active
            return;
        }

        if (isFilterActive(DROPDOWN_FILTER_NAME)) {
            // remove the filter first since the filter value has changed
            removeColumnFilter(DROPDOWN_FILTER_NAME);
        }
        addDropDownFilter(filterEntry);
    }

    public boolean isDropdownSelectionFilterActive() {
        return isFilterActive(DROPDOWN_FILTER_NAME);
    }

    public String getDropdownSelectionFilterValue() {
        return (String) getFilterValue(DROPDOWN_FILTER_NAME);
    }

    public void removeDropdownSelectionFilter() {
        removeColumnFilter(DROPDOWN_FILTER_NAME);
    }

    private synchronized boolean isFilterActive(String filterName) {
        return activeFilters.containsKey(filterName);
    }

    private synchronized Object getFilterValue(String filterName) {
        return activeFilters.get(filterName);
    }

    private synchronized void addDropDownFilter(PreAgreementTableFilterEntry filterEntry) {
        addFilter(filterEntry.createFilterRelation(DROPDOWN_FILTER_NAME));
        activeFilters.put(DROPDOWN_FILTER_NAME, filterEntry.getDescription());
    }

    private synchronized void addColumnFilter(String filterName, ColumnMetaData columnMetaData, Object filterValue) {
        FilterRelation relation = new FilterRelation(FilterRelations.AND);
        int idx = findColumn(columnMetaData.colName);
        relation.addChild(new FilterOperator(filterName, FilterOperators.EQ, filterValue, idx));
        addFilter(relation);
        activeFilters.put(filterName, filterValue);
    }

    private synchronized void removeColumnFilter(String filterName) {
        if (isFilterActive(filterName)) {
            removeFilter(filterName);
            activeFilters.remove(filterName);
        }
    }

    public abstract List<ReflectionTableMetaDataWithSize> getDataModelMetaData();

    // Table Columns
    public enum ColumnMetaData {
        //@formatter:off
        COL_VAID(                          0, "Vorabstimmungs-ID", "vaid", String.class, 130),
        COL_AUFTRAGSNUMMER_TECH(           1, "Tech. Auftragsnr.", "auftragId", Long.class, 90),
        COL_AUFTRAGSNUMMER_TAIFUN(         2, "Taifun Auftragsnr.", "auftragNoOrig", Long.class, 90),
        COL_PRE_AGREEMENT_TYPE(            3, "PK/GK/WS", "preAgreementType", String.class, 40),
        COL_GF(                            4, "Geschäftsfall", "gfTypeShortName", String.class, 80),
        COL_AENDERUNGSKZ(                  5, "AEK", "aenderungskz", String.class, 35),
        COL_EKP_AUF(                       6, "EKPauf", "ekpAufITU", String.class,70, CarrierRole.ABGEBEND),
        COL_EKP_ABG(                       6, "EKPabg", "ekpAbgITU", String.class, 70, CarrierRole.AUFNEHMEND),
        COL_VORGABEDATUM(                  7, "KWT", "vorgabeDatum", Date.class, 70),
        COL_WECHSELTERMIN(                 8, "Wechseltermin", "wechseltermin", Date.class, 70),
        COL_STATUS(                        9, "Status", "requestStatus", WbciRequestStatus.class, 120),
        COL_UEBERMITTELT(                 10, "übermittelt am", "processedAt", Date.class, 70),
        COL_RUECKMELDUNG(                 11, "Rückmeldung", "rueckmeldung", String.class, 90),
        COL_RUECKMELDE_DATUM(             12, "Rückmeldung am", "rueckmeldeDatum", Date.class, 70),
        COL_USER(                         13, "Bearbeiter", "userName", String.class, 70),
        COL_TEAM(                         14, "Team", "teamName", String.class, 70),
        COL_CURRENT_USER(                 15, "Aktueller Bearbeiter", "currentUserName", String.class, 70),
        COL_NIEDERLASSUNG(                16, "Niederlassung", "niederlassung", String.class, 70),
        COL_OPT_MNET_TECHNOLOGIE(         17, "Technologie M-net", "mnetTechnologie", Technologie.class, 120),
        COL_INTERNAL_STATUS(              18, "Bearbeitungsstatus", "internalStatus", String.class, 120),
        COL_KLAERFALL(                    19, "Klärfall", "klaerfall", Boolean.class, 50),
        COL_AUTOMATABLE(                  20, "Autom.", "automatable", Boolean.class, 50),
        COL_GF_STATUS(                    21, "GF Status", "geschaeftsfallStatusDescription", String.class, 120),
        COL_DAYS_UNTILL_DEADLINE_MNET(    22, "M-net Tageszähler", "daysUntilDeadlineMnet", Integer.class, 45),
        COL_DAYS_UNTILL_DEADLINE_PARTNER( 23, "Partner Tageszähler", "daysUntilDeadlinePartner", Integer.class, 45);
        //@formatter:on

        protected final int colNumber;
        protected final String colName;
        protected final String propertyName;
        protected final Class colType;
        protected final int colSize;

        /**
         * Used for filtering out certain columns in the pre-agreement table.<br /> When displaying the Aufnehmend
         * (receiving) table all columns that have either a {@code carrierRole} equal to {@code null} or {@link
         * CarrierRole#AUFNEHMEND} will be rendered. For Abgebend (donating) the columns that have a {@code carrierRole}
         * equal to {@code null} or {@link CarrierRole#ABGEBEND} will be rendered.
         */
        protected final CarrierRole carrierRole;

        private ColumnMetaData(int colNumber, String colName, String propertyName, Class colType, int colSize) {
            this(colNumber, colName, propertyName, colType, colSize, null);
        }

        private ColumnMetaData(int colNumber, String colName, String propertyName, Class colType, int colSize, CarrierRole carrierRole) {
            this.colNumber = colNumber;
            this.colName = colName;
            this.propertyName = propertyName;
            this.colType = colType;
            this.colSize = colSize;
            this.carrierRole = carrierRole;
        }
    }

    public static class PreAgreementTableFilterEntry {
        private final String description;
        private FilterRelations filterRelations;
        private List<PreAgreementTableFilter> tableFilters;
        private List<PreAgreementTableFilterRelation> tableFilterRelations;

        public PreAgreementTableFilterEntry(String description) {
            this.description = description;
        }

        public PreAgreementTableFilterEntry(String description, FilterRelations filterRelations,
                PreAgreementTableFilter... tableFilters) {
            this(description);
            this.filterRelations = filterRelations;
            this.tableFilters = Arrays.asList(tableFilters);
        }

        public PreAgreementTableFilterEntry(String description, FilterRelations filterRelations,
                PreAgreementTableFilterRelation... tableFilterRelations) {
            this(description);
            this.filterRelations = filterRelations;
            this.tableFilterRelations = Arrays.asList(tableFilterRelations);
        }

        public FilterRelation createFilterRelation(String filterName) {
            if (!CollectionUtils.isEmpty(tableFilters)) {
                return createRelation(filterName, filterRelations, tableFilters);
            }
            else if (!CollectionUtils.isEmpty(tableFilterRelations)) {
                return createdNestedRelation(filterName, filterRelations, tableFilterRelations);
            }
            throw new IllegalStateException("One of the collections tableFilters or tableFilterRelations should be set!");
        }

        private FilterRelation createRelation(String filterName, FilterRelations filterRelations,
                List<PreAgreementTableFilter> dropDownFilters) {
            FilterRelation relation = new FilterRelation(filterRelations);
            for (PreAgreementTableFilter tableFilter : dropDownFilters) {
                int idx = tableFilter.getColumnMetaData().colNumber;
                relation.addChild(new FilterOperator(filterName, tableFilter.getFilterOperator(),
                        tableFilter.getFilterValue(), idx, tableFilter.getPropertyName()));
            }
            return relation;
        }

        private FilterRelation createdNestedRelation(String filterName, FilterRelations filterRelations,
                List<PreAgreementTableFilterRelation> dropDownFilterRelations) {
            FilterRelation relation = new FilterRelation(filterRelations);
            for (PreAgreementTableFilterRelation tableFilterRelation : dropDownFilterRelations) {
                relation.addChild(createRelation(filterName, tableFilterRelation.getFilterRelations(),
                        tableFilterRelation.getDropDownFilters()));
            }
            return relation;
        }

        public String getDescription() {
            return description;
        }
    }

}
