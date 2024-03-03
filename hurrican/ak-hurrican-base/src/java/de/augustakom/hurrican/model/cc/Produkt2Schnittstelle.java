/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2004 15:56:41
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modell bildet ein Mapping zwischen einem Produkt und einer (Hardware-)Schnittstelle ab.
 *
 *
 */
public class Produkt2Schnittstelle extends AbstractCCModel implements CCProduktModel {

    private Long produktId = null;
    private Long schnittstelleId = null;

    /**
     * @see de.augustakom.hurrican.model.shared.iface.CCProduktModel#getProdId()
     */
    public Long getProdId() {
        return getProduktId();
    }

    /**
     * @return Returns the produktId.
     */
    public Long getProduktId() {
        return produktId;
    }

    /**
     * @param produktId The produktId to set.
     */
    public void setProduktId(Long produktId) {
        this.produktId = produktId;
    }

    /**
     * @return Returns the schnittstelleId.
     */
    public Long getSchnittstelleId() {
        return schnittstelleId;
    }

    /**
     * @param schnittstelleId The schnittstelleId to set.
     */
    public void setSchnittstelleId(Long schnittstelleId) {
        this.schnittstelleId = schnittstelleId;
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

        Produkt2Schnittstelle p2s = (Produkt2Schnittstelle) obj;
        if (getProduktId() != null) {
            if (!getProduktId().equals(p2s.getProduktId())) {
                return false;
            }
        }
        else if (p2s.getProduktId() != null) {
            return false;
        }

        if (getSchnittstelleId() != null) {
            if (!getSchnittstelleId().equals(p2s.getSchnittstelleId())) {
                return false;
            }
        }
        else if (p2s.getSchnittstelleId() != null) {
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
                .append(produktId)
                .append(schnittstelleId)
                .toHashCode();
    }
}


