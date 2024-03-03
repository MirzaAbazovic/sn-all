/*
 * Copyright (c) 2013 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 13.09.13
 */
package de.mnet.wbci.model;

import javax.persistence.*;

/**
 *
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@DiscriminatorValue("ABBM-TR")
public class AbbruchmeldungTechnRessource extends Meldung<MeldungPositionAbbruchmeldungTechnRessource> {

    private static final long serialVersionUID = -1443643226122062111L;

    public AbbruchmeldungTechnRessource() {
        super(MeldungTyp.ABBM_TR);
    }

    @Override
    public String toString() {
        return "Abbruchmeldung techn. Ressource [" + super.toString() + "]";
    }

}
