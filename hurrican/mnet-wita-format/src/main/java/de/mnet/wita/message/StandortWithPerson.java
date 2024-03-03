/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 12:08:10
 */
package de.mnet.wita.message;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.auftrag.AbstractStandort;
import de.mnet.wita.message.common.Kundenname;

/**
 * Klasse, die einen WITA-Standort mit Personen-Informationen besitzt.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@MappedSuperclass
public class StandortWithPerson extends AbstractStandort {

    private static final long serialVersionUID = -1304222581104611137L;

    private Kundenname kundenname;

    @NotNull
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "KUNDENNAME_ID")
    public Kundenname getKundenname() {
        return kundenname;
    }

    public void setKundenname(Kundenname kundenname) {
        this.kundenname = kundenname;
    }

    @Override
    public String toString() {
        return "StandortWithPerson [kundenname=" + kundenname + ", toString()=" + super.toString() + "]";
    }


}
