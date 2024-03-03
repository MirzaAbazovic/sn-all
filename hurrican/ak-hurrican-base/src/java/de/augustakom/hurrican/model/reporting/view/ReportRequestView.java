/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.03.2007 16:48:28
 */
package de.augustakom.hurrican.model.reporting.view;

import java.util.*;

import de.augustakom.hurrican.model.reporting.AbstractReportModel;
import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * View für einen ReportRequest
 *
 *
 */
public class ReportRequestView extends AbstractReportModel implements KundenModel {

    private Long requestId = null;
    private Long reportId = null;
    private String reportName = null;
    private String reportUserw = null;
    private String reportDescription = null;
    private Integer buendelNo = null;
    private Long kundeNo = null;
    private Long orderNoOrig = null;
    private Long auftragId = null;
    private String requestFrom = null;
    private Date requestAt = null;
    private Date requestFinishedAt = null;
    private Date reportDownloadedAt = null;
    private Date reportArchivedAt = null;
    private Boolean reportRFA = null;
    private String file = null;
    private String error = null;
    private String printReason = null;

    /**
     * @return auftragId
     */
    public Long getAuftragId() {
        return auftragId;
    }

    /**
     * @param auftragId Festzulegender auftragId
     */
    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    /**
     * @return file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file Festzulegender file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return orderNoOrig
     */
    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    /**
     * @param orderNoOrig Festzulegender orderNoOrig
     */
    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    /**
     * @return reportDescription
     */
    public String getReportDescription() {
        return reportDescription;
    }

    /**
     * @param reportDescription Festzulegender reportDescription
     */
    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    /**
     * @return reportDownloadedAt
     */
    public Date getReportDownloadedAt() {
        return reportDownloadedAt;
    }

    /**
     * @param reportDownloadedAt Festzulegender reportDownloadedAt
     */
    public void setReportDownloadedAt(Date reportDownloadedAt) {
        this.reportDownloadedAt = reportDownloadedAt;
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
     * @return reportUserw
     */
    public String getReportUserw() {
        return reportUserw;
    }

    /**
     * @param reportUserw Festzulegender reportUserw
     */
    public void setReportUserw(String reportUserw) {
        this.reportUserw = reportUserw;
    }

    /**
     * @return requestAt
     */
    public Date getRequestAt() {
        return requestAt;
    }

    /**
     * @param requestAt Festzulegender requestAt
     */
    public void setRequestAt(Date requestAt) {
        this.requestAt = requestAt;
    }

    /**
     * @return requestFinishedAt
     */
    public Date getRequestFinishedAt() {
        return requestFinishedAt;
    }

    /**
     * @param requestFinishedAt Festzulegender requestFinishedAt
     */
    public void setRequestFinishedAt(Date requestFinishedAt) {
        this.requestFinishedAt = requestFinishedAt;
    }

    /**
     * @return requestFrom
     */
    public String getRequestFrom() {
        return requestFrom;
    }

    /**
     * @param requestFrom Festzulegender requestFrom
     */
    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    /**
     * @return requestId
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * @param requestId Festzulegender requestId
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * @return error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error Festzulegender error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return buendelNo
     */
    public Integer getBuendelNo() {
        return buendelNo;
    }

    /**
     * @param buendelNo Festzulegender buendelNo
     */
    public void setBuendelNo(Integer buendelNo) {
        this.buendelNo = buendelNo;
    }

    /**
     * @return reportArchivedAt
     */
    public Date getReportArchivedAt() {
        return reportArchivedAt;
    }

    /**
     * @param reportArchivedAt Festzulegender reportArchivedAt
     */
    public void setReportArchivedAt(Date reportArchivedAt) {
        this.reportArchivedAt = reportArchivedAt;
    }

    /**
     * @return reportRFA
     */
    public Boolean getReportRFA() {
        return reportRFA;
    }

    /**
     * @param reportRFA Festzulegender reportRFA
     */
    public void setReportRFA(Boolean reportRFA) {
        this.reportRFA = reportRFA;
    }

    /**
     * @return printReason
     */
    public String getPrintReason() {
        return printReason;
    }

    /**
     * @param printReason Festzulegender printReason
     */
    public void setPrintReason(String printReason) {
        this.printReason = printReason;
    }

}
