/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 14:00:59
 */
package de.augustakom.hurrican.model.wholesale;

/**
 * DTO zur Abbildung eines ChangePort-Requests
 */
public class WholesaleChangePortRequest {

    private String lineId;
    private Long newPortId;
    private Long changeReasonId;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Long getNewPortId() {
        return newPortId;
    }

    public void setNewPortId(Long newPortId) {
        this.newPortId = newPortId;
    }

    public Long getChangeReasonId() {
        return changeReasonId;
    }

    public void setChangeReasonId(Long changeReasonId) {
        this.changeReasonId = changeReasonId;
    }

}


