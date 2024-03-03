/*
 * Copyright (c) 2007 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.04.2007 16:24:25
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modell fuer die Zuordnung eines DSLAM-Profils zu einem Produkt.
 *
 *
 */
public class Produkt2DSLAMProfile extends AbstractCCModel implements CCProduktModel {

    private Long prodId = null;
    private Long dslamProfileId = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCProduktModel#getProdId()
     */
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the dslamProfileId.
     */
    public Long getDslamProfileId() {
        return dslamProfileId;
    }

    /**
     * @param dslamProfileId The dslamProfileId to set.
     */
    public void setDslamProfileId(Long dslamProfileId) {
        this.dslamProfileId = dslamProfileId;
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

        Produkt2DSLAMProfile p2d = (Produkt2DSLAMProfile) obj;
        if (getProdId() != null) {
            if (!getProdId().equals(p2d.getProdId())) {
                return false;
            }
        }
        else if (p2d.getProdId() != null) {
            return false;
        }

        if (getDslamProfileId() != null) {
            if (!getDslamProfileId().equals(p2d.getDslamProfileId())) {
                return false;
            }
        }
        else if (p2d.getDslamProfileId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(351, 47)
                .append(prodId)
                .append(dslamProfileId)
                .toHashCode();
    }
}


