/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.05.2011 14:20:52
 */
package de.mnet.wita.message.auftrag;

import java.util.*;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.mnet.wita.message.MwfEntity;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_MWF_SCHALTANGABEN")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_MWF_SCHALTANGABEN_0", allocationSize = 1)
public class Schaltangaben extends MwfEntity {

    private static final long serialVersionUID = 4263803394890171115L;

    private List<SchaltungKupfer> schaltungKupfer;
    private List<SchaltungKvzTal> schaltungKvzTal;

    @Override
    public String toString() {
        return "Schaltung [schaltungKupfer=" + schaltungKupfer + ", schaltungKvzTal=" + schaltungKvzTal + "]";
    }

    @Size(max = 2)
    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "SCHALTANGABEN_ID", nullable = false)
    public List<SchaltungKupfer> getSchaltungKupfer() {
        return schaltungKupfer;
    }

    public void setSchaltungKupfer(List<SchaltungKupfer> schaltungKupfer) {
        this.schaltungKupfer = schaltungKupfer;
    }

    @Size(max = 2)
    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 1000)
    @JoinColumn(name = "SCHALTANGABEN_ID", nullable = false)
    public List<SchaltungKvzTal> getSchaltungKvzTal() {
        return schaltungKvzTal;
    }

    public void setSchaltungKvzTal(List<SchaltungKvzTal> schaltungKvzTal) {
        this.schaltungKvzTal = schaltungKvzTal;
    }
}
