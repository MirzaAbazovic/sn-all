/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.08.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;
import javax.validation.*;

import de.mnet.wbci.validation.constraints.CheckRuemVaMeldungscodeADA;
import de.mnet.wbci.validation.groups.V1Meldung;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@CheckRuemVaMeldungscodeADA(groups = V1Meldung.class)
@DiscriminatorValue("MPOS-RUEM-VA")
public class MeldungPositionRueckmeldungVa extends MeldungPosition {
    private static final long serialVersionUID = 6713564256093186148L;

    private Standort standortAbweichend;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "STANDORT_ABWEICHEND_ID")
    @Valid
    public Standort getStandortAbweichend() {
        return standortAbweichend;
    }

    public void setStandortAbweichend(Standort standortAbweichend) {
        this.standortAbweichend = standortAbweichend;
    }

    @Override
    @Transient
    public MeldungPositionTyp getPositionTyp() {
        return MeldungPositionTyp.RUEM_VA;
    }

    @Override
    public String toString() {
        return "MeldungspositionRuemVa [standortAbweichend=" + standortAbweichend + ", toString=" + super.toString();
    }
}
