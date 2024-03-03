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
@DiscriminatorValue("MPOS-ERLM")
public class MeldungPositionErledigtmeldung extends MeldungPosition {

    private static final long serialVersionUID = 5623136935913658611L;

    @Override
    @Transient
    public MeldungPositionTyp getPositionTyp() {
        return MeldungPositionTyp.ERLM;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "MeldungspositionErlm [" + super.toString() + "]";
    }

}
