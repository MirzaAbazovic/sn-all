/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.03.2012 15:15:29
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

import de.augustakom.hurrican.service.wholesale.WholesaleService;

/**
 * Request-Objekt fuer die Methode {@link WholesaleService#modifyPortReservationDate(WholesaleModifyPortReservationDateRequest)}
 */
public class WholesaleModifyPortReservationDateRequest {

    private String lineId;
    private LocalDate desiredExecutionDate;
    private Long sessionId;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public LocalDate getDesiredExecutionDate() {
        return desiredExecutionDate;
    }

    public void setDesiredExecutionDate(LocalDate desiredExecutionDate) {
        this.desiredExecutionDate = desiredExecutionDate;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}


