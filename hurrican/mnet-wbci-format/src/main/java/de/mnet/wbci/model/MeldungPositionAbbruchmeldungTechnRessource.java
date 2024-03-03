/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 12.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("MPOS-ABBM-TR")
public class MeldungPositionAbbruchmeldungTechnRessource extends MeldungPosition {

    private static final long serialVersionUID = 5623136935933658611L;

    @Override
    @Transient
    public MeldungPositionTyp getPositionTyp() {
        return MeldungPositionTyp.ABBM_TR;
    }

    @Override
    public String toString() {
        return "MeldungspositionAbbmTr [" + super.toString() + "]";
    }

}
