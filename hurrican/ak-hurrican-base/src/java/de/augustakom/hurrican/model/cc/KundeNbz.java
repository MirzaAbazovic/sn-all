/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 30.06.2004 08:05:28
 */
package de.augustakom.hurrican.model.cc;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Modell bildet eine Zuordnung von Kunde - Nutzerbzeichnung ab. <br>
 *
 *
 */
public class KundeNbz extends AbstractCCIDModel {

    private Long kundeNo = null;
    private String nbz = null;

    /**
     * @return the nbz
     */
    public String getNbz() {
        return nbz;
    }

    /**
     * @param nbz the nbz to set
     */
    public void setNbz(String nbz) {
        this.nbz = nbz;
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
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

        KundeNbz a = (KundeNbz) obj;
        if (getId() != null) {
            if (!getId().equals(a.getId())) {
                return false;
            }
        }
        else if (a.getId() != null) {
            return false;
        }

        if (getKundeNo() != null) {
            if (!getKundeNo().equals(a.getKundeNo())) {
                return false;
            }
        }
        else if (a.getKundeNo() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(33, 97)
                .append(getId())
                .append(kundeNo)
                .toHashCode();
    }
}
