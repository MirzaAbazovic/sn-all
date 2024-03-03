/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 11:40:45
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;


@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_AUFTRAGSKENNER")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_AUFTRAGSKENNER_0", allocationSize = 1)
public class AuftragsKenner extends MwfEntity {
    private static final long serialVersionUID = -1832401814033183683L;
    private Long auftragsKlammer;
    private Integer anzahlAuftraege;

    public AuftragsKenner() {
        // required by Hibernate
    }

    public AuftragsKenner(Long auftragsKlammer, Integer anzahlAuftraege) {
        this.auftragsKlammer = auftragsKlammer;
        this.anzahlAuftraege = anzahlAuftraege;
    }

    @Column(name = "AUFTRAG_KLAMMER")
    @Digits(fraction = 0, integer = 10)
    public Long getAuftragsKlammer() {
        return auftragsKlammer;
    }

    public void setAuftragsKlammer(Long auftragsKlammer) {
        this.auftragsKlammer = auftragsKlammer;
    }

    @Column(name = "ANZAHL_AUFTRAEGE")
    @Min(2)
    @Max(20)
    public Integer getAnzahlAuftraege() {
        return anzahlAuftraege;
    }

    public void setAnzahlAuftraege(Integer anzahlAuftraege) {
        this.anzahlAuftraege = anzahlAuftraege;
    }

    @Override
    public String toString() {
        return "AuftragsKenner [auftragsKlammer=" + auftragsKlammer + ", anzahlAuftraege=" + anzahlAuftraege + "]";
    }

}


