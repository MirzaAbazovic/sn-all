/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 18.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("MPOS-AKM-TR")
public class MeldungPositionUebernahmeRessourceMeldung extends MeldungPosition {

    private static final long serialVersionUID = 5623136935913658611L;

    @Override
    @Transient
    public MeldungPositionTyp getPositionTyp() {
        return MeldungPositionTyp.AKM_TR;
    }

    @Override
    public String toString() {
        return "MeldungPositionUebernahmeRessource [" + super.toString() + "]";
    }

}
