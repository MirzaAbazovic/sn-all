/*
 * Copyright (c) 2012 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 20.04.2012 09:28:45
 */
package de.augustakom.hurrican.model.cc.fttx;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;
import de.augustakom.hurrican.model.cc.TechLeistung;

/**
 * Modellklasse zur Konfiguration eines {@link CVlan} zu einer Hurrican {@link TechLeistung}. Die Konfiguration verhaelt
 * sich abhängig vom #removeLogic wie folgt: <ul> <li>false: Wenn das Produkt den CvlanServiceTyp als optional
 * konfiguriert, wird das CVlan berechnet</li> <li>true: das CVlan (der CvlanServiceTyp) wird NICHT berechnet, d.h.
 * immer entfernt (auch wenn das Produkt diesen als mandatory konfiguriert).</li> </ul>
 */
@Entity
@Table(name = "T_TECH_LEISTUNG_2_CVLAN",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "TECH_LS_ID", "CVLAN_TYP" }) })
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_TECH_LEISTUNG_2_CVLAN_0", allocationSize = 1)
public class TechLeistung2Cvlan extends AbstractCCIDModel {

    private Long techLeistungId;
    private CvlanServiceTyp cvlanTyp;
    private Boolean removeLogic;

    @NotNull
    @Column(name = "TECH_LS_ID")
    public Long getTechLeistungId() {
        return techLeistungId;
    }

    public void setTechLeistungId(Long techLeistungId) {
        this.techLeistungId = techLeistungId;
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "CVLAN_TYP")
    public CvlanServiceTyp getCvlanTyp() {
        return cvlanTyp;
    }

    public void setCvlanTyp(CvlanServiceTyp cvlanTyp) {
        this.cvlanTyp = cvlanTyp;
    }

    @NotNull
    @Column(name = "REMOVE_LOGIC")
    public Boolean getRemoveLogic() {
        return removeLogic;
    }

    public void setRemoveLogic(Boolean removeLogic) {
        this.removeLogic = removeLogic;
    }
}
