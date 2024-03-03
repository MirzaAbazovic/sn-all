/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2007 08:45:01
 */
package de.augustakom.hurrican.model.reporting;

import java.io.*;
import java.util.*;

import de.augustakom.hurrican.model.shared.iface.KundenModel;

/**
 * Model-Klasse für einen zu erstellenden Report
 *
 *
 */
public class ReportRequest extends AbstractReportLongIdModel implements KundenModel {

    private Long repId;
    private Integer buendelNo;
    private Long kundeNo;
    private Long orderNoOrig;
    private Long auftragId;
    private String requestFrom;
    private Date requestAt;
    private Date requestFinishedAt;
    private Date reportDownloadedAt;
    private Boolean rfa;
    private Date reportArchivedAt;
    private Date reportMovedToArchive;
    private String requestType;
    private Long printReasonId;
    private String file;
    private String error;

    public Long getAuftragId() {
        return auftragId;
    }

    public void setAuftragId(Long auftragId) {
        this.auftragId = auftragId;
    }

    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    public Long getOrderNoOrig() {
        return orderNoOrig;
    }

    public void setOrderNoOrig(Long orderNoOrig) {
        this.orderNoOrig = orderNoOrig;
    }

    public Long getRepId() {
        return repId;
    }

    public void setRepId(Long repId) {
        this.repId = repId;
    }

    public Date getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(Date requestAt) {
        this.requestAt = requestAt;
    }

    public Date getRequestFinishedAt() {
        return requestFinishedAt;
    }

    public void setRequestFinishedAt(Date requestFinishedAt) {
        this.requestFinishedAt = requestFinishedAt;
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * Gibt den korrigierten Pfad zum generierten Report aus. Das Pfadtrennungszeichen wird wird je nach Betriebssystem
     * korrigiert
     * <p/>
     * Beispiel: Archiv/2008-02-11/Report-ReqID10539-1201884355138.pdf -> Archiv/2008-02-11/Report-ReqID10539-1201884355138.pdf
     * -> unter Linux/Unix -> Archiv\2008-02-11\Report-ReqID10539-1201884355138.pdf -> unter Windows
     *
     * @return den relativen Pfad (von ${report.path.output}) des erzeugten Reports.
     */
    public String getFilePathForCurrentPlatform() {
        if (file != null) {
            String filePath = file.replace("/", File.separator);
            return filePath.replace("\\", File.separator);
        }
        return null;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getReportDownloadedAt() {
        return reportDownloadedAt;
    }

    public void setReportDownloadedAt(Date reportDownloadedAt) {
        this.reportDownloadedAt = reportDownloadedAt;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getBuendelNo() {
        return buendelNo;
    }

    public void setBuendelNo(Integer buendelNo) {
        this.buendelNo = buendelNo;
    }

    public Date getReportArchivedAt() {
        return reportArchivedAt;
    }

    public void setReportArchivedAt(Date reportArchivedAt) {
        this.reportArchivedAt = reportArchivedAt;
    }

    public Date getReportMovedToArchive() {
        return reportMovedToArchive;
    }

    public void setReportMovedToArchive(Date reportMovedToArchive) {
        this.reportMovedToArchive = reportMovedToArchive;
    }

    public Long getPrintReasonId() {
        return printReasonId;
    }

    public void setPrintReasonId(Long reprintReasonId) {
        this.printReasonId = reprintReasonId;
    }

    public Boolean getRfa() {
        return rfa;
    }

    public void setRfa(Boolean rfa) {
        this.rfa = rfa;
    }
}
