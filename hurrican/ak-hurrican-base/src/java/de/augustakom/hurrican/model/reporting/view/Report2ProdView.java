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
 * View für die Zuordnung eines Reports zu Produkten und Auftragsstatus
 *
 *
 */
public class Report2ProdView extends AbstractReportModel {

    private Long reportId = null;
    private String reportName = null;
    private Long produktId = null;
    private String produktName = null;
    private Long statusId = null;
    private String statusName = null;

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
     * @return statusId
     */
    public Long getStatusId() {
        return statusId;
    }

    /**
     * @param statusId Festzulegender statusId
     */
    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    /**
     * @return statusName
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     * @param statusName Festzulegender statusName
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    /**
     * Funktion überprüft das übergebene Objekt auf Gleichheit mit der aktuellen Instanz
     *
     * @return
     *
     */
    public Boolean hasSameValues(Report2ProdView view) {
        Boolean test = this.produktId.equals(view.getProduktId())
                && StringUtils.equals(this.produktName, view.getProduktName())
                && this.reportId.equals(view.getReportId())
                && StringUtils.equals(this.reportName, view.getReportName())
                && this.statusId.equals(view.getStatusId())
                && StringUtils.equals(this.statusName, view.getStatusName());
        return test;
    }


}
