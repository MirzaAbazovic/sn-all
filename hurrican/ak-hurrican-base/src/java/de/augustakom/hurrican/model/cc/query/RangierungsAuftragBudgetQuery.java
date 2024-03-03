/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 16.10.2007 10:42:36
 */
package de.augustakom.hurrican.model.cc.query;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche nach Budgets, die einem Rangierungs-Auftrag zugeordnet sind.
 *
 *
 */
public class RangierungsAuftragBudgetQuery extends AbstractHurricanQuery {

    private Long hvtStandortId = null;
    private Date faelligVon = null;
    private Date faelligBis = null;

    /**
     * @return Returns the hvtStandortId.
     */
    public Long getHvtStandortId() {
        return hvtStandortId;
    }

    /**
     * @param hvtStandortId The hvtStandortId to set.
     */
    public void setHvtStandortId(Long hvtStandortId) {
        this.hvtStandortId = hvtStandortId;
    }

    /**
     * @return Returns the faelligVon.
     */
    public Date getFaelligVon() {
        return faelligVon;
    }

    /**
     * @param faelligVon The faelligVon to set.
     */
    public void setFaelligVon(Date faelligVon) {
        this.faelligVon = faelligVon;
    }

    /**
     * @return Returns the faelligBis.
     */
    public Date getFaelligBis() {
        return faelligBis;
    }

    /**
     * @param faelligBis The faelligBis to set.
     */
    public void setFaelligBis(Date faelligBis) {
        this.faelligBis = faelligBis;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getHvtStandortId() != null) {
            return false;
        }
        if (getFaelligVon() != null) {
            return false;
        }
        if (getFaelligBis() != null) {
            return false;
        }
        return true;
    }

}


