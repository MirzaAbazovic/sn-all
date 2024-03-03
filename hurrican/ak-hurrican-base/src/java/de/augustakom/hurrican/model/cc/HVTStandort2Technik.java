/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 24.05.2005 08:22:42
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.shared.iface.HvtIdStandortModel;


/**
 * Mapping-Modell fuer eine Verbindung zwischen HVTStandort und HVTTechnik.
 *
 *
 */
public class HVTStandort2Technik extends AbstractCCModel implements HvtIdStandortModel {

    private Long hvtIdStandort = null;
    private Long hvtTechnikId = null;

    /**
     * @return Returns the hvtIdStandort.
     */
    public Long getHvtIdStandort() {
        return hvtIdStandort;
    }

    /**
     * @param hvtIdStandort The hvtIdStandort to set.
     */
    public void setHvtIdStandort(Long hvtIdStandort) {
        this.hvtIdStandort = hvtIdStandort;
    }

    /**
     * @return Returns the hvtTechnikId.
     */
    public Long getHvtTechnikId() {
        return hvtTechnikId;
    }

    /**
     * @param hvtTechnikId The hvtTechnikId to set.
     */
    public void setHvtTechnikId(Long hvtTechnikId) {
        this.hvtTechnikId = hvtTechnikId;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        HVTStandort2Technik h2t = (HVTStandort2Technik) obj;
        if (getHvtIdStandort() != null) {
            if (!getHvtIdStandort().equals(h2t.getHvtIdStandort())) {
                return false;
            }
        }
        else if (h2t.getHvtIdStandort() != null) {
            return false;
        }

        if (getHvtTechnikId() != null) {
            if (!getHvtTechnikId().equals(h2t.getHvtTechnikId())) {
                return false;
            }
        }
        else if (h2t.getHvtTechnikId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(57, 71)
                .append(hvtIdStandort)
                .append(hvtTechnikId)
                .toHashCode();
    }
}


