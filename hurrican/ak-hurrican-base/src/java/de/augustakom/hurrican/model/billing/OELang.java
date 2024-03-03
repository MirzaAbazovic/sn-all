/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.06.2006 13:09:10
 */
package de.augustakom.hurrican.model.billing;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Uebersetzungsmodell fuer OE.
 *
 *
 */
public class OELang extends AbstractLanguageModel {

    private Long oeNo = null;

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

        OELang oelang = (OELang) obj;
        if (getOeNo() != null) {
            if (!getOeNo().equals(oelang.getOeNo())) {
                return false;
            }
        }
        else if (oelang.getOeNo() != null) {
            return false;
        }

        if (getLanguage() != null) {
            if (!getLanguage().equals(oelang.getLanguage())) {
                return false;
            }
        }
        else if (oelang.getLanguage() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(139, 73)
                .append(language)
                .append(oeNo)
                .toHashCode();
    }

    /**
     * @return Returns the oeNo.
     */
    public Long getOeNo() {
        return this.oeNo;
    }

    /**
     * @param oeNo The oeNo to set.
     */
    public void setOeNo(Long oeNo) {
        this.oeNo = oeNo;
    }

}


