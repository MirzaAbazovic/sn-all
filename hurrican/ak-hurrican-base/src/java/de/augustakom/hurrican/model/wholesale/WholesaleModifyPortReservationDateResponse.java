/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2012 15:16:47
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

import de.augustakom.hurrican.service.wholesale.WholesaleService;

/**
 * Response-Objekt fuer die Methode {@link WholesaleService#modifyPortReservationDate(WholesaleModifyPortReservationDateRequest)}
 */
public class WholesaleModifyPortReservationDateResponse {

    private LocalDate executionDate;

    private String lineId;

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }
}


