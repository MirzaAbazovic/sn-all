/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 26.05.2011 12:08:10
 */
package de.mnet.wita.message.common;

import javax.persistence.*;
import javax.validation.constraints.*;

import de.mnet.wita.message.MwfEntity;
import de.mnet.wita.message.auftrag.Anrede;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_KUNDENNAME")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "kundennametyp", discriminatorType = DiscriminatorType.STRING)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_KUNDENNAME_0", allocationSize = 1)
public abstract class Kundenname extends MwfEntity {

    private static final long serialVersionUID = 2265410371667970063L;

    private Anrede anrede;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar2(10)")
    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    @Override
    public String toString() {
        return "Kundenname [anrede=" + anrede + "]";
    }

}
