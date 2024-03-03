/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2005 13:39:22
 */
package de.augustakom.hurrican.model.cc.dn;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.cc.AbstractCCModel;

/**
 * Modell zuordnung der Rufnummerntyps zu Produkt
 *
 *
 */
public class Leistungsbuendel2Produkt extends AbstractCCModel {

    private Long lbId = null;
    private Long leistungNoOrig = null;
    private Long protokollLeistungNoOrig = null;
    private Long productOeNo = null;

    /**
     * @return Returns the lbId.
     */
    public Long getLbId() {
        return lbId;
    }

    /**
     * @param lbId The lbId to set.
     */
    public void setLbId(Long lbId) {
        this.lbId = lbId;
    }

    /**
     * @return Returns the leistungNoOrig.
     */
    public Long getLeistungNoOrig() {
        return this.leistungNoOrig;
    }

    /**
     * @param leistungNoOrig The leistungNoOrig to set.
     */
    public void setLeistungNoOrig(Long leistungNoOrig) {
        this.leistungNoOrig = leistungNoOrig;
    }

    /**
     * @return Returns the productOeNo.
     */
    public Long getProductOeNo() {
        return productOeNo;
    }

    /**
     * @param productOeNo The productOeNo to set.
     */
    public void setProductOeNo(Long productOeNo) {
        this.productOeNo = productOeNo;
    }

    /**
     * Gibt die Leistungsnummer einer Protokoll-Leistung (z.B. 1TR6) zurueck. Ist dieser Parameter gefuellt, muss dem
     * Auftrag diese Leistung zugeordnet sein, um auf das Leistungsbuendel zu kommen.
     *
     * @return Returns the protokollLeistungNoOrig.
     */
    public Long getProtokollLeistungNoOrig() {
        return this.protokollLeistungNoOrig;
    }

    /**
     * @param protokollLeistungNoOrig The protokollLeistungNoOrig to set.
     */
    public void setProtokollLeistungNoOrig(Long protokollLeistungNoOrig) {
        this.protokollLeistungNoOrig = protokollLeistungNoOrig;
    }

    /**
     * @see java.lang.Integer#equals(java.lang.Object)
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

        Leistungsbuendel2Produkt oeT = (Leistungsbuendel2Produkt) obj;
        if (getLbId() != null) {
            if (!getLbId().equals(oeT.getLbId())) {
                return false;
            }
        }
        else if (oeT.getLbId() != null) {
            return false;
        }

        if (getLeistungNoOrig() != null) {
            if (!getLeistungNoOrig().equals(oeT.getLeistungNoOrig())) {
                return false;
            }
        }
        else if (oeT.getLeistungNoOrig() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Integer#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 79)
                .append(lbId)
                .append(leistungNoOrig)
                .toHashCode();
    }

}
