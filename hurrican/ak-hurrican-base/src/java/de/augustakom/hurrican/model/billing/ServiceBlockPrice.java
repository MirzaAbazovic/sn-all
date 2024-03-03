/*
 * Copyright (c) 2009 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 08.05.2009 13:16:38
 */
package de.augustakom.hurrican.model.billing;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Modell-Klasse fuer die Abbildung von sog. Block-Preisen zu einer Leistung. <br> Ueber diese Block-Preise sind z.B.
 * Staffelungen bei Volumenleistungen realisiert.
 *
 *
 */
public class ServiceBlockPrice extends AbstractBillingModel {

    private Long leistungNo = null;
    private Float quantityFrom = null;
    private Float blockQuantity = null;
    private Float blockPrice = null;

    /**
     * @return the leistungNo
     */
    public Long getLeistungNo() {
        return leistungNo;
    }

    /**
     * @param leistungNo the leistungNo to set
     */
    public void setLeistungNo(Long leistungNo) {
        this.leistungNo = leistungNo;
    }

    /**
     * @return the quantityFrom
     */
    public Float getQuantityFrom() {
        return quantityFrom;
    }

    /**
     * @param quantityFrom the quantityFrom to set
     */
    public void setQuantityFrom(Float quantityFrom) {
        this.quantityFrom = quantityFrom;
    }

    /**
     * @return the blockQuantity
     */
    public Float getBlockQuantity() {
        return blockQuantity;
    }

    /**
     * @param blockQuantity the blockQuantity to set
     */
    public void setBlockQuantity(Float blockQuantity) {
        this.blockQuantity = blockQuantity;
    }

    /**
     * @return the blockPrice
     */
    public Float getBlockPrice() {
        return blockPrice;
    }

    /**
     * @param blockPrice the blockPrice to set
     */
    public void setBlockPrice(Float blockPrice) {
        this.blockPrice = blockPrice;
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

        ServiceBlockPrice sbp = (ServiceBlockPrice) obj;
        if (getLeistungNo() != null) {
            if (!getLeistungNo().equals(sbp.getLeistungNo())) {
                return false;
            }
        }
        else if (sbp.getLeistungNo() != null) {
            return false;
        }

        if (getQuantityFrom() != null) {
            if (!getQuantityFrom().equals(sbp.getQuantityFrom())) {
                return false;
            }
        }
        else if (sbp.getQuantityFrom() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(111, 47)
                .append(leistungNo)
                .append(quantityFrom)
                .toHashCode();
    }

}


