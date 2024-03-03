/*
 * Copyright (c) 2006 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 15.06.2006 07:51:56
 */
package de.augustakom.hurrican.model.billing;

import java.io.*;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.shared.iface.ExternLeistungAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternMiscAwareModel;
import de.augustakom.hurrican.model.shared.iface.ExternProduktAwareModel;


/**
 * Modell zur Abbildung eines Leistungs-Wertes (Werteliste).
 *
 *
 */
public class ServiceValue extends AbstractBillingModel implements ExternProduktAwareModel,
        ExternLeistungAwareModel, ExternMiscAwareModel, Serializable {

    private Long leistungNo = null;
    private String value = null;
    private Long externLeistungNo = null;
    private Long externProduktNo = null;
    private Long externMiscNo = null;

    /**
     * Ueberprueft, ob die Leistung fuer externe Systeme gekennzeichnet ist.
     *
     * @return true wenn eine der externen IDs gesetzt ist.
     *
     */
    public boolean hasKennzeichnungExtern() {
        if (getExternLeistungNo() != null || getExternMiscNo() != null || getExternProduktNo() != null) {
            return true;
        }
        return false;
    }

    /**
     * Ueberprueft, ob dem Value eine externe Produkt- od. Leistungs-Kennzeichnung zugeordnet ist.
     *
     * @return
     *
     */
    public boolean isProduktOrLeistung() {
        return (getExternProduktNo() != null || getExternLeistungNo() != null) ? true : false;
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

        ServiceValue sv = (ServiceValue) obj;
        if (getLeistungNo() != null) {
            if (!getLeistungNo().equals(sv.getLeistungNo())) {
                return false;
            }
        }
        else if (sv.getLeistungNo() != null) {
            return false;
        }

        if (getValue() != null) {
            if (!getValue().equals(sv.getValue())) {
                return false;
            }
        }
        else if (sv.getValue() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(87, 51)
                .append(getLeistungNo())
                .append(getValue())
                .toHashCode();
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.ExternLeistungAwareModel#getExternLeistungNo()
     */
    public Long getExternLeistungNo() {
        return this.externLeistungNo;
    }

    /**
     * @param externLeistungNo The externLeistungNo to set.
     */
    public void setExternLeistungNo(Long externLeistungNo) {
        this.externLeistungNo = externLeistungNo;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.ExternMiscAwareModel#getExternMiscNo()
     */
    public Long getExternMiscNo() {
        return this.externMiscNo;
    }

    /**
     * @param externMiscNo The externMiscNo to set.
     */
    public void setExternMiscNo(Long externMiscNo) {
        this.externMiscNo = externMiscNo;
    }

    /**
     * @see de.augustakom.hurrican.model.shared.iface.ExternProduktAwareModel#getExternProduktNo()
     */
    public Long getExternProduktNo() {
        return this.externProduktNo;
    }

    /**
     * @param externProduktNo The externProduktNo to set.
     */
    public void setExternProduktNo(Long externProduktNo) {
        this.externProduktNo = externProduktNo;
    }

    /**
     * @return Returns the leistungNo.
     */
    public Long getLeistungNo() {
        return this.leistungNo;
    }

    /**
     * @param leistungNo The leistungNo to set.
     */
    public void setLeistungNo(Long leistungNo) {
        this.leistungNo = leistungNo;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String name) {
        this.value = name;
    }

}


