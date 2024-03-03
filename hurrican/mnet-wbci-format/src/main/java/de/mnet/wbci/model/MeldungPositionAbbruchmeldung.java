/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.13
 */
package de.mnet.wbci.model;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("MPOS-ABBM")
public class MeldungPositionAbbruchmeldung extends MeldungPosition {

    private static final long serialVersionUID = -2202394694531090814L;
    private Set<MeldungPositionAbbmRufnummer> rufnummern;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "MPOS_ABBM_ID", nullable = false)
    @Fetch(FetchMode.SUBSELECT)
    @Valid
    public Set<MeldungPositionAbbmRufnummer> getRufnummern() {
        return rufnummern;
    }

    public void setRufnummern(Set<MeldungPositionAbbmRufnummer> rufnummern) {
        this.rufnummern = rufnummern;
    }

    @Override
    @Transient
    public MeldungPositionTyp getPositionTyp() {
        return MeldungPositionTyp.ABBM;
    }

    @Override
    public String toString() {
        return "MeldungspositionAbbm [rufnummern=" + rufnummern + ", toString=" + super.toString() + "]";
    }

}
