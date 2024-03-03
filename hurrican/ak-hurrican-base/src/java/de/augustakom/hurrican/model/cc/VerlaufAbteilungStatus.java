/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.12.2004 14:38:43
 */
package de.augustakom.hurrican.model.cc;


/**
 * Modell bildet einen Verlaufs-Status ab. <br>
 *
 *
 */
public class VerlaufAbteilungStatus extends AbstractCCIDModel {

    private Long abteilungId;
    public static final String ABTEILUNG_ID = "abteilungId";
    private String status;
    public static final String STATUS = "status";


    public Long getAbteilungId() {
        return abteilungId;
    }

    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
