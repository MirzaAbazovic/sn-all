/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2012 17:30:00
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

/**
 * Request-Objekt fuer die modifyPort-Methode des Wholesale-Services.
 */
public class WholesaleModifyPortRequest {

    /**
     * MNet generierte eindeutige Bezeichnung fuer die TAL
     */
    private String lineId;

    /**
     * Endkundenprovider Id
     */
    private String ekpId;

    /**
     * Vertragsnummer mit dem Endkundenprovider
     */
    private String ekpContractId;

    private WholesaleProduct product;

    /**
     * Wunschtermin für Schaltung
     */
    private LocalDate desiredExecutionDate;

    /**
     * Portwechsel ist erlaubt
     */
    private boolean changeOfPortAllowed;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getEkpId() {
        return ekpId;
    }

    public void setEkpId(String ekpId) {
        this.ekpId = ekpId;
    }

    public String getEkpContractId() {
        return ekpContractId;
    }

    public void setEkpContractId(String ekpContractId) {
        this.ekpContractId = ekpContractId;
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

    public boolean isChangeOfPortAllowed() {
        return changeOfPortAllowed;
    }

    public void setChangeOfPortAllowed(boolean changeOfPortAllowed) {
        this.changeOfPortAllowed = changeOfPortAllowed;
    }

}


