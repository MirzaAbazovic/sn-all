/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 22.10.2004 10:33:40
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Ueber dieses Modell wird definiert, welche bei welchem Produkt welche Abteilung ueber eine Sperre informiert wird.
 *
 *
 */
public class SperreVerteilung extends AbstractCCModel {

    private Long produktId = null;
    private Long abteilungId = null;

    /**
     * @return Returns the abteilungId.
     */
    public Long getAbteilungId() {
        return abteilungId;
    }

    /**
     * @param abteilungId The abteilungId to set.
     */
    public void setAbteilungId(Long abteilungId) {
        this.abteilungId = abteilungId;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProduktId(Long prodId) {
        this.produktId = prodId;
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

        SperreVerteilung sv = (SperreVerteilung) obj;
        if (getProduktId() != null) {
            if (!getProduktId().equals(sv.getProduktId())) {
                return false;
            }
        }
        else if (sv.getProduktId() != null) {
            return false;
        }

        if (getAbteilungId() != null) {
            if (!getAbteilungId().equals(sv.getAbteilungId())) {
                return false;
            }
        }
        else if (sv.getAbteilungId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 79)
                .append(produktId)
                .append(abteilungId)
                .toHashCode();
    }
}


