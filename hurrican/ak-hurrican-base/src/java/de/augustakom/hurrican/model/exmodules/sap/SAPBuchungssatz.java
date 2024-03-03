/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.08.2007 13:06:23
 */
package de.augustakom.hurrican.model.exmodules.sap;

import java.util.*;


/**
 * Modell fuer einen SAP-Buchungssatz
 *
 *
 */
public class SAPBuchungssatz extends AbstractSAPModel {

    // SAP-Header Informationen
    // Belegnummer
    public static final String DOC_NO = "DOC_NO";
    // Zuordnung
    public static final String ALLOC_NMBR = "ALLOC_NMBR";
    // Mahnbereich
    public static final String DUNN_AREA = "DUNN_AREA";
    // Mahnstufe
    public static final String DUNN_LEVEL = "DUNN_LEVEL";
    // Datum ltz. Mahnung
    public static final String LAST_DUNN = "LAST_DUNN";
    // Nettofälligkeit
    public static final String BLINE_DATE = "BLINE_DATE";
    // Buchungstext
    public static final String ITEM_TEXT = "ITEM_TEXT";
    // Betrag HW
    public static final String LC_AMOUNT = "LC_AMOUNT";
    // Art
    public static final String DOC_TYPE = "DOC_TYPE";
    // Referenz
    public static final String REF_DOC_NO = "REF_DOC_NO";
    // Belegdatum
    public static final String DOC_DATE = "DOC_DATE";
    // Buchungsdatum
    public static final String PSTNG_DATE = "PSTNG_DATE";
    // Ausgleichsdatum
    public static final String CLEAR_DATE = "CLEAR_DATE";
    // Soll/Haben
    public static final String DB_CR_IND = "DB_CR_IND";
    // Zahlungsbedingung
    public static final String PMNTTRMS = "PMNTTRMS";


    private String docNo = null;
    private String allocNo = null;
    private String dunnArea = null;
    private Integer dunnLevel = null;
    private Date lastDunn = null;
    private Date blineDate = null;
    private String itemText = null;
    private Float lcAmount = null;
    private String docType = null;
    private String refDocNo = null;
    private Date docDate = null;
    private Date pstngDate = null;
    private Date clearDate = null;
    private String dbCrInd = null;
    private String pmntTrms = null;

    /**
     * @return allocNo
     */
    public String getAllocNo() {
        return allocNo;
    }

    /**
     * @param allocNo Festzulegender allocNo
     */
    public void setAllocNo(String allocNo) {
        this.allocNo = allocNo;
    }

    /**
     * @return blineDate
     */
    public Date getBlineDate() {
        return blineDate;
    }

    /**
     * @param blineDate Festzulegender blineDate
     */
    public void setBlineDate(Date blineDate) {
        this.blineDate = blineDate;
    }

    /**
     * @return clearDate
     */
    public Date getClearDate() {
        return clearDate;
    }

    /**
     * @param clearDate Festzulegender clearDate
     */
    public void setClearDate(Date clearDate) {
        this.clearDate = clearDate;
    }

    /**
     * @return dbCrInd
     */
    public String getDbCrInd() {
        return dbCrInd;
    }

    /**
     * @param dbCrInd Festzulegender dbCrInd
     */
    public void setDbCrInd(String dbCrInd) {
        this.dbCrInd = dbCrInd;
    }

    /**
     * @return docDate
     */
    public Date getDocDate() {
        return docDate;
    }

    /**
     * @param docDate Festzulegender docDate
     */
    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }

    /**
     * @return docNo
     */
    public String getDocNo() {
        return docNo;
    }

    /**
     * @param docNo Festzulegender docNo
     */
    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    /**
     * @return docType
     */
    public String getDocType() {
        return docType;
    }

    /**
     * @param docType Festzulegender docType
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * @return dunnArea
     */
    public String getDunnArea() {
        return dunnArea;
    }

    /**
     * @param dunnArea Festzulegender dunnArea
     */
    public void setDunnArea(String dunnArea) {
        this.dunnArea = dunnArea;
    }

    /**
     * @return dunnLevel
     */
    public Integer getDunnLevel() {
        return dunnLevel;
    }

    /**
     * @param dunnLevel Festzulegender dunnLevel
     */
    public void setDunnLevel(Integer dunnLevel) {
        this.dunnLevel = dunnLevel;
    }

    /**
     * @return itemText
     */
    public String getItemText() {
        return itemText;
    }

    /**
     * @param itemText Festzulegender itemText
     */
    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    /**
     * @return lastDunn
     */
    public Date getLastDunn() {
        return lastDunn;
    }

    /**
     * @param lastDunn Festzulegender lastDunn
     */
    public void setLastDunn(Date lastDunn) {
        this.lastDunn = lastDunn;
    }

    /**
     * @return lcAmount
     */
    public Float getLcAmount() {
        return lcAmount;
    }

    /**
     * @param lcAmount Festzulegender lcAmount
     */
    public void setLcAmount(Float lcAmount) {
        this.lcAmount = lcAmount;
    }

    /**
     * @return pmntTrms
     */
    public String getPmntTrms() {
        return pmntTrms;
    }

    /**
     * @param pmntTrms Festzulegender pmntTrms
     */
    public void setPmntTrms(String pmntTrms) {
        this.pmntTrms = pmntTrms;
    }

    /**
     * @return pstngDate
     */
    public Date getPstngDate() {
        return pstngDate;
    }

    /**
     * @param pstngDate Festzulegender pstngDate
     */
    public void setPstngDate(Date pstngDate) {
        this.pstngDate = pstngDate;
    }

    /**
     * @return refDocNo
     */
    public String getRefDocNo() {
        return refDocNo;
    }

    /**
     * @param refDocNo Festzulegender refDocNo
     */
    public void setRefDocNo(String refDocNo) {
        this.refDocNo = refDocNo;
    }

}


