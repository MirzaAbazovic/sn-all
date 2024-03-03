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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Modell-Klasse bildet die moeglichen 'Level3' fuer Innenauftraege ab.
 */
@Entity
@Table(name = "T_IA_LEVEL3")
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_T_IA_LEVEL3_0", allocationSize = 1)
public class IaLevel3 extends AbstractIaLevel {

    private static final long serialVersionUID = -6343744223542298399L;

    private String abteilungsName;

    private List<IaLevel5> level5s;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LEVEL3_ID", referencedColumnName = "id", nullable = false)
    @Fetch(value = FetchMode.SUBSELECT)
    public List<IaLevel5> getLevel5s() {
        return level5s;
    }

    public void setLevel5s(List<IaLevel5> level5s) {
        this.level5s = level5s;
    }

    public void addIaLevel(IaLevel5 toAdd) {
        if (getLevel5s() == null) {
            level5s = new ArrayList<>();
        }
        level5s.add(toAdd);
    }

    public void removeChilds() {
        if (getLevel5s() != null) {
            getLevel5s().clear();
        }
    }

    @Nullable
    @Column(name = "ABTEILUNG_NAME")
    public String getAbteilungsName() {
        return abteilungsName;
    }

    public void setAbteilungsName(final String abteilungsName) {
        this.abteilungsName = abteilungsName;
    }
}
