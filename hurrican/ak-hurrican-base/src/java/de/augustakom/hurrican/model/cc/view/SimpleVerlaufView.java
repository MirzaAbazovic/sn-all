/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 09.03.2005 07:55:36
 */
package de.augustakom.hurrican.model.cc.view;

import java.util.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;


/**
 * View-Klasse, um die wichtigsten Daten zu einem Verlauf darzustellen.
 *
 *
 */
public class SimpleVerlaufView extends AbstractCCIDModel {

    private String anlass = null;
    private String status = null;
    private Date realisierungstermin = null;
    private Boolean projektierung = null;

    /**
     * @return Returns the anlass.
     */
    public String getAnlass() {
        return anlass;
    }

    /**
     * @param anlass The anlass to set.
     */
    public void setAnlass(String anlass) {
        this.anlass = anlass;
    }

    /**
     * @return Returns the projektierung.
     */
    public Boolean getProjektierung() {
        return projektierung;
    }

    /**
     * @param projektierung The projektierung to set.
     */
    public void setProjektierung(Boolean projektierung) {
        this.projektierung = projektierung;
    }

    /**
     * @return Returns the realisierungstermin.
     */
    public Date getRealisierungstermin() {
        return realisierungstermin;
    }

    /**
     * @param realisierungstermin The realisierungstermin to set.
     */
    public void setRealisierungstermin(Date realisierungstermin) {
        this.realisierungstermin = realisierungstermin;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

}


