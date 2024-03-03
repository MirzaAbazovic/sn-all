/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.07.2004 12:53:28
 */
package de.augustakom.hurrican.model.shared.view;

import java.util.*;
import org.apache.commons.lang.StringUtils;


/**
 * View-Modell, um Auftrags- und Rufnummerdaten darzustellen. (Die Informationen werden ueber Billing- und CC-Services
 * zusammengetragen.)
 *
 *
 */
public class AuftragDNView extends DefaultSharedAuftragView {

    private Long dnNo = null;
    private Long dnNoOrig = null;
    private String onKz = null;
    private String dnBase = null;
    private String rangeFrom = null;
    private String rangeTo = null;
    private Date realDate = null;
    private String lastCarrier = null;
    private String actCarrier = null;
    private String futureCarrier = null;
    private String histStatus = null;
    private String strasse = null;
    private String nummer = null;
    private String postfach = null;
    private String plz = null;
    private String ort = null;

    /**
     * @return Returns the dnBase.
     */
    public String getDnBase() {
        return dnBase;
    }

    /**
     * @param dnBase The dnBase to set.
     */
    public void setDnBase(String dnBase) {
        this.dnBase = dnBase;
    }

    /**
     * @return Returns the histStatus.
     */
    public String getHistStatus() {
        return histStatus;
    }

    /**
     * @param histStatus The histStatus to set.
     */
    public void setHistStatus(String histStatus) {
        this.histStatus = histStatus;
    }

    /**
     * @return Returns the nummer.
     */
    public String getNummer() {
        return nummer;
    }

    /**
     * @param nummer The nummer to set.
     */
    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    /**
     * @return Returns the onKz.
     */
    public String getOnKz() {
        return onKz;
    }

    /**
     * @param onKz The onKz to set.
     */
    public void setOnKz(String onkz) {
        this.onKz = onkz;
    }

    /**
     * @return Returns the ort.
     */
    public String getOrt() {
        return ort;
    }

    /**
     * @param ort The ort to set.
     */
    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * @return Returns the plz.
     */
    public String getPlz() {
        return plz;
    }

    /**
     * @param plz The plz to set.
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * @return Returns the postfach.
     */
    public String getPostfach() {
        return postfach;
    }

    /**
     * @param postfach The postfach to set.
     */
    public void setPostfach(String postfach) {
        this.postfach = postfach;
    }

    /**
     * @return Returns the rangeFrom.
     */
    public String getRangeFrom() {
        return rangeFrom;
    }

    /**
     * @param rangeFrom The rangeFrom to set.
     */
    public void setRangeFrom(String rangeFrom) {
        this.rangeFrom = rangeFrom;
    }

    /**
     * @return Returns the rangeTo.
     */
    public String getRangeTo() {
        return rangeTo;
    }

    /**
     * @param rangeTo The rangeTo to set.
     */
    public void setRangeTo(String rangeTo) {
        this.rangeTo = rangeTo;
    }

    /**
     * @return Returns the strasse.
     */
    public String getStrasse() {
        return strasse;
    }

    /**
     * @param strasse The strasse to set.
     */
    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    /**
     * @return Returns the actCarrier.
     */
    public String getActCarrier() {
        return StringUtils.trim(actCarrier);
    }

    /**
     * @param actCarrier The actCarrier to set.
     */
    public void setActCarrier(String actCarrier) {
        this.actCarrier = actCarrier;
    }

    /**
     * @return Returns the futureCarrier.
     */
    public String getFutureCarrier() {
        return StringUtils.trim(futureCarrier);
    }

    /**
     * @param futureCarrier The futureCarrier to set.
     */
    public void setFutureCarrier(String futureCarrier) {
        this.futureCarrier = futureCarrier;
    }

    /**
     * @return Returns the lastCarrier.
     */
    public String getLastCarrier() {
        return StringUtils.trim(lastCarrier);
    }

    /**
     * @param lastCarrier The lastCarrier to set.
     */
    public void setLastCarrier(String lastCarrier) {
        this.lastCarrier = lastCarrier;
    }

    /**
     * @return Returns the realDate.
     */
    public Date getRealDate() {
        return realDate;
    }

    /**
     * @param realDate The realDate to set.
     */
    public void setRealDate(Date realDate) {
        this.realDate = realDate;
    }

    /**
     * @return Returns the dnNo.
     */
    public Long getDnNo() {
        return this.dnNo;
    }

    /**
     * @param dnNo The dnNo to set.
     */
    public void setDnNo(Long dnNo) {
        this.dnNo = dnNo;
    }

    /**
     * @return Returns the dnNoOrig.
     */
    public Long getDnNoOrig() {
        return this.dnNoOrig;
    }

    /**
     * @param dnNoOrig The dnNoOrig to set.
     */
    public void setDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
    }

}


