/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 19.08.2009 13:43:24
 */
package de.augustakom.hurrican.model.cc.query;

import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Auftragssuche ueber SAP-IDs
 *
 *
 */
public class AuftragSAPQuery extends AbstractHurricanQuery {

    private String sapId = null;
    private String sapDebitorId = null;
    private boolean nurAktuelle = false;

    public boolean isNurAktuelle() {
        return nurAktuelle;
    }

    /**
     * Wenn gesetzt, werden nur aktuelle (keine gekuendigten/historisierten) Auftraege zurueckgeliefert.
     *
     * @param nurAktuelle
     */
    public void setNurAktuelle(boolean nurAktuelle) {
        this.nurAktuelle = nurAktuelle;
    }

    /**
     * @return the sapId
     */
    public String getSapId() {
        return sapId;
    }

    /**
     * @param sapId the sapId to set
     */
    public void setSapId(String sapId) {
        this.sapId = sapId;
    }

    /**
     * @return the sapDebitorId
     */
    public String getSapDebitorId() {
        return sapDebitorId;
    }

    /**
     * @param sapDebitorId the sapDebitorId to set
     */
    public void setSapDebitorId(String sapDebitorId) {
        this.sapDebitorId = sapDebitorId;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (StringUtils.isNotBlank(getSapId())) {
            return false;
        }
        if (StringUtils.isNotBlank(getSapDebitorId())) {
            return false;
        }
        return true;
    }

}
