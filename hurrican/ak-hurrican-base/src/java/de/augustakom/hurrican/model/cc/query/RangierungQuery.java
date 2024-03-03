/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.11.2004 15:33:38
 */
package de.augustakom.hurrican.model.cc.query;

import org.apache.log4j.Logger;

import de.augustakom.common.model.DebugModel;
import de.augustakom.hurrican.model.base.AbstractHurricanQuery;


/**
 * Query-Objekt fuer die Suche nach (freien) Rangierungen.
 *
 *
 */
public class RangierungQuery extends AbstractHurricanQuery implements DebugModel {

    private Long hvtStandortId = null;
    private Long physikTypId = null;
    private Integer leitungGesamtId = null;
    private Integer leitungLfdNr = null;
    private Boolean includeFreigabebereit = null;
    private Boolean includeDefekt = null;

    /**
     * @return Returns the includeFreigabebereit.
     */
    public Boolean getIncludeFreigabebereit() {
        return includeFreigabebereit;
    }

    /**
     * @param includeFreigabebereit The includeFreigabebereit to set.
     */
    public void setIncludeFreigabebereit(Boolean includeFreigabebereit) {
        this.includeFreigabebereit = includeFreigabebereit;
    }

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
     * @return Returns the leitungGesamtId.
     */
    public Integer getLeitungGesamtId() {
        return leitungGesamtId;
    }

    /**
     * @param leitungGesamtId The leitungGesamtId to set.
     */
    public void setLeitungGesamtId(Integer leitungGesamtId) {
        this.leitungGesamtId = leitungGesamtId;
    }

    /**
     * @return Returns the leitungLfdNr.
     */
    public Integer getLeitungLfdNr() {
        return leitungLfdNr;
    }

    /**
     * @param leitungLfdNr The leitungLfdNr to set.
     */
    public void setLeitungLfdNr(Integer leitungLfdNr) {
        this.leitungLfdNr = leitungLfdNr;
    }

    /**
     * @return Returns the physikTypId.
     */
    public Long getPhysikTypId() {
        return physikTypId;
    }

    /**
     * @param physikTypId The physikTypId to set.
     */
    public void setPhysikTypId(Long physikTypId) {
        this.physikTypId = physikTypId;
    }

    public Boolean getIncludeDefekt() {
        return includeDefekt;
    }

    public void setIncludeDefekt(Boolean includeDefekt) {
        this.includeDefekt = includeDefekt;
    }

    /**
     * @see de.augustakom.hurrican.model.base.AbstractHurricanQuery#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (getHvtStandortId() != null) {
            return false;
        }
        if (getPhysikTypId() != null) {
            return false;
        }
        if (getLeitungGesamtId() != null) {
            return false;
        }
        if (getLeitungLfdNr() != null) {
            return false;
        }
        if (getIncludeFreigabebereit() != null) {
            return false;
        }
        if (getIncludeDefekt() != null) {
            return false;
        }
        return true;
    }

    /**
     * @see de.augustakom.common.model.DebugModel#debugModel(org.apache.log4j.Logger)
     */
    public void debugModel(Logger logger) {
        if ((logger != null) && logger.isDebugEnabled()) {
            logger.debug("Eigenschaften von " + this.getClass().getName());
            logger.debug("  HVT-ID   : " + getHvtStandortId());
            logger.debug("  PhysikTyp: " + getPhysikTypId());
            logger.debug("  LtgGesId : " + getLeitungGesamtId());
            logger.debug("  LtgLfdNr : " + getLeitungLfdNr());
        }
    }

}


