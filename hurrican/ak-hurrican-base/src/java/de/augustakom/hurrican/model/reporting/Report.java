/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.02.2007 11:25:01
 */
package de.augustakom.hurrican.model.reporting;


/**
 * Model-Klasse für einen erzeugten Report
 *
 *
 */
public class Report extends AbstractReportIdModel {

    public static final Long REPORT_TYPE_AUFTRAG = Long.valueOf(1L);
    public static final Long REPORT_TYPE_KUNDE = Long.valueOf(2L);
    public static final Long REPORT_TYPE_INTERN = Long.valueOf(3L);

    // IDs für spezielle Reports
    public static final Long REPORT_ID_JASPER_ACCOUNT = Long.valueOf(1L);
    public static final Long REPORT_INSTALLATIONSAUFTRAG_ENDSTELLE_B = Long.valueOf(93);

    private String name;
    private String userw;
    private String description;
    private Long type;
    private Long reportGruppeId;
    private Long firstPage;
    private Long secondPage;
    private Boolean duplexDruck;
    private Boolean disabled;

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Festzulegender description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Festzulegender name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return userw
     */
    public String getUserw() {
        return userw;
    }

    /**
     * @param userw Festzulegender userw
     */
    public void setUserw(String userw) {
        this.userw = userw;
    }

    /**
     * @return type
     */
    public Long getType() {
        return type;
    }

    /**
     * @param type Festzulegender type
     */
    public void setType(Long type) {
        this.type = type;
    }

    /**
     * @return firstPage
     */
    public Long getFirstPage() {
        return firstPage;
    }

    /**
     * @param firstPage Festzulegender firstPage
     */
    public void setFirstPage(Long firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * @return secondPage
     */
    public Long getSecondPage() {
        return secondPage;
    }

    /**
     * @param secondPage Festzulegender secondPage
     */
    public void setSecondPage(Long secondPage) {
        this.secondPage = secondPage;
    }

    /**
     * @return reportGroupId
     */
    public Long getReportGruppeId() {
        return reportGruppeId;
    }

    /**
     * @param reportGroupId Festzulegender reportGroupId
     */
    public void setReportGruppeId(Long reportGroupId) {
        this.reportGruppeId = reportGroupId;
    }

    /**
     * @return duplexDruck
     */
    public Boolean getDuplexDruck() {
        return duplexDruck;
    }

    /**
     * @param duplexDruck Festzulegender duplexDruck
     */
    public void setDuplexDruck(Boolean duplexDruck) {
        this.duplexDruck = duplexDruck;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}
