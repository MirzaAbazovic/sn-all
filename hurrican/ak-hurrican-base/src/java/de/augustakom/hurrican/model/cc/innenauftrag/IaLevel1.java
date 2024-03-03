/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.02.2015
 */
package de.augustakom.hurrican.model.cc.innenauftrag;

import java.util.*;
import javax.annotation.*;
import javax.persistence.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Modell-Klasse bildet die moeglichen 'Level1' fuer Innenauftraege ab.
 */
@Entity
@Table(name = "T_IA_LEVEL1")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IA_LEVEL1_0", allocationSize = 1)
public class IaLevel1 extends AbstractIaLevel {

    private static final long serialVersionUID = 3589337919664826509L;

    private List<IaLevel3> level3s;

    private String bereichName;

    private boolean lockMode;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LEVEL1_ID", referencedColumnName = "id", nullable = false)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<IaLevel3> getLevel3s() {
        return level3s;
    }

    public void setLevel3s(List<IaLevel3> level3s) {
        this.level3s = level3s;
    }

    @Nullable
    @Column(name = "bereich_name")
    public String getBereichName() {
        return bereichName;
    }

    public void setBereichName(final String bereichName) {
        this.bereichName = bereichName;
    }

    @Column(name = "lock_mode", nullable = false)
    public boolean isLockMode() {
        return lockMode;
    }

    public void setLockMode(boolean lockMode) {
        this.lockMode = lockMode;
    }

    public void addIaLevel(IaLevel3 toAdd) {
        if (getLevel3s() == null) {
            level3s = new ArrayList<>();
        }
        level3s.add(toAdd);
    }

    public void removeChilds() {
        if (getLevel3s() != null) {
            getLevel3s().stream()
                    .forEach(l3 -> l3.removeChilds());
            getLevel3s().clear();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("level3s", level3s).
                append("bereichName", bereichName).
                append("lockMode", lockMode).
                toString();
    }
}
