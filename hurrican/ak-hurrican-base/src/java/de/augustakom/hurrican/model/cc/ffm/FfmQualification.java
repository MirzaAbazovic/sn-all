/*
 * Copyright (c) 2014 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 04.09.14
 */
package de.augustakom.hurrican.model.cc.ffm;

import javax.persistence.*;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell-Klasse stellt eine 'Qualification' (in FFM: Skill) dar, die ein Service-Techniker fuer die Erledigung eines
 * Vorgangs benoetigt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity(name = "de.augustakom.hurrican.model.cc.ffm.FfmQualification")
@Table(name = "T_FFM_QUALIFICATION")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_FFM_QUALIFICATION_0", allocationSize = 1)
public class FfmQualification extends AbstractCCIDModel {

    private static final long serialVersionUID = -5422494832819915953L;

    private String qualification;
    /**
     * represents an additonal duration for a special FFM skill, which should be added to normal calculated duration at
     * the {@link FfmProductMapping#ffmPlannedDuration}.
     */
    private Integer additionalDuration;

    @Column(name = "QUALIFICATION", unique = true, nullable = false)
    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    @Column(name = "ADDITIONAL_DURATION", nullable = true)
    public Integer getAdditionalDuration() {
        return additionalDuration;
    }

    public void setAdditionalDuration(Integer additionalDuration) {
        this.additionalDuration = additionalDuration;
    }

    public String toString() {
        return "FfmQualification{" +
                "id=" + getId() +
                ", qualification=" + getQualification() +
                ", additionalDuration=" + getAdditionalDuration() +
                ", toString='" + super.toString() + '\'' +
                "}";
    }

}
