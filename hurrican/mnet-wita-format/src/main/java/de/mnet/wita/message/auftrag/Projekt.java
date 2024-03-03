/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.08.2011 11:37:34
 */
package de.mnet.wita.message.auftrag;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_PROJEKT")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_PROJEKT_0", allocationSize = 1)
public class Projekt extends MwfEntity {
    private static final long serialVersionUID = -1927980480356514240L;

    private String projektKenner;
    private String kopplungsKenner;

    public Projekt() {
        // required by Hibernate
    }

    public Projekt(String projektKenner) {
        this(projektKenner, null);
    }

    public Projekt(String projektKenner, String kopplungsKenner) {
        this.projektKenner = projektKenner;
        this.kopplungsKenner = kopplungsKenner;
    }

    @Column(name = "PROJEKT_KENNER")
    @Size(max = 30)
    @NotNull
    public String getProjektKenner() {
        return projektKenner;
    }

    public void setProjektKenner(String projektKenner) {
        this.projektKenner = projektKenner;
    }

    @Column(name = "KOPPLUNG_KENNER")
    @Size(max = 30)
    public String getKopplungsKenner() {
        return kopplungsKenner;
    }

    public void setKopplungsKenner(String kopplungsKenner) {
        this.kopplungsKenner = kopplungsKenner;
    }
}


