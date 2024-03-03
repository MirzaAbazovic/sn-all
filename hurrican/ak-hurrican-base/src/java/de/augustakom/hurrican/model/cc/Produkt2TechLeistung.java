/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.07.2004 15:56:41
 */
package de.augustakom.hurrican.model.cc;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.shared.iface.CCProduktModel;


/**
 * Modell bildet ein Mapping zwischen einem Produkt und den moeglichen technischen Leistungen ab.
 *
 *
 */
@Entity
@Table(name = "T_PROD_2_TECH_LEISTUNG")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_PROD_2_TECH_LEISTUNG_0", allocationSize = 1)
public class Produkt2TechLeistung extends AbstractCCIDModel implements CCProduktModel {

    private Long prodId = null;
    private TechLeistung techLeistung;
    private Long techLeistungDependency = null;
    private Boolean defaultLeistung = null;
    private Integer priority;

    /**
     * @return Returns the prodId.
     */
    @Column(name = "PROD_ID", nullable = false)
    @NotNull
    public Long getProdId() {
        return prodId;
    }

    /**
     * @param prodId The prodId to set.
     */
    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    @ManyToOne
    @JoinColumn(name = "TECH_LS_ID", nullable = false)
    @NotNull
    public TechLeistung getTechLeistung() {
        return techLeistung;
    }

    public void setTechLeistung(TechLeistung techLeistung) {
        this.techLeistung = techLeistung;
    }

    /**
     * @return Returns the defaultLeistung.
     */
    @Column(name = "IS_DEFAULT")
    public Boolean getDefaultLeistung() {
        return this.defaultLeistung;
    }

    /**
     * @param defaultLeistung The defaultLeistung to set.
     */
    public void setDefaultLeistung(Boolean defaultLeistung) {
        this.defaultLeistung = defaultLeistung;
    }

    /**
     * @return Returns the techLeistungDependency.
     */
    @Column(name = "TECH_LS_DEPENDENCY")
    public Long getTechLeistungDependency() {
        return this.techLeistungDependency;
    }

    /**
     * @param techLeistungDependency The techLeistungDependency to set.
     */
    public void setTechLeistungDependency(Long techLeistungDependency) {
        this.techLeistungDependency = techLeistungDependency;
    }

    @Column(name = "PRIORITY")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}


