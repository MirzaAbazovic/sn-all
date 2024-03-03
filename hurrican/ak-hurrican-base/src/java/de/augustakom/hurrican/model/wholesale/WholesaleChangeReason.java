/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 31.05.2012 07:11:04
 */
package de.augustakom.hurrican.model.wholesale;


/**
 * DTO zur Abbildung einer moeglichen "ChangeReason" fuer einen Port- oder VDSL Profil Wechsel.
 */
public class WholesaleChangeReason {

    private Long changeReasonId;
    private String description;

    public Long getChangeReasonId() {
        return changeReasonId;
    }

    public void setChangeReasonId(Long changeReasonId) {
        this.changeReasonId = changeReasonId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}


