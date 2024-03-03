/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.03.2012 17:30:00
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

/**
 * Response-Objekt fuer die modifyPort Methode des Wholesale-Services.
 */
public class WholesaleModifyPortResponse {

    /**
     * MNet generierte eindeutige Bezeichnung fuer die TAL
     */
    private String lineId;

    /**
     * Port wird zu jetzt reserviert, der {@code executionDate} gibt Hurrican die {@code desiredExecutionDate} wieder
     * mit
     */
    private LocalDate executionDate;

    private boolean portChanged;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public void setPortChanged(boolean portChanged) {
        this.portChanged = portChanged;
    }

    public boolean isPortChanged() {
        return portChanged;
    }
}
