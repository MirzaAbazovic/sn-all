/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.01.2006 16:08:53
 */
package de.mnet.hurrican.scheduler.model;

import java.util.*;


/**
 * Modell zur Protokollierung, wann eine bestimmte Datei in welches Verzeichnis exportiert wurde.
 *
 *
 */
public class ExportedBillingFile extends BaseSchedulerModel {

    private static final long serialVersionUID = -1258599412381088023L;

    private String billingYear;
    private String billingMonth;
    private String filename;
    private Date exportedAt;
    private String exportedToHost;
    private String exportedToDir;
    private String billingStream;

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public String getBillingYear() {
        return billingYear;
    }

    public void setBillingYear(String billingYear) {
        this.billingYear = billingYear;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getExportedAt() {
        return exportedAt;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setExportedAt(Date exportedAt) {
        this.exportedAt = exportedAt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExportedToDir() {
        return exportedToDir;
    }

    public void setExportedToDir(String exportToDir) {
        this.exportedToDir = exportToDir;
    }

    public String getExportedToHost() {
        return exportedToHost;
    }

    public void setExportedToHost(String exportToHost) {
        this.exportedToHost = exportToHost;
    }

    public String getBillingStream() {
        return billingStream;
    }

    public void setBillingStream(String billingStream) {
        this.billingStream = billingStream;
    }

}
