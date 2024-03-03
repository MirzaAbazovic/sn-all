/*
 * Copyright (c) 2011 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 27.12.2011 08:44:49
 */
package de.augustakom.hurrican.model.cc.housing;

import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.augustakom.hurrican.model.cc.AbstractCCIDModel;

/**
 * Modell-Klasse, die eine Transponder-Gruppe abbildet.
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "equals in super class just compares id therefore no overriding necessary")
@Entity
@Table(name = "T_TRANSPONDER_GROUP")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_TRANSPONDER_GROUP_0", allocationSize = 1)
public class TransponderGroup extends AbstractCCIDModel {

    public static final String KUNDE_NO = "kundeNo";
    private Long kundeNo;
    public static final String GET_TRANSPONDER_DESCRIPTION = "getTransponderDescription";
    private String transponderDescription;
    private Set<Transponder> transponders = new HashSet<Transponder>();

    @Column(name = "KUNDE__NO")
    @NotNull
    public Long getKundeNo() {
        return kundeNo;
    }

    public void setKundeNo(Long kundeNo) {
        this.kundeNo = kundeNo;
    }

    @Column(name = "TRANSPONDER_DESCRIPTION")
    @NotNull
    public String getTransponderDescription() {
        return transponderDescription;
    }

    public void setTransponderDescription(String transponderDescription) {
        this.transponderDescription = transponderDescription;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TRANSPONDER_GROUP_ID", nullable = true)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade({ org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    public Set<Transponder> getTransponders() {
        return transponders;
    }

    public void setTransponders(Set<Transponder> transponder2Group) {
        this.transponders = transponder2Group;
    }

}


