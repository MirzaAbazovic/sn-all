/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 14:00:59
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

/**
 * DTO zur Abbildung eines ChangeVdslProfile-Requests.
 *
 *
 */
public class WholesaleChangeVdslProfileRequest {

    private String lineId;
    private Long newProfileId;
    private LocalDate validFrom;
    private Long changeReasonId;
    private String comment;
    private String username;

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public Long getNewProfileId() {
        return newProfileId;
    }

    public void setNewProfileId(Long newProfileId) {
        this.newProfileId = newProfileId;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public Long getChangeReasonId() {
        return changeReasonId;
    }

    public void setChangeReasonId(Long changeReasonId) {
        this.changeReasonId = changeReasonId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}


