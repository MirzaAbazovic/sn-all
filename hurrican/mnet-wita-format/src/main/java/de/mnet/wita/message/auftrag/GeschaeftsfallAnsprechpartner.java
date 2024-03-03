/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2011 17:06:26
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_GF_ANSPRECHPARTNER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_GF_ANSPRECHPARTNER_0", allocationSize = 1)
public class GeschaeftsfallAnsprechpartner extends MwfEntity {

    // <ansprechpartner>
    // ... auftragsmanagement
    // ... ansprechpartner ...
    // </ansprechpartner>

    private static final long serialVersionUID = -3301822156723185335L;

    private Ansprechpartner ansprechpartner;
    private Auftragsmanagement auftragsmanagement;

    @Override
    public String toString() {
        return "GeschaeftsfallAnsprechpartner [ansprechpartner=" + ansprechpartner
                + ", auftragsmanagement=" + auftragsmanagement + "]";
    }

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Ansprechpartner getAnsprechpartner() {
        return ansprechpartner;
    }

    public void setAnsprechpartner(Ansprechpartner ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    public Auftragsmanagement getAuftragsmanagement() {
        return auftragsmanagement;
    }

    public void setAuftragsmanagement(Auftragsmanagement auftragsmanagement) {
        this.auftragsmanagement = auftragsmanagement;
    }

}
