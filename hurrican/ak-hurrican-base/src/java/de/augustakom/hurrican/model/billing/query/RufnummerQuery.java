/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 07.06.2004 15:40:24
 */
package de.augustakom.hurrican.model.billing.query;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import de.augustakom.hurrican.model.base.AbstractHurricanQuery;
import de.augustakom.hurrican.model.shared.iface.KundenModel;


/**
 * Query-Objekt fuer die Suche nach Rufnummern.
 *
 *
 */
public class RufnummerQuery extends AbstractHurricanQuery implements KundenModel {

    private Long dnNoOrig = null;
    private String onKz = null;
    private String dnBase = null;
    private Long kundeNo = null;
    private Long auftragNoOrig = null;
    private boolean onlyValidToFuture = false;
    private boolean onlyActive = false;
    private Date valid = null;

    /**
     * @return Returns the dnBase.
     */
    public String getDnBase() {
        return dnBase;
    }

    /**
     * @param dnBase The dnBase to set.
     */
    public void setDnBase(String dnBase) {
        this.dnBase = dnBase;
    }

    /**
     * @return Returns the onKz.
     */
    public String getOnKz() {
        return onKz;
    }

    /**
     * @param onKz The onKz to set.
     */
    public void setOnKz(String onKz) {
        this.onKz = onKz;
    }

    /**
     * @return Returns the auftragNoOrig.
     */
    public Long getAuftragNoOrig() {
        return auftragNoOrig;
    }

    /**
     * @param auftragNoOrig The auftragNoOrig to set.
     */
    public void setAuftragNoOrig(Long auftragNoOrig) {
        this.auftragNoOrig = auftragNoOrig;
    }

    /**
     * @return Returns the kundeNo.
     */
    @Override
    public Long getKundeNo() {
        return kundeNo;
    }

    /**
     * @param kundeNo The kundeNo to set.
     */
    @Override
    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    /**
     * @return Returns the onlyValidToFuture.
     */
    public boolean isOnlyValidToFuture() {
        return onlyValidToFuture;
    }

    /**
     * Flag, um nur nach Rufnummern zu suchen, deren VALID_TO Datum in der Zukunft liegt.
     *
     * @param onlyValidToFuture The onlyValidToFuture to set.
     */
    public void setOnlyValidToFuture(boolean onlyValidToFuture) {
        this.onlyValidToFuture = onlyValidToFuture;
    }

    /**
     * @return Returns the onlyActive.
     */
    public boolean isOnlyActive() {
        return onlyActive;
    }

    /**
     * Flag bestimmt, ob nur nach Rufnummern mit Hist-Status 'AKT' und 'NEU' (true) oder nach Rufnummern mit beliebigen
     * (false) Hist-Status gesucht wird.
     *
     * @param onlyActive The onlyActive to set.
     */
    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }

    /**
     * @return Returns the dnNoOrig.
     */
    public Long getDnNoOrig() {
        return this.dnNoOrig;
    }

    /**
     * @param dnNoOrig The dnNoOrig to set.
     */
    public void setDnNoOrig(Long dnNoOrig) {
        this.dnNoOrig = dnNoOrig;
    }

    /**
     * @return the valid
     */
    public Date getValid() {
        return valid;
    }

    /**
     * Datum zu dem die Rufnummer gueltig sein soll (valid_from <= valid <= valid_to).
     *
     * @param valid the valid to set
     */
    public void setValid(Date valid) {
        this.valid = valid;
    }

    @Override
    public boolean isEmpty() {
        if (getDnNoOrig() != null) {
            return false;
        }
        if (StringUtils.isNotBlank(getDnBase())) {
            return false;
        }
        if (StringUtils.isNotBlank(getOnKz())) {
            return false;
        }
        if (getKundeNo() != null) {
            return false;
        }
        if (getAuftragNoOrig() != null) {
            return false;
        }
        if (onlyValidToFuture) {
            return false;
        }
        if (onlyActive) {
            return false;
        }
        return true;
    }

}


