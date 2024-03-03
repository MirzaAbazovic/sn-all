/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.07.2004 14:59:12
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Modell bildet ein Mapping zwischen einem Produkt und einer Bauauftragsverlauf-Aenderungsgruppe ab.
 *
 *
 */
public class BAVerlaufAG2Produkt extends AbstractCCModel {

    private Long produktId = null;
    private Long baVerlaufAenderungGruppeId = null;

    /**
     * @return Returns the baVerlaufAenderungGruppeId.
     */
    public Long getBaVerlaufAenderungGruppeId() {
        return baVerlaufAenderungGruppeId;
    }

    /**
     * @param baVerlaufAenderungGruppeId The baVerlaufAenderungGruppeId to set.
     */
    public void setBaVerlaufAenderungGruppeId(Long baVerlaufAenderungGruppeId) {
        this.baVerlaufAenderungGruppeId = baVerlaufAenderungGruppeId;
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

        BAVerlaufAG2Produkt b2p = (BAVerlaufAG2Produkt) obj;
        if (getProduktId() != null) {
            if (!getProduktId().equals(b2p.getProduktId())) {
                return false;
            }
        }
        else if (b2p.getProduktId() != null) {
            return false;
        }

        if (getBaVerlaufAenderungGruppeId() != null) {
            if (!getBaVerlaufAenderungGruppeId().equals(b2p.getBaVerlaufAenderungGruppeId())) {
                return false;
            }
        }
        else if (b2p.getBaVerlaufAenderungGruppeId() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(25, 77)
                .append(produktId)
                .append(baVerlaufAenderungGruppeId)
                .toHashCode();
    }
}


