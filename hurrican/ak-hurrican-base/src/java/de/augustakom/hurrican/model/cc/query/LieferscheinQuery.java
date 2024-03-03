/*
 * Copyright (c) 2008 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.03.2008 11:05:33
 */
package de.augustakom.hurrican.model.cc.query;

import java.util.*;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach Lieferschein-Objekten.
 *
 *
 */
public class LieferscheinQuery extends AbstractHurricanQuery implements KundenModel {

    private Long lieferscheinId = null;
    private Long status = null;
    private Date versandAm = null;
    private Date druckAm = null;
    private Long kundeNo = null;

    /**
     * @return Returns the lieferscheinId.
     */
    public Long getLieferscheinId() {
        return lieferscheinId;
    }

    /**
     * @param lieferscheinId The lieferscheinId to set.
     */
    public void setLieferscheinId(Long lieferscheinId) {
        this.lieferscheinId = lieferscheinId;
    }

    /**
     * @return druckAm
     */
    public Date getDruckAm() {
        return druckAm;
    }

    /**
     * @param druckAm Festzulegender druckAm
     */
    public void setDruckAm(Date druckAm) {
        this.druckAm = druckAm;
    }

    /**
     * @return Returns the kundeNo.
     */
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return status
     */
    public Long getStatus() {
        return status;
    }

    /**
     * @param status Festzulegender status
     */
    public void setStatus(Long status) {
        this.status = status;
    }

    /**
     * @return versandAm
     */
    public Date getVersandAm() {
        return versandAm;
    }

    /**
     * @param versandAm Festzulegender versandAm
     */
    public void setVersandAm(Date versandAm) {
        this.versandAm = versandAm;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getLieferscheinId() != null) {
            return false;
        }
        if (getKundeNo() != null) {
            return false;
        }
        if (getStatus() != null) {
            return false;
        }
        if (getDruckAm() != null) {
            return false;
        }
        if (getVersandAm() != null) {
            return false;
        }
        return true;
    }

}


