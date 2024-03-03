/*
 * Copyright (c) 2006 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.09.2006 11:17:54
 */
package de.augustakom.hurrican.model.cc;

import java.io.*;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.augustakom.hurrican.model.base.AbstractHurricanModel;
import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modell dient zum Produkt-Mapping zwischen den Billing- und Hurrican-Produkten. <br> Es wird davon ausgegangen, dass
 * immer genau eine 'extProdNo' auf eine 'prodId' verweist bzw. dass eine Menge von 'extProdNo's zusammen(!) auf eine
 * 'prodId' verweist. Verweisen mehrere 'extProdNo's auf eine Hurrican Produkt-ID, besitzen diese die gleiche
 * 'mappingGroup'.
 *
 *
 */
public class ProduktMapping extends AbstractHurricanModel implements CCProduktModel, Serializable {

    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'phone' - fuer Phone-Leistungen.
     */
    public static final String MAPPING_PART_TYPE_PHONE = "phone";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'dsl' - fuer DSL-Leistungen.
     */
    public static final String MAPPING_PART_TYPE_DSL = "dsl";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'phone_dsl'. Sonder-Konfiguration fuer alte Auftraege aus MUC, die
     * keine Telefonie-Leistung im Billing-System besitzen!
     */
    public static final String MAPPING_PART_TYPE_PHONE_DSL = "phone_dsl";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'sdsl' - fuer SDSL-Leistungen.
     */
    public static final String MAPPING_PART_TYPE_SDSL = "sdsl";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'sdsl_sub' - fuer SDSL-Zusatzleistungen.
     */
    public static final String MAPPING_PART_TYPE_SDSL_SUB = "sdsl_sub";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'connect' - fuer Connect-Leistungen.
     */
    public static final String MAPPING_PART_TYPE_CONNECT = "connect";
    /**
     * Definiert den Wert fuer den Mapping Part-Typ 'online' - fuer Online-Leistungen.
     */
    public static final String MAPPING_PART_TYPE_ONLINE = "online";

    public static final String MAPPING_GROUP = "mappingGroup";
    private Long mappingGroup = null;
    private Long extProdNo = null;
    private Long prodId = null;
    private String mappingPartType = null;
    public static final String PRIORITY = "priority";
    private Long priority = null;

    /**
     * Default-Const.
     */
    public ProduktMapping() {
    }

    /**
     * @param mappingGroup
     * @param extProdNo
     * @param prodId
     */
    public ProduktMapping(Long mappingGroup, Long extProdNo, Long prodId) {
        super();
        this.mappingGroup = mappingGroup;
        this.extProdNo = extProdNo;
        this.prodId = prodId;
    }

    /**
     * @return Returns the extProdNo.
     */
    public Long getExtProdNo() {
        return this.extProdNo;
    }

    /**
     * @param extProdNo The extProdNo to set.
     */
    public void setExtProdNo(Long extProdNo) {
        this.extProdNo = extProdNo;
    }

    /**
     * @return Returns the prodId.
     */
    public Long getProdId() {
        return this.prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    /**
     * @return Returns the mappingGroup.
     */
    public Long getMappingGroup() {
        return this.mappingGroup;
    }

    /**
     * @param mappingGroup The mappingGroup to set.
     */
    public void setMappingGroup(Long mappingGroup) {
        this.mappingGroup = mappingGroup;
    }

    /**
     * Gibt den Leistungs-Typ zurueck, der dem Mapping-Anteil entspricht. Als Leistungs-Typ (definiert als Konstanten in
     * diesem Modell) werden z.B. Phone-Leistungen, DSL-Leistungen oder aehnliches verstanden.
     *
     * @return Returns the mappingPartType.
     */
    public String getMappingPartType() {
        return this.mappingPartType;
    }

    /**
     * @param mappingPartType The mappingPartType to set.
     */
    public void setMappingPartType(String mappingPartType) {
        this.mappingPartType = mappingPartType;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
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

        ProduktMapping pm = (ProduktMapping) obj;
        if (getMappingGroup() != null) {
            if (!getMappingGroup().equals(pm.getMappingGroup())) {
                return false;
            }
        }
        else if (pm.getMappingGroup() != null) {
            return false;
        }

        if (getExtProdNo() != null) {
            if (!getExtProdNo().equals(pm.getExtProdNo())) {
                return false;
            }
        }
        else if (pm.getExtProdNo() != null) {
            return false;
        }

        if (getProdId() != null) {
            if (!getProdId().equals(pm.getProdId())) {
                return false;
            }
        }
        else if (pm.getProdId() != null) {
            return false;
        }

        if (getPriority() != null) {
            if (!getPriority().equals(pm.getPriority())) {
                return false;
            }
        }
        else if (pm.getPriority() != null) {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(281, 15)
                .append(mappingGroup)
                .append(extProdNo)
                .append(prodId)
                .append(priority)
                .toHashCode();
    }

}


