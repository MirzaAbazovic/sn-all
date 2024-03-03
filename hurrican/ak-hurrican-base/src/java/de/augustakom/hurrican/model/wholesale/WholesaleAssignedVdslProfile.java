/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 13:56:01
 */
package de.augustakom.hurrican.model.wholesale;

import java.time.*;

/**
 * DTO für ein Wholesale VDSL Profil das einem Auftrag zugeordnet ist.
 *
 *
 */
public class WholesaleAssignedVdslProfile {
    private String profileName;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String assignedBy;
    private String comment;
    private String changeReason;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String value) {
        this.profileName = value;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

}


