/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2005 13:37:34
 */
package de.augustakom.hurrican.model.cc.dn;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.cc.AbstractCCModel;


/**
 * Modell zuordnung Rufnummernleistung zu Leistungsparamter
 *
 *
 */
public class Leistung2Parameter extends AbstractCCModel {

    private Long leistungId = null;
    private Long parameterId = null;

    /**
     * @return Returns the leistungId.
     */
    public Long getLeistungId() {
        return leistungId;
    }

    /**
     * @param leistungId The leistungId to set.
     */
    public void setLeistungId(Long leistungId) {
        this.leistungId = leistungId;
    }

    /**
     * @return Returns the parameterId.
     */
    public Long getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId The parameterId to set.
     */
    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
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

        Leistung2Parameter l2p = (Leistung2Parameter) obj;
        if (getLeistungId() != null) {
            if (!getLeistungId().equals(l2p.getLeistungId())) {
                return false;
            }
        }
        else if (l2p.getLeistungId() != null) {
            return false;
        }

        if (getParameterId() != null) {
            if (!getParameterId().equals(l2p.getParameterId())) {
                return false;
            }
        }
        else if (l2p.getParameterId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(111, 73)
                .append(leistungId)
                .append(parameterId)
                .toHashCode();
    }


}
