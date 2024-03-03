/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.03.2007 11:04:28
 */
package de.augustakom.hurrican.model.reporting.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.reporting.AbstractReportModel;

/**
 * View für die Zuordnung eines Reports zu Produkten und techn. Leistungen
 *
 *
 */
public class Report2TechLsView extends AbstractReportModel {

    private Long reportId = null;
    private String reportName = null;
    private Long produktId = null;
    private String produktName = null;
    private Long techLsId = null;
    private String techLsName = null;

    /**
     * @return produktId
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId Festzulegender produktId
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @return produktName
     */
    public String getProduktName() {
        return produktName;
    }

    /**
     * @param produktName Festzulegender produktName
     */
    public void setProduktName(String produktName) {
        this.produktName = produktName;
    }

    /**
     * @return reportId
     */
    public Long getReportId() {
        return reportId;
    }

    /**
     * @param reportId Festzulegender reportId
     */
    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    /**
     * @return reportName
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * @param reportName Festzulegender reportName
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * @return techLsId
     */
    public Long getTechLsId() {
        return techLsId;
    }

    /**
     * @param techLsId Festzulegender techLsId
     */
    public void setTechLsId(Long techLsId) {
        this.techLsId = techLsId;
    }

    /**
     * @return techLsName
     */
    public String getTechLsName() {
        return techLsName;
    }

    /**
     * @param techLsName Festzulegender techLsName
     */
    public void setTechLsName(String techLsName) {
        this.techLsName = techLsName;
    }

    /**
     * Funktion überprüft das übergebene Objekt auf Gleichheit mit der aktuellen Instanz
     *
     * @return
     *
     */
    public Boolean hasSameValues(Report2TechLsView view) {
        Boolean test = this.produktId.equals(view.getProduktId())
                && StringUtils.equals(this.produktName, view.getProduktName())
                && this.reportId.equals(view.getReportId())
                && StringUtils.equals(this.reportName, view.getReportName())
                && this.techLsId.equals(view.getTechLsId())
                && StringUtils.equals(this.techLsName, view.getTechLsName());
        return test;
    }


}
