/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 25.01.2006 17:34:21
 */
package de.mnet.hurrican.scheduler.model;

import java.util.*;

/**
 * Modell fuer die Protokollieren, wann ein File digital signiert wurde.
 *
 *
 */
public class SignaturedFile extends BaseSchedulerModel {

    private static final long serialVersionUID = -3919211560571964702L;

    private String billingYear;
    private String billingMonth;
    private String filename;
    private String absolutePath;
    private String billingStream;
    private Date signatureStart;
    private Date signatureEnd;
    private Date signaturedReCopied;

    /**
     * Erzeugt ein neues SignaturedFile-Objekt mit den angegebenen Daten.
     */
    public static SignaturedFile createSF(String year, String month, String filename, String absolutePath,
            String billingStream) {
        SignaturedFile sf = new SignaturedFile();
        sf.setBillingYear(year);
        sf.setBillingMonth(month);
        sf.setFilename(filename);
        sf.setAbsolutePath(absolutePath);
        sf.setBillingStream(billingStream);
        return sf;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getSignatureEnd() {
        return signatureEnd;
    }

    /**
     * Datum-/Zeitangabe, wann die Datei signiert wurde.
     *
     * @param signatureEnd The signatureEnd to set.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setSignatureEnd(Date signatureEnd) {
        this.signatureEnd = signatureEnd;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getSignatureStart() {
        return signatureStart;
    }

    /**
     * Datum-/Zeitangabe, wann die Datei zur Signierung gegeben wurde.
     *
     * @param signatureStart The signatureStart to set.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setSignatureStart(Date signatureStart) {
        this.signatureStart = signatureStart;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Dateobjekt wird nicht veraendert!")
    public Date getSignaturedReCopied() {
        return signaturedReCopied;
    }

    /**
     * Datum-/Zeitangabe, wann die Datei zurueck auf die Billing-Maschine gespielt wurde.
     *
     * @param signaturedReCopied The signaturedReCopied to set.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Dateobjekt wird nicht veraendert!")
    public void setSignaturedReCopied(Date signaturedReCopied) {
        this.signaturedReCopied = signaturedReCopied;
    }

    public String getBillingStream() {
        return billingStream;
    }

    public void setBillingStream(String billingStream) {
        this.billingStream = billingStream;
    }

}
