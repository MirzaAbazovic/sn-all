/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.03.2009 08:54:55
 */
package de.augustakom.hurrican.model.shared.view;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche ueber Innenauftrags-Daten.
 *
 *
 */
public class InnenauftragQuery extends AbstractHurricanQuery {

    private String iaNummer = null;
    private String bedarfsNr = null;

    /**
     * @return Returns the iaNummer.
     */
    public String getIaNummer() {
        return iaNummer;
    }

    /**
     * @param iaNummer The iaNummer to set.
     */
    public void setIaNummer(String iaNummer) {
        this.iaNummer = iaNummer;
    }

    /**
     * @return Returns the bedarfsNr.
     */
    public String getBedarfsNr() {
        return bedarfsNr;
    }

    /**
     * @param bedarfsNr The bedarfsNr to set.
     */
    public void setBedarfsNr(String bedarfsNr) {
        this.bedarfsNr = bedarfsNr;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getIaNummer())) {
            return false;
        }
        if (StringUtils.isNotBlank(getBedarfsNr())) {
            return false;
        }

        return true;
    }

}


