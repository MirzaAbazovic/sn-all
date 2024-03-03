/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 11.05.2004 12:01:13
 */
package de.augustakom.authentication.model;

import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Abbildung einer Abteilung. <br>
 *
 *
 */
@Entity
@Table(name = "DEPARTMENT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SequenceGenerator(name = "SEQ_GEN", sequenceName = "S_DEPARTMENT_0", allocationSize = 1)
public class AKDepartment extends AbstractAuthenticationModel {

    public static final Long DEP_ST_CONNECT = Long.valueOf(1);
    public static final Long DEP_ST_ONLINE = Long.valueOf(2);
    public static final Long DEP_ST_VOICE = Long.valueOf(3);
    public static final Long DEP_DISPO = Long.valueOf(4);
    public static final Long DEP_FIELD_SERVICE = Long.valueOf(5);
    public static final Long DEP_AM = Long.valueOf(7);
    public static final Long DEP_PM = Long.valueOf(8);
    public static final Long DEP_FIBU = Long.valueOf(9);
    public static final Long DEP_ITS = Long.valueOf(10);
    public static final Long DEP_NP = Long.valueOf(11);
    public static final Long DEP_VERTRIEB = Long.valueOf(12);
    public static final Long DEP_GF = Long.valueOf(13);
    public static final Long DEP_CALL_CENTER = Long.valueOf(14);
    public static final Long DEP_SHOP_MUC = Long.valueOf(15);
    public static final Long DEP_QM = Long.valueOf(16);
    public static final Long DEP_ZP = Long.valueOf(17);
    public static final Long DEP_EXTERN = Long.valueOf(18);
    public static final Long DEP_SYSTEM_INTEGRATION = Long.valueOf(19);


    private Long id = null;
    private String name = null;
    private String description = null;
    private Long hurricanAbteilungId = null;
    private List<AKTeam> teams = new ArrayList<AKTeam>(0);

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "HURRICAN_ABT_ID")
    public Long getHurricanAbteilungId() {
        return hurricanAbteilungId;
    }

    public void setHurricanAbteilungId(Long hurricanAbteilungId) {
        this.hurricanAbteilungId = hurricanAbteilungId;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade({ org.hibernate.annotations.CascadeType.DELETE_ORPHAN, org.hibernate.annotations.CascadeType.ALL })
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = false)
    public List<AKTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<AKTeam> teams) {
        this.teams = teams;
    }
}
