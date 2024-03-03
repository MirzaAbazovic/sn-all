/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 05.03.2007 08:32:01
 */
package de.augustakom.hurrican.model.reporting;

import java.util.*;


/**
 * Model-Klasse für einen zugeordnete Report-Vorlage
 *
 *
 */
public class ReportTemplate extends AbstractReportLongIdModel {

    private Long reportId = null;
    private String filename = null;
    private String filenameOrig = null;
    private Date gueltigVon = null;
    private Date gueltigBis = null;
    private String userw = null;

    /**
     * @return gueltigBis
     */
    public Date getGueltigBis() {
        return gueltigBis;
    }

    /**
     * @param gueltigBis Festzulegender gueltigBis
     */
    public void setGueltigBis(Date gueltigBis) {
        this.gueltigBis = gueltigBis;
    }

    /**
     * @return gueltigVon
     */
    public Date getGueltigVon() {
        return gueltigVon;
    }

    /**
     * @param gueltigVon Festzulegender gueltigVon
     */
    public void setGueltigVon(Date gueltigVon) {
        this.gueltigVon = gueltigVon;
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
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename Festzulegender filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return filenameOrig
     */
    public String getFilenameOrig() {
        return filenameOrig;
    }

    /**
     * @param filenameOrig Festzulegender filenameOrig
     */
    public void setFilenameOrig(String filenameOrig) {
        this.filenameOrig = filenameOrig;
    }


}
