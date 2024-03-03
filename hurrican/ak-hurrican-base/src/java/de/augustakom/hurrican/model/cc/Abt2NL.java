/*
 * Copyright (c) 2009 - M-net Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.04.2009 10:08:56
 */
package de.augustakom.hurrican.model.cc;


/**
 * Bildet das Mapping zwischen Niederlassung und Abteilungen ab
 *
 *
 */
public class Abt2NL extends AbstractCCIDModel {

    private Long niederlassungId = null;
    private Long abteilungId = null;

    /**
     * @return the niederlassungId
     */
    public Long getNiederlassungId() {
        return niederlassungId;
    }

    /**
     * @param niederlassungId the niederlassungId to set
     */
    public void setNiederlassungId(Long niederlassungId) {
        this.niederlassungId = niederlassungId;
    }

    /**
     * @return the abteilungId
     */
    public Long getAbteilungId() {
        return abteilungId;
    }

    /**
     * @param abteilungId the abteilungId to set
     */
    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

}


