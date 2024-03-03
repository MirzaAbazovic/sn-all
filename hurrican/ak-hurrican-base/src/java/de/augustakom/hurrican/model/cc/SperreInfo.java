/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2004 10:49:50
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell enthaelt Informationen, wer bei einer Sperre ueber EMail benachrichtigt werden soll.
 *
 *
 */
public class SperreInfo extends AbstractCCIDModel {

    private Long abteilungId = null;
    private String email = null;
    private Boolean active = null;

    /**
     * @return Returns the abteilungId.
     */
    public Long getAbteilungId() {
        return abteilungId;
    }

    /**
     * @param abteilungId The abteilungId to set.
     */
    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    /**
     * @return Returns the active.
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}


