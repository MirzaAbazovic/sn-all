/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.10.2008 10:22:00
 */
package de.augustakom.hurrican.model.billing;

import java.util.*;


/**
 * Modell fuer die Abbildung der Mehrwertsteuer-Tabelle
 *
 *
 */
public class VatRate extends AbstractBillingModel {

    private Long vatRateNo = null;
    private Date validFrom = null;
    private Date validTo = null;
    private String vatCodeId = null;
    private Float vatRate = null;

    /**
     * @return validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom Festzulegender validFrom
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @param validTo Festzulegender validTo
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * @return vatCodeId
     */
    public String getVatCodeId() {
        return vatCodeId;
    }

    /**
     * @param vatCodeId Festzulegender vatCodeId
     */
    public void setVatCodeId(String vatCodeId) {
        this.vatCodeId = vatCodeId;
    }

    /**
     * @return vatRate
     */
    public Float getVatRate() {
        return vatRate;
    }

    /**
     * @param vatRate Festzulegender vatRate
     */
    public void setVatRate(Float vatRate) {
        this.vatRate = vatRate;
    }

    /**
     * @return vatRateNo
     */
    public Long getVatRateNo() {
        return vatRateNo;
    }

    /**
     * @param vatRateNo Festzulegender vatRateNo
     */
    public void setVatRateNo(Long vatRateNo) {
        this.vatRateNo = vatRateNo;
    }

}


