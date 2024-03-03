/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 29.02.2012 10:16:21
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;
import java.util.*;

/**
 * Request-Objekt fuer die reservePort-Methode des Wholesale-Services.
 */
public class WholesaleReservePortRequest {

    /**
     * AuftragId aus dem Tibco System zum Fehler-Reporting
     */
    private String extOrderId;

    private String orderId;

    private WholesaleEkpFrameContract ekpFrameContract;

    private List<WholesaleContactPerson> contactPersons;

    private WholesaleProduct product;

    /**
     * Wunschtermin für Schaltung
     */
    private LocalDate desiredExecutionDate;

    /**
     * Standort
     */
    private long geoId;

    private Long sessionId;

    private String lageTaeOnt;

    private LocalTime zeitFensterAnfang;

    private LocalTime zeitfensterEnde;

    public String getExtOrderId() {
        return extOrderId;
    }

    public void setExtOrderId(String extOrderId) {
        this.extOrderId = extOrderId;
    }

    public WholesaleEkpFrameContract getEkpFrameContract() {
        return ekpFrameContract;
    }

    public void setEkpFrameContract(WholesaleEkpFrameContract ekpFrameContract) {
        this.ekpFrameContract = ekpFrameContract;
    }

    public List<WholesaleContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<WholesaleContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public WholesaleProduct getProduct() {
        return product;
    }

    public void setProduct(WholesaleProduct product) {
        this.product = product;
    }

    public LocalDate getDesiredExecutionDate() {
        return desiredExecutionDate;
    }

    public void setDesiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
    }

    public long getGeoId() {
        return geoId;
    }

    public void setGeoId(long geoId) {
        this.geoId = geoId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLageTaeOnt() {
        return lageTaeOnt;
    }

    public void setLageTaeOnt(String lageTaeOnt) {
        this.lageTaeOnt = lageTaeOnt;
    }

    public LocalTime getZeitFensterAnfang() {
        return zeitFensterAnfang;
    }

    public void setZeitFensterAnfang(LocalTime zeitFensterAnfang) {
        this.zeitFensterAnfang = zeitFensterAnfang;
    }

    public LocalTime getZeitfensterEnde() {
        return zeitfensterEnde;
    }

    public void setZeitfensterEnde(LocalTime zeitfensterEnde) {
        this.zeitfensterEnde = zeitfensterEnde;
    }
}


